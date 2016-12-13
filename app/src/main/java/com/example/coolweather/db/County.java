package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by user on 2016/12/13.
 */

public class County extends DataSupport {
    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeatherId() {
        return WeatherId;
    }

    public void setWeatherId(String weatherId) {
        WeatherId = weatherId;
    }

    private int id;
    private String countyName;
    private String WeatherId;
    private int cityId;
}
