package com.example.adrian.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adrian.myapplication.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    ArrayList<String> listItem;
    ArrayList<String> product;
    ArrayAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        listItem = new ArrayList<>();
        listView = findViewById(R.id.listView);

        readFromDB();
    }

    private void readFromDB() {
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        String query = "Select * from " + DBContract.Product.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        float pricePerOne;
        float fullPrice = 0;
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Koszyk pusty", Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                pricePerOne = Float.parseFloat(cursor.getString(3)) * Float.parseFloat(cursor.getString(4));
                fullPrice += pricePerOne;
                listItem.add(cursor.getString(2) + " | sztuk:  " + cursor.getString(4)
                        + " | łączna cena: " + pricePerOne + " (" + cursor.getString(3) + " za szt.)");
            }
            listItem.add("DO ZAPŁATY: " + fullPrice);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItem);
            listView.setAdapter(adapter);
        }
    }
}
