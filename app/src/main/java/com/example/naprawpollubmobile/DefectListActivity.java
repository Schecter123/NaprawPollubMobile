package com.example.naprawpollubmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class DefectListActivity extends AppCompatActivity {
    private String login_url = "http://192.168.0.10:8000/api/v1/defects";
    private SessionHandler session;
    private ProgressDialog pDialog;
    private ArrayList<Defect> defectsList = new ArrayList<Defect>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect_list);
        login_url = "http://"+getString(R.string.ip)+":8000/api/v1/defects";

        session = new SessionHandler(getApplicationContext());

        try {
            if(session.isLoggedIn()){
                loadDefects();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadDefects() {
        displayLoader();
        JSONArray request = new JSONArray();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, login_url, request, response -> {
                    int a = 10;

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Defect>>(){}.getType();
                    try {
                        defectsList = gson.fromJson(response.toString(), type);
                    }
                    catch (Exception e){
                        int bb = 10;
                    }
                    int b = 10;

                    pDialog.dismiss();
                }, error -> {
                    pDialog.dismiss();

                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();

                });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(DefectListActivity.this);
        pDialog.setMessage("Logowanie proszę czekać");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}
