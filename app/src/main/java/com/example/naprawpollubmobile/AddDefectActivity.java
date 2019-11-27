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

import java.text.ParseException;
import java.util.ArrayList;
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
    private String room;
    private String type;
    private String content;
    private String add_defect_url = "http://192.168.1.116:8000/api/v1/defects/";
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_defect);

        session = new SessionHandler(getApplicationContext());


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

                // Showing selected spinner item
                Toast.makeText(adapterView.getContext(), "Selected: " + place, Toast.LENGTH_LONG).show();
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
                room = adapterView.getItemAtPosition(i).toString();

                // Showing selected spinner item
                Toast.makeText(adapterView.getContext(), "Selected: " + type, Toast.LENGTH_LONG).show();

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

                // Showing selected spinner item
                Toast.makeText(adapterView.getContext(), "Selected: " + type, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnAddDefect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //tvTest.setText(place + "," + type + ",");
            }
        });


    }


    public void setSpinnerType(List type) {
        type.add("Komputery");
        type.add("Elektroniczny");
        type.add("Hydrauliczny");
        type.add("Infrastruktora");
        type.add("Mechaniczny");
        type.add("Inny");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(dataAdapter2);
    }

    public void getAllPlacesForSpinner(List place) {

        String URL = "http://192.168.1.116:8000/api/v1/places";
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
        String URL = "http://192.168.1.116:8000/api/v1/rooms/" + idString + "/place";
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


}
