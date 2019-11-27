package com.example.naprawpollubmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
                (Request.Method.GET, login_url, request, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                    }


                }, new Response.ErrorListener() {

                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
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
