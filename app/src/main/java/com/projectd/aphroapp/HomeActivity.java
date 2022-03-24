package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.se.omapi.Session;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.projectd.aphroapp.dao.InternetDAO;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.language.AllWord;
import com.projectd.aphroapp.model.ChatBox;
import com.projectd.aphroapp.model.ReactUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView logoAphro;
    public static int idNotity = 0;
    private AppBarLayout appBarLayout;
    private DatabaseReference dataUser = FirebaseDatabase.getInstance().getReference().child("data_user");

    public static boolean first = true;

    private HomeFragment homeFragment;
    private ChatListFragment chatListFragment;
    private ProfileFragment profileFragment;

    protected void bindingView() {
        viewPager = findViewById(R.id.view_paper);
        tabLayout = findViewById(R.id.layout_menu);
        appBarLayout = findViewById(R.id.appBarLayout);
        homeFragment = new HomeFragment();
        chatListFragment = new ChatListFragment();
        profileFragment = new ProfileFragment();
        logoAphro = findViewById(R.id.logo_aphro);

        TextPaint paint = logoAphro.getPaint();
        float width = paint.measureText("APHRO");

        Shader textShader = new LinearGradient(45, 45, width, logoAphro.getTextSize(),
                new int[]{
                        Color.parseColor("#305690"),
                        Color.parseColor("#A08798"),
                }, null, Shader.TileMode.MIRROR);
        logoAphro.getPaint().setShader(textShader);
    }

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindingView();

        try {
            File dir = new File(this.getFilesDir(), "image");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, UserDAO.CURRENT_USER_ID + ".png");
            if (!file.exists()) {
                try {
                    Bitmap imageBox = UserDAO.imageUser;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBox.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    FileOutputStream fileOut = new FileOutputStream(file);
                    ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                    objectOut.writeObject(byteArray);
                    objectOut.flush();
                    objectOut.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }

        animationIntro(appBarLayout, -1000f, 0);
        tabLayout.setupWithViewPager(viewPager);
        ViewPaperAdapter viewPaperAdapter = new ViewPaperAdapter(getSupportFragmentManager(), 0);
        viewPaperAdapter.addFragment(homeFragment, "");
        viewPaperAdapter.addFragment(chatListFragment, "");
        viewPaperAdapter.addFragment(profileFragment, "");
        viewPager.setAdapter(viewPaperAdapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_profile);

        if (first) {
            BadgeDrawable badgeDrawable = tabLayout.getTabAt(1).getOrCreateBadge();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            int size = 0;
                            for (int i = 0; i < UserDAO.listChat.size(); i++) {
                                if (!UserDAO.listChat.get(i).isReaded()) {
                                    ++size;
                                }
                            }
                            if (size == 0 && badgeDrawable.isVisible()) {
                                badgeDrawable.setVisible(false);
                            } else if (size != badgeDrawable.getNumber()) {
                                badgeDrawable.setVisible(true);
                                badgeDrawable.setNumber(size);
                            }
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                    }
                }
            }).start();

            final int[] size = {UserDAO.listChat.size()};
            dataUser.child(UserDAO.CURRENT_USER_ID + "/chat_room").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (size[0] > 0) {
                        size[0]--;
                    } else {
                        if (!"".equals(ChatActivity.idRoom) && ChatActivity.idRoom != null) {
                            ChatActivity.position++;
                        }
                        dataUser.child(UserDAO.CURRENT_USER_ID + "/chat_room/" + snapshot.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                ChatBox chatBox = new ChatBox();
                                chatBox.setIdUser(task.getResult().child("idUser").getValue(String.class));
                                chatBox.setIdRoom(task.getResult().child("idRoom").getValue(String.class));
                                chatBox.setNameUser(task.getResult().child("nameUser").getValue(String.class));
                                chatBox.setReaded(false);
                                chatBox.setFirst(true);
                                UserDAO.listChat.add(0, chatBox);
                                if (UserDAO.listChat.size() == 1) {
                                    ChatListFragment.adapter.notifyDataSetChanged();
                                } else {
                                    ChatListFragment.adapter.notifyItemInserted(0);
                                }
                                Intent i = new Intent(HomeActivity.this, ChatActivity.class);
                                i.putExtra("idRoom", chatBox.getIdRoom());
                                i.putExtra("idUser", chatBox.getIdUser());
                                i.putExtra("nameUser", chatBox.getNameUser());
                                i.putExtra("position", 0);
                                i.putExtra("first", chatBox.isFirst() + "");
                                i.putExtra("list", chatBox.getMessengers());
                                PendingIntent pending = PendingIntent.getActivity(
                                        HomeActivity.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                                final String NOTIFICATION_CHANNEL_ID = chatBox.getIdRoom();
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this, NOTIFICATION_CHANNEL_ID);
                                Notification notification = builder
                                        .setSmallIcon(R.drawable.logo)
                                        .setContentTitle(AllWord.titleNotification)
                                        .setContentText(AllWord.messageNotification)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setContentIntent(pending)
                                        .setAutoCancel(true)
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText(AllWord.messageNotification))
                                        .build();

                                NotificationManagerCompat manager = NotificationManagerCompat.from(HomeActivity.this);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel notificationChannel =
                                            new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Channel Notify ver 8+",
                                                    NotificationManager.IMPORTANCE_DEFAULT);
                                    manager.createNotificationChannel(notificationChannel);
                                }

                                manager.notify(idNotity, notification);
                                idNotity++;

                                if (ChatListFragment.recyclerView != null) {
                                    ChatListFragment.recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
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
        this.first = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void animationIntro(View view, float infoNum1, float infoNum2) {
        ValueAnimator move = ValueAnimator.ofFloat(infoNum1, infoNum2);
        move.setInterpolator(new AccelerateDecelerateInterpolator());
        move.setDuration(1000);
        move.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            view.setTranslationY(progress);
        });
        move.start();
    }

    private class ViewPaperAdapter extends FragmentPagerAdapter {
        private List<Fragment> listFragment = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPaperAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            listFragment.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}