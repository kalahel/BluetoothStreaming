package com.ucp.bluetoothstreaming.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ucp.bluetoothstreaming.ClientActivity;
import com.ucp.bluetoothstreaming.ClientServerPairing;
import com.ucp.bluetoothstreaming.ServerActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class BluetoothClientService extends Service {

    private static final int BUFFER_COUNT_PAQUETS = 3000;
    public static final float FILE_SIZE = 5785;
    public static final String TAG = "BLUETOOTH_CLIENT_SERVICE";
    public static final String TAG_INTENT = "BLUETOOTH_CLIENT_INTENT";
    public static final String SEND_MESSAGE_TAG = "com.app.ucp.bluetoothstreaming.Services.BluetoothClient.SEND_MESSAGE";
    public static final String PLAY_TAG = "PLAYING_BABY";
    public static final String UPDATE_TAG = "UPDATE_TAG";


    private final IBinder mBinder = new BluetoothClientService.LocalBinder();  // interface for clients that bind
    private Thread clientThread;
    private LocalBroadcastManager localBroadcastManager;


    public BluetoothClientService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothDevice bd = intent.getExtras().getParcelable(TAG_INTENT);
        clientThread = new ConnectThread(bd);
        clientThread.start();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * Source : https://developer.android.com/guide/components/bound-services.html#Binder
     */
    public class LocalBinder extends Binder {
        public BluetoothClientService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BluetoothClientService.this;
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private InputStream inputStream;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(BluetoothServerService.APP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

        private void manageMyConnectedSocket(BluetoothSocket socket) {
            // TODO FIll
            Log.d(TAG, "Connected to the server !");
            Intent intent = new Intent(ClientServerPairing.FILTER);
            intent.putExtra(SEND_MESSAGE_TAG, "Connected to Server");
            localBroadcastManager.sendBroadcast(intent);

            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + "Video";
                File rootFile = new File(rootDir);
                rootFile.mkdir();


                File localFile = new File(rootFile, ServerActivity.OUTPUT_FILE_NAME);
                String output = "PATH OF LOCAL FILE : " + localFile.getPath();
                if (rootFile.exists()) Log.d("SERVICE_ACTIVITY", output);
                if (!localFile.exists()) {
                    localFile.createNewFile();
                } else {
                    localFile.delete();
                    localFile.createNewFile();
                }
                FileOutputStream f = new FileOutputStream(localFile);

                byte[] buffer = new byte[1024];
                int len1 = 0;
                int nbOfPaquetsReceived = 0;
                FileDescriptor fileDescriptor = f.getFD();
                try {
                    while ((len1 = inputStream.read(buffer)) > 0) {
                        nbOfPaquetsReceived++;
                        Log.d(TAG, "Nbs of paquets received BEFORE: " + nbOfPaquetsReceived + "  READ SIZE : " + len1);

                        f.write(buffer, 0, len1);
                        f.flush();
                        fileDescriptor.sync();

                        Intent i = new Intent(ClientServerPairing.FILTER);
                        i.putExtra(UPDATE_TAG, (int) (((float) nbOfPaquetsReceived / FILE_SIZE) * 100));
                        localBroadcastManager.sendBroadcast(i);


                        Log.d(TAG, "Nbs of paquets received  AFTER: " + nbOfPaquetsReceived);
                    }
                } catch (IOException se) {
                    Log.d(TAG, "Connexion CLOSED by SERVER ");
                }
                Log.d(TAG, "Sending Intent ");
                f.close();


                Intent i = new Intent(ClientServerPairing.FILTER);
                i.putExtra(PLAY_TAG, localFile.toString());
                localBroadcastManager.sendBroadcast(i);


                Log.d(TAG, "Intent Sended ");
            } catch (IOException e) {
                Log.d("Error....", e.toString());
            }

        }
    }


}
