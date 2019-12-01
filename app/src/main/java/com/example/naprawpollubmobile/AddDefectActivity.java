package com.example.naprawpollubmobile;

import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddDefectActivity extends AppCompatActivity {

    private Spinner spinnerPlace;
    private Spinner spinnerType;
    private Spinner spinnerRoom;
    private EditText etContent;
    private Button btnAddDefect;
    private Button btnAddImage;
    private ImageView imageView;

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
    private Bitmap bitmap;
    private Uri fileUri;
    private File file;
    public static final String UPLOAD_URL = "http://192.168.1.116:8000/api/v1/images";
    public static final String UPLOAD_KEY = "Image";
    private static final int READ_REQUEST_CODE = 300;
    public static final String TAG = "MY MESSAGE";
    private int PICK_IMAGE_REQUEST = 1;


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

        //Buttons
        btnAddDefect = findViewById(R.id.btnAddDefect);
        btnAddImage = findViewById(R.id.btnAddImage);

        // Spinner element
        spinnerPlace = findViewById(R.id.spinnerPlace);
        spinnerRoom = findViewById(R.id.spinnerRoom);
        spinnerType = findViewById(R.id.spinnerType);

        //ImageView
        imageView = findViewById(R.id.imageView);

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
                content = etContent.getText().toString();
                addDefect();
                uploadImage();
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EasyPermissions.hasPermissions(AddDefectActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showFileChooser();
                } else {
                    //If permission is not present request for the same.
                    //EasyPermissions.requestPermissions(AddDefectActivity.this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
        });


    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            fileUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddDefectActivity.this, "Uploading Image", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("idDefect", "2");

                String result = rh.sendPostRequest(UPLOAD_URL, data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
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

    public void getUserId() {

        String URL = "http://192.168.1.116:8000/api/v1/users/" + username + "/login";
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
        String URL = "http://192.168.1.116:8000/api/v1/defects";
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
        String URL = "http://192.168.1.116:8000/api/v1/markers/" + idString + "/place";
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

