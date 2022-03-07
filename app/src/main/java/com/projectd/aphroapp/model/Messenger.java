package com.projectd.aphroapp.model;

import java.io.Serializable;
import java.util.Date;

public class Messenger implements Serializable {
    private String idUser, text;
    private Date date;

    public Messenger() {
    }

    public Messenger(String idUser, String text) {
        this.idUser = idUser;
        this.text = text;
    }

    public Messenger(String idUser, String text, Date date) {
        this.idUser = idUser;
        this.text = text;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
