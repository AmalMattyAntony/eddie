package com.example.chandhanu.stt;
//import com.example.chandhanu.stt.R;
import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.AuthFailureError;
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
    String phno;
    String whoCalledListen="";
    JsonObjectRequest1 jsonObjectRequest;
    String token;
    MediaPlayer mediaPlayer;
    TextToSpeech t1;
    RequestQueue queue;
    TextView tv;
    int voice_select=1;
    String android_id;
    NotificationManager notificationManager;
    NotificationCompat.Builder mBuilder;
    String lastHeard;
    String CHANNEL_ID="eddie_n1";
    int i=0;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb=new DatabaseHelper(this);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        queue = Volley.newRequestQueue(getApplicationContext());
        createNotificationChannel();
        Button contacts=(Button)findViewById(R.id.contacts);
        contacts.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"Putting contacts into db",Toast.LENGTH_LONG).show();
                getContactList();
            }
        });
        //Notification n=new Notification();
        //n.setNotification(this,Calendar.getInstance().getTimeInMillis()+3000);
        textView = this.findViewById(R.id.textView);
        tv=(TextView)findViewById(R.id.textView2);
        mediaPlayer=new MediaPlayer();
        token=getToken();
        if(token=="notFound")Toast.makeText(this,"token not set",Toast.LENGTH_LONG).show();
        Button bu=(Button)findViewById(R.id.button2);
        bu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                textView.setText(getToken());
                voice_select=1-voice_select;
                //sendMessage("");
            }

        });
        Button b1= findViewById(R.id.b1);
        ImageButton button;
        button = this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //speak("hello world");
                //speak("hello world");
                listen();
            }
        });
    //experiemental
        Button b4= (Button)findViewById(R.id.b4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("speak",textView.getText().toString());
                startActivity(intent);
                /*Calendar c = Calendar.getInstance();
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
               // notificationManager.notify(++i, mBuilder.build());

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
        b1.setText("hello world");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String toSpeak = textView.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                textView.setText(Calendar.getInstance().getTimeInMillis()+"");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.round_active)
                        .setContentTitle("My notification")
                        .setContentText("Much longer text that cannot fit one line...")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Much longer text that cannot fit one line..."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(8,mBuilder.build());*/
                //speak("");

                //getContactList();
                //textView.setText("caling db");
                //textView.setText(myDb.getData("Amal"));
                textView.setText("lh"+lastHeard);
                //Notification n=new Notification();
                //n.setNotification(getApplicationContext(),Calendar.getInstance().getTimeInMillis()+300);
            }
        });

    }
    public void speak(String query)
    {
        //temp quick fix to convert all speak to speakLyreBird
        int voice_select=Integer.parseInt(getSharedPreferences("eddie-overall",Context.MODE_PRIVATE).getString(getString(R.string.voice_toggle),"0"));
        if(query=="") {
            query = textView.getText().toString();
        }
        else
        {
            textView.setText(query);
        }

        Toast.makeText(getApplicationContext(), query,Toast.LENGTH_SHORT).show();
        if(voice_select==1)
            speakLyreBird(query);
        else
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
                /*switch(whoCalledListen)
                {
                     case("sms"): sendSMS(lastHeard,request);
                     default: sendMessage(request);;
                }*/
                if(whoCalledListen=="sms") sendSMS(phno,request);
                else sendMessage(request);
                lastHeard=request;
            }
        }
    }

