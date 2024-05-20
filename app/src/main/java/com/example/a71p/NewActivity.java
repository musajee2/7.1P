package com.example.a71p;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NewActivity extends AppCompatActivity {

    private RadioGroup radioGroupType;
    private EditText editTextName;
    private EditText editTextPhone;
    private TextInputEditText editTextDesc;
    private TextView textViewWordCount;
    private EditText editTextDate;
    private TextView textViewLocation;
    private Button buttonSave;

    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;

    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        radioGroupType = findViewById(R.id.radioGroupType);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDesc = findViewById(R.id.editTextDesc);
        textViewWordCount = findViewById(R.id.textViewWordCount);
        editTextDate = findViewById(R.id.editTextDate);
        textViewLocation = findViewById(R.id.textViewLocation);
        buttonSave = findViewById(R.id.buttonSave);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Places.initialize(getApplicationContext(), "AIzaSyBpVq0VKrN3odVXiCvZ200i0qBVWkJMytw");
        PlacesClient placesClient = Places.createClient(this);

        editTextDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewWordCount.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        editTextDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewActivity.this, (view, year, month, dayOfMonth) -> {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                editTextDate.setText(selectedDate);
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        textViewLocation.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(NewActivity.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        buttonSave.setOnClickListener(v -> {
            String type = ((RadioButton) findViewById(radioGroupType.getCheckedRadioButtonId())).getText().toString();
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String desc = editTextDesc.getText().toString();
            String date = editTextDate.getText().toString();
            String location = textViewLocation.getText().toString();

            if (name.isEmpty() || phone.isEmpty() || desc.isEmpty() || date.isEmpty() || location.isEmpty()) {
                Toast.makeText(NewActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            saveDataToDatabase(type, name, phone, desc, date, location, selectedLatitude, selectedLongitude);
        });

        requestLocationPermission();
    }

    private void saveDataToDatabase(String type, String name, String phone, String desc, String date, String location, double latitude, double longitude) {
        LAFDB dbHelper = new LAFDB(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LAFDB.COLUMN_TYPE, type);
        values.put(LAFDB.COLUMN_NAME, name);
        values.put(LAFDB.COLUMN_PHONE, phone);
        values.put(LAFDB.COLUMN_DESC, desc);
        values.put(LAFDB.COLUMN_DATE, date);
        values.put(LAFDB.COLUMN_LOCATION, location);
        values.put(LAFDB.COLUMN_LATITUDE, latitude);
        values.put(LAFDB.COLUMN_LONGITUDE, longitude);

        long newRowId = db.insert(LAFDB.TABLE_LAF, null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can access location here
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                textViewLocation.setText(place.getName());
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    selectedLatitude = latLng.latitude;
                    selectedLongitude = latLng.longitude;
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("NewActivity", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
