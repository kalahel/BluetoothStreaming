package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ucp.bluetoothstreaming.Services.BluetoothClientService;
import com.ucp.bluetoothstreaming.Services.BluetoothServerService;

public class ClientServerPairing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_server_pairing);


        BluetoothDevice bd = getIntent().getExtras().getParcelable(ClientActivity.INTENT_SELECTOR);
        if (bd == null) {
            Log.e("bluetooth device transmission", "FAIL");
        } else {
            Log.e("bluetooth device transmission", "OK");

        }
        Intent startBluetoothCLientIntent = new Intent(this, BluetoothClientService.class);

        startBluetoothCLientIntent.putExtra(BluetoothClientService.TAG_INTENT, bd);
        startService(startBluetoothCLientIntent);


    }
}
