package com.xiaodong.warmweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yxd on 2017/2/18.
 */
public class County extends DataSupport {
    private int id;
    private int countyCode;
    private int cityId;
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

    public int getId() {

        return id;
    }

    public int getCountyCode() {
        return countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCountyName() {
        return countyName;
    }
}
