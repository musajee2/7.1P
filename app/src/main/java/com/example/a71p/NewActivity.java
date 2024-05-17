package com.example.a71p;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

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

            saveDataToDatabase(type, name, phone, desc, date, location);
        });

        requestLocationPermission();
        getCurrentLocation();
    }

    private void saveDataToDatabase(String type, String name, String phone, String desc, String date, String location) {
        LAFDB dbHelper = new LAFDB(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LAFDB.COLUMN_TYPE, type);
        values.put(LAFDB.COLUMN_NAME, name);
        values.put(LAFDB.COLUMN_PHONE, phone);
        values.put(LAFDB.COLUMN_DESC, desc);
        values.put(LAFDB.COLUMN_DATE, date);
        values.put(LAFDB.COLUMN_LOCATION, location);

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

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    textViewLocation.setText(location.getLatitude() + ", " + location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onProviderDisabled(@NonNull String provider) {}
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
