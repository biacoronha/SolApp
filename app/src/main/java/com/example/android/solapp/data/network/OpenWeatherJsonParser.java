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
package com.example.android.sunshine.data.network;

import android.support.annotation.Nullable;

import com.example.android.sunshine.data.database.WeatherEntry;
import com.example.android.sunshine.utilities.SolAppDateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Date;

/**
 * Parser for OpenWeatherMap JSON data.
 */
final class OpenWeatherJsonParser {

    // Weather information. Each day's forecast info is an element of the "list" array
    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";
    private static final String OWM_TEMPERATURE = "temp";
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";
    private static final String OWM_MESSAGE_CODE = "cod";

    private static boolean hasHttpError(JSONObject forecastJson) throws JSONException {
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    return false;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                default:
                    // Server probably down
                    return true;
            }
        }
        return false;
    }

    private static WeatherEntry[] fromJson(final JSONObject forecastJson) throws JSONException {
        JSONArray jsonWeatherArray = forecastJson.getJSONArray(OWM_LIST);

        WeatherEntry[] weatherEntries = new WeatherEntry[jsonWeatherArray.length()];

        long normalizedUtcStartDay = SolAppDateUtils.getNormalizedUtcMsForToday();

        for (int i = 0; i < jsonWeatherArray.length(); i++) {
            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);
            long dateTimeMillis = normalizedUtcStartDay + SolAppDateUtils.DAY_IN_MILLIS * i;
            WeatherEntry weather = fromJson(dayForecast, dateTimeMillis);

            weatherEntries[i] = weather;
        }
        return weatherEntries;
    }

    private static WeatherEntry fromJson(final JSONObject dayForecast,
                                         long dateTimeMillis) throws JSONException {

        double pressure = dayForecast.getDouble(OWM_PRESSURE);
        int humidity = dayForecast.getInt(OWM_HUMIDITY);
        double windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
        double windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

        JSONObject weatherObject =
                dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

        int weatherId = weatherObject.getInt(OWM_WEATHER_ID);

        JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
        double max = temperatureObject.getDouble(OWM_MAX);
        double min = temperatureObject.getDouble(OWM_MIN);

        return new WeatherEntry(weatherId, new Date(dateTimeMillis), max, min,
                humidity, pressure, windSpeed, windDirection);
    }

    @Nullable
    WeatherResponse parse(final String forecastJsonStr) throws JSONException {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        // Is there an error?
        if (hasHttpError(forecastJson)) {
            return null;
        }

        WeatherEntry[] weatherForecast = fromJson(forecastJson);

        return new WeatherResponse(weatherForecast);
    }
}