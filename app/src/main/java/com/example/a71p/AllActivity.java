package com.example.a71p;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AllActivity extends AppCompatActivity {

    private ArrayList<String> entriesList;
    private ArrayAdapter<String> adapter;
    private LAFDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        ListView listViewEntries = findViewById(R.id.listViewEntries);
        entriesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entriesList);
        listViewEntries.setAdapter(adapter);

        dbHelper = new LAFDB(this);
        displayEntries();

        listViewEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetails(entriesList.get(position));
            }
        });

        listViewEntries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteEntry(entriesList.get(position));
                return true;
            }
        });
    }

    private void displayEntries() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                LAFDB.COLUMN_TYPE,
                LAFDB.COLUMN_NAME
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

        entriesList.clear();
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_TYPE));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(LAFDB.COLUMN_NAME));
            entriesList.add(type + ": " + name);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    private void showDetails(String entry) {
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("entry", entry);
        startActivity(intent);
    }

    private void deleteEntry(final String entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        String selection = LAFDB.COLUMN_TYPE + "||' - '||" + LAFDB.COLUMN_NAME + "=?";
                        String[] selectionArgs = {entry};
                        int deletedRows = db.delete(LAFDB.TABLE_LAF, selection, selectionArgs);
                        if (deletedRows > 0) {
                            Toast.makeText(AllActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                            displayEntries();
                        } else {
                            Toast.makeText(AllActivity.this, "Failed to delete entry", Toast.LENGTH_SHORT).show();
                            Log.e("Delete Entry", "Failed to delete entry: " + entry);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
