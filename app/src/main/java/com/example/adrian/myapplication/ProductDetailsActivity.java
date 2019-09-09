package com.example.adrian.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adrian.myapplication.databinding.ActivityProductDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetailsActivity extends AppCompatActivity {

    private ActivityProductDetailsBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseProducts = database.getReference("products");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReferenceFromUrl("gs://scanandbuy-53a52.appspot.com");
    private static final String TAG = "ProductDetailsActivity";
    private String message;
    private ProductClass productClass = new ProductClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); //przypisanie kodu kreskowego odebranego z MainActivity

        readFromFirebase(); //odczytanie danych z bazy w chmurze

        TextView textView = findViewById(R.id.barcodeTextView);
        textView.setText(message);

        Button scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() { //przypisanie akcji powrót do funkcji skanowania po naciśnięciu przycisku
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button addToCartBtn = findViewById(R.id.addToCartBtn);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //przypisanie akcji dodawania produktu do koszyka po naciśnięciu przycisku
                if (productClass.getQuantity() == null) {
                    showToastMessage("Nie odnaleziono produktu, sprawdź połączenie z internetem" +
                            " i zeskanuj ponownie!");
                } else {
                    saveToCart();
                }
            }
        });

        Button showCartBtn = findViewById(R.id.showCartBtn); //przypisanie akcji przejścia do okna z koszykiem po naciśnięciu przycisku
        showCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(ProductDetailsActivity.this,
                        CartActivity.class);
                startActivity(cartIntent);
            }
        });
    }

    private void readFromFirebase() { //odczyt informacji z bazy danych w chmurze na podstawie kodu kreskowego oraz przypisanie ich do obiektu a następnie wyświetlenie
        Query query = databaseProducts.orderByChild("barcode").equalTo(message);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    productClass = ds.getValue(ProductClass.class);
                }
                try {
                    binding.nameTextView.setText(productClass.getProductName());
                    binding.priceTextView.setText(productClass.getPrice());
                    binding.quantityNumberPicker.setMinValue(1);
                    binding.quantityNumberPicker.setMaxValue(Integer.parseInt(productClass
                            .getQuantity()));
                } catch (NumberFormatException e) {
                    showToastMessage("Nie odnaleziono produktu, zeskanuj ponownie!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Error", databaseError.toException());
            }
        });

        storageReference.child(message + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) { //odczyt i wyświetlenie zdjęcia produktu
                Glide.with(ProductDetailsActivity.this).load(uri).into(binding.productImage);
            }
        });
    }

    private void saveToCart() { //zapisanie danych produktu oraz wybranej przez użytkownika ilości do koszyka będącego lokalną relacyjną bazą danych
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DBContract.Product.COLUMN_BARCODE_NUMBER, binding.barcodeTextView.getText()
                .toString());
        values.put(DBContract.Product.COLUMN_PRODUCT_NAME, binding.nameTextView.getText()
                .toString());
        values.put(DBContract.Product.COLUMN_PRODUCT_PRICE, binding.priceTextView.getText()
                .toString());
        values.put(DBContract.Product.COLUMN_PRODUCT_QUANTITY,
                String.valueOf(binding.quantityNumberPicker.getValue()));
        values.put(DBContract.Product.COLUMN_PRODUCT_FULL_QUANTITY,
                String.valueOf(binding.quantityNumberPicker.getMaxValue()));
        long newRowId = 0; //błąd - wartosc -1, sukces - wartosc >=1, nieunikatowy kod kreskowy 0

        try {
            newRowId = db.insertOrThrow(DBContract.Product.TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("Exception", "SQLException" + String.valueOf(e.getMessage()));
            e.printStackTrace();
        }

        if (newRowId == -1) {
            Toast.makeText(this, "Podczas dodawania wystąpił błąd",
                    Toast.LENGTH_LONG).show();
        } else if (newRowId == 0) {
            Toast.makeText(this, "Produt znajduje się już w koszyku",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Dodano do koszyka", Toast.LENGTH_LONG).show();
        }
    }

    private void showToastMessage(String message) { //wyświetlenie komunikatu w postaci toast'a
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
