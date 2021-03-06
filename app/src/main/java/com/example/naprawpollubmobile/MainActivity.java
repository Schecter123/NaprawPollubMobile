package com.example.naprawpollubmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String EXPIRES_AT = "expires_at";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_ID = "idUser";
    private static final String KEY_USERNAME = "login";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String REMEMBER_ME = "remember_me";
    private static final String KEY_EMPTY = "";
    private EditText etUsername;
    private EditText etPassword;
    private String id;
    private String username;
    private String email;

    private String password;
    private ProgressDialog pDialog;
    private String login_url;
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        login_url = "http://" + getString(R.string.ip) + ":8000/api/auth/login/";
        try {
            if (session.isLoggedIn()) {
                loadDashboard();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);

        Button register = findViewById(R.id.btnLoginRegister);
        Button login = findViewById(R.id.btnLogin);

        register.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        login.setOnClickListener(v -> {
            username = etUsername.getText().toString().toLowerCase().trim();
            password = etPassword.getText().toString().trim();
            if (validateInputs()) {
                login();
            }
        });
    }

    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(i);
        finish();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logowanie proszę czekać");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void login() {
        displayLoader();
        getUserId();
        JSONObject request = new JSONObject();
        try {
            request.put(KEY_ID, id);
            request.put(KEY_USERNAME, username);
            request.put(KEY_EMAIL, email);
            request.put(KEY_PASSWORD, password);
            request.put(REMEMBER_ME, false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, response -> {
                    pDialog.dismiss();
                    try {
                        String expiresAt = response.getString(EXPIRES_AT);
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        Date dateExpireToken = format.parse(expiresAt);
                        Date dateNow = new Date();
                        if (dateExpireToken.after(dateNow)) {
                            session.loginUser(id, username, email, response);
                            loadDashboard();

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    pDialog.dismiss();

                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();

                });

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);

    }

    private boolean validateInputs() {
        if (KEY_EMPTY.equals(username)) {
            etUsername.setError("Nazwa użytkownika nie poprawna");
            etUsername.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
            etPassword.setError("Hasło niepoprawne");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    public void getUserId() {

        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/users/" + username + "/login";
        Ion.with(MainActivity.this).load(URL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try {
                    id = result.get("id").getAsString();
                    email = result.get("email").getAsString();
                } catch (Exception erro) {

                }
            }
        });


    }
}
