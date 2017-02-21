package com.xiaodong.warmweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xiaodong.warmweather.gson.Now;
import com.xiaodong.warmweather.gson.Suggestion;
import com.xiaodong.warmweather.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private String weatherCode;
    private String cityName;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Intent intent = getIntent();
        if (intent!=null) {
            weatherCode = intent.getStringExtra(ChooseAreaFragment.SELECTED_WEATHERCODE);
            cityName = intent.getStringExtra(ChooseAreaFragment.SELECTED_CITYNAME);
        }

        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        weather_icon = (ImageView) findViewById(R.id.weather_icon);
        textTmp = (TextView) findViewById(R.id.text_tmp);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

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

        handleWeatherPic();
        queryWeatherFromServer();

    }

    public void queryWeatherFromServer(){
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/weather?cityid=" + weatherCode + "&key=4e5ec8e307ba48e2921c023b78e45435", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("failure=========",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("response=========",response.body().string());
                String respStr = response.body().string();
                Log.d("response=========",respStr);
                try {
                    JSONObject jsonObject = new JSONObject(respStr);
                    JSONArray heWeather = jsonObject.getJSONArray("HeWeather");
                    JSONObject info = heWeather.getJSONObject(0);
                    Gson mGson = new Gson();
                    final Now now=mGson.fromJson(info.getJSONObject("now").toString(), Now.class);
                    final Suggestion suggestion = mGson.fromJson(info.getJSONObject("suggestion").toString(),Suggestion.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textTmp.setText(now.getCond().getTxt()+now.getTmp() + "℃ | " + now.getWind().getDir()+now.getWind().getSc()+" | 相对湿度"+now.getHum()+"%");
                            Spanned title = Html.fromHtml("<font>"+cityName+"</font>"+"&nbsp;<small><font>"+now.getTmp()+"℃</font></small>");
                            toolbar_title.setText(title);
                            Glide.with(WeatherActivity.this).load("http://files.heweather.com/cond_icon/"+now.getCond().getCode() + ".png").into(weather_icon);
                            sugs_comf.setText(suggestion.getComf().getTxt());
                            sugs_cw.setText(suggestion.getCw().getTxt());
                            sugs_drsg.setText(suggestion.getDrsg().getTxt());
                            sugs_flu.setText(suggestion.getFlu().getTxt());
                            sugs_sport.setText(suggestion.getSport().getTxt());
                            sugs_trav.setText(suggestion.getTrav().getTxt());
                            sugs_uv.setText(suggestion.getUv().getTxt());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
