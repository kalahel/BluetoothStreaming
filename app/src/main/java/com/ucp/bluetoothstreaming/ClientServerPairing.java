package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ucp.bluetoothstreaming.Services.BluetoothClientService;
import com.ucp.bluetoothstreaming.Services.BluetoothServerService;
import com.ucp.bluetoothstreaming.Services.ClientReceiver;
import com.ucp.bluetoothstreaming.Services.Displayable;

public class ClientServerPairing extends AppCompatActivity implements Displayable {

    VideoView videoView;
    public final static String FILTER = "com.app.ucp.bluetoothstreaming.ClientServerPairing.FILTER";
    private LocalBroadcastManager localBroadcastManager;
    private ProgressBar progressBar;
    private TextView downloadTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_server_pairing);
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.downloadProgressBar);
        downloadTextView = findViewById(R.id.downloadStatusText);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        BluetoothDevice bd = getIntent().getExtras().getParcelable(ClientActivity.INTENT_SELECTOR);
        if (bd == null) {
            Log.e("bluetooth device transmission", "FAIL");
        } else {
            Log.e("bluetooth device transmission", "OK");

        }
        Intent startBluetoothCLientIntent = new Intent(this, BluetoothClientService.class);

        startBluetoothCLientIntent.putExtra(BluetoothClientService.TAG_INTENT, bd);
        startService(startBluetoothCLientIntent);


        //broadcast receiver
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        BroadcastReceiver broadcastReceiver = new ClientReceiver(this);
        IntentFilter intentFilter = new IntentFilter(FILTER);
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);


    }

    @Override
    public void handleTextReception(String textReceived) {
        Toast.makeText(this, textReceived, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void playVideo(String textReceived) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        Uri uri = Uri.parse(textReceived);

        videoView.setVideoURI(uri);

        videoView.start();
    }

    @Override
    public void updateProgressBar(int progress) {
        if (progress >= 96) {
            this.progressBar.setVisibility(View.INVISIBLE);
            this.downloadTextView.setVisibility(View.INVISIBLE);
        }
        else{
            this.progressBar.setProgress(progress);
        }
    }


}
