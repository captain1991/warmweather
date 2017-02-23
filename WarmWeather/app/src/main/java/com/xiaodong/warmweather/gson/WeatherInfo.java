package com.xiaodong.warmweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yxd on 2017/2/21.
 */
public class WeatherInfo {
    Now now;
    Suggestion suggestion;
    @SerializedName("daily_forecast")
    List<DailyForcast> dailyForcasts;
    Aqi aqi;

    public Aqi getAqi() {
        return aqi;
    }

    public void setAqi(Aqi aqi) {
        this.aqi = aqi;
    }

    public void setDailyForcasts(List<DailyForcast> dailyForcasts) {
        this.dailyForcasts = dailyForcasts;
    }

    public List<DailyForcast> getDailyForcasts() {

        return dailyForcasts;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public Now getNow() {

        return now;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }
}

