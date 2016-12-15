package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 2016/12/14.
 */

public class AQI {
    @SerializedName("city")
    public AQICity aqiCity;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
