package com.example.callable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= findViewById(R.id.textView);
        Callable<Integer> my;
        nice(my);
    }
    void nice(Callable<Integer> f)
    {
        try {
            f.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Integer my()
    {
        tv.setText("hello from my");
        return new Integer("0");
    }
}
abstract class  MyInterface {
    abstract String doSomething(int param1, String param2);
    String doSomething1(int param1, String param2)
    {

    }
}
class MyClass {
    public MyInterface myInterface = new MyInterface() {
        @Override
        public String doSomething(int p1, String p2) {
            return p2 + p1;
        }
    };
}