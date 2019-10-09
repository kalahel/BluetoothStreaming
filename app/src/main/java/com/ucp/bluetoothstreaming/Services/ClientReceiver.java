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
        if (intent.getStringExtra(BluetoothClientService.PLAY_TAG) != null)
            this.displayable.playVideo(intent.getStringExtra(BluetoothClientService.PLAY_TAG));
        else if (intent.getIntExtra(BluetoothClientService.UPDATE_TAG, -1) > 0) {
            this.displayable.updateProgressBar(intent.getIntExtra(BluetoothClientService.UPDATE_TAG, -1));
        } else
            this.displayable.handleTextReception(intent.getStringExtra(BluetoothClientService.SEND_MESSAGE_TAG));
    }
}
