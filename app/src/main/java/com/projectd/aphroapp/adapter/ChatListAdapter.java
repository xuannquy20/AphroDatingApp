package com.projectd.aphroapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.ChatActivity;
import com.projectd.aphroapp.ChatListFragment;
import com.projectd.aphroapp.R;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ChatBox;
import com.projectd.aphroapp.model.Messenger;
import com.projectd.aphroapp.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.WordViewHolder> {
    private LinkedList<ChatBox> wordList;
    private LayoutInflater mInflater;
    private Context context;
    private DatabaseReference chatbox = FirebaseDatabase.getInstance().getReference().child("chat_box");

    public class WordViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutChatList;
        private ImageView avtProfile;
        private TextView nameUser, lastText, timeLastText;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            avtProfile = itemView.findViewById(R.id.avatar_profile);
            nameUser = itemView.findViewById(R.id.userName);
            lastText = itemView.findViewById(R.id.lastText);
            timeLastText = itemView.findViewById(R.id.timeLastText);
            layoutChatList = itemView.findViewById(R.id.layout_chat_item);
        }
    }

    public ChatListAdapter(Context context, LinkedList<ChatBox> wordList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.chat_list_item, parent, false);
        return new WordViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChatBox mCurrent = wordList.get(position);
        holder.nameUser.setText(mCurrent.getNameUser());
        try {
            File dir = new File(context.getFilesDir(), "image");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, mCurrent.getIdUser() + ".png");
            if (!file.exists()) {
                StorageReference storageReference = InternetDAO.storage.child(mCurrent.getIdUser());
                storageReference.getFile(file).addOnSuccessListener(taskSnapshot -> {
                    try {
                        Bitmap imageBox = BitmapFactory.decodeFile(file.getAbsolutePath());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imageBox.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        holder.avtProfile.setImageBitmap(imageBox);
                        FileOutputStream fileOut = new FileOutputStream(file);
                        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                        objectOut.writeObject(byteArray);
                        objectOut.flush();
                        objectOut.close();
                    } catch (Exception e) {
                    }
                });
            } else {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                byte[] byteArray = (byte[]) objectIn.readObject();
                Bitmap avtUser = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                holder.avtProfile.setImageBitmap(avtUser);
                objectIn.close();
            }
        } catch (Exception e) {
        }

        if (mCurrent.getMessengers().size() > 0) {
            String text = mCurrent.getMessengers().get(0).getText();
            if (text.length() > 25) {
                text = text.substring(0, 22) + "...";
            }
            holder.lastText.setText(text);

            Calendar check = Calendar.getInstance();
            check.setTime(mCurrent.getMessengers().get(0).getDate());

            int day = mCurrent.getMessengers().get(0).getDate().getDate();
            int month = mCurrent.getMessengers().get(0).getDate().getMonth();
            int year = mCurrent.getMessengers().get(0).getDate().getYear();
            Date calendar = Calendar.getInstance().getTime();

            if (day == calendar.getDate() && month == calendar.getMonth() && year == calendar.getYear()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                holder.timeLastText.setText(simpleDateFormat.format(mCurrent.getMessengers().get(0).getDate()));
            } else if (Calendar.getInstance().get(Calendar.WEEK_OF_MONTH) == check.get(Calendar.WEEK_OF_MONTH)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E HH:mm");
                holder.timeLastText.setText(simpleDateFormat.format(mCurrent.getMessengers().get(0).getDate()));
            } else if (year == calendar.getYear()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM, HH:mm");
                holder.timeLastText.setText(simpleDateFormat.format(mCurrent.getMessengers().get(0).getDate()));
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
                holder.timeLastText.setText(simpleDateFormat.format(mCurrent.getMessengers().get(0).getDate()));
            }
        } else {
            holder.lastText.setText("");
            holder.timeLastText.setText("");
        }

        if (!mCurrent.isReaded()) {
            holder.lastText.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else{
            holder.lastText.setTypeface(Typeface.DEFAULT);
        }

        holder.layoutChatList.setOnClickListener(v -> {
            UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room").get().addOnCompleteListener(task -> {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    if (ds.child("idRoom").getValue(String.class).equals(mCurrent.getIdRoom())) {
                        UserDAO.refCheck.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + ds.getKey() + "/readed").setValue(true);
                        UserDAO.listChat.get(position).setReaded(true);
                        ChatListFragment.adapter.notifyDataSetChanged();
                        holder.lastText.setTextColor(Color.parseColor("#737373"));
                        break;
                    }
                }
            });
            Intent i = new Intent(v.getContext(), ChatActivity.class);
            i.putExtra("idRoom", mCurrent.getIdRoom());
            i.putExtra("idUser", mCurrent.getIdUser());
            i.putExtra("nameUser", mCurrent.getNameUser());

            i.putExtra("first", mCurrent.isFirst() + "");
            i.putExtra("list", mCurrent.getMessengers());
            v.getContext().startActivity(i);
        });
        final int[] size = {UserDAO.listChat.get(position).getMessengers().size()};

        if (mCurrent.isFirst()) {
            chatbox.child(mCurrent.getIdRoom()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    int finalPosition = -1;
                    for (int i = 0; i < UserDAO.listChat.size(); i++) {
                        if (UserDAO.listChat.get(i).getIdRoom().equals(mCurrent.getIdRoom())) {
                            finalPosition = i;
                            break;
                        }
                    }
                    if (size[0] > 0) {
                        size[0]--;
                    } else {
                        if (!snapshot.child("idUser").getValue(String.class).equals(UserDAO.CURRENT_USER_ID)) {
                            UserDAO.listChat.get(finalPosition).getMessengers().add(0, snapshot.getValue(Messenger.class));
                            ChatListFragment.swapItems(finalPosition);
                            if (mCurrent.getIdRoom().equals(ChatActivity.idRoom)) {
                                UserDAO.listChat.get(finalPosition).setReaded(true);
                                ChatListFragment.adapter.notifyDataSetChanged();
                                ChatActivity.adapter.notifyItemInserted(0);
                                ChatActivity.recyclerView.scrollToPosition(0);
                            }
                            else{
                                UserDAO.listChat.get(finalPosition).setReaded(false);
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        UserDAO.listChat.get(position).setFirst(false);
    }


    @Override
    public int getItemCount() {
        return this.wordList.size();
    }
}
