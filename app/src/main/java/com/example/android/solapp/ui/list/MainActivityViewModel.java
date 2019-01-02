package com.example.android.sunshine.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.sunshine.data.SolAppRepository;
import com.example.android.sunshine.data.database.ListWeatherEntry;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final SolAppRepository mRepository;
    private final LiveData<List<ListWeatherEntry>> mForecast;


    public MainActivityViewModel(SolAppRepository repository) {
        mRepository = repository;
        mForecast = mRepository.getCurrentWeatherForecasts();
    }

    public LiveData<List<ListWeatherEntry >> getForecast() {
        return mForecast;
    }
}
