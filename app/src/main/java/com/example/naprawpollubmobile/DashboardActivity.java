package com.example.naprawpollubmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.text.ParseException;

public class DashboardActivity extends AppCompatActivity {
    private SessionHandler session;
    SpeedDialView speedDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        speedDialView = findViewById(R.id.speedDial);

        session = new SessionHandler(getApplicationContext());
        User user = null;
        try {
            user = session.getUserDetails();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Witaj użytkowniku " + user.getUsername() + " !");

        TextView welcomeText1 = findViewById(R.id.welcomeText1);
        welcomeText1.setText("Z twoją pomocą nasza uczelnia stanie się jeszcze lepsza.");


        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.user_link, R.drawable.ic_user_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBrown, getTheme())).setLabel("Panel użytkownika").create());

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.defect_link, R.drawable.ic_add_white_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_primary, getTheme())).setLabel("Dodaj usterkę").create());

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.map_link, R.drawable.ic_map_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorGreen, getTheme())).setLabel("Mapa usterek").create());


        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.userList_link, R.drawable.ic_list_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorYellow, getTheme())).setLabel("Usterki użytkownika").create());

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.list_link, R.drawable.ic_list_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorOrange, getTheme())).setLabel("Wszystkie usterki").create());


        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.user_link:
                        Intent i = new Intent(DashboardActivity.this, UserPanelActivity.class);
                        startActivity(i);
                        return false;

                    case R.id.defect_link:
                        i = new Intent(DashboardActivity.this, AddDefectActivity.class);
                        startActivity(i);
                        return false;

                    case R.id.map_link:
                        i = new Intent(DashboardActivity.this, MarkerMapActivity.class);
                        startActivity(i);
                        return false;

                    case R.id.userList_link:
                        i = new Intent(DashboardActivity.this, UserDefectListActivity.class);
                        startActivity(i);
                        return false;

                    case R.id.list_link:
                        i = new Intent(DashboardActivity.this, DefectListActivity.class);
                        startActivity(i);
                        return false;

                    default:
                        return false;

                }
            }
        });
    }

}