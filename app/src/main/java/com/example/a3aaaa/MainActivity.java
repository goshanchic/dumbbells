
package com.example.a3aaaa;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private GraphView graphView;
    private TextView feedbackText;
    private LinearLayout feedbackCard;

    private Handler dataHandler, checkHandler;
    private Runnable dataRunnable, checkRunnable;

    private float lastX = 0, lastY = 0, lastZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphView = findViewById(R.id.graphView);
        feedbackText = findViewById(R.id.feedbackText);
        feedbackCard = findViewById(R.id.feedbackCard);

        ApiService apiService = RetrofitClient.getApiService();

        // Получение данных каждые 100 мс
        dataHandler = new Handler();
        dataRunnable = new Runnable() {
            @Override
            public void run() {
                fetchLiveData(apiService);
                dataHandler.postDelayed(this, 100);
            }
        };
        dataHandler.post(dataRunnable);

        // Проверка выполнения раз в 10 секунд
        checkHandler = new Handler();
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                validateExercise();
                checkHandler.postDelayed(this, 10000);
            }
        };
        checkHandler.post(checkRunnable);
    }

    private void fetchLiveData(ApiService apiService) {
        apiService.getExerciseData().enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExerciseData data = response.body();
                    lastX = data.getX();
                    lastY = data.getY();
                    lastZ = data.getZ();

                    graphView.addPoint(lastX, lastY, lastZ);
                } else {
                    feedbackText.setText("Ошибка загрузки данных!");
                }
            }

            @Override

            public void onFailure(Call<ExerciseData> call, Throwable t) {
                feedbackText.setText("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void validateExercise() {
        float threshold = 1.0f; // можно вынести в настройку

        boolean isCorrect = Math.abs(lastX) < threshold &&
                Math.abs(lastY) < threshold &&
                Math.abs(lastZ) < threshold;

        if (isCorrect) {
            feedbackCard.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            feedbackText.setText("✅ Правильно");
        } else {
            feedbackCard.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            feedbackText.setText("❌ Неправильно");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataHandler.removeCallbacks(dataRunnable);
        checkHandler.removeCallbacks(checkRunnable);
    }
}
