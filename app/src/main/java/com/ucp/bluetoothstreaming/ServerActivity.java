package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ucp.bluetoothstreaming.Services.BluetoothClientService;
import com.ucp.bluetoothstreaming.Services.BluetoothServerService;
import com.ucp.bluetoothstreaming.Services.ClientReceiver;
import com.ucp.bluetoothstreaming.Services.Displayable;
import com.ucp.bluetoothstreaming.Services.DownloadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ServerActivity extends AppCompatActivity implements Displayable {
    public static final String VIDEO_URL = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    public static final String OUTPUT_FILE_NAME = "projectVideo.mp4";
    public static final int REQUEST_ENABLE_BT = 16;
    public static final String FILTER = "com.app.ucp.bluetoothstreaming.ServerActivity.FILTER";
    private Switch ShareSwitch;
    private BluetoothServerService bluetoothServerService;
    private boolean mBound = false;
    private LocalBroadcastManager localBroadcastManager;
    private ProgressBar progressBar;
    private TextView downloadTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        ShareSwitch = findViewById(R.id.ShareSwitch);
        progressBar = findViewById(R.id.downloadProgressBar);
        downloadTextView = findViewById(R.id.downloadStatus);

        progressBar.setVisibility(View.INVISIBLE);
        downloadTextView.setVisibility(View.INVISIBLE);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Log.d("SERVER_ACTIVITY", "Bluetooth not supported by device");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        Intent bluetoothServiceIntent = new Intent(this, BluetoothServerService.class);
        bindService(bluetoothServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

        //broadcast receiver
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        BroadcastReceiver broadcastReceiver = new ClientReceiver(this);
        IntentFilter intentFilter = new IntentFilter(FILTER);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent stopServiceIntent = new Intent(this, BluetoothServerService.class);
        stopService(stopServiceIntent);
    }

    public void startDownload(View view) {
        Intent downloadService = new Intent(this, DownloadService.class);
        startService(downloadService);

    }

    public void switchActivated(View view) {
        if (this.ShareSwitch.isChecked()) {
            Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
            if (!mBound) {
                Intent bluetoothServiceIntent = new Intent(this, BluetoothServerService.class);
                bindService(bluetoothServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
            }
            Intent startServiceIntent = new Intent(this, BluetoothServerService.class);
            startServiceIntent.putExtra(BluetoothServerService.START_ROUTINE_TAG, true);
            startService(startServiceIntent);

        } else {
            Toast.makeText(this, "NOT CHECKED", Toast.LENGTH_SHORT).show();
            Intent stopServiceIntent = new Intent(this, BluetoothServerService.class);
            stopServiceIntent.putExtra(BluetoothServerService.START_ROUTINE_TAG, false);
            stopService(stopServiceIntent);
            mBound = false;
        }
    }

    @Override
    public void handleTextReception(String textReceived) {
        Toast.makeText(this, textReceived, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void playVideo(String textReceived) {

    }

    @Override
    public void updateProgressBar(int progress) {
        if (progress >= 96) {
            this.progressBar.setVisibility(View.INVISIBLE);
            this.downloadTextView.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            downloadTextView.setVisibility(View.VISIBLE);
            this.progressBar.setProgress(progress);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     * when the service is connected start the game
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BluetoothServerService.LocalBinder binder = (BluetoothServerService.LocalBinder) service;
            bluetoothServerService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
