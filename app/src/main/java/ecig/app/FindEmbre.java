package ecig.app;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class FindEmbre extends Activity {
    ListView listView;
    ArrayAdapter adapter;
    EmbreAgent embre;
    public static final String TAG = "ecig.app.FindEmbre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_find_embre);


        MyApplication app = (MyApplication) getApplication();
        embre = app.embre;
        embre.initialize(this);

        listView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = (BluetoothDevice) adapter.getItem(position);
            MyApplication app = (MyApplication) getApplication();
            app.embreMac = device.getAddress();
            Toast t = Toast.makeText(getApplicationContext(), "Registered Embre", Toast.LENGTH_SHORT);
            t.show();
            FindEmbre.this.finish();
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
            Log.e(TAG, "gulp");
        } else {
            setProgressBarIndeterminateVisibility(true);
        }
    }

    private void stopScan() {
        embre.stopScan();
    }

}
