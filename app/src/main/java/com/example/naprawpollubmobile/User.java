package com.example.naprawpollubmobile;

import java.util.Date;


public class User {
    String username;
    Date sessionExpiryDate;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

    public String getUsername() {
        return username;
    }


    public Date getSessionExpiryDate() {
        return sessionExpiryDate;
    }
}
