package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

import com.ucp.bluetoothstreaming.Services.BluetoothServerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ServerActivity extends AppCompatActivity {
    public static final String VIDEO_URL = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    public static final String OUTPUT_FILE_NAME = "projectVideo.mp4";
    public static final int REQUEST_ENABLE_BT = 16;
    private Switch ShareSwitch;
    private BluetoothServerService bluetoothServerService;
    private boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        ShareSwitch = findViewById(R.id.ShareSwitch);

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent stopServiceIntent = new Intent(this, BluetoothServerService.class);
        stopService(stopServiceIntent);
    }

    public void startDownload(View view) {
        new VideoDownloadTask().execute("");
    }

    public void switchActivated(View view) {
        if (this.ShareSwitch.isChecked()) {
            Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
            if (!mBound) {
                Intent bluetoothServiceIntent = new Intent(this, BluetoothServerService.class);
                bindService(bluetoothServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
            }
            Intent startServiceIntent = new Intent(this, BluetoothServerService.class);
            startService(startServiceIntent);

        } else {
            Toast.makeText(this, "NOT CHECKED", Toast.LENGTH_SHORT).show();
            Intent stopServiceIntent = new Intent(this, BluetoothServerService.class);
            stopService(stopServiceIntent);
            mBound = false;
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

    /**
     * Asynchronous task responsible for the download of the video
     */
    private class VideoDownloadTask extends AsyncTask<String, Integer, VideoDownloadTask.Result> {
        ProgressBar progressBar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = findViewById(R.id.downloadProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Result doInBackground(String... strings) {
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + "Video";
                File rootFile = new File(rootDir);
                rootFile.mkdir();
                URL url = new URL(ServerActivity.VIDEO_URL);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                int status = httpURLConnection.getResponseCode();

                if (status != HttpURLConnection.HTTP_OK) {
                    // httpURLConnection.getErrorStream();
                    Log.d("SERVICE_ACTIVITY", "HTTP STATUS NOT OK");
                }

                File localFile = new File(rootFile, ServerActivity.OUTPUT_FILE_NAME);
                String output = "PATH OF LOCAL FILE : " + localFile.getPath();
                if (rootFile.exists()) Log.d("SERVICE_ACTIVITY", output);
                if (!localFile.exists()) {
                    localFile.createNewFile();
                }
                FileOutputStream f = new FileOutputStream(localFile);
                InputStream in = httpURLConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();

            } catch (IOException e) {
                Log.d("Error....", e.toString());
            }
            return null;
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the
         * download task has completed, either the result value or exception can be a non-null
         * value. This allows you to pass exceptions to the UI thread that were thrown during
         * doInBackground().
         */
        class Result {
            public String mResultValue;
            public Exception mException;

            public Result(String resultValue) {
                mResultValue = resultValue;
            }

            public Result(Exception exception) {
                mException = exception;
            }
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(Integer... values) {
            // setting progress percentage
            super.onProgressUpdate(values);
            this.progressBar.setProgress(values[0]);
            //progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


    }
}
