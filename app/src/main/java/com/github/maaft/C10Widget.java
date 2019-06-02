package com.github.maaft;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class C10Widget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

    }
    private static final String MyOnClick = "myOnClickTag";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(Integer id : appWidgetIds)
        {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.c10_widget);

            Intent widgetIntent = new Intent(context, C10Widget.class);
            widgetIntent.setAction(MyOnClick);

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

        if (MyOnClick.equals(intent.getAction()))
        {
            SharedPreferences prefs = context.getSharedPreferences("C10Prefs", 0);
            float holdings = prefs.getFloat("c10_holdings", 0.0f);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.c10_widget);

            ComponentName thisWidget = new ComponentName(context, C10Widget.class);

            new JSONTask(holdings, remoteViews, thisWidget, appWidgetManager).execute("https://api.invictuscapital.com/v2/funds/crypto10/nav");
        }
        super.onReceive(context, intent);
    };

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        SharedPreferences prefs = context.getSharedPreferences("C10Prefs", 0);
        float holdings = prefs.getFloat("c10_holdings", 0.0f);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.c10_widget);

        ComponentName thisWidget = new ComponentName(context, C10Widget.class);

        new JSONTask(holdings, remoteViews, thisWidget, appWidgetManager).execute("https://api.invictuscapital.com/v2/funds/crypto10/nav");


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

