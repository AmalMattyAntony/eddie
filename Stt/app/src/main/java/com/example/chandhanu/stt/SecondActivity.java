package com.example.chandhanu.stt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        TextView tv4=findViewById(R.id.tv4);
        tv4.setText(getIntent().getStringExtra("hello"));
    }
}
