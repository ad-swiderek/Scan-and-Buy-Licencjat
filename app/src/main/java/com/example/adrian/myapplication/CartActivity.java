package com.example.adrian.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.myapplication.databinding.ActivityCartBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private ArrayList<String> listItem;
    private ArrayAdapter adapter;
    private ListView listView;
    private TextView totalAmountTV;
    float fullPrice;
    public static final String EXTRA_MESSAGE = "com.example.adrian.myapplication";
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        listItem = new ArrayList<>();
        listView = findViewById(R.id.listView);
        totalAmountTV = findViewById(R.id.totalAmountTV);
        readFromDB();
        Button offlinePaymentBtn = findViewById(R.id.offlinePaymentBtn);
        offlinePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRActivity();

            }
        });
    }

    private void readFromDB() {
        db = new DBHelper(this).getReadableDatabase();
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
                listItem.add(cursor.getString(2) + " | sztuk:  " + cursor.getString(4)
                        + " | łączna cena: " + String.format("%.2f", pricePerOne) + " (" + cursor.getString(3) + " za szt.)");
            }
            displayProducts();
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    private void displayProducts() {
        totalAmountTV.setText("DO ZAPŁATY: " + String.format("%.2f", fullPrice) + " PLN");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listItem);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //adapter.remove(adapter.getItem(position));
                //readFromDB();
                //SparseBooleanArray sp = listView.getCheckedItemPositions();
            }
        });*/

        Button deleteBtn = findViewById(R.id.deleteBtn);
        //deleteBtn.setEnabled(false);

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
                adapter.remove(adapter.getItem(checked));
            }
        });
    }

    private void deleteProduct() {
        //SparseBooleanArray sp = getListView
    }

    private void startQRActivity() {
        Intent intent = new Intent(this, QRCodePayment.class);
        String message = String.format("%.2f", fullPrice);
        intent.putExtra(EXTRA_MESSAGE, message);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);
    }

    /*private boolean deleteRow(String barcode){
        return db.delete(DBContract.Product.TABLE_NAME, DBContract.Product.COLUMN_BARCODE_NUMBER + "=" + );
    }*/
}
