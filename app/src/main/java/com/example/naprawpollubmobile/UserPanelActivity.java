package com.example.naprawpollubmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;

public class UserPanelActivity extends AppCompatActivity {

    private SessionHandler session;
    private String username;
    private int idUser;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        session = new SessionHandler(getApplicationContext());
        User user = null;
        try {
            user = session.getUserDetails();
            username = user.getUsername();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Button deleteBtn = findViewById(R.id.btnDelete);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserPanelActivity.this);

                builder.setMessage("Czy jesteś pewien, że chcesz usunąć konto ?")
                        .setCancelable(false)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getUserId();

                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });

        Button logoutBtn = findViewById(R.id.btnLogout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent i = new Intent(UserPanelActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button passwordBtn = findViewById(R.id.btnPassword);

        passwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent i = new Intent(UserPanelActivity.this, ChangePassword.class);
                startActivity(i);
            }
        });

    }


    private void delete(int idUser) {
        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/users/" + idUser;
        Ion.with(UserPanelActivity.this).load("DELETE", URL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try {
                    session.logoutUser();
                    Intent z = new Intent(UserPanelActivity.this, MainActivity.class);
                    startActivity(z);
                    finish();
                } catch (Exception erro) {

                }
            }
        });
    }


    public void getUserId() {

        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/users/" + username + "/login";
        Ion.with(UserPanelActivity.this).load(URL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try {
                    idUser = result.get("id").getAsInt();
                    System.out.println(idUser);
                    delete(idUser);
                } catch (Exception erro) {

                }
            }
        });
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(UserPanelActivity.this);
        pDialog.setMessage("Zmieniam hasło");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }
}
