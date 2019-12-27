package com.example.naprawpollubmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;

public class ChangePassword extends AppCompatActivity {


    private SessionHandler session;
    private ProgressDialog pDialog;

    String username;
    int idUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText oldPassword = findViewById(R.id.oldPassword);
        EditText newPassword = findViewById(R.id.newPassword);
        Button changePassBtn = findViewById(R.id.changePassBtn);

       session = new SessionHandler(getApplicationContext());
        User user = null;
        try {
            user = session.getUserDetails();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        username = user.getUsername();



        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserId(oldPassword, newPassword);
            }
        });

    }

    private void changePassword(String oldPassword, String newPassword, int idUser) {
        displayLoader();

        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/users/" + idUser + "/password/update";
        Ion.with(ChangePassword.this).load("PUT", URL)
                .setBodyParameter("oldPassword", String.valueOf(oldPassword))
                .setBodyParameter("newPassword", String.valueOf(newPassword))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            Toast.makeText(ChangePassword.this, "Zmieniono hasło", Toast.LENGTH_LONG).show();


                        } catch (Exception erro) {

                        }
                    }
                });
    }


    public void getUserId(EditText oldPassword, EditText newPassword) {

        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/users/" + username + "/login";
        Ion.with(ChangePassword.this).load(URL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try {
                    idUser = result.get("id").getAsInt();
                    changePassword(oldPassword.getText().toString(), newPassword.getText().toString(), idUser);
                } catch (Exception erro) {

                }
            }
        });
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(ChangePassword.this);
        pDialog.setMessage("Zmieniam hasło");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }
}
