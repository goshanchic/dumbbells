package com.example.a3aaaa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
            startActivity(intent);
        });
    }
}

/*
«// Пустышки — без действия пока
        dummyButton1.setOnClickListener(v -> {
            // Заглушка
        });

        dummyButton2.setOnClickListener(v -> {
            // Заглушка
        });
    }  /»
    */
