package com.projectd.aphroapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.projectd.aphroapp.model.User;
import java.util.LinkedList;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {
    private LinkedList<User> wordList;
    private LayoutInflater mInflater;
    int speedAni = 300;

    public class WordViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout layoutChatList;
        private ImageView avtProfile;
        private TextView nameUser;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            avtProfile = itemView.findViewById(R.id.avatar_profile);
            nameUser = itemView.findViewById(R.id.userName);
            layoutChatList = itemView.findViewById(R.id.layout_chat_item);
        }
    }

    public WordListAdapter(Context context, LinkedList<User> wordList) {
        mInflater = LayoutInflater.from(context);
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.chat_item, parent, false);
        return new WordViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        User mCurrent = wordList.get(position);
        holder.avtProfile.setImageResource(R.drawable.logo);
        holder.nameUser.setText(mCurrent.getName());
        setFadeAnimation(holder.layoutChatList);
    }

    private void setFadeAnimation(View view) {
        TranslateAnimation anim = new TranslateAnimation(-800, 0, 0, 0);
        anim.setDuration(speedAni);
        view.startAnimation(anim);
        speedAni+=10;
    }

    @Override
    public int getItemCount() {
        return this.wordList.size();
    }


}
