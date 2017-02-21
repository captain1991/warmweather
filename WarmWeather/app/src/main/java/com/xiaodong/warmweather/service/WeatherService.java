package com.xiaodong.warmweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaodong.warmweather.ChooseAreaFragment;
import com.xiaodong.warmweather.WeatherActivity;
import com.xiaodong.warmweather.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yxd on 2017/2/21.
 */
public class WeatherService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("startCommand=========", "startCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryWeatherFromServer();
            }
        }).start();

        Intent mIntent = new Intent(this,WeatherService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,mIntent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime()+7*60*60*1000;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    public void queryWeatherFromServer(){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //从服务器重新获取数据时要用最新的weathercode和cityname
        String weatherCode = sharedPreferences.getString(ChooseAreaFragment.SELECTED_WEATHERCODE,null);
        String cityName = sharedPreferences.getString(ChooseAreaFragment.SELECTED_CITYNAME,null);
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/weather?cityid=" + weatherCode + "&key=4e5ec8e307ba48e2921c023b78e45435", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("failure=========", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("response=========",response.body().string());
                String respStr = response.body().string();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WeatherActivity.WEATHER_JSON_STRING, respStr);
                editor.commit();
                Log.d("response=========", respStr);
            }
        });
    }
}
