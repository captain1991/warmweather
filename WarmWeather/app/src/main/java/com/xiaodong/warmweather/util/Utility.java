package com.xiaodong.warmweather.util;

import com.google.gson.Gson;
import com.xiaodong.warmweather.db.City;
import com.xiaodong.warmweather.db.County;
import com.xiaodong.warmweather.db.Province;
import com.xiaodong.warmweather.gson.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by yxd on 2017/2/20.
 */
public class Utility {

    public static boolean handleProvinceResponse(Response response){
        try {
            String respStr = response.body().string();
            JSONArray provinces = new JSONArray(respStr);
            for (int i=0;i<provinces.length();i++){
                JSONObject jObject= provinces.getJSONObject(i);
                Province province = new Province();
                province.setProvinceCode(jObject.getInt("id"));
                province.setProvinceName(jObject.getString("name"));
                province.save();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean handleCityResponse(int provinceId,Response response){
        try {
            String respStr = response.body().string();
            JSONArray JACity = new JSONArray(respStr);
            for (int i=0;i<JACity.length();i++){
                JSONObject jsonObject = JACity.getJSONObject(i);
                City city = new City();
                city.setCityCode(jsonObject.getInt("id"));
                city.setCityName(jsonObject.getString("name"));
                city.setProvinceId(provinceId);
                city.save();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean handleCountyResponse(int cityId,Response response){
        try {
            String respStr = response.body().string();
            JSONArray JACounty = new JSONArray(respStr);
            for (int i=0;i<JACounty.length();i++){
                JSONObject jsonObject = JACounty.getJSONObject(i);
                County county = new County();
                county.setCountyCode(jsonObject.getInt("id"));
                county.setCountyName(jsonObject.getString("name"));
                county.setWeather_id(jsonObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static WeatherInfo handleWeatherResponse(String respStr){

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(respStr);
            JSONArray heWeather = jsonObject.getJSONArray("HeWeather");
            JSONObject info = heWeather.getJSONObject(0);
            Gson mGson = new Gson();
            WeatherInfo weatherInfo = mGson.fromJson(info.toString(),WeatherInfo.class);
            return weatherInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
