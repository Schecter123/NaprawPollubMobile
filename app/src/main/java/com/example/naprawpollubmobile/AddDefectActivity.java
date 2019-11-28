package com.example.naprawpollubmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddDefectActivity extends AppCompatActivity {

    private Spinner spinnerPlace;
    private Spinner spinnerType;
    private Spinner spinnerRoom;
    private EditText etContent;
    private TextView tvTest;
    private Button btnAddDefect;

    private String place;
    private int idPlace;
    private String username;
    private int idUser;
    private String room;
    private int idRoom;
    private String type;
    private int defectTypeId;
    private int idMarker;
    private String content;
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_defect);

        session = new SessionHandler(getApplicationContext());
        User user = null;
        try {
            user = session.getUserDetails();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        username = user.getUsername();


        //EditText
        etContent = findViewById(R.id.etContent);

        //TextView
        tvTest = findViewById(R.id.tvTest);


        //Button
        btnAddDefect = findViewById(R.id.btnAddDefect);

        // Spinner element
        spinnerPlace = findViewById(R.id.spinnerPlace);
        spinnerRoom = findViewById(R.id.spinnerRoom);
        spinnerType = findViewById(R.id.spinnerType);

        List<String> placeSp = new ArrayList<String>();
        getAllPlacesForSpinner(placeSp);

        List<String> roomSp = new ArrayList<String>();
        getAllRoomsForSpinner(roomSp, idPlace);

        List<String> typeSp = new ArrayList<String>();
        setSpinnerType(typeSp);

        // Spinner click listener
        spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                place = ((Place) spinnerPlace.getSelectedItem()).getName();
                idPlace = ((Place) spinnerPlace.getSelectedItem()).getId();

                getIdMarker();
                getAllRoomsForSpinner(roomSp, idPlace);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                room = ((Room) spinnerRoom.getSelectedItem()).getName();
                idRoom = ((Room) spinnerRoom.getSelectedItem()).getId();

                roomSp.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                type = adapterView.getItemAtPosition(i).toString();
                defectTypeId = i + 1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                defectTypeId = 1;

            }
        });

        getUserId();


        btnAddDefect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTest.setVisibility(View.VISIBLE);
                content = etContent.getText().toString();
                addDefect();
            }
        });


    }

    public void setSpinnerType(List type) {
        type.add("Komputery");
        type.add("Infrastruktora");
        type.add("Elektroniczny");
        type.add("Hydrauliczny");
        type.add("Mechaniczny");
        type.add("Inny");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(dataAdapter2);
    }

    public void getAllPlacesForSpinner(List place) {

        String URL = "http://87.246.222.194:8000/api/v1/places";
        Ion.with(AddDefectActivity.this).load(URL).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                try {
                    for (int i = 0; i < result.size(); i++) {
                        JsonObject cli = result.get(i).getAsJsonObject();
                        place.add(new Place(cli.get("id").getAsInt(), cli.get("name").getAsString()));
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddDefectActivity.this, android.R.layout.simple_spinner_item, place);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPlace.setAdapter(dataAdapter);


                    }
                    //String id = result.get("id").getAsString();
                } catch (Exception erro) {

                }
            }
        });

    }

    public void getAllRoomsForSpinner(List room, int id) {

        String idString = Integer.toString(id);
        String URL = "http://87.246.222.194:8000/api/v1/rooms/" + idString + "/place";
        Ion.with(AddDefectActivity.this).load(URL).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                try {
                    for (int i = 0; i < result.size(); i++) {
                        JsonObject cli = result.get(i).getAsJsonObject();
                        room.add(new Room(cli.get("id").getAsInt(), cli.get("name").getAsString()));
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddDefectActivity.this, android.R.layout.simple_spinner_item, room);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRoom.setAdapter(dataAdapter);
                        System.out.println(result);

                    }
                    //String id = result.get("id").getAsString();
                } catch (Exception erro) {

                }
            }
        });

    }

    public void getUserId() {

        String URL = "http://87.246.222.194:8000/api/v1/users/" + username + "/login";
        Ion.with(AddDefectActivity.this).load(URL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try {
                    idUser = result.get("id").getAsInt();
                } catch (Exception erro) {

                }
            }
        });


    }

    public void addDefect() {
        String URL = "http://87.246.222.194:8000/api/v1/defects";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setTime(timestamp.getTime() + (((60 * 60) + 0) * 1000));


        System.out.println(idMarker);
        Ion.with(AddDefectActivity.this)
                .load("POST", URL)
                .setBodyParameter("defectType", String.valueOf(defectTypeId))
                .setBodyParameter("idPlace", String.valueOf(idPlace))
                .setBodyParameter("idUser", String.valueOf(idUser))
                .setBodyParameter("idRoom", String.valueOf(idRoom))
                .setBodyParameter("idMarker", String.valueOf(idMarker))
                .setBodyParameter("defectState", "ForRepair")
                .setBodyParameter("description", content)
                .setBodyParameter("date", String.valueOf(timestamp))
                .setBodyParameter("photoURL", "")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            Toast.makeText(AddDefectActivity.this, "Hurra", Toast.LENGTH_LONG).show();

                        } catch (Exception erro) {
                            Toast.makeText(AddDefectActivity.this, "Nie Hurra", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void getIdMarker() {

        String idString = Integer.toString(idPlace);
        String URL = "http://87.246.222.194:8000/api/v1/markers/" + idString + "/place";
        Ion.with(AddDefectActivity.this).load(URL).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                try {
                    JsonObject cli = result.get(0).getAsJsonObject();
                    idMarker = cli.get("id").getAsInt();

                    //String id = result.get("id").getAsString();
                } catch (Exception erro) {

                }
            }
        });

    }
}

