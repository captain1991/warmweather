package com.xiaodong.warmweather;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xiaodong.warmweather.gson.DailyForcast;
import com.xiaodong.warmweather.gson.Now;
import com.xiaodong.warmweather.gson.Suggestion;
import com.xiaodong.warmweather.gson.WeatherInfo;
import com.xiaodong.warmweather.service.WeatherService;
import com.xiaodong.warmweather.util.HttpUtil;
import com.xiaodong.warmweather.util.Utility;

import java.io.IOException;
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
    public static final String WEATHER_JSON_STRING="weather_json_string";
    private  SharedPreferences sharedPreferences;
    private LinearLayout forecast_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
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
                if(verticalOffset>=0){
                    swipeRefreshLayout.setEnabled(true);
                }else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

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

    public void queryWeather(){
        //关闭左侧菜单
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherStr=sharedPreferences.getString(WEATHER_JSON_STRING, null);
        if(weatherStr!=null){
            WeatherInfo weatherInfo = Utility.handleWeatherResponse(weatherStr);
            showWeatherInfo(weatherInfo);
        }else {
            queryWeatherFromServer();
        }

    }

    public void queryWeatherFromServer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        //从服务器重新获取数据时要用最新的weathercode和cityname
        weatherCode = sharedPreferences.getString(ChooseAreaFragment.SELECTED_WEATHERCODE,null);
        cityName = sharedPreferences.getString(ChooseAreaFragment.SELECTED_CITYNAME,null);
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/weather?cityid=" + weatherCode + "&key=4e5ec8e307ba48e2921c023b78e45435", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("failure=========",e.toString());
                Toast.makeText(WeatherActivity.this,"获取天气出错",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                editor.putString(WEATHER_JSON_STRING,respStr);
                editor.commit();
                Log.d("response=========",respStr);
                try {
                    WeatherInfo weatherInfo = Utility.handleWeatherResponse(respStr);
                    showWeatherInfo(weatherInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showWeatherInfo( final WeatherInfo weatherInfo ){
        if(weatherInfo==null){
            Toast.makeText(WeatherActivity.this,"获取天气信息出错",Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        final Now now = weatherInfo.getNow();
        final Suggestion suggestion = weatherInfo.getSuggestion();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textTmp.setText(now.getCond().getTxt() + now.getTmp() + "℃ | " + now.getWind().getDir() + now.getWind().getSc() + " | 相对湿度" + now.getHum() + "%");
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
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }

    public void handleWeatherPic(){
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

    public void addForeCast(List<DailyForcast> dailyForcasts){
        forecast_container.removeAllViews();
        for (DailyForcast dailyForcast:dailyForcasts) {
            View view = LayoutInflater.from(this).inflate(R.layout.daily_item,null);
            TextView text_date = (TextView)view.findViewById(R.id.text_date);
            TextView text_weather = (TextView)view.findViewById(R.id.text_weather);
            TextView text_daily_tmp = (TextView)view.findViewById(R.id.text_daily_tmp);
            Log.d("dailyForcast===========", dailyForcast.getDate());
            String date = dailyForcast.getDate();
            String weather = "白天"+dailyForcast.getCond().getTxt_d()+"|夜晚"+dailyForcast.getCond().getTxt_n()+"|"+dailyForcast.getWind().getDir()+dailyForcast.getWind().getSc();
            String tmp = dailyForcast.getTmp().getMax()+"° / "+dailyForcast.getTmp().getMin()+"°";
            text_date.setText(date);
            text_weather.setText(weather);
            text_daily_tmp.setText(tmp);
            forecast_container.addView(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
