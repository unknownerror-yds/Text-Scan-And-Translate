package com.example.textscanandtranslate.feature.prevscantext;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textandtranslate.R;
import com.example.textscanandtranslate.feature.db.DataBaseHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import com.example.textscanandtranslate.feature.db.TextPojo;

public class DataRecordPast extends AppCompatActivity {
    private DataBaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private TextEntryAdapter adapter;

    private MaterialToolbar toolbar;
    private TextView emptytextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_record_past);

        toolbar = findViewById(R.id.topAppBar);
        databaseHelper = new DataBaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptytextview = findViewById(R.id.emptyTextView);

        List<TextPojo> textEntries = getLast10TextEntries();
        adapter = new TextEntryAdapter(textEntries,getApplicationContext());
        recyclerView.setAdapter(adapter);

        if(textEntries.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptytextview.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            emptytextview.setVisibility(View.GONE);
        }
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.themeChange) {
                // Replace this with your button click action
                // For example, open a new activity or show a dialog
                // You can also handle the button click here directly.
                // Add your logic here.
                // Toggle between light and dark themes

                return true;
            }
            if(item.getItemId() == R.id.deleteAll){
                if(textEntries.size() == 0){

                }
                else{
                    clearAllTextEntries();
                    Toast toast = Toast.makeText(getApplicationContext(),"Previous DataBase Deleted",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            return false;
        });
    }

    private List<TextPojo> getLast10TextEntries() {
        List<TextPojo> textEntries = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query the database for the last 10 text entries
        Cursor cursor = db.query(
                DataBaseHelper.TABLE_NAME,
                null, // columns
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                DataBaseHelper.COLUMN_ID + " DESC", // orderBy - sort by ID in descending order
                "10" // limit - retrieve only 10 entries
        );

        while (cursor.moveToNext()) {
            @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.COLUMN_ID));
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_TEXT));
            @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_TIMESTAMP));
            TextPojo entry = new TextPojo(id, text,timestamp);
            textEntries.add(entry);
        }

        cursor.close();
        db.close();

        return textEntries;
    }

    private void clearAllTextEntries() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(DataBaseHelper.TABLE_NAME, null, null); // Delete all rows in the table
        db.close();

        // Update the RecyclerView by clearing the adapter
        adapter.clear();
    }

}
