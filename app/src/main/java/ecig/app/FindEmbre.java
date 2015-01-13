package ecig.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;


public class FindEmbre extends Activity {
    ListView listView;
    ArrayAdapter adapter;
    EmbreAgent embre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_find_embre);


        embre = new EmbreAgent();
        embre.initialize(this);

        listView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = (BluetoothDevice) adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        startScan();
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        if (devices == null)
            devices = new HashMap<String, BluetoothDevice>();
        String[] macAddresses = outState.getStringArray("devices");
        for (int i = 0; i < macAddresses.length; i ++) {
            devices.clear();
            devices.put(macAddresses[i], null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String[] macAddresses = devices.keySet().toArray(new String[devices.size()]);
        outState.putStringArray("devices", macAddresses);
    }

    @Override
    protected void onStop() {
        stopScan();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        embre.destroy();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void setData(BluetoothDevice[] devices) {
        adapter.clear();
        adapter.addAll(devices);
    }

    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            FindEmbre.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String addr = device.getAddress();
                    if (!devices.containsKey(addr)) {
                        adapter.add(device);
                        devices.put(addr, device);
                    }
                }
            });
        }
    };

    private void startScan() {
        if (devices == null)
            devices = new HashMap<String, BluetoothDevice>();
        boolean didItWork = embre.startScan(mLeScanCallback);

        if (!didItWork) {
            // TODO error handling
        } else {
            setProgressBarIndeterminateVisibility(true);
        }
    }

    private void stopScan() {
        embre.stopScan();
    }

}
