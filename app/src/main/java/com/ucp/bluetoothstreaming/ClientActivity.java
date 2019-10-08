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
        registerReceiver(receiver, filter);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }


        Set<BluetoothDevice> PairedDevices = bluetoothAdapter.getBondedDevices();
        bluetoothAdapter.startDiscovery();
        if (PairedDevices.size() > 0) {
            for (BluetoothDevice device : PairedDevices) {
                discoveredDevices.add(device);
            }
        }

    }

    List<String> mArrayAdapter;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Quand la recherche trouve un terminal
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // On récupère l'object BluetoothDevice depuis l'Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // On ajoute le nom et l'adresse du périphérique dans un ArrayAdapter (par exemple pour l'afficher dans une ListView)
                if(!discoveredDevices.contains(device))
                    discoveredDevices.add(device);

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
        ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, discoveredDevices);
        listView.setAdapter(arrayAdapter);

    }
}


