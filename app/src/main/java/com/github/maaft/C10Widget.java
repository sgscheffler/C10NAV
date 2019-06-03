package com.github.maaft;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Implementation of App Widget functionality.
 */
public class C10Widget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

    }

    private static final String WidgetClicked = "WidgetClicked";
    public static final String ID_NAV = "ID_NAV";
    public static final String ID_MOVEMENTS_24H = "ID_MOVEMENTS_24H";
    public static final String ID_MOVEMENTS_7D = "ID_MOVEMENTS_7D";
    private DecimalFormat df2 = new DecimalFormat("#.##");


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(Integer id : appWidgetIds)
        {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.c10_widget);

            Intent widgetIntent = new Intent(context, C10Widget.class);
            widgetIntent.setAction(WidgetClicked);

            PendingIntent widgetPendingIntent = PendingIntent.getBroadcast(context, 0, widgetIntent, 0);

            remoteViews.setOnClickPendingIntent(R.id.relativeLayout, widgetPendingIntent);
            appWidgetManager.updateAppWidget(id, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {

        if (WidgetClicked.equals(intent.getAction()))
        {
            new JSONTask(context, ID_NAV).execute("https://api.invictuscapital.com/v2/funds/crypto10/nav");
            new JSONTask(context, ID_MOVEMENTS_24H).execute("https://api.invictuscapital.com/v2/funds/crypto10/movement?range=24h");
            new JSONTask(context, ID_MOVEMENTS_7D).execute("https://api.invictuscapital.com/v2/funds/crypto10/movement?range=7d");
        }
        else if (JSONTask.JSONReady.equals(intent.getAction()))
        {
            if(intent.getStringExtra("id").equals(ID_NAV))
            {
                try {
                    SharedPreferences prefs = context.getSharedPreferences("C10Prefs", 0);
                    float holdings = prefs.getFloat("c10_holdings", 0.0f);
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("json"));
                    float nav = Float.parseFloat(jsonObject.getString("nav_per_token"));

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.c10_widget);

                    ComponentName thisWidget = new ComponentName(context, C10Widget.class);

                    remoteViews.setTextViewText(R.id.txtNAV, "$ " + df2.format(nav));
                    remoteViews.setTextViewText(R.id.txtholdings, "$ " + df2.format(nav * holdings));
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);


                } catch (JSONException ex) {
                    Log.e("App", "couldn't get JSON data.", ex);
                }
            }
            else if (intent.getStringExtra("id").equals(ID_MOVEMENTS_24H))
            {
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("json"));
                    float movement = Float.parseFloat(jsonObject.getString("percentage"));

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.c10_widget);

                    ComponentName thisWidget = new ComponentName(context, C10Widget.class);

                    remoteViews.setTextViewText(R.id.txt24h, df2.format(movement) + " %");
                    if (movement < 0)
                    {
                        remoteViews.setTextColor(R.id.txt24h, Color.parseColor("#FF0000"));
                    }
                    else
                    {
                        remoteViews.setTextColor(R.id.txt24h, Color.parseColor("#00FF00"));
                    }
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                } catch (JSONException ex) {
                    Log.e("App", "couldn't get JSON data.", ex);
                }
            }
            else if (intent.getStringExtra("id").equals(ID_MOVEMENTS_7D))
            {
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("json"));
                    float movement = Float.parseFloat(jsonObject.getString("percentage"));

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.c10_widget);

                    ComponentName thisWidget = new ComponentName(context, C10Widget.class);

                    remoteViews.setTextViewText(R.id.txt7d, df2.format(movement) + " %");
                    if (movement < 0)
                    {
                        remoteViews.setTextColor(R.id.txt7d, Color.parseColor("#FF0000"));
                    }
                    else
                    {
                        remoteViews.setTextColor(R.id.txt7d, Color.parseColor("#00FF00"));
                    }
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                } catch (JSONException ex) {
                    Log.e("App", "couldn't get JSON data.", ex);
                }
            }
        }
        super.onReceive(context, intent);
    };

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        new JSONTask(context, ID_NAV).execute("https://api.invictuscapital.com/v2/funds/crypto10/nav");
        new JSONTask(context, ID_MOVEMENTS_24H).execute("https://api.invictuscapital.com/v2/funds/crypto10/movement?range=24h");
        new JSONTask(context, ID_MOVEMENTS_7D).execute("https://api.invictuscapital.com/v2/funds/crypto10/movement?range=7d");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

