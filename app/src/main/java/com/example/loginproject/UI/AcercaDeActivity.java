package com.example.loginproject.UI;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginproject.R;


import android.widget.Button;


public class AcercaDeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca);

        TextView textViewInfo = findViewById(R.id.textViewInfo);
        textViewInfo.setText("Nombre de la aplicación: Guia 6\n" +
                "Versión: 1.0.0\n" +
                "Desarrollado por:Dora Gabriela Rivas Pimentel & Mario Humberto Sandoval Cabrera");


    }
}


