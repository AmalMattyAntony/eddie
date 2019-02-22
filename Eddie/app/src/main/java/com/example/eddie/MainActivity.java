package com.example.eddie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RequestQueue queue;
    String android_id;
    MediaPlayer mediaPlayer;
    JsonObjectRequest1 jsonObjectRequest;
    private DatabaseHelper myDb;
    TextToSpeech tts;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = Volley.newRequestQueue(getApplicationContext());
        myDb=new DatabaseHelper(this);
        tv=findViewById(R.id.mainText);
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                checkAndAskPermissions(0);
                checkAndAskPermissions(1);
                checkAndAskPermissions(2);
                checkAndAskPermissions(3);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageButton mainButton=(ImageButton)findViewById(R.id.mainButton);
        mainButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                listen(0);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.lyrebird_auth) {
            Intent intent = new Intent(getApplicationContext(), Lyrebird.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void toggleVoice(View v)
    {
        Switch s=(Switch)v;
        if(s.isChecked())
        {
         setGlobalConfig("voice-agent","lyre_bird");
         s.setText("LyreBird");
        }
        else
        {
            setGlobalConfig("voice-agent","google_voice");
            s.setText("Google voice");
        }
        toaster("Voice profile:"+getGlobalConfig("voice-agent"));

    }
    void setGlobalConfig(String key,String val)
    {
        SharedPreferences sharedPref = getSharedPreferences("eddie-overall", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key,val);
        editor.commit();
    }
    String getGlobalConfig(String key)
    {
        SharedPreferences sharedPref = getSharedPreferences("eddie-overall",Context.MODE_PRIVATE);
        return sharedPref.getString(key,"notFound");
    }
    void toaster(String text)
    {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
    void listen(int reqCode)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        try{
            startActivityForResult(intent,reqCode);
        }catch (ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(),"Couldn't wake up Eddie!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        String result="";
        if(resultCode == RESULT_OK && data != null)
            result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
        switch (requestCode)
        {
            case 0:sendMessage(result);break;
            case 1:speak(result);toaster(result);break;
        }

    }
    public void sendMessage(String request) {
        /*if(offlineResponse(request))
        {
            return;
        }*/
        String url = getString(R.string.droplet);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("request", request);
            jsonObject.put("userId", android_id);
            jsonObject.put("token", "blah");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String reply=response.getString("reply");
                            tv.setText("Response: " + reply);
                            //textView.setText(response.getString("reply"));
                            speak(reply);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        toaster(error.toString());

                    }
                });
        queue.add(jsonObjectRequest);
        Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
    }

    void speak(String speak)
    {
        String mode=getGlobalConfig("voice-agent");
        if(mode=="lyre_bird")
        {
            lyreBirdSpeak(speak);
        }
        else if(mode=="google_voice")
        {
            tts.speak(speak,TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    void lyreBirdSpeak(final String speak)
    {
        final String token=getGlobalConfig("token_id");
        if(token=="notFound") {  toaster("Token must be set before LyreBird voice can be activated!");return;}
        File temp=new File(getString(R.string.voice_path)+speak+".mp3");
        if(!temp.exists()) {
            String url = "https://avatar.lyrebird.ai/api/v0/generate";
            final JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("text", speak);
            }
            catch(JSONException e)
            {
                toaster(e.toString());
            }
            jsonObjectRequest = new JsonObjectRequest1
                    (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(JSONObject response) {

                            //textView.setText("Response: ");// + response.toString());
                            // tv4.setText(jsonObjectRequest.responseBody.toString());
                            writeFile(speak);
                        }
                    }, new Response.ErrorListener() {

                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.i("error in volley:", error.toString());
                            //Log.i("network errorapparently",error.networkResponse.toString());
                            writeFile(speak);

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }

            };
            //jsonObjectRequest.c=getApplicationContext();
            //jsonObjectRequest.a=this;
            // textView.setText(getToken());
            queue.add(jsonObjectRequest);
        }
        else
        {
            playFromFile(speak);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    void writeFile(String fileName)
    {
        checkAndAskPermissions(3);
        if(fileName=="")fileName="temp";
        Log.i("writing to file",fileName);
        File p=new File(getString(R.string.voice_path));
        try {
            if (!p.exists())
                p.mkdir();
            //Log.i("p is",p);
            File dataFile = new File(getString(R.string.voice_path)+fileName+".mp3");
            dataFile.createNewFile();
            dataFile.setWritable(true);
            OutputStream o = new FileOutputStream(dataFile);
            while (jsonObjectRequest.body == null) ;
            o.write(jsonObjectRequest.body);
            o.flush();
            o.close();
            // speak1();
            playFromFile(fileName);
        }catch (IOException e) {
            Log.i("error in writing",e.toString());
            e.printStackTrace();
            toaster(e.toString());
        }
        // AsyncTaskRunner t=new AsyncTaskRunner();
        //t.execute("5");
    }
    void playFromFile(String fileName)
    {
        if(fileName=="")
            fileName="temp";
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(getString(R.string.voice_path)+fileName+".mp3"));
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
                mp.release();
            }

        });

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean checkAndAskPermissions(int option) {
        //ask for permissions @author == Suresh Kumar R
        int READ_EXTERNAL_STORAGE_CODE = 1;
        int SEND_SMS_CODE = 1;
        int READ_CONTACTS_CODE = 1;
        int CALL_PHONE_CODE = 1;
        switch (option) {
            case 0:     //for SEND_SMS
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                        Toast.makeText(MainActivity.this, "permission is needed to SEND_SMS", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_CODE);
                }
                break;
            case 1:     //for CALL_PHONE
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(MainActivity.this, "permission is needed to CALL_PHONE", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_CODE);
                }
                break;
            case 2:     //for READ_CONTACTS
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        Toast.makeText(MainActivity.this, "permission is needed to READ_CONTACTS", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_CODE);
                }
                break;
            case 3:     //for READING EXTERNAL STORAGE
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(MainActivity.this, "permission is needed to READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
                }
                break;
        }
        return false;
    }
}
