package com.example.naprawpollubmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class DefectListActivity extends AppCompatActivity {
    private boolean isLoaded;
    private String login_url;
    private SessionHandler session;
    private ProgressDialog pDialog;
    private ArrayList<Defect> defectsList = new ArrayList<Defect>();
    private ArrayList<DefectForList> defectsForList = new ArrayList<DefectForList>();
    List<Place> places = new ArrayList<Place>();
    List<Room> rooms = new ArrayList<Room>();
    List<Defect> defects = new ArrayList<Defect>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defect_list);
        login_url = "http://"+getString(R.string.ip)+":8000/api/v1/defects";
        isLoaded = false;
        listView = (ListView)findViewById(R.id.defectsList);

        session = new SessionHandler(getApplicationContext());

        try {
            if(session.isLoggedIn()){
                loadDefects(places, rooms);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getAllPlaces(List placesL) {

        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/places";
        Ion.with(DefectListActivity.this).load(URL).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                try {
                    for (int i = 0; i < result.size(); i++) {
                        JsonObject cli = result.get(i).getAsJsonObject();
                        placesL.add(new Place(cli.get("id").getAsInt(), cli.get("name").getAsString()));
                    }
                    if(isLoaded)
                    {
                        prepareArrayList();
                    }
                    else {
                        isLoaded = true;
                    }
                    //String id = result.get("id").getAsString();
                } catch (Exception erro) {
                    int dwadwa = 10;
                }
            }
        });

    }

    public void getAllRooms(List roomsL) {

        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/rooms";
        Ion.with(DefectListActivity.this).load(URL).asJsonArray().setCallback((e, result) -> {
            try {
                for (int i = 0; i < result.size(); i++) {
                    JsonObject cli = result.get(i).getAsJsonObject();
                    roomsL.add(new Room(cli.get("id").getAsInt(), cli.get("name").getAsString()));
                }
                if(isLoaded)
                {
                    prepareArrayList();
                }
                else {
                    isLoaded = true;
                }
            } catch (Exception erro) {

            }
        });

    }

    private void prepareArrayList() {
        for (int i = 0; i <defectsList.size(); i++){
            int placeId = Integer.parseInt(defectsList.get(i).idPlace);
            String t1 = places.get(placeId - 1).name;
            String t2 = "";
            int roomId;
            try {
                roomId = Integer.parseInt(defectsList.get(i).idRoom);
                 t2 = rooms.get(roomId - 1).name;
            }
            catch (NumberFormatException ex){
            }
            defectsForList.add(new DefectForList(t1, t2, defectsList.get(i).defectType, defectsList.get(i).description));  //defectsList.get(i).idPlace)
        }

        CustomAdapter myCustomAdapter = new CustomAdapter(DefectListActivity.this, defectsForList);
        listView.setAdapter(myCustomAdapter);
    }

    private void loadDefects(List places, List rooms) {
        displayLoader();
        JSONArray request = new JSONArray();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, login_url, request, response -> {
                    int a = 10;

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Defect>>(){}.getType();
                    try {
                        defectsList = gson.fromJson(response.toString(), type);

                        if(defectsList.size() > 0)
                        {
                            getAllPlaces(places);
                            getAllRooms(rooms);
                        }
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
