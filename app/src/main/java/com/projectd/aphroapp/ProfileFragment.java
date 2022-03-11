package com.projectd.aphroapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ChatBox;
import com.projectd.aphroapp.model.ReactUser;
import com.projectd.aphroapp.model.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {
    Button signOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signOut = getView().findViewById(R.id.button_sign_out);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserDAO.isGoogle){
                    signOut();
                }
                else{
                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    resetData();
                    getActivity().finish();
                }
            }
        });
    }

    private void resetData(){
        UserDAO.CURRENT_USER_ID = null;
        UserDAO.CURRENT_USER = new User();
        UserDAO.ORDER_NUMBER = -1;
        UserDAO.GENDER = "";
        UserDAO.GENDER_FINDING = "";
        UserDAO.imageBitmap = null;
        UserDAO.age = -1;

        UserDAO.takedLike = new ArrayList<>();
        UserDAO.givedLike = new ArrayList<>();
        UserDAO.listCanFind = new ArrayList<>();

        UserDAO.listChat = new LinkedList<>();

        UserDAO.randomPosition = -1;
        UserDAO.userFound = new ArrayList<>();
        UserDAO.imageUserFound = new ArrayList<>();

        UserDAO.getDataComplete = false;
        UserDAO.isGoogle = true;

        ChatActivity.recyclerView = null;
        ChatActivity.adapter = null;
        ChatActivity.idUser = null;
        ChatActivity.idRoom = null;
        ChatActivity.nameUser = null;

        ChatListFragment.recyclerView = null;
        ChatListFragment.adapter = null;
    }

    private void signOut() {
        IntroActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                    }
                });
    }
}