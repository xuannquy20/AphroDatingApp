package com.projectd.aphroapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.language.AllWord;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {
    Button signOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signOut = getView().findViewById(R.id.button_sign_out);

        signOut.setOnClickListener(v -> {
            exits();
        });
    }

    private void exits() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(AllWord.titleDialog)
                .setContentText(AllWord.messageDialog)
                .setConfirmText(AllWord.okButtonDialog)
                .setConfirmClickListener(sweetAlertDialog -> {
                    if (UserDAO.isGoogle) {
                        IntroActivity.mGoogleSignInClient.signOut();
                    } else {
                        LoginManager.getInstance().logOut();
                    }
                    LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                    loadingDialog.show();
                    new Handler().postDelayed(() -> {
                        loadingDialog.cancel();
                        Intent mStartActivity = new Intent(getActivity(), IntroActivity.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }, 500);
                })
                .setCancelButton(AllWord.cancelButtonDialog, null)
                .show();
    }
}