package com.xiaodong.warmweather.gson;

/**
 * Created by yxd on 2017/2/21.
 */
public class WeatherInfo {
    Now now;
    Suggestion suggestion;

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

