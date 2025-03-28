package com.example.a3aaaa;

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
    private static final int GRAPH_HEIGHT = 400; // Высота графика для масштабирования

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
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);

        // передаём данные и генератор случайных чисел
        dataX = new int[DATA_SIZE];
        dataY = new int[DATA_SIZE];
        dataZ = new int[DATA_SIZE];
        random = new Random();

        // Заполнение массивов случайными данными
        for (int i = 0; i < DATA_SIZE; i++) {
            dataX[i] = random.nextInt(200); // Случ знач для оси X
            dataY[i] = random.nextInt(200); // Случ знач для оси Y
            dataZ[i] = random.nextInt(200); // Случ знач для оси Z
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рисуем оси
        drawAxes(canvas);

        // Масштабирование графиков
        float scaleFactor = GRAPH_HEIGHT / 200.0f;

        // Рисуем график X (Красный)
        linePaint.setColor(Color.RED);
        drawGraph(canvas, dataX, scaleFactor, linePaint);

        // Рисуем график Y (Зеленый)
        linePaint.setColor(Color.GREEN);
        drawGraph(canvas, dataY, scaleFactor, linePaint);

        // Рисуем график Z (Синий)
        linePaint.setColor(Color.BLUE);
        drawGraph(canvas, dataZ, scaleFactor, linePaint);
    }

    private void drawGraph(Canvas canvas, int[] data, float scaleFactor, Paint paint) {
        int startX = 100;
        int startY = getHeight() - 100 - (int) (data[0] * scaleFactor);

        for (int i = 1; i < data.length; i++) {
            int stopX = startX + 30; // Расстояние между точками
            int stopY = getHeight() - 100 - (int) (data[i] * scaleFactor);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            startX = stopX;
            startY = stopY;
        }
    }

    private void drawAxes(Canvas canvas) {
        int canvasHeight = getHeight();
        int canvasWidth = getWidth();

        // Ось Y
        canvas.drawLine(100, 50, 100, canvasHeight - 100, axisPaint); // Ось Y
        canvas.drawText("Y", 50, 70, textPaint); // Метка Y

        // Ось X
        canvas.drawLine(100, canvasHeight - 100, canvasWidth - 50, canvasHeight - 100, axisPaint); // Ось X
        canvas.drawText("X", canvasWidth - 70, canvasHeight - 50, textPaint); // Метка X

        // Стрелка на оси Y
        drawArrow(canvas, 100, 50, 100, 80);

        // Стрелка на оси X
        drawArrow(canvas, canvasWidth - 50, canvasHeight - 100, canvasWidth - 80, canvasHeight - 100);
    }

    private void drawArrow(Canvas canvas, float startX, float startY, float endX, float endY) {
        Path arrowPath = new Path();
        arrowPath.moveTo(startX, startY);
        arrowPath.lineTo(endX, endY);
        arrowPath.moveTo(endX, endY);
        arrowPath.lineTo(endX - 10, endY - 10); // Левая часть стрелки
        arrowPath.lineTo(endX - 10, endY + 10); // Правая часть стрелки
        canvas.drawPath(arrowPath, axisPaint);
    }

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
}

