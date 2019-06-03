package com.github.maaft;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class JSONTask extends AsyncTask<String, Void, JSONObject>
{
    private WeakReference<Context> context;
    private String id;
    public static final String JSONReady = "JSONReady";

    public JSONTask(Context context, String id)
    {
        this.context = new WeakReference<>(context);
        this.id = id;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try
        {
            URL url = new URL(params[0]);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            return new JSONObject(stringBuffer.toString());
        }
        catch(Exception ex)
        {
            Log.e("App", "JSONTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject response)
    {
        Intent intent = new Intent(context.get(), C10Widget.class);
        intent.setAction(JSONReady);
        intent.putExtra("json", response.toString());
        intent.putExtra("id", id);
        context.get().sendBroadcast(intent);
    }
}
