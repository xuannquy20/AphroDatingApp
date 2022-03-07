package com.projectd.aphroapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectd.aphroapp.dao.UserDAO;
import com.projectd.aphroapp.model.ChatBox;


public class ChatListFragment extends Fragment {
    RecyclerView recyclerView;
    public static ChatListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.chat_list);
        if (adapter == null) {
            adapter = new ChatListAdapter(getActivity(), UserDAO.listChat);
        }
        if (recyclerView.getAdapter() == null && UserDAO.listChat.size() > 0) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

//    public static void swapItems(int itemAIndex) {
//        ChatBox item = ChatListAdapter.wordList.get(itemAIndex);
//        ChatListAdapter.wordList.remove(item);
//        ChatListAdapter.wordList.push(item);
//
//        for(int i = 0; i<ChatListAdapter.wordList.size(); i++){
//            adapter.notifyItemChanged(i);
//        }
//    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

}