package com.github.maaft;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class JSONTask extends AsyncTask<String, Void, JSONObject>
{
    private float holdings;
    private RemoteViews views;
    private ComponentName WidgetID;
    private AppWidgetManager appWidgetManager;

    private DecimalFormat df2 = new DecimalFormat("#.##");

    public JSONTask(float holdings, RemoteViews views, ComponentName appWidgetID, AppWidgetManager appWidgetManager)
    {
        this.holdings = holdings;
        this.views = views;
        this.WidgetID = appWidgetID;
        this.appWidgetManager = appWidgetManager;
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
        try {
            float nav = Float.parseFloat(response.getString("nav_per_token"));
            views.setTextViewText(R.id.txtNAV, "$ " + df2.format(nav));
            views.setTextViewText(R.id.txtholdings, "$ " + df2.format(nav * holdings));
            appWidgetManager.updateAppWidget(WidgetID, views);


        } catch (JSONException ex) {
            Log.e("App", "couldn't get JSON data.", ex);
        }
    }
}
