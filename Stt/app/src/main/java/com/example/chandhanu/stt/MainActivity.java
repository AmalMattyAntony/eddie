package com.example.chandhanu.stt;
//import com.example.chandhanu.stt.R;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
//import com.example.chandhanu.stt.Alarm;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    TextToSpeech t1;
    RequestQueue queue;
    TextView tv;
    String android_id;
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder mBuilder;
    String CHANNEL_ID="eddie_n1";
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        queue = Volley.newRequestQueue(getApplicationContext());
        createNotificationChannel();
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
        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_record)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line." +
                                "hrloosrgslgnsglnsgljnrsg" +
                                "glnsrjnd;jnjdjn.."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        ImageButton button;
        button = this.findViewById(R.id.button);
        //button.setText("All in One");
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
                /*Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("hello","world");
                startActivity(intent);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss");
                String datetime = dateformat.format(c.getTime());
                //System.out.println(datetime);
                textView.setText(datetime);
                startActivity(intent);
                String url = "http://www.google.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                */
                notificationManager.notify(++i, mBuilder.build());

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
    //speak(input);
    if(Pattern.matches("(?i).*alarm.*",input))
    {
        String pattern = "(?i)\\d+[.|:]?\\d*.*[A.?M.?|P.?M.?]?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        if (m.find()) {
             String time=m.group(0);
             //String hr,min=time.split(":");
            int i,hr=0,min=0;
            if(Pattern.matches("(?i).*[A.?M.?|P.?M.?].*",input))
                hr+=12;
            time=time.replaceAll("[^\\d|^:]","");
             speak("Setting alarm for "+time);
             String[] t=time.split(":");
             hr+=Integer.parseInt(t[0]);
             min+=Integer.parseInt(t[1]);
             textView.setText(hr+" "+min);
             //Alarm al=new Alarm();
             alarm_call(hr,min,"new alarm");

            //System.out.println("Found value: " + m.group(1) );
            //System.out.println("Found value: " + m.group(2) );
        }else {
            textView.setText("I am sorry, please say that again");
            speak("");
        }
        return true;
    }
    else if(Pattern.matches("(?i).*remind(er)?.*",input))
    {

        String pattern = "(?i)\\d+[.|:]?\\d*.*[A.?M.?|P.?M.?]?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        if (m.find()) {
            String time=m.group(0);
            //String hr,min=time.split(":");
            int i,hr=0,min=0;
            if(Pattern.matches("(?i).*[A.?M.?|P.?M.?].*",input))
                hr+=12;
            time=time.replaceAll("[^\\d|^:]","");
            speak("Setting alarm for "+time);
            String[] t=time.split(":");
            hr+=Integer.parseInt(t[0]);
            min+=Integer.parseInt(t[1]);
            textView.setText(hr+" "+min);
            //Alarm al=new Alarm();
            //alarm_call(hr,min,msg);

            //System.out.println("Found value: " + m.group(1) );
            //System.out.println("Found value: " + m.group(2) );
        }else {
            textView.setText("I am sorry, please say that again2");
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
    public void alarm_call(int hour, int minutes , String message)
    {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        //speak("trying to set alarm");
        intent.putExtra(AlarmClock.EXTRA_HOUR,hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE,message);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);    //for going to clock
        startActivity(intent);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
