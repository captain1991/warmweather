package com.xiaodong.warmweather.gson;

import java.util.List;

/**
 * Created by yxd on 2017/2/21.
 */
public class WeatherInfo {
    Now now;
    Suggestion suggestion;
//    @SerializedName("daily_forecast")
    List<DailyForcast> daily_forecast;

    public void setDailyForcasts(List<DailyForcast> dailyForcasts) {
        this.daily_forecast = dailyForcasts;
    }

    public List<DailyForcast> getDailyForcasts() {

        return daily_forecast;
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

