package com.ucp.bluetoothstreaming.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClientReceiver extends BroadcastReceiver {
    private Displayable displayable;

    public ClientReceiver(Displayable displayable) {
        super();
        this.displayable = displayable;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.displayable.handleTextReception(intent.getStringExtra(BluetoothClientService.SEND_MESSAGE_TAG));
    }
}
