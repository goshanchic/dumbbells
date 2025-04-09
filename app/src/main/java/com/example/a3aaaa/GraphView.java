


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

