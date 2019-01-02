/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.ui.detail;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.example.android.sunshine.R;
import com.example.android.sunshine.data.database.WeatherEntry;
import com.example.android.sunshine.databinding.ActivityDetailBinding;
import com.example.android.sunshine.utilities.InjectorUtils;
import com.example.android.sunshine.utilities.SolAppDateUtils;
import com.example.android.sunshine.utilities.SolAppWeatherUtils;

import java.util.Date;

public class DetailActivity extends LifecycleActivity {

    public static final String WEATHER_ID_EXTRA = "WEATHER_ID_EXTRA";

    private ActivityDetailBinding mDetailBinding;
    private DetailActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        long timestamp = getIntent().getLongExtra(WEATHER_ID_EXTRA, -1);
        Date date = new Date(timestamp);

        DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext(), date);
        mViewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel.class);


        mViewModel.getWeather().observe(this, weatherEntry -> {
            if (weatherEntry != null) bindWeatherToUI(weatherEntry);
        });

    }

    private void bindWeatherToUI(WeatherEntry weatherEntry) {
        /****************
         * Weather Icon *
         ****************/

        int weatherId = weatherEntry.getWeatherIconId();
        int weatherImageId = SolAppWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);

        /****************
         * Weather Date *
         ****************/

        long localDateMidnightGmt = weatherEntry.getDate().getTime();
        String dateText = SolAppDateUtils.getFriendlyDateString(DetailActivity.this, localDateMidnightGmt, true);
        mDetailBinding.primaryInfo.date.setText(dateText);

        /***********************
         * Weather Description *
         ***********************/
        String description = SolAppWeatherUtils.getStringForWeatherCondition(DetailActivity.this, weatherId);

        String descriptionA11y = getString(R.string.a11y_forecast, description);

        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);

        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionA11y);

        /**************************
         * High (max) temperature *
         **************************/

        double maxInCelsius = weatherEntry.getMax();

        String highString = SolAppWeatherUtils.formatTemperature(DetailActivity.this, maxInCelsius);

        String highA11y = getString(R.string.a11y_high_temp, highString);

        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);

        /*************************
         * Low (min) temperature *
         *************************/

        double minInCelsius = weatherEntry.getMin();

        String lowString = SolAppWeatherUtils.formatTemperature(DetailActivity.this, minInCelsius);

        String lowA11y = getString(R.string.a11y_low_temp, lowString);

        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);

        /************
         * Humidity *
         ************/

        double humidity = weatherEntry.getHumidity();
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityA11y = getString(R.string.a11y_humidity, humidityString);

        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityA11y);

        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityA11y);

        /****************************
         * Wind speed and direction *
         ****************************/
        double windSpeed = weatherEntry.getWind();
        double windDirection = weatherEntry.getDegrees();
        String windString = SolAppWeatherUtils.getFormattedWind(DetailActivity.this, windSpeed, windDirection);
        String windA11y = getString(R.string.a11y_wind, windString);

        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windA11y);

        /************
         * Pressure *
         ************/
        double pressure = weatherEntry.getPressure();

        String pressureString = getString(R.string.format_pressure, pressure);

        String pressureA11y = getString(R.string.a11y_pressure, pressureString);

        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureA11y);
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);
    }
}