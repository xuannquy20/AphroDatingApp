package com.projectd.aphroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView logoAphro;

    private HomeFragment homeFragment;
    private ChatListFragment chatListFragment;

    protected void bindingView(){
        viewPager = findViewById(R.id.view_paper);
        tabLayout = findViewById(R.id.layout_menu);
        homeFragment = new HomeFragment();
        chatListFragment = new ChatListFragment();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindingView();
        tabLayout.setupWithViewPager(viewPager);
        ViewPaperAdapter viewPaperAdapter = new ViewPaperAdapter(getSupportFragmentManager(), 0);
        viewPaperAdapter.addFragment(homeFragment, "");
        viewPaperAdapter.addFragment(chatListFragment, "");
        viewPager.setAdapter(viewPaperAdapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat);

        BadgeDrawable badgeDrawable =  tabLayout.getTabAt(1).getOrCreateBadge();
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(2);
    }

    private class ViewPaperAdapter extends FragmentPagerAdapter {
        private List<Fragment> listFragment = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPaperAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
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
}