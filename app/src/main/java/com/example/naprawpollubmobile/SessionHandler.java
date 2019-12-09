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
    private static final String KEY_IDUSER = "idUser";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
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


    public void loginUser(String id, String username, String email, JSONObject response) throws JSONException, ParseException {
        String expiresAt = response.getString(KEY_EXPIRES);
        String userData = response.getString("user");
        String idFromResponse = stripNonDigits(userData);

        mEditor.putString(KEY_IDUSER, id);
        mEditor.putString(KEY_USERNAME, username);
        mEditor.putString(KEY_EMAIL, email);
        mEditor.putString(KEY_EXPIRES, expiresAt);
        mEditor.putString(KEY_ID, idFromResponse);

        mEditor.commit();
    }

    public static String stripNonDigits(final CharSequence input) {
        final StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
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
        } else {
            return false;
        }
    }


    public User getUserDetails() throws ParseException {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setId(mPreferences.getString(KEY_IDUSER, KEY_EMPTY));
        user.setUsername(mPreferences.getString(KEY_USERNAME, KEY_EMPTY));
        user.setEmail(mPreferences.getString(KEY_EMAIL, KEY_EMPTY));


        String expiration = mPreferences.getString(KEY_EXPIRES, KEY_EMPTY);


        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date dateExpireToken = format.parse(expiration);

        user.setSessionExpiryDate(dateExpireToken);

        return user;
    }


    public void logoutUser() {
        mEditor.clear();
        mEditor.commit();
    }

}