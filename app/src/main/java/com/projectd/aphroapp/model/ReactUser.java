package com.projectd.aphroapp.model;

public class ReactUser {
    private int orderNumber;
    private boolean gender;

    public ReactUser(int orderNumber, boolean gender) {
        this.orderNumber = orderNumber;
        this.gender = gender;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
}
