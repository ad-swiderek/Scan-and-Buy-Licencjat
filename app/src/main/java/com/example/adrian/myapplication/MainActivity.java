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

    private Button customerBtn;
    private Button helpBtn;
    public static final String EXTRA_MESSAGE = "com.example.adrian.scanandbuy";
    private final Activity activity = this;
    private boolean firstUse = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (firstUse) { //sprawdzanie czy aplikacja została po raz pierwszy lub po jej zrestartowaniu, jeśli tak to usuwana jest lokalna baza danych "products_database" o ile taka istnieje
            this.deleteDatabase("products_database");
            firstUse = false;
        }
        customerBtn = findViewById(R.id.scanBtn);
        helpBtn = findViewById(R.id.helpBtn);

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //wyświetlenie komunikatu dotyczącego wymagań aplikacji po naciśnięciu przycisku help
                showToastMessage("Do działania aplikacji wymagany jest dostęp do internetu " +
                        "oraz moduł aparatu. Aplikacja została przystosowana do wyświetlania na " +
                        "ekranach o przekątnej powyzej 4,5\"");
            }
        });
    }

    protected void scan() { //uruchomienie skanera kodów oraz jego konfiguracja
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.initiateScan();
        integrator.setOrientationLocked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //uzyskanie rezultatu skanowania
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            try {
                Intent intent = new Intent(this, ProductDetailsActivity.class); //utworzenie "intentu" do kolejnego okna aplikacji
                String message = result.getContents();
                intent.putExtra(EXTRA_MESSAGE, message); //przekazanie resultatu skanowania kodu kreskowego do okna aplikacji które zostanie otwarte
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showToastMessage(String message) { //wyświetlenie wiadomości na ekranie w postaci "toast'a"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
