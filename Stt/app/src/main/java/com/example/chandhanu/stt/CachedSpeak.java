package com.example.chandhanu.stt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

public class CachedSpeak {
    String words[];
    int length;
    static int counter = 0;
    CountDownLatch doneLoadingFiles;
    RequestQueue queue;
    Context context;
    CachedSpeak(Context c,String speak)
    {
        context=c;
        words=speak.split(" ");
        length=words.length;
        doneLoadingFiles=new CountDownLatch(length);
        queue= Volley.newRequestQueue(context);
        fetch();
    }
    boolean notCached(String fileName)
    {
        File p=new File(context.getString(R.string.voice_path)+fileName+".mp3");
        try {
            if (p.exists()) {
                doneLoadingFiles.countDown();
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }
    void fetch()
    {
        for(int i=0;i<length;++i)
            if(notCached(words[i]))
            networkFetch(words[i]);
        new AsyncCaller().execute();
    }
    synchronized void networkFetch(final String speak)
    {
        Log.i("eddie","fetch words");
            //temp.exists(t)
            //if(true){
        JsonObjectRequest1 jsonObjectRequest;
            Log.i("eddie","fetch words if");
            String url = "https://avatar.lyrebird.ai/api/v0/generate";
            final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", speak.replaceAll(","," "));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonObjectRequest = new JsonObjectRequest1
                    (context,speak,Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("eddie","response");
                            doneLoadingFiles.countDown();
                            Log.i("eddie","count:"+doneLoadingFiles.getCount());
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.i("error in volley:", error.toString());
                            //writeFile(speak);

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    String token=getToken();
                    if (token == "notFound") {
                        Toast.makeText(context,"token is still not set",Toast.LENGTH_LONG);
                    }
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }

            };
            queue.add(jsonObjectRequest);

    }
    String getToken()
    {
        SharedPreferences sharedPref = context.getSharedPreferences("eddie-overall",Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.token),"notFound");
    }
    private class AsyncCaller extends AsyncTask<String, Void, String>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            //pdLoading.setMessage("\tLoading...");
            //pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                Log.i("eddie","done:"+doneLoadingFiles.getCount());
                doneLoadingFiles.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("done");
            Log.i("eddie","done loading ");
            Toast.makeText(context,"done loading files",Toast.LENGTH_SHORT);
            //playFromFileCached(words,0);
            playFromFileCached1(words);

        }
        void playFromFileCached(final String fileNames[],final int i) {
            final MediaPlayer mediaPlayer=new MediaPlayer();
            Log.i("eddie", "hello world" + fileNames[i]);
            if (i >= fileNames.length)
                return;
            final String fileName = fileNames[i];
            try {
                mediaPlayer.setDataSource(context, Uri.parse(context.getString(R.string.voice_path) + fileName + ".mp3"));
                //mediaPlayer.setDataSource(this,Uri.parse("temp.mp3"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    if(i<length)
                        playFromFileCached(fileNames, i + 1);

                }
            });
        }
        void playFromFileCached1(final String fileNames[]) {
            Log.i("eddie","alternative playback:"+length);
            ArrayList<MediaPlayer> mPlayerList = new ArrayList<MediaPlayer>();
            mPlayerList.clear();
            for (int i=0;i<length;++i)
            {
                Log.i("eddie","loop:"+i);
                String fileName=fileNames[i];
                try {
                    MediaPlayer mPlayerT = new MediaPlayer();

                   // AssetFileDescriptor descriptor = context.getAssets().openFd(context.getString(R.string.voice_path)+fileName+".mp3");
                    //mPlayerT.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    mPlayerT.setDataSource(context, Uri.parse(context.getString(R.string.voice_path) + fileName + ".mp3"));
                    //descriptor.close();

                    mPlayerT.prepare();
                    mPlayerT.setVolume(1f, 1f);
                    mPlayerT.setLooping(false);
                    Log.i("eddie","inside the adder loop:"+i);
                    mPlayerList.add(mPlayerT);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            for (int i=0; i<mPlayerList.size()-1; i++) //Do not include last element
            {
                mPlayerList.get(i).setNextMediaPlayer(mPlayerList.get(i+1));

            }
            Log.i("eddie","mplayer size:"+mPlayerList.size());
            mPlayerList.get(0).start();

        }


    }
}
