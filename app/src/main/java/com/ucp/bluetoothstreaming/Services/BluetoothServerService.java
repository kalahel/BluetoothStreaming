package com.ucp.bluetoothstreaming.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothServerService extends Service {
    public static final String TAG = "BLUETOOTH_SERVER_SERVICE";
    public static final String START_ROUTINE_TAG = "START_ROUTINE_TAG";
    public static final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9BFFFF");
    private final IBinder mBinder = new LocalBinder();  // interface for clients that bind
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
        private final BluetoothServerSocket mmServerSocket;


        public AcceptThread() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(TAG, APP_UUID);
                Log.d(TAG,"Thread Accepted");
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
//            while (true) {
            Log.d(TAG, "Trying to accept");
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
//                    break;
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
//                    break;
            }

//            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

        private void manageMyConnectedSocket(BluetoothSocket socket) {
            // TODO FIll
        }
    }


}
