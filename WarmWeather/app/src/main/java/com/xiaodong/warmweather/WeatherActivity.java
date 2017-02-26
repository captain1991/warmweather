package com.xiaodong.warmweather;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xiaodong.warmweather.gson.Aqi;
import com.xiaodong.warmweather.gson.DailyForcast;
import com.xiaodong.warmweather.gson.Now;
import com.xiaodong.warmweather.gson.Suggestion;
import com.xiaodong.warmweather.gson.WeatherInfo;
import com.xiaodong.warmweather.service.WeatherService;
import com.xiaodong.warmweather.util.HttpUtil;
import com.xiaodong.warmweather.util.LogUtil;
import com.xiaodong.warmweather.util.StatusBarCompat;
import com.xiaodong.warmweather.util.Utility;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private String weatherCode;
    private String cityName;
    private AppBarLayout appbar_layout;
    public SwipeRefreshLayout swipeRefreshLayout;
    private ImageView weatherImage;
    private TextView textTmp;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView toolbar_title;
    private ImageView weather_icon;
    private TextView sugs_comf;
    private TextView sugs_cw;
    private TextView sugs_drsg;
    private TextView sugs_flu;
    private TextView sugs_sport;
    private TextView sugs_trav;
    private TextView sugs_uv;
    public DrawerLayout drawerLayout;
    public static final String WEATHER_JSON_STRING = "weather_json_string";
    private SharedPreferences sharedPreferences;
    private LinearLayout forecast_container;
    private TextView text_aqi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