public boolean offlineResponse(String input)
{
    if(Pattern.matches("(?i).*alarm.*",input))
    {
        return setAlarm(input);
    }
    else if(Pattern.matches("(?i).*remind(er)?.*",input))
    {
        return setReminder(input);
    }
    else if(Pattern.matches("(?i).*call.*",input))
    {
        return call(input);
    }
    else if(Pattern.matches("(?i).*(sms|text.*message).*",input))
    {
        return sms(input);
    }
    else if(Pattern.matches("(?i).*(google|search).*",input))
    {
        return googleSearch(input);
    }
    else if(Pattern.matches("(?i).*open.*",input))
    {
        openApps(input);
        return true;
        //return googleSearch(input);
    }
    return false;
}
void openApps(String input)
{
    input=input.replaceAll("(?i)open ","");
    //input=input.replaceAll(" ","");
    toaster(input);
    new AsyncCaller().execute(input);
}
boolean googleSearch(String input)
{
    //enna pa
    String search_query=input.replaceAll("(?i)(google|search)","");
    String url = "http://google.com/search?q="+search_query;
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    CustomTabsIntent customTabsIntent = builder.build();
    builder.addDefaultShareMenuItem();          //making share button iin menu list da
    builder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlack));	//for color da
    builder.setShowTitle(true);
    customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
    return true;
}
void listen()
{
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
    try{
        startActivityForResult(intent,200);
    }catch (ActivityNotFoundException a){
        Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
    }
}
boolean call(String input)
{
    //textView.setText("in calling");
   // speak("let me try");
    String input1=input.replaceAll("call ","");
    input=input.replaceAll("\\D","");
    //textView.setText(input);
    Intent dialIntent = new Intent();
    dialIntent.setAction(Intent.ACTION_CALL);
    //speak("this "+input1);
    String no2=myDb.getData(input1);//.replaceAll(" ",""));
    String pattern = "(?i)(\\d{12})|(\\d{10})";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(input);
    Matcher m1 = r.matcher(no2);
    //speak(no2);
    Log.i("number kedaichadu ithu","what is the problem");
    if (m.find()) {
        String no="";
        for(int i=0;i<input.length();++i)
            no+=input.charAt(i)+" ";
        speak("Calling "+input1);
        dialIntent.setData(Uri.parse("tel:"+input));
        startActivity(dialIntent);
    }
    else if (m1.find()) {
        String no="";
        for(int i=0;i<no2.length();++i)
            no+=no2.charAt(i)+" ";
        speak("Calling "+input1);
        if(no2.length()==12)
        {
            no2="+"+no2;
        }
        dialIntent.setData(Uri.parse("tel:"+no2));
        startActivity(dialIntent);
    }
    else {
        textView.setText("I am sorry, please say that again, Failed to call");
        speak("");
    }
    textView.setText("no2"+no2+",input1:"+input1);
    return true;
}
boolean setAlarm(String input)
{
    String pattern = "(?i)\\d+[.|:]?\\d*.*[A.?M.?|P.?M.?]?";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(input);
    if (m.find()) {
        String time=m.group(0);
        int i,hr=0,min=0;
        String ap="A M";
        if(Pattern.matches("(?i).*P.?M.?.*",input))
        {hr+=12;ap="P M";}
        time=time.replaceAll("[^\\d|^:]","");
        speak("Setting alarm for "+time+ap);
        String[] t=time.split(":");
        hr+=Integer.parseInt(t[0]);
        min+=Integer.parseInt(t[1]);
        textView.setText(hr+" "+min);
        alarm_call(hr,min,"new alarm");
    }else {
        textView.setText("I am sorry, please say that again");
        speak("");
    }
    return true;
}
boolean sms(String input)
{
    String input0[]=input.split(" ");
    String input1=input0[input0.length-1].replaceAll(" ","");
   // speak(input1);

    input=input.replaceAll("\\D","");
    //tv.setText("!"+input+"!");
    if(input.matches("\\w*"))
    {
        input=myDb.getData(input1);
        input=input.replaceAll("\\D","");
        tv.setText("!"+input+"!");
        //speak(input);
        //if(input)
    }
    textView.setText(input);
    String pattern = "(?i)(\\d{12})|(\\d{10})";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(input);
    if (m.find()) {
        if(input.length()==12)
        {
            input=input.substring(2,12);
            Toast.makeText(getApplicationContext(),input,Toast.LENGTH_LONG).show();
            tv.setText(input);
        }
        lastHeard=input;
        phno=input;
        whoCalledListen="sms";
        speak("what do you want the body to say?");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        }, 2000);
        //
    }else {
        textView.setText("I am sorry, please say that again, could not send message");
        speak("");
    }
    textView.setText(input);
    //textView.setText(myDb.getData(input1));
    return true;
}

public boolean sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent"+phoneNo+","+msg,
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        whoCalledListen="";
        return true;
    }
