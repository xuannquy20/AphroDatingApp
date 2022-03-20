package com.projectd.aphroapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.projectd.aphroapp.ChatActivity;
import com.projectd.aphroapp.R;
import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.Messenger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.WordViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Messenger> messengerList;
    private Context context;
    String nowLangFirst = Locale.getDefault().getLanguage();
    String display = "Đã dịch";
    TranslatorOptions optionsFirst =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                    .setTargetLanguage(nowLangFirst)
                    .build();
    Translator translatorFirst = Translation.getClient(optionsFirst);

    DownloadConditions conditionsFirst = new DownloadConditions.Builder().requireWifi().build();

    public MessengerAdapter(Context context, ArrayList<Messenger> messengerList) {
        mInflater = LayoutInflater.from(context);
        this.messengerList = messengerList;
        this.context = context;
        translatorFirst.downloadModelIfNeeded(conditionsFirst);
        translatorFirst.translate(display).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                display = s;
            }
        });
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

        Calendar check = Calendar.getInstance();
        check.setTime(messenger.getDate());

        int day = messenger.getDate().getDate();
        int month = messenger.getDate().getMonth();
        int year = messenger.getDate().getYear();

        SimpleDateFormat simpleDateFormat;
        if (day == now.getDate() && month == now.getMonth() && year == now.getYear()) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        } else if (check.get(Calendar.WEEK_OF_MONTH) == Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)) {
            simpleDateFormat = new SimpleDateFormat("E HH:mm");
        } else if (year == now.getYear()) {
            simpleDateFormat = new SimpleDateFormat("dd-MM HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        }

        if (messenger.getIdUser().equals(UserDAO.CURRENT_USER_ID)) {
            holder.userSend.setText(messenger.getText());
            holder.timeUserSend.setText(simpleDateFormat.format(messenger.getDate()));
            holder.layoutOtherSend.setVisibility(View.GONE);
        } else {
            holder.ortherSend.setText(messenger.getText());
            holder.warningTranslate.setVisibility(View.GONE);
            holder.timeOtherSend.setText(simpleDateFormat.format(messenger.getDate()));
            holder.layoutUserSend.setVisibility(View.GONE);

            LanguageIdentifier languageIdentifier =
                    LanguageIdentification.getClient();
            languageIdentifier.identifyLanguage(messenger.getText())
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@Nullable String languageCode) {
                                    if (languageCode.equals("und")) {
                                        Log.i("tag", "khong xac dinh");
                                    } else {
                                        String nowLang = Locale.getDefault().getLanguage();
                                        TranslatorOptions options =
                                                new TranslatorOptions.Builder()
                                                        .setSourceLanguage(languageCode)
                                                        .setTargetLanguage(nowLang)
                                                        .build();
                                        Translator translator = Translation.getClient(options);

                                        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
                                        translator.downloadModelIfNeeded(conditions);

                                        longClickToTranslate(holder.ortherSend, holder.warningTranslate, translator, messenger);

                                    }
                                }
                            });
        }

        setFadeAnimation(holder.constraintLayout);
    }

    private void longClickToTranslate(TextView view, TextView warning, Translator translator, Messenger messenger) {
        view.setOnLongClickListener(v -> {
            translator.translate(messenger.getText().toString())
                    .addOnSuccessListener(
                            new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    String dich = (String) o;
                                    if (view.getText().toString().equals(dich)) {
                                        warning.setVisibility(View.GONE);
                                        view.setText(messenger.getText().toString());
                                    } else {
                                        warning.setText(display);
                                        warning.setVisibility(View.VISIBLE);
                                        view.setText((String) o);
                                    }
                                    ChatActivity.recyclerView.scrollToPosition(0);
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Chưa tải xong ngôn ngữ", Toast.LENGTH_LONG).show();
                        }
                    });
            return false;
        });
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
        TextView ortherSend, userSend, timeOtherSend, timeUserSend, warningTranslate;
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
            warningTranslate = itemView.findViewById(R.id.warning_translate);
        }
    }
}
