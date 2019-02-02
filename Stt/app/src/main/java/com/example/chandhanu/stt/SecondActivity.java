package com.example.chandhanu.stt;
import android.media.MediaPlayer;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import com.example.chandhanu.stt.JsonObjectRequest1;
import static java.net.Proxy.Type.HTTP;
import static com.example.chandhanu.stt.R.raw.a;
public class SecondActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    TextView tv4,tv5;
    JsonObjectRequest1 jsonObjectRequest;
    String token;
    String speak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mediaPlayer = new MediaPlayer();//MediaPlayer.create(this,Uri.parse(getString(R.string.voice_path)+"temp.mp3"));   //change perfect to ur song.mp3
        tv4=findViewById(R.id.tv4);
        tv5=findViewById(R.id.tv5);
        tv5.setText(getIntent().getStringExtra("speak"));
        speak=tv5.getText().toString();
        Button b=(Button)findViewById(R.id.button3);
        b.setText("Authorize");
        check();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://myvoice.lyrebird.ai/authorize?response_type=token&client_id=1FnNrPWBuoHsz3aGgxOf9Tbbl5Z&redirect_uri=http%3A%2F%2F142.93.211.225%2Fauth%2Flyrebird&scope=voice&state=987654321";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            tv4.setText(uri.split("=")[1].replace("&token_type",""));
            Log.i("token",tv4.getText().toString());
            token=tv4.getText().toString();
            setToken(token);
            tv4.setText("token is:"+getToken());
            // tv4.setText(uri+":"+data.getQueryParameter("access_token"));

        }
        Button b2=findViewById(R.id.button4);
        b2.setText("CURL replacement");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    token=getToken();
                    if(token=="notFound")
                    {
                        Toast.makeText(v.getContext(),"Token Not SET",Toast.LENGTH_LONG).show();
                    }
                    else {
                        http1(speak);

                        tv4.setText(getApplicationContext().getFilesDir().getPath().toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    void check()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            tv4.setText("WHY ME -__- ");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        // tv.setText("yaaaaay");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            tv4.setText("WHY ME -__- ");
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        else
        {
            tv4.setText("finally");
        }
    }
    void writeFile()
    {
        File rootPath = new File(Environment.getExternalStorageDirectory(), "directory_name");
        tv4.setText(Environment.getExternalStorageDirectory().toString());
        File p=new File(getString(R.string.voice_path));
        try {
            if(!p.exists())
                p.mkdir();
            File dataFile = new File(getString(R.string.voice_path)+"temp.mp3");
            dataFile.createNewFile();
            dataFile.setWritable(true);
            OutputStream o=new FileOutputStream(dataFile);
            o.write(jsonObjectRequest.body);
            o.flush();
            o.close();

        } catch (IOException e) {
            e.printStackTrace();
            tv4.setText(e.toString());
        }
        //mediaPlayer = MediaPlayer.create(this,Uri.parse(getString(R.string.voice_path)+"temp.mp3"));   //change perfect to ur song.mp3
        try {
            mediaPlayer.setDataSource(this,Uri.parse(getString(R.string.voice_path)+"temp.mp3"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
        //mediaPlayer.start();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer player) {
                player.start();
            }

        });
        //mediaPlayer.start();
        // AsyncTaskRunner t=new AsyncTaskRunner();
        //t.execute("5");
    }
    void setToken(String token)
    {
        SharedPreferences sharedPref = getSharedPreferences("eddie-overall",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.token),token);
        editor.commit();
    }
    String getToken()
    {
        SharedPreferences sharedPref = getSharedPreferences("eddie-overall",Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.token),"notFound");
    }
    void http1(String speak) throws JSONException {
        String url="https://avatar.lyrebird.ai/api/v0/generate";
        final JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", speak);
        jsonObjectRequest = new JsonObjectRequest1
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        tv4.setText("Response: ");// + response.toString());
                       // tv4.setText(jsonObjectRequest.responseBody.toString());
                        writeFile();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        tv4.setText(error.toString());
                       writeFile();

                    }}){

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Content-Type", "application/json");
            params.put("Authorization", "Bearer "+token);
            return params;
        }

        };
        //jsonObjectRequest.c=getApplicationContext();
        //jsonObjectRequest.a=this;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);


    }
}
