package com.example.coolweather;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mweatherId;
    public DrawerLayout drawerLayout;
    public Button navButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        scrollView=(ScrollView)findViewById(R.id.weather_layout);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        degreeText=(TextView)findViewById(R.id.degree_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic=preferences.getString("bing_pic",null);
        Log.d("WeatherActivity", "onCreate: ");
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadingPic();
        }
        String weatherString=preferences.getString("weather",null);
        if(weatherString!=null){
            Weather weather= Utility.handWeatherResponse(weatherString);
            mweatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            mweatherId=getIntent().getStringExtra("weather_id");
            scrollView.setVisibility(View.INVISIBLE);
            requestWeather(mweatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                requestWeather(mweatherId);
                loadingPic();
                Log.d("WeatherActivity", "can i refresh: ");
            }
        });
    }
    public void loadingPic(){
        final String requestBingPic="http://guolin.tech/api/bing_pic";
        //final Uri uri=Uri.parse("http://cn.bing.com/az/hprichbg/rb/VelikoTarnovo_ZH-CN11165433151_1920x1080.jpg");
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("WeatherActivity", "run: ");
                    }
                });
            }
        });
    }
    public void showWeatherInfo(Weather weather){
        Log.d("WeatherActivity", "showWeatherInfo: ");
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];//取日期时间
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }if(weather.aqi!=null){
            aqiText.setText(weather.aqi.aqiCity.aqi);
            pm25Text.setText(weather.aqi.aqiCity.pm25);
        }
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车建议："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        scrollView.setVisibility(View.VISIBLE);
    }
    public void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=7932ff558b1d4896ab6777b9d55b7d1f";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        //swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            //mweatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败了额",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
}
