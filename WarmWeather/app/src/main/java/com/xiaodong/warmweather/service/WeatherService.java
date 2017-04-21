package com.xiaodong.warmweather.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaodong.warmweather.ChooseAreaFragment;
import com.xiaodong.warmweather.WeatherActivity;
import com.xiaodong.warmweather.util.HttpUtil;
import com.xiaodong.warmweather.util.LogUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yxd on 2017/2/21.
 */
public class WeatherService extends Service {

    private SharedPreferences sharedPreferences;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("startCommand=========", "startCommand");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherService.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryWeatherFromServer();
            }
        }).start();

        Intent mIntent = new Intent(this,WeatherService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,mIntent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime()+3*60*60*1000;
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
//                checkAndChangeIcon();
            }
        });
    }

    public void checkAndChangeIcon() {
        String save_status = sharedPreferences.getString("last_status", null);
        PackageManager pm = getPackageManager();
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int amp = calendar.get(Calendar.HOUR_OF_DAY);
        LogUtil.d("time==========" + amp);
        if (save_status != null) {
            //
            if ( amp >= 20 || amp < 6 &&save_status.equals("day")) {
                //20-6点为夜晚
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity0"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity1"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                sharedPreferences.edit().putString("last_status","night").commit();
                restartDeskTop(pm);
            } else if (amp>=6&&amp<20&&save_status.equals("night")){
                //
                sharedPreferences.edit().putString("last_status","day").commit();
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity0"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity1"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                restartDeskTop(pm);
            }
        }else {
            if (amp >= 20 || amp < 6) {
                //20-6点为夜晚
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity0"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity1"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                sharedPreferences.edit().putString("last_status","night").commit();
                restartDeskTop(pm);
            }
        }
    }
//重启手机桌面进程（否则再次进入程序会报错）
    private void restartDeskTop(PackageManager pm){
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> resolves = pm.queryIntentActivities(i, 0);
        for (ResolveInfo res : resolves) {
            if (res.activityInfo != null) {
                LogUtil.d("activityInfo.packageName"+res.activityInfo.packageName);
                am.killBackgroundProcesses(res.activityInfo.packageName);
            }
        }
    }
}
