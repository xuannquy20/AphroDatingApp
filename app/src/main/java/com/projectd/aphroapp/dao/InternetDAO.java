package com.projectd.aphroapp.dao;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.net.InetAddress;

public class InternetDAO {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

