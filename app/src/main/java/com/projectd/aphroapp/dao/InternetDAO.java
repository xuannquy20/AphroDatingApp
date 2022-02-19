package com.projectd.aphroapp.dao;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.InetAddress;

public class InternetDAO {
    public static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static final StorageReference storage = FirebaseStorage.getInstance().getReference();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

