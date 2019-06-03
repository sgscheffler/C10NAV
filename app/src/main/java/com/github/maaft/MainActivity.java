package com.github.maaft;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

public class MainActivity extends AppCompatActivity
{

    EditText editc10;
    EditText editInterval;
    Button btnSave;
    SharedPreferences prefs;
    PendingIntent service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editc10 = (EditText) findViewById(R.id.editTextc10);
        editInterval = (EditText) findViewById(R.id.edInterval);
        btnSave = (Button) findViewById(R.id.btnSave);

        prefs = getApplicationContext().getSharedPreferences("C10Prefs", 0);

        float holdings = prefs.getFloat("c10_holdings", 0.0f);
        long interval = prefs.getLong("update_interval", 60);

        editc10.setText("" + holdings);
        editInterval.setText("" + interval);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();

                float holdings = Float.parseFloat(editc10.getText().toString());
                editor.putFloat("c10_holdings", holdings);
                long update_interval = Long.parseLong(editInterval.getText().toString());
                editor.putLong("update_interval", update_interval);
                editor.apply();

                new JSONTask(getApplicationContext(), C10Widget.ID_NAV).execute("https://api.invictuscapital.com/v2/funds/crypto10/nav");
                new JSONTask(getApplicationContext(), C10Widget.ID_MOVEMENTS_24H).execute("https://api.invictuscapital.com/v2/funds/crypto10/movement?range=24h");
                new JSONTask(getApplicationContext(), C10Widget.ID_MOVEMENTS_7D).execute("https://api.invictuscapital.com/v2/funds/crypto10/movement?range=7d");

            }
        });
    }
}

