package com.example.a71p;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SliderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_view);

        String entry = getIntent().getStringExtra("entry");
        String[] entryParts = entry.split(": ");

        String type = entryParts[0];
        String name = entryParts[1];

        // Fetch all details of the entry from the database
        String details = getEntryDetails(type, name);

        TextView textViewDetails = findViewById(R.id.textViewDetails);
        textViewDetails.setText(details);
    }

    private String getEntryDetails(String type, String name) {
        StringBuilder detailsBuilder = new StringBuilder();

        LAFDB dbHelper = new LAFDB(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LAFDB.COLUMN_NAME,
                LAFDB.COLUMN_PHONE,
                LAFDB.COLUMN_DESC,
                LAFDB.COLUMN_DATE,
                LAFDB.COLUMN_LOCATION
        };

        String selection = LAFDB.COLUMN_TYPE + " = ? AND " + LAFDB.COLUMN_NAME + " = ?";
        String[] selectionArgs = {type, name};

        Cursor cursor = db.query(
                LAFDB.TABLE_LAF,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String entryName = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_PHONE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_DESC));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_DATE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_LOCATION));

            detailsBuilder.append("Name: ").append(entryName).append("\n");
            detailsBuilder.append("Phone: ").append(phone).append("\n");
            detailsBuilder.append("Description: ").append(desc).append("\n");
            detailsBuilder.append("Date: ").append(date).append("\n");
            detailsBuilder.append("Location: ").append(location);
        }

        cursor.close();
        return detailsBuilder.toString();
    }
}