boolean setReminder(String input)
{
    //still in works
    String pattern = "(?i)\\d+[.|:]?\\d*.*[A.?M.?|P.?M.?]?";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(input);
    if (m.find()) {
        String time=m.group(0);

        int i,hr=0,min=0;
        if(Pattern.matches("(?i).*[A.?M.?|P.?M.?].*",input))
            hr+=12;
        time=time.replaceAll("[^\\d|^:]","");
        speak("Setting alarm for "+time);
        String[] t=time.split(":");
        hr+=Integer.parseInt(t[0]);
        min+=Integer.parseInt(t[1]);
        textView.setText(hr+" "+min);
    }else {
        textView.setText("I am sorry, please say that again2");
        speak("");
    }
    return true;
}
//nw
public void sendMessage(String request) {
        if(offlineResponse(request))
        {
            return;
        }
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
                        tv.setText("Response: " + response.getString("reply"));
                        textView.setText(response.getString("reply"));
                        String toSpeak = textView.getText().toString();
                        speak(toSpeak);
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
    void createNotificationChannel() {
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
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    void speakLyreBird(String speak)
    {
        try {
            http1(speak);
            //flow
            // speakLyreBird -> http1 -> volley with modified request -> onresult in modified request class its own body arh(byte[]) is set -> calls mediaplayer.start()
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
    }
    //lyrebird dependencies
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
    void http1(final String speak) throws JSONException {
        File temp=new File(getString(R.string.voice_path)+speak+".mp3");
        if(!temp.exists()) {
            //temp.exists(t)
            String url = "https://avatar.lyrebird.ai/api/v0/generate";
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", speak);
            jsonObjectRequest = new JsonObjectRequest1
                    (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            //textView.setText("Response: ");// + response.toString());
                            // tv4.setText(jsonObjectRequest.responseBody.toString());
                            writeFile(speak);
                        }
                    }, new Response.ErrorListener() {

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
                    if (token == "notFound") {
                        textView.setText("token is still not set");
                    }
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

    void writeFile(String fileName)
    {
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
            textView.setText(e.toString());
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
                mediaPlayer.setDataSource(this,Uri.parse(getString(R.string.voice_path)+fileName+".mp3"));
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
    //end of lyrebird dependencies
    private void getContactList() {
        //ask for permissions @author == Suresh Kumar R
        //myDb.onUpgrade(myDb,1,2);
        String TAG="getting contacts";
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Boolean ifInserted =  myDb.insertdata(name,phoneNo);
                        if(ifInserted = true)
                            Toast.makeText(MainActivity.this,"Data Inserted" , Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this,"Data not Inserted" , Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
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
    void toaster(String text)
    {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
    private class AsyncCaller extends AsyncTask<String, Void, String[]>
    {
        ProgressDialog pdLoading = new ProgressDialog(getApplicationContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            //pdLoading.setMessage("\tLoading...");
            //pdLoading.show();
        }
        @Override
        protected String[] doInBackground(String... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            //for(int i=0;i<10000;++i)for(int j=0;j<10000;++j);

            return findPackageName(params[0]);
            //return "paniten pa";//Void.TYPE;
        }

        @Override
        protected void onPostExecute(String... result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            //toaster("uhm this?");
            toaster(result[0]+":"+result[1]);
            if(result[0].equals("notFound"))
            {
                speak("Not installed?");
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/search?q="+result[1]+"&c=apps")));
            }
            else
            {
                Log.i("cause of crash",result[0]);
                speak("Opening "+result[1]);
                startActivity(getPackageManager().getLaunchIntentForPackage(result[0]));
            }
            // pdLoading.dismiss();
        }
        boolean matcher(String input,String target)
        {
            input=input.toLowerCase();
            target=target.toLowerCase();
            target=target.replaceAll(" ","");
            //if()
            input=input.replaceAll(" ","");
            //if(Pattern.matches("(?i).*alarm.*",input))
            if(target.matches("(?i).*("+input+").*"))
                return true;
            else
                return false;
        }
        String[] findPackageName(String name) {
            String a[]=new String[2];
            a[0]="notFound";
            a[1]=name;
            PackageManager packageManager = getPackageManager();
            List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for(int i=0;i<appList.size();++i){
                try {
                    String curr=packageManager.getApplicationLabel(packageManager.getApplicationInfo(appList.get(i).packageName.toString(),0)).toString();
                    curr=curr.toLowerCase();
                    if(matcher(name,curr))
                    {
                        a[0]=appList.get(i).packageName.toString();
                        a[1]=curr;
                        return a;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return a;

        }
    }
}

