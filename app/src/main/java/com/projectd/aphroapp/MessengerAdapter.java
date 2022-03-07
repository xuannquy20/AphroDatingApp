package com.projectd.aphroapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.Messenger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.WordViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Messenger> messengerList;

    public MessengerAdapter(Context context, ArrayList<Messenger> messengerList) {
        mInflater = LayoutInflater.from(context);
        this.messengerList = messengerList;
    }

    @NonNull
    @Override
    public MessengerAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.messenger_item, parent, false);
        return new MessengerAdapter.WordViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessengerAdapter.WordViewHolder holder, int position) {
        Messenger messenger = messengerList.get(position);

        Date now = Calendar.getInstance().getTime();
        int day = messenger.getDate().getDate();
        int month = messenger.getDate().getMonth();
        int year = messenger.getDate().getYear();

        SimpleDateFormat simpleDateFormat;
        if (day == now.getDate() && month == now.getMonth() && year == now.getYear()) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        } else if (year == now.getYear()) {
            simpleDateFormat = new SimpleDateFormat("dd:MM");
        } else {
            simpleDateFormat = new SimpleDateFormat("MM:yyyy");
        }

        if (messenger.getIdUser().equals(UserDAO.CURRENT_USER_ID)) {
            holder.userSend.setText(messenger.getText());
            holder.timeUserSend.setText(simpleDateFormat.format(messenger.getDate()));
            holder.layoutOtherSend.setVisibility(View.GONE);
        } else {
            holder.ortherSend.setText(messenger.getText());
            holder.timeOtherSend.setText(simpleDateFormat.format(messenger.getDate()));
            holder.layoutUserSend.setVisibility(View.GONE);
        }

        setFadeAnimation(holder.constraintLayout);
    }

    private void setFadeAnimation(View view) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 100, 0);
        anim.setDuration(100);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return messengerList.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        TextView ortherSend, userSend, timeOtherSend, timeUserSend;
        LinearLayout layoutOtherSend, layoutUserSend;
        ConstraintLayout constraintLayout;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            ortherSend = itemView.findViewById(R.id.orther_send);
            userSend = itemView.findViewById(R.id.user_send);
            layoutOtherSend = itemView.findViewById(R.id.layout_other_send);
            layoutUserSend = itemView.findViewById(R.id.layout_user_send);
            timeOtherSend = itemView.findViewById(R.id.time_other_send);
            timeUserSend = itemView.findViewById(R.id.time_user_send);
            constraintLayout = itemView.findViewById(R.id.layout_each_mess);
        }
    }
}
