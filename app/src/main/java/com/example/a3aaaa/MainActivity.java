package com.example.a3aaaa;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private TextView feedbackText;
    private final List<Entry> entriesX = new ArrayList<>();
    private final List<Entry> entriesY = new ArrayList<>();
    private final List<Entry> entriesZ = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.chart);
        feedbackText = findViewById(R.id.feedbackText);

        readDataFromFile();
        setupChart();
        analyzeMovement();
    }

    private void readDataFromFile() {
        try {
            InputStream is = getAssets().open("dumbbell_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { // пропускаем заголовок
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    float time = Float.parseFloat(parts[0]);
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);

                    entriesX.add(new Entry(time, x));
                    entriesY.add(new Entry(time, y));
                    entriesZ.add(new Entry(time, z));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupChart() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);

        chart.getAxisRight().setEnabled(false);

        LineDataSet setX = new LineDataSet(entriesX, "Горизонталь (X)");
        setX.setColor(Color.RED);
        setX.setLineWidth(1f);

        LineDataSet setY = new LineDataSet(entriesY, "Вертикаль (Y)");
        setY.setColor(Color.GREEN);
        setY.setLineWidth(1f);

        LineDataSet setZ = new LineDataSet(entriesZ, "Ускорение (Z)");
        setZ.setColor(Color.BLUE);
        setZ.setLineWidth(1f);

        LineData data = new LineData(setX, setY, setZ);
        chart.setData(data);
        chart.invalidate();
        chart.animateXY(1500, 1500);
    }

    private void analyzeMovement() {
        float[] ideal = {0.15f, 0.08f, 9.8f};
        float threshold = 0.1f;
        int correctPoints = 0;

        for (int i = 0; i < entriesX.size(); i++) {
            if (Math.abs(entriesX.get(i).getY() - ideal[0]) < threshold &&
                    Math.abs(entriesY.get(i).getY() - ideal[1]) < threshold &&
                    Math.abs(entriesZ.get(i).getY() - ideal[2]) < threshold) {
                correctPoints++;
            }
        }

        int accuracy = (int) ((float) correctPoints / entriesX.size() * 100);
        feedbackText.setText(accuracy > 80 ? "✅ Правильно (" + accuracy + "%)" : "❌ Ошибка (" + accuracy + "%)");
        feedbackText.setTextColor(accuracy > 80 ? Color.GREEN : Color.RED);
    }
}

/*package com.example.a3aaaa;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 101;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showToast("Bluetooth не поддерживается на этом устройстве");
            finish();
            return;
        }

        // Проверка и запрос разрешений
        checkBluetoothPermissions();
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.BLUETOOTH_SCAN)) {

                    showPermissionExplanationDialog();
                } else {
                    requestBluetoothPermissions();
                }
            } else {
                startBleScan();
            }
        } else {
            // Для Android < 12
            startBleScan();
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Необходимо разрешение")
                .setMessage("Для сканирования BLE-устройств необходимо разрешение BLUETOOTH_SCAN")
                .setPositiveButton("OK", (dialog, which) -> requestBluetoothPermissions())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.BLUETOOTH_SCAN},
                REQUEST_BLUETOOTH_SCAN_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_SCAN_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBleScan();
            } else {
                showToast("Без разрешения сканирование BLE невозможно");
            }
        }
    }

    private void startBleScan() {
        try {
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();

            bleScanner.startScan(null, settings, scanCallback);
            showToast("Сканирование BLE начато");

            // Остановка сканирования через 10 секунд
            handler.postDelayed(this::stopBleScan, 10000);
        } catch (SecurityException e) {
            Log.e("BLE", "Ошибка разрешения", e);
            showToast("Ошибка: нет необходимых разрешений");
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result != null && result.getDevice() != null) {
                String deviceInfo = "Найдено устройство: " + result.getDevice().getName() +
                        "\nMAC: " + result.getDevice().getAddress();
                Log.d("BLE", deviceInfo);
                showToast(deviceInfo);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            showToast("Ошибка сканирования: " + errorCode);
        }
    };

    private void stopBleScan() {
        if (bleScanner != null) {
            try {
                bleScanner.stopScan(scanCallback);
                showToast("Сканирование BLE остановлено");
            } catch (SecurityException e) {
                Log.e("BLE", "Ошибка остановки сканирования", e);
            }
        }
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBleScan();
        handler.removeCallbacksAndMessages(null);
    }
}

 */


/*
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

 */
