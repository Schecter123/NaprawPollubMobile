package com.example.naprawpollubmobile;

import java.util.Date;


public class User {
    String id;
    String username;
    String email;
    Date sessionExpiryDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
