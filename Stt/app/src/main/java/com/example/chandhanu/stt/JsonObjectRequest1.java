package com.example.chandhanu.stt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class JsonObjectRequest1 extends JsonRequest<JSONObject> {
    //public Context c;
    //public SecondActivity a;
    String fileName;
    public byte[] body;
    Context context;
    /**
     * Creates a new request.
     *
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null indicates no
     *     parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonObjectRequest1(
            Context c,String fn,int method,
            String url,
            @Nullable JSONObject jsonRequest,
            Response.Listener<JSONObject> listener,
            @Nullable Response.ErrorListener errorListener) {
        super(
                method,
                url,
                (jsonRequest == null) ? null : jsonRequest.toString(),
                listener,
                errorListener);
        fileName=fn;
        context=c;
        setShouldCache(false);
    }
    public JsonObjectRequest1(
            int method,
            String url,
            @Nullable JSONObject jsonRequest,
            Response.Listener<JSONObject> listener,
            @Nullable Response.ErrorListener errorListener) {
        super(
                method,
                url,
                (jsonRequest == null) ? null : jsonRequest.toString(),
                listener,
                errorListener);
        fileName="temp";
        setShouldCache(false);
        //context=MainActivity.class.get;
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
           // t.setText(response.);
            Log.i("eddie","response in volley jsonobjectreq1");
            body=response.data;
            if(fileName!="temp"){writeFile(fileName);}
            String jsonString =
                    new String(HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Log.i("edidie",jsonString);
            jsonString="{file:"+fileName+"}";
            Log.i("edidie",jsonString);
            return Response.success(
                    new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception je) {
            Log.i("eddie","error in response");
            return Response.error(new ParseError(je));
        }
    }
    void writeFile(String fileName)
    {
       // Log.i("eddie")
        try {
            if (body != null) {
                File dataFile = new File(context.getString(R.string.voice_path)+fileName+".mp3");
                dataFile.createNewFile();
                dataFile.setWritable(true);
                OutputStream o = new FileOutputStream(dataFile);
                o.write(body);
                o.flush();
                o.close();
            }
            else
            {
                Toast.makeText(context,"couldnt find stuff in the body",Toast.LENGTH_SHORT);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
