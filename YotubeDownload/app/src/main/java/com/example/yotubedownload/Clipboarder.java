package com.example.yotubedownload;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class Clipboarder extends Service {
    private ClipboardManager mClipboardManager;
    NotificationManager notificationManager;
    String url;
    NotificationCompat.Builder builder;
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mClipboardManager =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                new ClipboardManager.OnPrimaryClipChangedListener() {
                    @Override
                    public void onPrimaryClipChanged() {
                        ClipData clip = mClipboardManager.getPrimaryClip();
                        //;
                        url=mClipboardManager.getText().toString();
                        displayNotification(url);
                        Toast.makeText(getApplicationContext(),"You copied:"+mClipboardManager.getText(),Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "hello";//getString(R.string.channel_name);
            String description = "hello";//getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("trial", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    String getSetting()
    {
        SharedPreferences sharedPref = getSharedPreferences("eddie-overall", Context.MODE_PRIVATE);
        return sharedPref.getString("auto","0");
    }
    void displayNotification(String text)
    {
        //Intent i=new Intent(getApplicationContext(),MainActivity.class);
        //text=text.split("=")[1];
        String url="http://amalmattyantony.me:8080/download/?id="+text.substring(text.length()-11);
        //String url = "https://youtube7.download/mini.php?id="+text;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        int setting=Integer.parseInt(getSetting());
        if(setting==1)
            startActivity(i);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,0);
        builder = new NotificationCompat.Builder(this, "trial")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Youtube Downloader")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(text)
                .addAction(R.drawable.ic_launcher_background, "Download", pi);;
        notificationManager.notify(1, builder.build());
    }
}
