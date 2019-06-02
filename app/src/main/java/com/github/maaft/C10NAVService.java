package com.github.maaft;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Random;

public class C10NAVService extends Service {
    public C10NAVService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private PendingIntent service = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.c10_widget);

        ComponentName theWidget = new ComponentName(this, C10Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("C10Prefs", 0);

        float c10_holdings = prefs.getFloat("c10_holdings", 0.0f);

        new JSONTask(c10_holdings, views, theWidget, appWidgetManager).execute("https://api.invictuscapital.com/v2/funds/crypto10/nav");

        Log.e("asfasf", "asfasfasf");

        /*final AlarmManager m = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(getApplicationContext(), C10NAVService.class);

        if (service == null)
        {
            service = PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        long interval = prefs.getLong("update_interval", 60);

        m.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),interval*1000,service);*/

        return super.onStartCommand(intent, flags, startId);
    }
}
