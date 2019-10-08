package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.R.layout;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClientActivity extends AppCompatActivity {
    public final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);



        }


        Set<BluetoothDevice> PairedDevices = bluetoothAdapter.getBondedDevices();
        if (PairedDevices.size() > 0) {
            for (BluetoothDevice dev : PairedDevices) {
                discoveredDevices.add(dev);
                Log.i("PAIRED", "Added " + dev.getName());

            }
        }
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();


    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!discoveredDevices.contains(dev) && dev.getName()!=null) {
                    discoveredDevices.add(dev);
                    Log.i("NEW", "Added " + dev.getName());


                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }


    public void refreshList(View view) {

        ListView listView = findViewById(R.id.listView);
        ArrayList list = new ArrayList();
        for (BluetoothDevice dev: discoveredDevices)
        {
            list.add(dev.getName() +"     "+dev.getAddress());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);

    }
}


