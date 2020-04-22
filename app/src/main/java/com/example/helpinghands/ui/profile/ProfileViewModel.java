package com.example.helpinghands.ui.profile;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<Button> mText;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is profile fragment");
    }

    //public LiveData<String> getText() {
      //  return mText;
    //}
}