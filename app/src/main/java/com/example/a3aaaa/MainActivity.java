package com.example.a3aaaa;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GraphView graphView;
    private Button statusButton;
    private Handler handler = new Handler();
    private boolean isCorrect = true;
    private Random random = new Random();
    private MediaPlayer correctSound, errorSound;
    private int correctCount = 0;
    private int errorCount = 0;
    private long exerciseStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация UI элементов
        graphView = findViewById(R.id.graphView);
        statusButton = findViewById(R.id.statusButton);

        // Инициализация звуковых эффектов
        initializeSounds();

        // Начало отсчета времени выполнения
        exerciseStartTime = System.currentTimeMillis();
        updateButtonStatus();

        // Кнопка назад с показом статистики
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> showStatisticsAndFinish());

        // Запуск обновлений
        startUpdates();
    }

    private void initializeSounds() {
        try {
            correctSound = MediaPlayer.create(this, R.raw.correct);
            errorSound = MediaPlayer.create(this, R.raw.error);
            correctSound.setVolume(0.7f, 0.7f);
            errorSound.setVolume(0.7f, 0.7f);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading sound files", Toast.LENGTH_SHORT).show();
        }
    }

    private void startUpdates() {
        // Обновление графика каждые 500 мс
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                graphView.updateData();
                handler.postDelayed(this, 500);
            }
        }, 500);

        // Проверка статуса выполнения с динамическими интервалами
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateExerciseStatus();
                int delay = getDynamicDelay();
                handler.postDelayed(this, delay);
            }
        }, 2000);
    }

    private void updateExerciseStatus() {
        // Вероятность правильного выполнения уменьшается со временем
        float timeFactor = getTimeFactor();
        float correctProbability = 0.7f - (timeFactor * 0.3f);

        if(random.nextFloat() < correctProbability) {
            isCorrect = true;
            correctCount++;
        } else {
            isCorrect = false;
            errorCount++;
        }

        updateButtonStatus();
        provideFeedback();
    }

    private float getTimeFactor() {
        long duration = System.currentTimeMillis() - exerciseStartTime;
        return Math.min(1, duration / (30 * 60 * 1000f)); // Нормализация к 30 минутам
    }

    private int getDynamicDelay() {
        // Более частые проверки при ошибках
        return isCorrect ? 3000 + random.nextInt(4000) : 1000 + random.nextInt(2000);
    }

    private void updateButtonStatus() {
        runOnUiThread(() -> {
            if (isCorrect) {
                statusButton.setText("✓ Правильно (" + correctCount + ")");
                statusButton.setBackgroundColor(Color.parseColor("#4CAF50")); // Зеленый
            } else {
                statusButton.setText("✗ Ошибка (" + errorCount + ")");
                statusButton.setBackgroundColor(Color.parseColor("#F44336")); // Красный
            }
            statusButton.setTextColor(Color.WHITE);
            animateButton();
        });
    }

    private void animateButton() {
        float scale = isCorrect ? 1.05f : 0.95f;
        statusButton.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(200)
                .withEndAction(() -> statusButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200));
    }

    private void provideFeedback() {
        runOnUiThread(() -> {
            if (!isCorrect) {
                // Вибрация
                vibrateDevice();

                // Звук ошибки
                playSound(errorSound);

                // Подсказка
                Toast.makeText(MainActivity.this, getRandomErrorTip(), Toast.LENGTH_SHORT).show();
            } else if (correctCount % 5 == 0) {
                // Поощрение каждые 5 правильных выполнений
                playSound(correctSound);
            }
        });
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(150);
            }
        }
    }

    private void playSound(MediaPlayer sound) {
        try {
            if (sound != null) {
                if (sound.isPlaying()) {
                    sound.seekTo(0);
                }
                sound.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRandomErrorTip() {
        String[] tips = {
                "Держите спину ровнее",
                "Контролируйте амплитуду",
                "Выполняйте медленнее",
                "Следите за дыханием",
                "Не напрягайте шею"
        };
        return tips[random.nextInt(tips.length)];
    }

    private void showStatisticsAndFinish() {
        long durationSec = (System.currentTimeMillis() - exerciseStartTime) / 1000;
        String stats = String.format(
                "Статистика:\nВремя: %d мин %d сек\nПравильно: %d\nОшибок: %d",
                durationSec / 60, durationSec % 60, correctCount, errorCount
        );

        Toast.makeText(this, stats, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayers();
    }

    private void releaseMediaPlayers() {
        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (errorSound != null) {
            errorSound.release();
            errorSound = null;
        }
    }
}

/*package com.example.a3aaaa;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final String TAG = "BLE_APP";

    private LineChart chart;
    private TextView feedbackText;
    private LinearLayout feedbackCard;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private BleService bleService;

    private final UUID SERVICE_UUID = UUID.fromString("4FAFC201-1FB5-459E-8FCC-C5C9C331914B");
    private final UUID CHARACTERISTIC_UUID = UUID.fromString("BEB5483E-36E1-4688-B7F5-EA07361B26A8");

    private final List<Entry> xEntries = new ArrayList<>();
    private final List<Entry> yEntries = new ArrayList<>();
    private final List<Entry> zEntries = new ArrayList<>();
    private float timeIndex = 0;
    private float lastX, lastY, lastZ;

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (device == null) return;

            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {

                    String deviceName = device.getName();
                    if (deviceName != null && deviceName.contains("ESP32")) {
                        bleScanner.stopScan(this);
                        bleService.connect(MainActivity.this, device);
                    }
                }
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException: " + e.getMessage());
            }
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt.discoverServices();
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this,
                                    "Необходимо разрешение BLUETOOTH_CONNECT",
                                    Toast.LENGTH_SHORT).show());
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                if (service == null) {
                    Log.w(TAG, "Сервис не найден");
                    return;
                }

                BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                if (characteristic == null) {
                    Log.w(TAG, "Характеристика не найдена");
                    return;
                }

                try {
                    gatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                    );
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                    }
                } catch (SecurityException e) {
                    Log.e(TAG, "SecurityException: " + e.getMessage());
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            parseData(characteristic.getValue());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        checkBluetoothSupport();
        setupChart();
        checkPermissions();
    }

    private void initViews() {
        chart = findViewById(R.id.chart);
        feedbackText = findViewById(R.id.feedbackText);
        feedbackCard = findViewById(R.id.feedbackCard);
    }

    private void checkBluetoothSupport() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Устройство не поддерживает Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Включите Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        bleService = new BleService();
        bleService.setGattCallback(gattCallback);
    }

    private void setupChart() {
        LineDataSet xDataSet = new LineDataSet(xEntries, "X");
        xDataSet.setColor(0xFFFF0000);
        xDataSet.setDrawCircles(false);

        LineDataSet yDataSet = new LineDataSet(yEntries, "Y");
        yDataSet.setColor(0xFF00FF00);
        yDataSet.setDrawCircles(false);

        LineDataSet zDataSet = new LineDataSet(zEntries, "Z");
        zDataSet.setColor(0xFF0000FF);
        zDataSet.setDrawCircles(false);

        LineData data = new LineData(xDataSet, yDataSet, zDataSet);
        chart.setData(data);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                    },
                    REQUEST_BLUETOOTH_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void startBleScan() {
        if (!checkPermissions()) return;

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().build());

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED) {
                bleScanner.startScan(filters, settings, scanCallback);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }

    private void parseData(byte[] data) {
        runOnUiThread(() -> {
            try {
                String[] values = new String(data, StandardCharsets.UTF_8).trim().split(",");
                if (values.length < 3) {
                    Log.w(TAG, "Недостаточно данных: " + values.length);
                    return;
                }

                lastX = Float.parseFloat(values[0]);
                lastY = Float.parseFloat(values[1]);
                lastZ = Float.parseFloat(values[2]);

                updateChart();
                giveFeedback();
            } catch (Exception e) {
                Log.e(TAG, "Ошибка парсинга данных: " + e.getMessage());
            }
        });
    }

    private void updateChart() {
        xEntries.add(new Entry(timeIndex, lastX));
        yEntries.add(new Entry(timeIndex, lastY));
        zEntries.add(new Entry(timeIndex, lastZ));
        timeIndex += 0.1f;

        if (xEntries.size() > 100) {
            xEntries.remove(0);
            yEntries.remove(0);
            zEntries.remove(0);
        }

        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void giveFeedback() {
        float threshold = 1.0f;
        boolean isCorrect = Math.abs(lastX) < threshold &&
                Math.abs(lastY) < threshold &&
                Math.abs(lastZ) < threshold;

        feedbackCard.setBackgroundColor(getResources().getColor(
                isCorrect ? android.R.color.holo_green_light : android.R.color.holo_red_light
        ));
        feedbackText.setText(isCorrect ? "✅ Правильно" : "❌ Неправильно");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBleScan();
            } else {
                Toast.makeText(this, "Необходимы разрешения для работы BLE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bleScanner != null) {
                bleScanner.stopScan(scanCallback);
            }
            bleService.disconnect();
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }
} /*

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
