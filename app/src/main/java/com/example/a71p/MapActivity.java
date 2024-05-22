package com.example.a71p;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LAFDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbHelper = new LAFDB(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadLostAndFoundItems();
    }

    private void loadLostAndFoundItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LAFDB.COLUMN_TYPE,
                LAFDB.COLUMN_NAME,
                LAFDB.COLUMN_LATITUDE,
                LAFDB.COLUMN_LONGITUDE
        };

        Cursor cursor = db.query(
                LAFDB.TABLE_LAF,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_TYPE));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_NAME));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_LONGITUDE));

            LatLng location = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(location).title(type + ": " + name));
        }
        cursor.close();

        // Move the camera to the first item if available
        if (cursor.moveToFirst()) {
            double firstLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_LATITUDE));
            double firstLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_LONGITUDE));
            LatLng firstLocation = new LatLng(firstLatitude, firstLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
        }
    }
}
