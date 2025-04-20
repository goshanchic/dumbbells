package com.example.a3aaaa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ValueAnimator;

public class GraphView extends View {

    private Paint linePaint;
    private Paint axisPaint;
    private Paint textPaint;
    private Paint shadowPaint;
    private Paint fillPaint;
    private Paint gridPaint;
    private int[] dataX, dataY, dataZ;
    private static final int DATA_SIZE = 20;
    private float animationPhase = 0f;
    private ValueAnimator animator;

    // Параметры для генерации данных
    private float phaseX, phaseY, phaseZ;
    private float amplitudeX = 50f;
    private float amplitudeY = 50f;
    private final float amplitudeZ = 250f;
    private int timeCounter;

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dataX = new int[DATA_SIZE];
        dataY = new int[DATA_SIZE];
        dataZ = new int[DATA_SIZE];
        initializeData();
        setupPaints();
        setupAnimation();
    }

    private void setupPaints() {
        axisPaint = new Paint();
        axisPaint.setColor(Color.parseColor("#607D8B"));
        axisPaint.setStrokeWidth(2);
        axisPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#78909C"));
        textPaint.setTextSize(36);
        textPaint.setAntiAlias(true);

        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStrokeWidth(1);

        shadowPaint = new Paint();
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setStrokeWidth(8);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setAlpha(60);

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
    }

    private void setupAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationPhase = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    private void initializeData() {
        phaseX = phaseY = phaseZ = 0f;
        for (int i = 0; i < DATA_SIZE; i++) {
            dataX[i] = 250 + (int) (Math.sin(phaseX) * amplitudeX);
            dataY[i] = 250 + (int) (Math.sin(phaseY) * amplitudeY);
            dataZ[i] = (int) ((Math.sin(phaseZ) + 1) * 250);

            phaseX += 0.05;
            phaseY += 0.07;
            phaseZ += 0.1;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int padding = 80;
        int chartHeight = height - 2 * padding;
        int chartWidth = width - 2 * padding;

        canvas.drawColor(Color.parseColor("#FAFAFA"));
        drawGrid(canvas, width, height, padding, chartWidth, chartHeight);
        drawAxes(canvas, width, height, padding);

        drawGraph(canvas, dataX, Color.parseColor("#FF5722"), padding, chartWidth, chartHeight);
        drawGraph(canvas, dataY, Color.parseColor("#4CAF50"), padding, chartWidth, chartHeight);
        drawGraph(canvas, dataZ, Color.parseColor("#2196F3"), padding, chartWidth, chartHeight);

        drawAxisLabels(canvas, width, height, padding);
    }

    private void drawGrid(Canvas canvas, int width, int height, int padding, int chartWidth, int chartHeight) {
        for (int i = 0; i < 10; i++) {
            float x = padding + (chartWidth) * i / 9;
            canvas.drawLine(x, padding, x, height - padding, gridPaint);
        }

        for (int i = 0; i < 6; i++) {
            float y = padding + (chartHeight) * i / 5;
            canvas.drawLine(padding, y, width - padding, y, gridPaint);
        }
    }

    private void drawAxes(Canvas canvas, int width, int height, int padding) {
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint);
        canvas.drawLine(padding, height - padding, padding, padding, axisPaint);

        Path arrowPath = new Path();
        arrowPath.moveTo(width - padding, height - padding);
        arrowPath.lineTo(width - padding - 15, height - padding - 10);
        arrowPath.moveTo(width - padding, height - padding);
        arrowPath.lineTo(width - padding - 15, height - padding + 10);
        arrowPath.moveTo(padding, padding);
        arrowPath.lineTo(padding - 10, padding + 15);
        arrowPath.moveTo(padding, padding);
        arrowPath.lineTo(padding + 10, padding + 15);
        canvas.drawPath(arrowPath, axisPaint);
    }

    private void drawAxisLabels(Canvas canvas, int width, int height, int padding) {
        canvas.drawText("Время", width - padding - 100, height - padding / 2, textPaint);
        canvas.save();
        canvas.rotate(-90, padding / 2, height / 2);
        canvas.drawText("Значения", padding / 2, height / 2, textPaint);
        canvas.restore();
    }

    private void drawGraph(Canvas canvas, int[] data, int color, int padding, int chartWidth, int chartHeight) {
        if (data == null || data.length < 2) return;

        Path path = new Path();
        Path fillPath = new Path();
        float step = (float) chartWidth / (data.length - 1);

        float firstX = padding;
        float firstY = padding + chartHeight - (data[0] * chartHeight / 500f * animationPhase);
        path.moveTo(firstX, firstY);
        fillPath.moveTo(firstX, padding + chartHeight);
        fillPath.lineTo(firstX, firstY);

        for (int i = 1; i < data.length; i++) {
            float x = padding + step * i;
            float y = padding + chartHeight - (data[i] * chartHeight / 500f * animationPhase);

            float prevX = padding + step * (i - 1);
            float prevY = padding + chartHeight - (data[i - 1] * chartHeight / 500f * animationPhase);
            float ctrlX = (prevX + x) / 2;

            path.cubicTo(ctrlX, prevY, ctrlX, y, x, y);
            fillPath.lineTo(x, y);
        }

        fillPath.lineTo(padding + chartWidth, padding + chartHeight);
        fillPath.close();

        setupGradient(color, padding, chartHeight);

        shadowPaint.setColor(color);
        canvas.drawPath(path, shadowPaint);
        canvas.drawPath(fillPath, fillPaint);

        linePaint = new Paint();
        linePaint.setColor(color);
        linePaint.setStrokeWidth(4);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(path, linePaint);
    }

    private void setupGradient(int color, int padding, int chartHeight) {
        LinearGradient gradient = new LinearGradient(
                0, padding,
                0, padding + chartHeight,
                Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)),
                Color.argb(20, Color.red(color), Color.green(color), Color.blue(color)),
                Shader.TileMode.CLAMP
        );
        fillPaint.setShader(gradient);
    }

    public void updateData() {
        System.arraycopy(dataX, 1, dataX, 0, dataX.length - 1);
        System.arraycopy(dataY, 1, dataY, 0, dataY.length - 1);
        System.arraycopy(dataZ, 1, dataZ, 0, dataZ.length - 1);

        dataX[dataX.length - 1] = 250 + (int) (Math.sin(phaseX) * amplitudeX);
        dataY[dataY.length - 1] = 250 + (int) (Math.sin(phaseY) * amplitudeY);
        dataZ[dataZ.length - 1] = (int) ((Math.sin(phaseZ) + 1) * 250);

        phaseX += 0.05;
        phaseY += 0.07;
        phaseZ += 0.1;

        if (timeCounter++ > 100) {
            amplitudeX = 200f;
            amplitudeY = 200f;
        }

        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
/*package com.example.a3aaaa;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Set;

public class GraphActivity extends AppCompatActivity {

    private static final BluetoothDevice TODO =  ;
    private LineChart lineChart;
    private LineDataSet xDataSet, yDataSet, zDataSet;
    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация графика
        lineChart = findViewById(R.id.lineChart);
        setupChart();

        // Bluetooth подключение
        bluetoothService = new BluetoothService();
        BluetoothDevice device = findPairedDevice("SmartDumbbell"); // имя твоего ESP32

        if (device != null && bluetoothService.connectToDevice(device)) {
            startBluetoothListener();
        } else {
            Toast.makeText(this, "Не удалось подключиться к ESP32", Toast.LENGTH_LONG).show();
        }
    }

    private void setupChart() {
        xDataSet = new LineDataSet(null, "X");
        yDataSet = new LineDataSet(null, "Y");
        zDataSet = new LineDataSet(null, "Z");

        xDataSet.setColor(getResources().getColor(R.color.teal_700));
        yDataSet.setColor(getResources().getColor(R.color.purple_700));
        zDataSet.setColor(getResources().getColor(R.color.black));

        LineData data = new LineData();
        data.addDataSet(xDataSet);
        data.addDataSet(yDataSet);
        data.addDataSet(zDataSet);

        lineChart.setData(data);
        lineChart.getDescription().setText("Smart Dumbbell Graph");
        lineChart.getLegend().setForm(Legend.LegendForm.LINE);
        lineChart.invalidate();
    }

    private void startBluetoothListener() {
        new Thread(() -> {
            while (true) {
                String data = bluetoothService.readData();
                if (!data.isEmpty()) {
                    runOnUiThread(() -> updateChart(data));
                }
            }
        }).start();
    }

    private void updateChart(String data) {
        try {
            String[] values = data.trim().split(",");
            if (values.length == 3) {
                float x = Float.parseFloat(values[0]);
                float y = Float.parseFloat(values[1]);
                float z = Float.parseFloat(values[2]);

                addEntry(xDataSet, x);
                addEntry(yDataSet, y);
                addEntry(zDataSet, z);

                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEntry(LineDataSet dataSet, float value) {
        LineData data = lineChart.getData();
        if (data != null) {
            data.addEntry(new Entry(dataSet.getEntryCount(), value), data.getIndexOfDataSet(dataSet));
        }
    }

    private BluetoothDevice findPairedDevice(String deviceName) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return TODO;
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(deviceName)) {
                    return device;
                }
            }
        }
        return null;
    }
}

 */



/*package com.example.a3aaaa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class GraphView extends View {

    private Paint linePaint;
    private Paint axisPaint;
    private Paint textPaint;
    private int[] dataX, dataY, dataZ;
    private Random random;
    private static final int DATA_SIZE = 50;

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Инициализация Paint для графика
        linePaint = new Paint();
        linePaint.setStrokeWidth(5);
        linePaint.setAntiAlias(true);

        // Инициализация Paint для осей
        axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(3);
        axisPaint.setAntiAlias(true);

        // Инициализация Paint для текста
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);

        // Инициализация данных и генератора случайных чисел
        dataX = new int[DATA_SIZE];
        dataY = new int[DATA_SIZE];
        dataZ = new int[DATA_SIZE];
        random = new Random();

        // Заполнение массивов случайными данными
        for (int i = 0; i < DATA_SIZE; i++) {
            dataX[i] = random.nextInt(200); // Случайные значения для оси X
            dataY[i] = random.nextInt(200); // Случайные значения для оси Y
            dataZ[i] = random.nextInt(200); // Случайные значения для оси Z
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рисуем оси с метками
        drawAxes(canvas);

        // Рисуем графики
        int startX = 100;
        int startY = getHeight() - 50 - dataX[0];
        linePaint.setColor(Color.RED);  // График X

        for (int i = 1; i < dataX.length; i++) {
            int stopX = startX + 50 * i; // Расстояние между точками
            int stopY = getHeight() - 50 - dataX[i];
            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
            startX = stopX;
            startY = stopY;
        }

        startX = 100;
        startY = getHeight() - 50 - dataY[0];
        linePaint.setColor(Color.GREEN);  // График Y

        for (int i = 1; i < dataY.length; i++) {
            int stopX = startX + 50 * i;
            int stopY = getHeight() - 50 - dataY[i];
            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
            startX = stopX;
            startY = stopY;
        }

        startX = 100;
        startY = getHeight() - 50 - dataZ[0];
        linePaint.setColor(Color.BLUE);  // График Z

        for (int i = 1; i < dataZ.length; i++) {
            int stopX = startX + 50 * i;
            int stopY = getHeight() - 50 - dataZ[i];
            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
            startX = stopX;
            startY = stopY;
        }
    }

    // Метод для рисования осей с метками и стрелками
    private void drawAxes(Canvas canvas) {
        // Рисуем ось Y
        canvas.drawLine(100, 50, 100, getHeight() - 50, axisPaint); // ось Y
        canvas.drawText("Y", 50, 100, textPaint); // метка оси Y

        // Рисуем ось X
        canvas.drawLine(100, getHeight() - 50, getWidth() - 50, getHeight() - 50, axisPaint); // ось X
        canvas.drawText("X", getWidth() - 100, getHeight() - 20, textPaint); // метка оси X

        // Рисуем стрелки на концах осей
        drawArrow(canvas, 100, 50, 100, 100);  // Стрелка на оси Y
        drawArrow(canvas, getWidth() - 50, getHeight() - 50, getWidth() - 100, getHeight() - 50); // Стрелка на оси X
    }

    // Метод для рисования стрелки на оси
    private void drawArrow(Canvas canvas, float startX, float startY, float endX, float endY) {
        Path arrowPath = new Path();
        arrowPath.moveTo(startX, startY);
        arrowPath.lineTo(endX, endY);
        arrowPath.moveTo(endX, endY);
        arrowPath.lineTo(endX - 10, endY - 10);  // Левая часть стрелки
        arrowPath.moveTo(endX, endY);
        arrowPath.lineTo(endX - 10, endY + 10);  // Правая часть стрелки
        canvas.drawPath(arrowPath, axisPaint);
    }

    // Метод для обновления данных графиков
    public void updateData() {
        for (int i = 0; i < dataX.length - 1; i++) {
            dataX[i] = dataX[i + 1];
            dataY[i] = dataY[i + 1];
            dataZ[i] = dataZ[i + 1];
        }
        dataX[dataX.length - 1] = random.nextInt(200);
        dataY[dataY.length - 1] = random.nextInt(200);
        dataZ[dataZ.length - 1] = random.nextInt(200);
        invalidate(); // Перерисовываем график
    }

    public void addPoint(float lastX, float lastY, float lastZ) {
    }
}
   <com.example.a3aaaa.GraphView
        android:id="@+id/graphView"
        android:layout_width="367dp"
        android:layout_height="358dp"
        android:layout_above="@+id/feedbackCard"
        android:layout_alignParentTop="true"
        android:layout_marginBottom="273dp"
        android:layout_weight="1" />

 */

