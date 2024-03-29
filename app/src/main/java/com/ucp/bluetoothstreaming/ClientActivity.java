package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClientActivity extends AppCompatActivity {
    public final static int REQUEST_ENABLE_BT = 42;
    public final static String INTENT_SELECTOR = "BLUETOOTH_SELECTED_DEVICE";

    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    ListView listView;


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
            Log.i("IS ENABLED", "FINISH ");

        }

        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                selectedItem = selectedItem.substring(selectedItem.length() - 17);
                BluetoothDevice selectedDevice = null;
                Log.d("TAG ITEM CLICK", "selected item :" + selectedItem);


                for (BluetoothDevice dev : discoveredDevices) {
                    //Log.d("TAG ITEM CLICK", "selected item :" + selectedItem);

                    if (dev.getAddress().equals(selectedItem)) {
                        selectedDevice = dev;
                        break;
                    }
                }
                for (BluetoothDevice dev : pairedDevices) {
                    //Log.d("TAG ITEM CLICK", "selected item :" + selectedItem);

                    if (dev.getAddress().equals(selectedItem)) {
                        selectedDevice = dev;
                        break;
                    }
                }



                Log.d(">>>>>>>>>>>>>>>>>>>ITEM CLICK", "selected item :" + selectedDevice.getAddress());

                goToClientServerPairingActivity(selectedDevice);

            }

        });

        startResearch();


    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Found, add to a device list
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!discoveredDevices.contains(dev) && dev.getName() != null) {
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
        unregisterReceiver(myReceiver);
    }


    public void refreshList(View view) {
        Log.i("Log", "in the start searching method");

        bluetoothAdapter.startDiscovery();
        updateListView();


    }

    public void startResearch() {


        Set<BluetoothDevice> PairedDevices = bluetoothAdapter.getBondedDevices();
        if (PairedDevices.size() > 0) {
            for (BluetoothDevice dev : PairedDevices) {
                pairedDevices.add(dev);

            }
        }
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        ClientActivity.this.registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();

        updateListView();

    }

    private void updateListView() {
        Set<BluetoothDevice> PairedDevices = bluetoothAdapter.getBondedDevices();
        if (PairedDevices.size() > 0) {
            for (BluetoothDevice dev : PairedDevices) {
                if (!pairedDevices.contains(dev))
                    pairedDevices.add(dev);

            }
        }
        ArrayList list = new ArrayList();
        for (BluetoothDevice dev : pairedDevices) {
            list.add(dev.getName() + "     " + dev.getAddress());
        }


        for (BluetoothDevice dev : discoveredDevices) {
            list.add(dev.getName() + "     " + dev.getAddress());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);

    }

    public void goToClientServerPairingActivity(BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(this, ClientServerPairing.class);
        intent.putExtra(INTENT_SELECTOR,bluetoothDevice);
        startActivity(intent);
    }

}