package com.example.chandhanu.stt;

//<uses-permission android:name="com.android.alarm.permission.SET_ALARM"/>    //in androidmanidfesf.xml

import android.content.Intent;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
//import static android.support.v4.content.ContextCompat.startActivity;

public class Alarm extends AppCompatActivity{
    public void alarm_call(int hour, int minutes , String message)
    {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);

        intent.putExtra(AlarmClock.EXTRA_HOUR,hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE,message);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);    //for going to clock
        startActivity(intent);
    }
    public void alarm_abort(int hour, int minutes, String message)
    {
        Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);

        intent.putExtra(AlarmClock.ACTION_DISMISS_ALARM,AlarmClock.ALARM_SEARCH_MODE_NEXT);
        intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE,message);

        //for going to clock
        startActivity(intent);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
    }
}

