package com.example.adrian.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.myapplication.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private ArrayList<String> listItem;
    private ArrayList<Integer> listOfId;
    private ArrayAdapter adapter;
    private ListView listView;
    private TextView totalAmountTV;
    private Button deleteBtn;
    private Button offlinePaymentBtn;
    private float fullPrice;
    public static final String EXTRA_MESSAGE = "com.example.adrian.myapplication";
    //private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        listItem = new ArrayList<>();
        listOfId = new ArrayList<>();
        listView = findViewById(R.id.listView);
        totalAmountTV = findViewById(R.id.totalAmountTV);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listItem);
        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setEnabled(false);
        readFromDB();
        deleteProduct();
        offlinePaymentBtn = findViewById(R.id.offlinePaymentBtn);
        offlinePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRActivity();
            }
        });
    }

    private void readFromDB() {
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        String query = "Select * from " + DBContract.Product.TABLE_NAME;
        final Cursor cursor = db.rawQuery(query, null);
        float pricePerOne;
        fullPrice = 0;
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Koszyk pusty", Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                pricePerOne = Float.parseFloat(cursor.getString(3)) * Float.parseFloat(cursor.getString(4));
                fullPrice += pricePerOne;
                listItem.add(cursor.getString(2) + "      " + cursor.getString(4)
                        + " * " + cursor.getString(3) + "  =  " + String.format("%.2f", pricePerOne));
                listOfId.add(cursor.getInt(0));
            }
            displayProducts();
        }
        totalAmountTV.setText("DO ZAPŁATY: " + String.format("%.2f", fullPrice) + " PLN");
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    private void displayProducts() {
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteBtn.setEnabled(true);
                //adapter.remove(adapter.getItem(position));
                //readFromDB();
                //SparseBooleanArray sp = listView.getCheckedItemPositions();
            }
        });

        //deleteBtn.setEnabled(false);

    }

    private void deleteProduct() {
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SparseBooleanArray checkedItem = listView.getCheckedItemPositions();
                int itemCount = listView.getCount();
                for (int i = 0; i < itemCount; i++) {
                    if (checkedItem.get(i)) {
                        adapter.remove(listItem.get(i));
                    }

                    adapter.notifyDataSetChanged();
                }*/
                int checked = listView.getCheckedItemPosition();
                //adapter.remove(adapter.getItem(checked));
                //deleteRow(listItem.indexOf(adapter.getItem(checked)));
                deleteRow(listOfId.get(checked));
                listView.setItemChecked(checked, false);
                deleteBtn.setEnabled(false);
                listItem.clear();
                listOfId.clear();
                readFromDB();
            }
        });
    }

    private void deleteRow(int id) {
        //id = id + 1;
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        String query = "DELETE FROM " + DBContract.Product.TABLE_NAME + " WHERE " + DBContract.Product._ID +
                " = '" + id + "'";
        db.execSQL(query);
    }

    private void startQRActivity() {
        Intent intent = new Intent(this, QRCodePayment.class);
        String message = String.format("%.2f", fullPrice);
        intent.putExtra(EXTRA_MESSAGE, message);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);
    }
}
