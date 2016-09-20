package com.catiweather.app.utils;

import android.text.TextUtils;

import com.catiweather.app.db.CatiWeatherDB;
import com.catiweather.app.model.City;
import com.catiweather.app.model.County;
import com.catiweather.app.model.Province;

/**
 * 服务器返回数据解析类
 * Created by Administrator on 2016/9/20.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     * */
    public synchronized static boolean handleProvincesResponse(CatiWeatherDB catiWeatherDB, String reponse) {
        if(!TextUtils.isEmpty(reponse)) {
            String[] allProvinces = reponse.split(",");
            if(allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    catiWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析处理服务器返回的市级数据
     * */
    public static boolean handleCitiesResponse(CatiWeatherDB catiWeatherDB, String response, int provinceId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0) {
                for(String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    catiWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     *
     * */
    public static boolean handleCountiesResponse(CatiWeatherDB catiWeatherDB, String response, int cityId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length  > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    catiWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }
}
