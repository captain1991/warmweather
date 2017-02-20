package com.xiaodong.warmweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yxd on 2017/2/18.
 */
public class County extends DataSupport {
    private int id;
    private int countyCode;
    private int cityId;
    private String weather_id;
    private String countyName;

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyCode(int countyCode) {
        this.countyCode = countyCode;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }

    public int getId() {
        return id;
    }

    public int getCountyCode() {
        return countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public String getCountyName() {
        return countyName;
    }
}
