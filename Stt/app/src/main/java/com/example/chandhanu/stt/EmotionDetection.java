package com.example.chandhanu.stt;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

import java.util.List;


public class EmotionDetection extends AsyncTask<String, Void, String> {
    Context context;
    String text;
    EmotionDetection(Context c,String toBeAnalyzed) {
        context=c;
        text=toBeAnalyzed;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... params) {

        String apiKey="MUL5TK3l9swIYGIO-1ookNMRxVuUOsxTTjf1kHmyatTt";
        String endPoint="https://gateway-lon.watsonplatform.net/tone-analyzer/api";
        String versionDate="2017-09-21";
        IamOptions options = new IamOptions.Builder().apiKey(apiKey).build();
        ToneAnalyzer toneAnalyzer = new ToneAnalyzer(versionDate,options);
        toneAnalyzer.setEndPoint(endPoint);
        ToneOptions toneOptions = new ToneOptions.Builder()
                .text(text)
                .build();
        ToneAnalysis tone=toneAnalyzer.tone(toneOptions).execute();
        List<ToneCategory> toneCategories=tone.getDocumentTone().getToneCategories();
        List<ToneScore>toneScores=tone.getDocumentTone().getTones();
        //Log.i("toneCat",toneCategories.toString());
        Log.i("analyzed",text);
        Log.i("toneScore",toneScores.toString());
        Log.i("tone",tone.toString());
        return tone.toString();

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
    }

}

