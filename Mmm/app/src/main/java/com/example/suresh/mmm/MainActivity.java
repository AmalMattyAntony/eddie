package com.example.suresh.mmm;

import android.content.Intent;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int hour = 22;     //hoursssss  //takes as 24hr format
        final int minutes = 23;   //minutesssss
        final String message = "Endhiri_daw";

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);

                intent.putExtra(AlarmClock.EXTRA_HOUR,hour);
                intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
                intent.putExtra(AlarmClock.EXTRA_MESSAGE,message);
                intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);    //for going to clock
                startActivity(intent);

            }
        });
       Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);

                intent.putExtra(AlarmClock.ACTION_DISMISS_ALARM,AlarmClock.ALARM_SEARCH_MODE_NEXT);
                intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);
                intent.putExtra(AlarmClock.EXTRA_MESSAGE,message);

             //   AlarmClock.ACTION_DISMISS_ALARM,"Endhiri_da");
                    //for going to clock
                startActivity(intent);
                intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            }

        });
    }
}
