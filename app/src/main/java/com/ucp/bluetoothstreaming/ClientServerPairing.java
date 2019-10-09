package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ucp.bluetoothstreaming.Services.BluetoothClientService;
import com.ucp.bluetoothstreaming.Services.BluetoothServerService;

public class ClientServerPairing extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_server_pairing);
        videoView = findViewById(R.id.videoView);

        BluetoothDevice bd = getIntent().getExtras().getParcelable(ClientActivity.INTENT_SELECTOR);
        if (bd == null) {
            Log.e("bluetooth device transmission", "FAIL");
        } else {
            Log.e("bluetooth device transmission", "OK");

        }
        Intent startBluetoothCLientIntent = new Intent(this, BluetoothClientService.class);

        startBluetoothCLientIntent.putExtra(BluetoothClientService.TAG_INTENT, bd);
        startService(startBluetoothCLientIntent);

        //partie video




    }

    public void playVideo(View view) {

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        Uri uri = Uri.parse("/storage/emulated/0/Video/projectVideo.mp4");
        videoView.setVideoURI(uri);
        videoView.start();
    }

    //partie video /storage/emulated/0/Video/projectVideo.mp4


}
