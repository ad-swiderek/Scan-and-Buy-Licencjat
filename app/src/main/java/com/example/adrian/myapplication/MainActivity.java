package com.example.adrian.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button staffBtn; //przyciski do wyboru funkcji klienta lub pracownika
    private Button customerBtn;
    private Button helpBtn;
    public static final String EXTRA_MESSAGE = "com.example.adrian.scanandbuy"; //wiadomosc (kod kreskowy) do pozniejszego przeslania w intencie
    private final Activity activity = this;
    private static boolean isStaff; //warunek sprawdzajacy czy wybrano funkcję klienta czy pracownika

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        staffBtn = (Button) findViewById(R.id.staffBtn); //przypisuje przyciski do tych na layoucie
        customerBtn = (Button) findViewById(R.id.customerBtn);
        helpBtn = findViewById(R.id.helpBtn);

        staffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //sprawdzam czy wcisnieto przycisk pracownika czy klienta i uruchamiam aparat i skaner kodow kreskowych
                isStaff = true;
                scan();
            }
        });

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStaff = false;
                scan();
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpMessage();
            }
        });
    }

    private void scan() { //uruchamiam skaner kodow kreskowych
        IntentIntegrator integrator = new IntentIntegrator(activity); //tworze specjalny intent z biblioteki zxing ktory pozwala przejsc do activity ze skanerem
        integrator.setBeepEnabled(false); //wylaczam dzwiek skanera
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES); //skaner jest w trybie skanowania wszystkich mozliwych kodow dostepnych w bibliotece
        integrator.initiateScan(); //inicjuje skanowanie
        integrator.setOrientationLocked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data); //zapisuje rezultat naszego skanowania
        if (result != null && isStaff == true) { //jezeli udalo sie zeskanowac i wczesniej wcisnieto przycisk "pracownik" to przechodze do activity w ktorym dodaje produkty i przekazuje w intencie moj numer z kodu kreskowego
            Intent intent = new Intent(this, AddProductActivity.class);
            String message = result.getContents().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (result != null && isStaff == false) { //to samo tylko dla klienta wiec przechodze do activity z ktorego odczytuje kod z bazy
            Intent intent = new Intent(this, SearchProductActivity.class);
            String message = result.getContents().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showHelpMessage(){
        Toast.makeText(this, "Do działania aplikacji wymagany jest dostęp do internetu oraz moduł aparatu", Toast.LENGTH_LONG).show();

    }
}
