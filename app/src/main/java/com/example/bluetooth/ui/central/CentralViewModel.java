package com.example.bluetooth.ui.central;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CentralViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CentralViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is central fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}