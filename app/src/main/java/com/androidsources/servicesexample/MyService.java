package com.androidsources.servicesexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gowtham Chandrasekar on 18-10-2015.
 */
public class MyService extends Service {

    GetXMLTask task;
    String url = "http://images.gamersyde.com/image_halo_4-19340-2311_0001.jpg";

    @Override
    public void onCreate() {
        Log.d("AsyncTaskMethod","onCreate is called");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("AsyncTaskMethod","onBind is called");

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AsyncTaskMethod","onStartCommand is called");
        Toast.makeText(getBaseContext(),"Service has been started",Toast.LENGTH_SHORT).show();
        task = new GetXMLTask();
        task.execute(url);
        return START_STICKY;
    }


    private class GetXMLTask extends AsyncTask<String, Integer, Boolean> {
        int counter=0;
        int contentLength = -1;

        @Override
        protected void onPreExecute() {
            Log.d("AsyncTaskMethod","onPreExecute is called");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d("AsyncTaskMethod","doInBackground is called");
            Boolean downloadStatus = false;
            File file = null;
            URL downloadUrl = null;
            HttpURLConnection connection = null;
            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;
            try {
                downloadUrl = new URL(params[0]);
                connection = (HttpURLConnection) downloadUrl.openConnection();
                inputStream = connection.getInputStream();
                int read = -1;
                file = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                        "/" + Uri.parse(params[0]).getLastPathSegment()));
                fileOutputStream = new FileOutputStream(file);
                contentLength = connection.getContentLength();
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    counter = counter + read;
                    Log.d("calcualting", "contentLenght " + contentLength + " counter " + counter);
                    publishProgress(counter);
                    if (isCancelled()==true){
                        Log.d("GettingCancelled","isCancelled");
                        break;
                    }
                }


                downloadStatus = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return downloadStatus;
        }


        protected void onProgressUpdate(Integer... progress) {
            Log.d("AsyncTaskMethod", "onProgressUpdate" + progress[0]);
//            Toast.makeText(getBaseContext(),"onProgressUpdate"+progress[0],Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("AsyncTaskMethod","onPostExecute is called");
            Toast.makeText(getApplicationContext(),"onPostExecute download completed",Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        task.cancel(true);
        Log.d("AsyncTaskMethod", "onDestroy is called");
        Toast.makeText(getBaseContext(),"Service has been stopped",Toast.LENGTH_SHORT).show();
    }
}
