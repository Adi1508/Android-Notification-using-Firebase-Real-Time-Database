package com.example.aditya.fireapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by aditya on 30/6/17.
 */

public class RegToken extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        System.out.println(refreshToken);
    }
}
