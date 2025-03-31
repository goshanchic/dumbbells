package com.example.a3aaaa;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private GraphView graphView;
    private TextView feedbackText;
    private Handler handler;
    private Runnable runnable;
    private List<Float> idealCoordinates;
    private float threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphView = findViewById(R.id.graphView);
        feedbackText = findViewById(R.id.feedbackText);

        handler = new Handler();
        ApiService apiService = RetrofitClient.getApiService();

        fetchExerciseData(apiService);

        runnable = new Runnable() {
            @Override
            public void run() {
                checkExercise();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private void fetchExerciseData(ApiService apiService) {
        apiService.getExerciseData().enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    idealCoordinates = response.body().getIdealCoordinates();
                    threshold = response.body().getThreshold();
                    Log.d("API", "Загружены эталонные координаты: " + idealCoordinates);
                } else {
                    Log.e("API", "Ошибка загрузки эталонных данных");
                }
            }

            @Override
            public void onFailure(Call<ExerciseData> call, Throwable t) {
                Log.e("API", "Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void checkExercise() {
        if (idealCoordinates == null || idealCoordinates.isEmpty()) {
            feedbackText.setText("Загрузка эталонных данных...");
            return;
        }

        List<Float> currentCoordinates = graphView.getCurrentData();

        if (currentCoordinates != null && compareCoordinates(currentCoordinates, idealCoordinates)) {
            feedbackText.setText("✅ Упражнение выполнено правильно!");
        } else {
            feedbackText.setText("⚠️ Ошибка в технике!");
        }
    }

    private boolean compareCoordinates(List<Float> current, List<Float> ideal) {
        if (current.size() != ideal.size()) return false;

        for (int i = 0; i < current.size(); i++) {
            if (Math.abs(current.get(i) - ideal.get(i)) > threshold) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}



