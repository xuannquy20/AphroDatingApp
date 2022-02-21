package com.projectd.aphroapp.dao;

import android.graphics.Bitmap;

import com.projectd.aphroapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static String CURRENT_USER_ID = null;
    public static User CURRENT_USER = new User();
    public static int ORDER_NUMBER = -1;
    public static String GENDER = "";
    public static Bitmap imageBitmap = null;
    public static List<Bitmap> imageUserFound = new ArrayList<>();
    public static List<User> userFound = new ArrayList<>();
    public static List<String> takedLike = new ArrayList<>();
    public static List<String> givedLike = new ArrayList<>();
    public static int maxGenderFinding = -1;
}
