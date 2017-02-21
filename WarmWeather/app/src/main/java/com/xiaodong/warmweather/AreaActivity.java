package com.xiaodong.warmweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName=sharedPreferences.getString(ChooseAreaFragment.SELECTED_CITYNAME,null);
        if(cityName!=null){
            Intent intent = new Intent();
            intent.setAction("com.warmweather.act");
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_area);
    }
}
