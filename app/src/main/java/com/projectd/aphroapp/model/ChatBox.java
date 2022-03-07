package com.projectd.aphroapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ChatBox implements Serializable {
    private String idRoom;
    private String idUser;
    private String nameUser;
    private boolean readed;
    private ArrayList<Messenger> messengers = new ArrayList<>();

    public ChatBox() {
    }

    public ChatBox(String idRoom, String idUser, String nameUser, boolean readed) {
        this.idRoom = idRoom;
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.readed = readed;
    }

    public ArrayList<Messenger> getMessengers() {
        return messengers;
    }

    public void setMessengers(ArrayList<Messenger> messengers) {
        this.messengers = messengers;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
