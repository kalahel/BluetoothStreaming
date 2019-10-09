package com.ucp.bluetoothstreaming.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ucp.bluetoothstreaming.ClientServerPairing;
import com.ucp.bluetoothstreaming.ServerActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DownloadService extends Service {
    public static final String TAG = "DOWNLOAD_SERVICE";
    private final IBinder mBinder = new DownloadService.LocalBinder();  // interface for clients that bind
    private BluetoothServerSocket mmServerSocket;
    private LocalBroadcastManager localBroadcastManager;
    private Thread serverThread;
    int mStartMode;                                     // indicates how to behave if the service is killed
    boolean mAllowRebind;                               // indicates whether onRebind should be used

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        this.serverThread = new DownloadThread();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        this.serverThread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * Source : https://developer.android.com/guide/components/bound-services.html#Binder
     */
    public class LocalBinder extends Binder {
        public DownloadService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DownloadService.this;
        }
    }

    private class DownloadThread extends Thread {

        public DownloadThread() {
        }

        public void run() {

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
                int nbOfPaquetsReceived = 0;
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    nbOfPaquetsReceived++;
                    Intent i = new Intent(ServerActivity.FILTER);
                    i.putExtra(BluetoothClientService.UPDATE_TAG, (int) (((float) nbOfPaquetsReceived / BluetoothClientService.FILE_SIZE) * 100));
                    localBroadcastManager.sendBroadcast(i);
                    f.write(buffer, 0, len1);
                }
                f.close();
                Intent intent = new Intent(ServerActivity.FILTER);
                intent.putExtra(BluetoothClientService.SEND_MESSAGE_TAG, "Download finished successfully");
                localBroadcastManager.sendBroadcast(intent);

            } catch (IOException e) {
                Log.d("Error....", e.toString());
            }
        }
    }

}

