package com.example.eddie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Lyrebird extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrebird);
        Button auth=findViewById(R.id.lyrebird_auth);
        checkForToken();
        auth.setText("Authorize");
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://myvoice.lyrebird.ai/authorize?response_type=token&client_id=1FnNrPWBuoHsz3aGgxOf9Tbbl5Z&redirect_uri=http%3A%2F%2F142.93.211.225%2Fauth%2Flyrebird&scope=voice&state=987654321";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }
    void checkForToken()
    {
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            uri=uri.split("=")[1].replace("&token_type","");
            setGlobalConfig("token_id",uri);
            toaster("Token ID set!");
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
    void setGlobalConfig(String key,String val)
    {
        SharedPreferences sharedPref = getSharedPreferences("eddie-overall", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key,val);
        editor.commit();
    }
    void toaster(String text)
    {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
}
