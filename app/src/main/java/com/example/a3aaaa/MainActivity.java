package com.example.a3aaaa;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

// Основной класс активности приложения.
public class MainActivity extends AppCompatActivity {

    private GraphView graphView;  // Объект для отображения графика (кастомный компонент).
    private Handler handler;     // Используется для периодического выполнения задач.
    private Runnable runnable;   // Определяет задачу, которая будет выполняться повторно.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // разметка

        // Инициализация графика
        graphView = findViewById(R.id.graphView);  //  ID в XML должен соответствовать!!!

        handler = new Handler();


        runnable = new Runnable() {
            @Override
            public void run() {
                graphView.updateData();
                handler.postDelayed(this, 1000); // отрисовка  через каждую 1 секунду
            }
        };
        handler.post(runnable); // Запускаем выполнение задачи.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}