//        if(Build.VERSION.SDK_INT>=21){
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        StatusBarCompat.compat(this, getResources().getColor(R.color.colorPrimaryDark));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        cityName = sharedPreferences.getString(ChooseAreaFragment.SELECTED_CITYNAME, null);
        drawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);

        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        weather_icon = (ImageView) findViewById(R.id.weather_icon);
        textTmp = (TextView) findViewById(R.id.text_tmp);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryWeatherFromServer();
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appbar_layout = (AppBarLayout) findViewById(R.id.appbar_layout);
        appbar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        text_aqi = (TextView) findViewById(R.id.text_aqi);
        weatherImage = (ImageView) findViewById(R.id.weather_pic);

        sugs_comf = (TextView) findViewById(R.id.text_comf);
        sugs_cw = (TextView) findViewById(R.id.text_cw);
        sugs_drsg = (TextView) findViewById(R.id.text_drsg);
        sugs_flu = (TextView) findViewById(R.id.text_flu);
        sugs_sport = (TextView) findViewById(R.id.text_sport);
        sugs_trav = (TextView) findViewById(R.id.text_trav);
        sugs_uv = (TextView) findViewById(R.id.text_uv);

        forecast_container = (LinearLayout) findViewById(R.id.forecast_container);

        handleWeatherPic();
        queryWeather();
    }

    public void queryWeather() {
        //关闭左侧菜单
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherStr = sharedPreferences.getString(WEATHER_JSON_STRING, "");
        if (!weatherStr.equals("")) {
            LogUtil.d("PreferencesWeatherInfo==============="+weatherStr);
            WeatherInfo weatherInfo = Utility.handleWeatherResponse(weatherStr);
            showWeatherInfo(weatherInfo);
        } else {
            queryWeatherFromServer();
        }

    }

    public void queryWeatherFromServer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        //从服务器重新获取数据时要用最新的weathercode和cityname
        weatherCode = sharedPreferences.getString(ChooseAreaFragment.SELECTED_WEATHERCODE, null);
        cityName = sharedPreferences.getString(ChooseAreaFragment.SELECTED_CITYNAME, null);
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/weather?cityid=" + weatherCode + "&key=4e5ec8e307ba48e2921c023b78e45435", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("failure=========" + e.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气出错", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("response=========",response.body().string());
                String respStr = response.body().string();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WEATHER_JSON_STRING, respStr);
                editor.commit();
                LogUtil.d("response=========" + respStr);
                try {
                    WeatherInfo weatherInfo = Utility.handleWeatherResponse(respStr);
                    showWeatherInfo(weatherInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showWeatherInfo(final WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            Toast.makeText(WeatherActivity.this, "获取天气信息出错", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        try {
            final Now now = weatherInfo.getNow();
            final Suggestion suggestion = weatherInfo.getSuggestion();
            final Aqi aqi = weatherInfo.getAqi();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String bottom_content = now.getCond().getTxt() + now.getTmp() + "℃ | " + now.getWind().getDir() + now.getWind().getSc() + " | 相对湿度" + now.getHum() + "%";
                    textTmp.setText(bottom_content);
                    Spanned title = Html.fromHtml("<font>" + cityName + "</font>" + "&nbsp;<small><font>" + now.getTmp() + "℃</font></small>");
                    toolbar_title.setText(title);
                    Glide.with(WeatherActivity.this).load("http://files.heweather.com/cond_icon/" + now.getCond().getCode() + ".png").into(weather_icon);
                    sugs_comf.setText(suggestion.getComf().getTxt());
                    sugs_cw.setText(suggestion.getCw().getTxt());
                    sugs_drsg.setText(suggestion.getDrsg().getTxt());
                    sugs_flu.setText(suggestion.getFlu().getTxt());
                    sugs_sport.setText(suggestion.getSport().getTxt());
                    sugs_trav.setText(suggestion.getTrav().getTxt());
                    sugs_uv.setText(suggestion.getUv().getTxt());
                    addForeCast(weatherInfo.getDailyForcasts());
                    Aqi.CityBean cityBean = null;
                    if (aqi != null) {
                        cityBean = aqi.getCity();
                        Spanned aqispanned = Html.fromHtml("<big><font>" + cityBean.getQlty() + "</font></big><br/><font>pm25指数" + cityBean.getPm25() + "</font>");
                        text_aqi.setText(aqispanned);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        //启动后台自动更新服务
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
//        切换图标
        checkAndChangeIcon();
    }

    public void handleWeatherPic() {
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imgUrl = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(imgUrl).into(weatherImage);
                    }
                });

            }
        });
    }

    //未来两天
    public void addForeCast(List<DailyForcast> dailyForcasts) {
        forecast_container.removeAllViews();
        for (DailyForcast dailyForcast : dailyForcasts) {
            View view = LayoutInflater.from(this).inflate(R.layout.daily_item, null);
            TextView text_date = (TextView) view.findViewById(R.id.text_date);
            TextView text_weather = (TextView) view.findViewById(R.id.text_weather);
            TextView text_daily_tmp = (TextView) view.findViewById(R.id.text_daily_tmp);
            LogUtil.d("dailyForcast===========" + dailyForcast.getDate());
            String date = dailyForcast.getDate();
            String weather = "白天" + dailyForcast.getCond().getTxt_d() + "|夜晚" + dailyForcast.getCond().getTxt_n() + "|" + dailyForcast.getWind().getDir() + dailyForcast.getWind().getSc();
            String tmp = dailyForcast.getTmp().getMax() + "° / " + dailyForcast.getTmp().getMin() + "°";
            text_date.setText(date);
            text_weather.setText(weather);
            text_daily_tmp.setText(tmp);
            forecast_container.addView(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
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
            if (amp >= 20 || amp < 6 && save_status.equals("day")) {
                //20-6点为夜晚
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity0"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(new ComponentName(this, "com.xiaodong.warmweather.AliasSplashActivity1"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                sharedPreferences.edit().putString("last_status","night").commit();
                restartDeskTop(pm);
            } else if (amp>=6&&amp<20&&save_status.equals("night")){
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

    private void restartDeskTop(PackageManager pm){
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> resolves = pm.queryIntentActivities(i, 0);
        for (ResolveInfo res : resolves) {
            if (res.activityInfo != null) {
                am.killBackgroundProcesses(res.activityInfo.packageName);
            }
        }
    }
}
