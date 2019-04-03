package com.example.adrian.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.myapplication.databinding.ActivityAddProductBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {

    ActivityAddProductBinding binding; //Zeby dodac binding trzeba wziac caly XML w <layout></layout>
    DatabaseReference databaseProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product); //dodajemy binding aby moc odnosic sie do komponentow w naszym pliku xml

        Intent intent = getIntent(); //przyjmujemy intent z naszego main activity (czyli nasz kod kreskowy)
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); //Przypisujemy kod kreskowy z intentu do Stringa

        TextView textView = findViewById(R.id.barcodeTextView); //wyswietlamy w naszym textView wczesniej przypisany kod kreskowy
        textView.setText(message);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");

        binding.saveBtn.setOnClickListener(new View.OnClickListener() { //tworzymy wydarzenie ktore po kliknieciu w button wywola metode zapisujaca dane do bazy
            @Override
            public void onClick(View v) {
                saveToDB();
                saveToFirebase();
            }
        });

    }

    private void saveToDB() {
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase(); //tworzymy komponent ktory pozwoli zapisac dane do bazy

        ContentValues values = new ContentValues();

        values.put(DBContract.Product.COLUMN_BARCODE_NUMBER, binding.barcodeTextView.getText().toString()); //wprowadzamy wartosci wpisane w naszym layoucie do wczesniej utworzonego obiektu values
        values.put(DBContract.Product.COLUMN_PRODUCT_NAME, binding.nameEditText.getText().toString());
        values.put(DBContract.Product.COLUMN_PRODUCT_PRICE, binding.priceEditText.getText().toString());
        values.put(DBContract.Product.COLUMN_PRODUCT_QUANTITY, binding.quantityEditText.getText().toString());

        long newRowId = 0; //tworzymy zmienna do ktorej przypiszemy id nowego wiersza (w przypadku bledu podczas dodawania zostanie zwrocona wartosc -1, w przypadku poprawnego dodania - wartosc >=1, w naszym przypadku jezeli kod kreskowy nie bedzie unikalny zostanie zwrocone 0)

        try { //łapanie wyjątków w przypadku bledu podczas dodawania do bazy
            newRowId = db.insertOrThrow(DBContract.Product.TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("Exception", "SQLException" + String.valueOf(e.getMessage()));
            e.printStackTrace();
        }

        if(newRowId == -1) { //wyswietlanie tego o czym pisalem powyzej w postaci toasta
            Toast.makeText(this, "Podczas dodawania wystąpił błąd", Toast.LENGTH_LONG).show();
        }
        else if(newRowId == 0){
            Toast.makeText(this, "Produt występuje już w bazie", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Dodano pomyślnie", Toast.LENGTH_LONG).show();
        }
    }

    private void saveToFirebase(){
        //String id = databaseProducts.push().getKey();
        ProductClass productObject = new ProductClass( binding.barcodeTextView.getText().toString(), binding.nameEditText.getText().toString(),
                binding.priceEditText.getText().toString(), binding.quantityEditText.getText().toString());
        databaseProducts.child(binding.barcodeTextView.getText().toString()).setValue(productObject);
    }
}
