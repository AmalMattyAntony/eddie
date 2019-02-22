package com.example.packages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b=findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AsyncAppList a=new AsyncAppList();
                //a.onCreate();
                new AsyncCaller().execute("Eddie");
                toaster("damn son");
                //new AsyncAppList().onResume();
                //openApps("");
            }
        });
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

            return openApps(params[0]);
            //return "paniten pa";//Void.TYPE;
        }

        @Override
        protected void onPostExecute(String... result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            //toaster("uhm this?");
            toaster(result[0]+":"+result[1]);
            if(result.equals("notFound"))
            {
                toaster("Not installed?");
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/search?q="+result[1]+"&c=apps")));
            }
            else
            {
                startActivity(getPackageManager().getLaunchIntentForPackage(result[0]));
            }
           // pdLoading.dismiss();
        }
        String[] openApps(String name) {
            String a[]=new String[2];
            a[0]="notFound";
            a[1]=name;
            PackageManager packageManager = getPackageManager();
            List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            //for (Iterator<ApplicationInfo> i = appList.iterator(); appList.iterator().hasNext();) {
              for(int i=0;i<appList.size();++i){
                try {
                    //Toast.makeText(MainActivity.this, "All  :: " + packageManager.getApplicationLabel(packageManager.getApplicationInfo(i.next().packageName, 0)), Toast.LENGTH_LONG).show();
                    String curr=packageManager.getApplicationLabel(packageManager.getApplicationInfo(appList.get(i).packageName.toString(),0)).toString();
                    if(curr.equals(name))
                    {
                        //toaster("found it"+i.next().packageName.toString());
                        a[0]=appList.get(i).packageName.toString();
                        return a;
                    }
                    //else
                      //  return "nope:"+curr+","+name;
                    //if(ii++==0)toaster(i.next().packageName.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //return name;

            return a;

        }
    }
}


