package com.example.chandhanu.stt;
//import com.example.chandhanu.stt.R;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    TextToSpeech t1;
    RequestQueue queue;
    TextView tv;
    String android_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        queue = Volley.newRequestQueue(getApplicationContext());
        textView = this.findViewById(R.id.textView);
        tv=(TextView)findViewById(R.id.textView2);
        Button bu=(Button)findViewById(R.id.button2);
        bu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                sendMessage("");
            }

        });
        Button b1= findViewById(R.id.b1);

        Button button;
        button = this.findViewById(R.id.button);
        button.setText("All in One");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                try{
                    startActivityForResult(intent,200);
                }catch (ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    //experiemental
        Button b4= (Button)findViewById(R.id.b4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("hello","world");
                startActivity(intent);
            }
        });
        //end of experimental

        //one time setup of voice
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String toSpeak = textView.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                */
                speak("");
            }
        });

    }
    public void speak(String query)
    {
        if(query=="") {
            query = textView.getText().toString();
        }
        else
        {
            textView.setText(query);
        }
            Toast.makeText(getApplicationContext(), query,Toast.LENGTH_SHORT).show();
            t1.speak(query, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String request=result.get(0);
                textView.setText(request);
                sendMessage(request);
            }
        }
    }

public boolean offlineResponse(String input)
{
    //String words[]=input.split(" ");
    //check for alarm
    if(Pattern.matches("(?i).*alarm.*",input))
    {
        String pattern = "(?i)\\d+[.|:]\\d+.*[A.?M.?|P.?M.?]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        if (m.find( )) {
             String time=m.group(0);
             speak("Setting alarm for "+time);
            //System.out.println("Found value: " + m.group(1) );
            //System.out.println("Found value: " + m.group(2) );
        }else {
            textView.setText("I am sorry, please say that again");
            speak("");
        }
        return true;
    }
    return false;
}
//nw
public void sendMessage(String request) {
        if(offlineResponse(request))
        {
            return;
        }
        String s=textView.getText().toString();
    String url = "http://142.93.211.225:80/";
    final JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("request", s);
        jsonObject.put("userId", android_id);
        jsonObject.put("token", "blah");
    } catch (JSONException e) {
        // handle exception
    }
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        tv.setText("Response: " + response.getString("reply"));
                        textView.setText(response.getString("reply"));
                        String toSpeak = textView.getText().toString();
                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    catch (Exception e)
                    {
                        ;
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    tv.setText(error.toString());

                }
            });
    queue.add(jsonObjectRequest);
    Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
}
}
