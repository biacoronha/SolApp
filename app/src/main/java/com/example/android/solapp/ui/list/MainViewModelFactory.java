package com.example.android.sunshine.ui.list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.sunshine.data.SolAppRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final SolAppRepository mRepository;

    public MainViewModelFactory(SolAppRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MainActivityViewModel(mRepository);
    }
}
