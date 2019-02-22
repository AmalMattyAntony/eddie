package com.example.packages;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

public class AsyncAppList extends Activity{


    private String url="http://www.google.co.in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //new AsyncCaller().execute();

    }


}


