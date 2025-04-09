package com.example.a3aaaa;

import android.Manifest;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.nio.charset.Charset;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private LineChart chart;
    private TextView feedbackText;
    private LinearLayout feedbackCard;

    private Handler dataHandler, checkHandler;
    private Runnable dataRunnable, checkRunnable;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bluetoothGatt;

    private final UUID SERVICE_UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ab");
    private final UUID CHARACTERISTIC_UUID = UUID.fromString("abcd1234-ab12-cd34-ef56-1234567890ab");

    private List<Entry> entriesX = new ArrayList<>();
    private List<Entry> entriesY = new ArrayList<>();
    private List<Entry> entriesZ = new ArrayList<>();

    private LineDataSet dataSetX, dataSetY, dataSetZ;
    private LineData lineData;
    private float timeIndex = 0f;

    private float lastX = 0, lastY = 0, lastZ = 0;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.chart);
        feedbackText = findViewById(R.id.feedbackText);
        feedbackCard = findViewById(R.id.feedbackCard);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();

        setupChart();

        if (checkBluetoothPermissions()) {
            startBLEScan();
        }

        dataHandler = new Handler();
        dataRunnable = new Runnable() {
            @Override
            public void run() {
                dataHandler.postDelayed(this, 100); // 100ms
            }
        };
        dataHandler.post(dataRunnable);

        checkHandler = new Handler();
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                validateExercise();
                checkHandler.postDelayed(this, 10000); // 10s
            }
        };
        checkHandler.post(checkRunnable);
    }

    private boolean checkBluetoothPermissions() {
        // Проверяем разрешения для использования Bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Запрашиваем разрешения
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void setupChart() {
        dataSetX = new LineDataSet(entriesX, "X");
        dataSetX.setColor(android.graphics.Color.RED);
        dataSetX.setDrawCircles(false);

        dataSetY = new LineDataSet(entriesY, "Y");
        dataSetY.setColor(android.graphics.Color.GREEN);
        dataSetY.setDrawCircles(false);

        dataSetZ = new LineDataSet(entriesZ, "Z");
        dataSetZ.setColor(android.graphics.Color.BLUE);
        dataSetZ.setDrawCircles(false);

        lineData = new LineData(dataSetX, dataSetY, dataSetZ);
        chart.setData(lineData);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(1024f);
    }

    private void startBLEScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
            return;
        }

        ScanFilter filter = new ScanFilter.Builder().setDeviceName("ESP32C3_SENSOR").build();
        ScanSettings settings = new ScanSettings.Builder().build();
        bleScanner.startScan(Collections.singletonList(filter), settings, scanCallback);
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null && result.getDevice() != null) {
                result.getDevice().connectGatt(MainActivity.this, false, gattCallback);
                bleScanner.stopScan(this);
            }
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                bluetoothGatt = gatt;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService service = gatt.getService(SERVICE_UUID);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
            gatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            String rawValue = new String(characteristic.getValue(), Charset.forName("UTF-8"));
            if (rawValue != null) {
                handleIncomingData(rawValue);
            }
        }
    };

    private void handleIncomingData(String data) {
        Map<String, Float> map = new HashMap<>();
        try {
            // Обрабатываем данные, разделяя их по запятой
            for (String entry : data.split(",")) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    map.put(parts[0], Float.parseFloat(parts[1]));
                } else {
                    throw new IllegalArgumentException("Invalid data format: " + entry);
                }
            }

            // Обновляем график
            runOnUiThread(() -> {
                Float x = map.get("X");
                Float y = map.get("Y");
                Float z = map.get("Z");

                if (x != null) {
                    entriesX.add(new Entry(timeIndex, x));
                    lastX = x;
                }
                if (y != null) {
                    entriesY.add(new Entry(timeIndex, y));
                    lastY = y;
                }
                if (z != null) {
                    entriesZ.add(new Entry(timeIndex, z));
                    lastZ = z;
                }

                timeIndex += 0.1f;

                lineData.notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();
            });
        } catch (NumberFormatException e) {
            // Логируем ошибку при парсинге числа
            Log.e("MainActivity", "Invalid number format in incoming data: " + data, e);
        } catch (IllegalArgumentException e) {
            // Логируем ошибку, если формат данных неверный
            Log.e("MainActivity", "Error parsing incoming data: " + e.getMessage(), e);
        } catch (Exception e) {
            // Логируем другие исключения
            Log.e("MainActivity", "Unexpected error processing incoming data", e);
        }
    }

    private void validateExercise() {
        float threshold = 1.0f;
        boolean isCorrect = Math.abs(lastX) < threshold && Math.abs(lastY) < threshold && Math.abs(lastZ) < threshold;

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
