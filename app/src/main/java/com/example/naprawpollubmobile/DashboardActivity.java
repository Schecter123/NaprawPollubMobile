package com.example.naprawpollubmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;

public class DashboardActivity extends AppCompatActivity {
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        session = new SessionHandler(getApplicationContext());
        User user = null;
        try {
            user = session.getUserDetails();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView welcomeText = findViewById(R.id.welcomeText);

        welcomeText.setText("Witaj "+user.getUsername()+", twoja sesja wygaśnie: "+user.getSessionExpiryDate());

        Button logoutBtn = findViewById(R.id.btnLogout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent i = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button listBtn =  findViewById(R.id.btnList);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, DefectListActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}