package ecig.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ecig.app.ble.BluetoothLeService;

/**
 * Created by kssworld93 on 1/4/15.
 *
 *
 * Provides code to help the UI
 *  Scan for devices,
 *  Pair w a device,
 *  Disconnect a device
 *
 * Scan: tells Android to scan, and schedules a stop callback after some TIMEOUT.
 *      on each result, adds BluetoothDevice to a set of devices.
 *      invokes stop callback with the BluetoothDevices which accumulated over time
 *
 */
public class EmbreAgent {

    Context context = null;

    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;


    // This is for implementing timeouts.
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private boolean mScanning = false;

    // This is the accelerometer service UUID of the SensorTag.
    static final String SERVICE_UUID = "F000AA10-0451-4000-B000-000000000000";
    static final String ACCEL_PER_UUID = "F000AA13-0451-4000-B000-000000000000";
    static final int SLEEP_MS = 150;
    static final int WRITE_TIMEOUT = 7000;


    public final static String TAG = "ecig.app.EmbreAgent";

    static class CDataError {
        String message;
        int index;
        public CDataError(String message, int index) {
            this.message = message; this.index = index;
        }
    }

    static class CData {
        String label;
        int value;

        public CData(String label, int value) {
            this.label = label; this.value = value;
        }
        public String valueString() {
            String dataValue = value == 0 ? "-" :Integer.toString(value) + "%";
            return dataValue;
        }
        public static CDataError validate(CData[] cDatas) {
            int sum = 0;
            for (int i = 0; i < cDatas.length; i ++) {
                if (cDatas[i].value < 0) {
                    return new CDataError("Percent can't be negative", i);
                }
                sum += cDatas[i].value;
                if (sum > 100) {
                    return new CDataError("This adds up to over 100%\nPlease fix and try again.", i);
                }

            }
            if (sum < 100) {
                return new CDataError("This doesn't add up to 100%.\nPlease fix and try again.", cDatas.length - 1);
            }

            return null;
        }

    }

    /* Safe to call multiple times, will only truly initialize the first time */
    public void initialize(Context context) {
        if (this.context == null) {
            this.context = context.getApplicationContext();
            initBluetoothAdapter();
        }
    }

    private void initBluetoothAdapter() {
        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to BluetoothAdapter through BluetoothManager.
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();


        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            throw new RuntimeException("No bluetooth");
        }

        // Ensures Bluetooth is enabled. If not, displays a dialog requesting user permission to enable Bluetooth.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
        }
    }


    BluetoothAdapter.LeScanCallback mLeScanCallback;

    /* Async function to start scanning for BLE devices, times out after delay milliseconds.
    * Returns bool if started scanning successfully. */
    public boolean startScan(BluetoothAdapter.LeScanCallback mLeScanCallback) {
        this.mLeScanCallback = mLeScanCallback;

        if (mScanning) {
            throw new RuntimeException("Attempt to scan while already scanning");
        }

        mScanning = true;

        return mBluetoothAdapter.startLeScan(mLeScanCallback);

    }

    public void stopScan() {
        if (mScanning) {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public void cancelTask() {
        if (task != null) {
            task.cleanUp();
        }
    }


    class WriteTask implements Runnable {
        public String TAG = "ecig.app.EmbreAgent.WriteTask";
        public CData[] data;
        public String macAddress;
        public WriteCB whenDone;

        int state;
        BluetoothGatt mGatt;
        long startTime;

        private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onReliableWriteCompleted (BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    written = true;
                } else {
                    // TODO handle error. for now the program just hangs.
                    Log.e(TAG, "Write Failed. dont know what to do");
                }
            }

            // https://developer.android.com/reference/android/bluetooth/BluetoothGattCallback.html#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                // If connected, do callback
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    connected = true;
                    Log.i(TAG, "connection state change: connected");
                } else {
                    connected = false;
                    Log.i(TAG, "connection state change: " + newState);
                }
                state = newState;
            }
        };


        boolean connected;
        private boolean written = false;


        private void waitTillConnected() throws TimeoutException {
            while(!connected) {
                try { Thread.sleep(20); } catch (InterruptedException e) {}
                checkTimeout();
            }
        }

        private void waitTillWritten() throws TimeoutException {
            while(!written) {
                try { Thread.sleep(20); } catch (InterruptedException e) {}
                checkTimeout();
            }
        }

        private void executeWriteSequence(byte b) throws TimeoutException {
            waitTillConnected();
            Log.i(TAG, "Connected");
            written = false;

            BluetoothGattCharacteristic characteristic =
                    new BluetoothGattCharacteristic(UUID.fromString(ACCEL_PER_UUID),
                            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                            BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
            byte[] val = new byte[1];
            val[0] = b;
            characteristic.setValue(val);

            Log.i(TAG, "Begin write transaction");
            mGatt.beginReliableWrite();
            mGatt.writeCharacteristic(characteristic);// TODO fix
            mGatt.executeReliableWrite();
            Log.i(TAG, "End write transaction");
            waitTillWritten();
            Log.i(TAG, "Written");
        }

        private void checkTimeout() throws TimeoutException{
            long nowTime = System.currentTimeMillis();
            if ((nowTime - startTime) > WRITE_TIMEOUT)
                throw new TimeoutException();
        }

        public void cleanUp() {
            if(mGatt != null)
                mGatt.disconnect();
            EmbreAgent.this.task = null;
            whenDone.call(false);
        }

        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            try {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);

                mGatt = device.connectGatt(context, true, mGattCallback);
                Log.i(TAG, "Issued connect call");

                executeWriteSequence((byte) 101);
                try {Thread.sleep(SLEEP_MS);} catch (InterruptedException e) {}
                for (int i = 0; i < 6; i++) {
                    executeWriteSequence((byte) data[i].value);
                    try {
                        Thread.sleep(SLEEP_MS);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (TimeoutException e) {
                mGatt.disconnect();
                EmbreAgent.this.task = null;
                whenDone.call(false);
                return;
            }
            mGatt.disconnect();
            EmbreAgent.this.task = null;
            whenDone.call(true);

            return;
        }

    }

    interface WriteCB { public void call(boolean success); }
    volatile WriteTask task;
    // Given the data, macAddress, timeout in ms, and done callback,
    // returns immediately and calls the callback when done.
    public void writeData(CData[] data, String macAddress, WriteCB whenDone) {
        if (task != null) {
            throw new RuntimeException("There is still a task running");
        }
        task = new WriteTask();
        task.data = data;
        task.macAddress = macAddress;
        task.whenDone = whenDone;

        worker.submit(task);
    }


}

