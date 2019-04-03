package com.example.adrian.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }

    /*private void readFromDB() {
        String barcode = binding.barcodeTextView.getText().toString(); //przypisuje numer kodu kreskowego do stronga tak aby moc potem szukac go w bazie
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase(); //tworzymy komponent do odczytu z bazy

        String[] projection = { //wybieram kolumny z bazy ktore bede chcial potem wyswietlic
                DBContract.Product.COLUMN_PRODUCT_NAME,
                DBContract.Product.COLUMN_PRODUCT_PRICE,
                DBContract.Product.COLUMN_PRODUCT_QUANTITY
        };

        String selection = DBContract.Product.COLUMN_BARCODE_NUMBER + " = ?"; //wybieram kolumne do ktorej bede chcial porownywac i na jej podstawie wyszukkiwac w bazie
        String[] selectionArgs = {barcode}; //przypisuje wczesniej zadeklarowany String z kodem kreskowym - w poszukiwaniu konkretnie tego kodu bedzie przeszukiwana baza

        Cursor cursor = db.query( //tworze ktory bedzie przeszukiwal baze danych
                DBContract.Product.TABLE_NAME, //nazwa przeszukiwanej tabeli
                projection, //kolumny do wyswietlenia
                selection, //kolumny do sprawdzenia
                selectionArgs, //argumenty do porownania z powyzsza kolumna
                null, //group by
                null, //having
                null //order by <- tych trzech nie uzywam poniewaz otrzymany wynik bedzie tylko jeden
        );

        binding.recyclerView.setAdapter(new RecyclerViewAdapter(this, cursor)); //przesylam kursor czyli wynik odczytu z bazy do mojego komponentu w layoucie
    } */
}
