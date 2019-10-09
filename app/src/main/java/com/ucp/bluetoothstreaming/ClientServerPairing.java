package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ucp.bluetoothstreaming.Services.BluetoothClientService;
import com.ucp.bluetoothstreaming.Services.BluetoothServerService;

public class ClientServerPairing extends AppCompatActivity {


    private String videoPath = "url";

    private static ProgressDialog progressDialog;
    String videourl;
    VideoView videoView;


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

        //partie video
        /*
        setContentView(R.layout.play_video);

        videoView = (VideoView) findViewById(R.id.videoView);


        progressDialog = ProgressDialog.show(PlayVideo.this, "", "Buffering video...", true);
        progressDialog.setCancelable(true);

        PlayVideo();
    */

    }
/*
    //partie video /storage/emulated/0/Video/projectVideo.mp4
    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(PlayVideo.this);
            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(videoPath);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    progressDialog.dismiss();
                    videoView.start();
                }
            });


        } catch (Exception e) {
            progressDialog.dismiss();
            System.out.println("Video Play Error :" + e.toString());
            finish();
        }

    }

*/
}
