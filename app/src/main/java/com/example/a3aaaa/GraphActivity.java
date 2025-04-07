/* package com.example.a3aaaa;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphActivity extends AppCompatActivity {

    private GraphView graphView; // Наш кастомный View для графика
    private TextView feedbackText; // Текст для отображения правильности выполнения
    private Handler handler;
    private Runnable runnable;
    private List<Float> idealCoordinatesX, idealCoordinatesY, idealCoordinatesZ;
    private float threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_graph.xml

        graphView = findViewById(R.id.graphView); // Получаем ссылку на кастомный GraphView
        feedbackText = findViewById(R.id.feedbackText); // Для текста о правильности выполнения

        handler = new Handler();
        ApiService apiService = RetrofitClient.getApiService();

        fetchExerciseData(apiService); // Загружаем эталонные данные для координат

        runnable = new Runnable() {
            @Override
            public void run() {
                checkExercise(); // Проверяем, правильно ли выполняется упражнение
                handler.postDelayed(this, 100); // Повторяем каждую 100 миллисекунд
            }
        };
        handler.post(runnable);
    }

    private void fetchExerciseData(ApiService apiService) {
        apiService.getExerciseData().enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    idealCoordinatesX = response.body().getIdealCoordinatesX();
                    idealCoordinatesY = response.body().getIdealCoordinatesY();
                    idealCoordinatesZ = response.body().getIdealCoordinatesZ();
                    threshold = response.body().getThreshold();
                    updateGraph(); // Обновляем график с этими данными
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

    private void updateGraph() {
        // Обновляем график с новыми данными (X, Y, Z)
        graphView.updateData(idealCoordinatesX, idealCoordinatesY, idealCoordinatesZ);
    }

    private void checkExercise() {
        // Логика для проверки выполнения упражнения
        if (idealCoordinatesX == null || idealCoordinatesY == null || idealCoordinatesZ == null) {
            feedbackText.setText("Загрузка данных...");
            return;
        }

        // Проверяем текущие координаты с эталонными
        List<Float> currentCoordinatesX = graphView.getCurrentDataX();
        List<Float> currentCoordinatesY = graphView.getCurrentDataY();
        List<Float> currentCoordinatesZ = graphView.getCurrentDataZ();

        if (compareCoordinates(currentCoordinatesX, idealCoordinatesX) &&
                compareCoordinates(currentCoordinatesY, idealCoordinatesY) &&
                compareCoordinates(currentCoordinatesZ, idealCoordinatesZ)) {
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

 */




