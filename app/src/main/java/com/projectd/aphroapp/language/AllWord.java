package com.projectd.aphroapp.language;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.projectd.aphroapp.dao.UserDAO;

public class AllWord {
    //Login Activity
    public static String loginByGoogle = "Đăng nhập bằng Google";
    public static String loginByFaceBook = "Đăng nhập bằng Facebook";
    public static String rule = "Điều khoản và dịch vụ";

    //All Register Activity
    public static String yourName = "Tên của bạn";
    public static String warningYourName = "Tên của bạn sẽ hiển thị công khai";
    public static String yourAddress = "Địa chỉ của bạn";
    public static String city = "Tỉnh/Thành phố";
    public static String district = "Quận/Huyện";
    public static String wards = "Xã/Phường";
    public static String yourBirthDate = "Ngày sinh của bạn";
    public static String day = "Ngày";
    public static String month = "Tháng";
    public static String year = "Năm";
    public static String warningBirthDate = "Tuổi của bạn sẽ được hiển thị công khai";
    public static String yourDescription = "Mô tả về bạn";
    public static String selfDescription = "Mô tả bản thân";
    public static String hintDescription = "Hãy viết gì đó về bản thân...";
    public static String yourGender = "Giới tính của bạn";
    public static String male = "Nam";
    public static String female = "Nữ";
    public static String yourAvatar = "Ảnh đại diện của bạn";
    public static String profileCompleted = "Hồ sơ đã hoàn thành";
    public static String goodLuck = "Chúc bạn sẽ sớm tìm được một nửa của mình với";
    public static String under18 = "Bạn chưa đủ 18 tuổi";
    public static String backAfter = "Mời bạn quay lại sau 1 ngày";

    public static String completed = "Hoàn thành";
    public static String next = "Tiếp tục";

    //Match Activity
    public static String clickToViewProfile = "Nhấn để xem thông tin";
    public static String connected = "Gắn kết";
    public static String talkNow = "Trò chuyện ngay";
    public static String skip = "Bỏ qua";

    //Dialog
    public static String titleDialog = "Khởi động lại";
    public static String messageDialog = "Aphro cần khởi động lại để cập nhật dữ liệu";
    public static String okButtonDialog = "Đồng ý";
    public static String cancelButtonDialog = "Huỷ";

    public static boolean complete = false;

    public static void translate(){
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                        .setTargetLanguage(UserDAO.nowLangFirst)
                        .build();
        Translator translator = Translation.getClient(options);

        translator.translate(loginByGoogle).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                loginByGoogle = s;
            }
        });
        translator.translate(loginByFaceBook).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                loginByFaceBook = s;
            }
        });
        translator.translate(rule).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                rule = s;
            }
        });

        translator.translate(yourName).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                yourName = s;
            }
        });
        translator.translate(warningYourName).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                warningYourName = s;
            }
        });
        translator.translate(yourAddress).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                yourAddress = s;
            }
        });
        translator.translate(city).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                city = s;
            }
        });
        translator.translate(district).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                district = s;
            }
        });
        translator.translate(wards).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                wards = s;
            }
        });
        translator.translate(yourBirthDate).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                yourBirthDate = s;
            }
        });
        translator.translate(day).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                day = s;
            }
        });
        translator.translate(month).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                month = s;
            }
        });
        translator.translate(year).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                year = s;
            }
        });
        translator.translate(warningBirthDate).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                warningBirthDate = s;
            }
        });
        translator.translate(yourDescription).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                yourDescription = s;
            }
        });
        translator.translate(selfDescription).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                selfDescription = s;
            }
        });
        translator.translate(hintDescription).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                hintDescription = s;
            }
        });
        translator.translate(yourGender).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                yourGender = s;
            }
        });
        translator.translate(male).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                male = s;
            }
        });
        translator.translate(female).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                female = s;
            }
        });
        translator.translate(yourAvatar).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                yourAvatar = s;
            }
        });
        translator.translate(profileCompleted).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                profileCompleted = s;
            }
        });
        translator.translate(goodLuck).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                goodLuck = s + " Aphro!!!";
            }
        });
        translator.translate(under18).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                under18 = s;
            }
        });
        translator.translate(backAfter).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                backAfter = s;
            }
        });
        translator.translate(completed).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                completed = s;
            }
        });
        translator.translate(next).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                next = s;
            }
        });
        translator.translate(clickToViewProfile).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                clickToViewProfile = s;
            }
        });
        translator.translate(connected).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                connected = s;
            }
        });
        translator.translate(talkNow).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                talkNow = s;
            }
        });
        translator.translate(skip).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                skip = s;
            }
        });
        translator.translate(titleDialog).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                titleDialog = s;
            }
        });
        translator.translate(messageDialog).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                messageDialog = s;
            }
        });
        translator.translate(okButtonDialog).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                okButtonDialog = s;
            }
        });
        translator.translate(cancelButtonDialog).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                cancelButtonDialog = s;
                complete = true;
            }
        });
    }
}
