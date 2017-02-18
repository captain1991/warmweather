package com.xiaodong.warmweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yxd on 2017/2/18.
 */
public class City extends DataSupport {
    private int id;
    private int cityCode;
    private String cityName;
    private int provinceId;

    public void setId(int id) {
        this.id = id;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {

        return id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }
}
