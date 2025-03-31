package com.example.a3aaaa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class GraphView extends View {
    private Paint paint;
    private List<Float> dataPoints;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        dataPoints = new ArrayList<>();
    }

    public void updateData(List<Float> newData) {
        dataPoints = newData;
        invalidate();
    }

    public List<Float> getCurrentData() {
        return dataPoints;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataPoints.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float stepX = width / (dataPoints.size() - 1);

        for (int i = 0; i < dataPoints.size() - 1; i++) {
            float startX = i * stepX;
            float startY = height - (dataPoints.get(i) * height / 10);
            float stopX = (i + 1) * stepX;
            float stopY = height - (dataPoints.get(i + 1) * height / 10);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }
}


