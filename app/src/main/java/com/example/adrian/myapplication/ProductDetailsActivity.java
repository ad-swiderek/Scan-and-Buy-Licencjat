package com.example.adrian.myapplication;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
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
    private StorageReference productsImagesJpg;
    private StorageReference productsImages;
    private static final String TAG = "ProductDetailsActivity";
    private String message;
    //private boolean isCartEmpty = true;
    private ProductClass productClass = new ProductClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details); //dodajemy binding aby moc odnosic sie bezposrednio do komponentow w naszym pliku xml
        Intent intent = getIntent(); //przyjmujemy intent z naszego main activity (czyli nasz kod kreskowy)
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); //Przypisujemy kod kreskowy z intentu do String
        /*if(isCartEmpty){
            binding.showCartBtn.setEnabled(false);
            isCartEmpty=false;
        }*/
        readFromFirebase();

        TextView textView = findViewById(R.id.barcodeTextView); //wyswietlamy nasz kod kreskowy w textview
        textView.setText(message);

        Button scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button addToCartBtn = findViewById(R.id.addToCartBtn);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productClass.getQuantity() == null) {
                    showToastMessage("Nie odnaleziono produktu, sprawdź połączenie z internetem i zeskanuj ponownie!");
                } else {
                    saveToCart();
                    //isCartEmpty = false;
                   // binding.showCartBtn.setEnabled(true);
                }

               /* try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }*/
            }
        });

        Button showCartBtn = findViewById(R.id.showCartBtn);
        showCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });
        //readFromDB(); //odczytujemy dane z bazy na podstawie zeskanowanego kodu kreskowego
    }

    private void readFromFirebase() {
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
                    binding.quantityNumberPicker.setMaxValue(Integer.parseInt(productClass.getQuantity()));
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
            public void onSuccess(Uri uri) {
                Glide.with(ProductDetailsActivity.this).load(uri).into(binding.productImage);
            }
        });
    }

    private void saveToCart() {
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase(); //tworzymy komponent ktory pozwoli zapisac dane do bazy

        ContentValues values = new ContentValues();

        values.put(DBContract.Product.COLUMN_BARCODE_NUMBER, binding.barcodeTextView.getText().toString()); //wprowadzamy wartosci wpisane w naszym layoucie do wczesniej utworzonego obiektu values
        values.put(DBContract.Product.COLUMN_PRODUCT_NAME, binding.nameTextView.getText().toString());
        values.put(DBContract.Product.COLUMN_PRODUCT_PRICE, binding.priceTextView.getText().toString());
        values.put(DBContract.Product.COLUMN_PRODUCT_QUANTITY, String.valueOf(binding.quantityNumberPicker.getValue()));

        long newRowId = 0; //tworzymy zmienna do ktorej przypiszemy id nowego wiersza (w przypadku bledu podczas dodawania zostanie zwrocona wartosc -1, w przypadku poprawnego dodania - wartosc >=1, w naszym przypadku jezeli kod kreskowy nie bedzie unikalny zostanie zwrocone 0)

        try { //łapanie wyjątków w przypadku bledu podczas dodawania do bazy
            newRowId = db.insertOrThrow(DBContract.Product.TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("Exception", "SQLException" + String.valueOf(e.getMessage()));
            e.printStackTrace();
        }

        if (newRowId == -1) { //wyswietlanie tego o czym pisalem powyzej w postaci toasta
            Toast.makeText(this, "Podczas dodawania wystąpił błąd", Toast.LENGTH_LONG).show();
        } else if (newRowId == 0) {
            Toast.makeText(this, "Produt znajduje się już w koszyku", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Dodano do koszyka", Toast.LENGTH_LONG).show();
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }
}
