package com.example.naprawpollubmobile;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EXPIRES = "expires_at";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_ID = "id";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }


    public void loginUser(String username, JSONObject response) throws JSONException, ParseException {
        String expiresAt = response.getString(KEY_EXPIRES);
        String userData = response.getString("user");
        String idFromResponse = userData.substring(6, 7);

        mEditor.putString(KEY_USERNAME, username);
        mEditor.putString(KEY_EXPIRES, expiresAt);
        mEditor.putString(KEY_ID, idFromResponse);

        mEditor.commit();
    }

    public boolean isLoggedIn() throws ParseException {
        Date currentDate = new Date();

        String expiration = mPreferences.getString(KEY_EXPIRES, KEY_EMPTY);

        if (expiration.isEmpty()) {
            return false;
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date dateExpireToken = format.parse(expiration);
        if (dateExpireToken.after(currentDate)) {
            return true;
        }
        else {
            return false;
        }
    }


    public User getUserDetails() throws ParseException {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setUsername(mPreferences.getString(KEY_USERNAME, KEY_EMPTY));

        String expiration = mPreferences.getString(KEY_EXPIRES, KEY_EMPTY);


        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date dateExpireToken = format.parse(expiration);

        user.setSessionExpiryDate(dateExpireToken);

        return user;
    }


    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

}