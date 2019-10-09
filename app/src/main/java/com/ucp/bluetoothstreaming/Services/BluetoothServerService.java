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
import android.os.Message;
import android.util.Log;

import com.ucp.bluetoothstreaming.ServerActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BluetoothServerService extends Service {
    public static final String TAG = "BLUETOOTH_SERVER_SERVICE";
    public static final String START_ROUTINE_TAG = "START_ROUTINE_TAG";
    public static final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9BFFFF");

    private final IBinder mBinder = new LocalBinder();  // interface for clients that bind
    private BluetoothServerSocket mmServerSocket;
    private LocalBroadcastManager localBroadcastManager;
    private Thread serverThread;
    int mStartMode;                                     // indicates how to behave if the service is killed
    boolean mAllowRebind;                               // indicates whether onRebind should be used

    public BluetoothServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BLUETOOTH SERVER SERVICE", "Service created");
        this.serverThread = new AcceptThread();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BLUETOOTH SERVER SERVICE", "Service started");

        if (intent.getBooleanExtra(START_ROUTINE_TAG, false)) {
            this.startRoutine();
        } else {
            this.stopRoutine();
        }
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

    private void startRoutine() {
        this.serverThread.start();

    }


    private void stopRoutine() {
        Log.d(TAG, "Stopping routine");
        cancelConnect();
        this.serverThread.interrupt();
        stopSelf();
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * Source : https://developer.android.com/guide/components/bound-services.html#Binder
     */
    public class LocalBinder extends Binder {
        public BluetoothServerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BluetoothServerService.this;
        }
    }


    private class AcceptThread extends Thread {
        private OutputStream mmOutStream;
        private Handler handler; // handler that gets info from Bluetooth service


        public AcceptThread() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(TAG, APP_UUID);
                Log.d(TAG, "Thread Accepted");
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            OutputStream mmOutStream = null;

            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                Log.d(TAG, "Trying to accept");
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    Log.d(TAG, "A connection was accepted");

                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }

        private void manageMyConnectedSocket(BluetoothSocket socket) {
            // TODO FIll
            Log.d(TAG, "Client connected");
            Log.d(TAG, "Connected to the server !");
            Intent intent = new Intent(ServerActivity.FILTER);
            intent.putExtra(BluetoothClientService.SEND_MESSAGE_TAG,"A client is connected to you");
            localBroadcastManager.sendBroadcast(intent);

            try {
                mmOutStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // READ LOCAL FILE
            File localFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Video/" + ServerActivity.OUTPUT_FILE_NAME);
            int size = (int) localFile.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(localFile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mmOutStream.write(bytes);

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }

        }


    }


    // Closes the connect socket and causes the thread to finish.
    public void cancelConnect() {
        try {
            mmServerSocket.close();
            Log.d(TAG, "Socket closed");
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }


}
