package com.ucp.bluetoothstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

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
    VideoView vidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
    }


    public void startDownload(View view) {
        new VideoDownloadTask().execute("");
    }

    private class VideoDownloadTask extends AsyncTask<String, Integer, VideoDownloadTask.Result> {
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

                if (status != HttpURLConnection.HTTP_OK){
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


    }
}
