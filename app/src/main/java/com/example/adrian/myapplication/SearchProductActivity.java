package com.example.adrian.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.adrian.myapplication.databinding.ActivitySearchProductBinding;
//
public class SearchProductActivity extends AppCompatActivity {

    ActivitySearchProductBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_product); //dodajemy binding aby moc odnosic sie bezposrednio do komponentow w naszym pliku xml
        Intent intent = getIntent(); //przyjmujemy intent z naszego main activity (czyli nasz kod kreskowy)
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); //Przypisujemy kod kreskowy z intentu do String
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this)); //modyfikujemy nasz komponent RecyclerView tak aby wyswietlal dane w interesujacy nas sposob czyli w tym przypadku za pomoca liniowo ulozonych pod soba textview

        TextView textView = findViewById(R.id.barcodeTextView); //wyswietlamy nasz kod kreskowy w textview
        textView.setText(message);

        readFromDB(); //odczytujemy dane z bazy na podstawie zeskanowanego kodu kreskowego
    }

    private void readFromDB(){
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
    }
}
