package com.ucp.bluetoothstreaming.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BluetoothServerService extends Service {

    int mStartMode;                                     // indicates how to behave if the service is killed
    private final IBinder mBinder = new LocalBinder();  // interface for clients that bind
    boolean mAllowRebind;                               // indicates whether onRebind should be used

    public BluetoothServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BLUETOOTH SERVER SERVICE","Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BLUETOOTH SERVER SERVICE","Service started");
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
        public BluetoothServerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BluetoothServerService.this;
        }
    }
}
