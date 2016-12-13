package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by user on 2016/12/13.
 */

public class City extends DataSupport {
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public int getCityCode() {
        return CityCode;
    }

    public void setCityCode(int cityCode) {
        CityCode = cityCode;
    }
    private int id;
    private String cityName;
    private int CityCode;
    private int provinceId;
}
