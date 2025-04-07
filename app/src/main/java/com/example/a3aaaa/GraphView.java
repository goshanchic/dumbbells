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
    private List<Float> idealCoordinatesX = new ArrayList<>();
    private List<Float> idealCoordinatesY = new ArrayList<>();
    private List<Float> idealCoordinatesZ = new ArrayList<>();

    private List<Float> currentCoordinatesX = new ArrayList<>();
    private List<Float> currentCoordinatesY = new ArrayList<>();
    private List<Float> currentCoordinatesZ = new ArrayList<>();

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
    }

    public void updateData(List<Float> coordinatesX, List<Float> coordinatesY, List<Float> coordinatesZ) {
        idealCoordinatesX = coordinatesX;
        idealCoordinatesY = coordinatesY;
        idealCoordinatesZ = coordinatesZ;
        invalidate();
    }

    public List<Float> getCurrentDataX() {
        return currentCoordinatesX;
    }

    public List<Float> getCurrentDataY() {
        return currentCoordinatesY;
    }

    public List<Float> getCurrentDataZ() {
        return currentCoordinatesZ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (idealCoordinatesX.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float stepX = width / (idealCoordinatesX.size() - 1);

        for (int i = 0; i < idealCoordinatesX.size() - 1; i++) {
            float startX = i * stepX;
            float startY = height - (idealCoordinatesX.get(i) * height / 10);
            float stopX = (i + 1) * stepX;
            float stopY = height - (idealCoordinatesX.get(i + 1) * height / 10);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }
}

/*package com.example.a3aaaa;

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
    private List<Float> idealCoordinatesX = new ArrayList<>();
    private List<Float> idealCoordinatesY = new ArrayList<>();
    private List<Float> idealCoordinatesZ = new ArrayList<>();

    private List<Float> currentCoordinatesX = new ArrayList<>();
    private List<Float> currentCoordinatesY = new ArrayList<>();
    private List<Float> currentCoordinatesZ = new ArrayList<>();

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
    }

    public void updateData(List<Float> coordinatesX, List<Float> coordinatesY, List<Float> coordinatesZ) {
        idealCoordinatesX = coordinatesX;
        idealCoordinatesY = coordinatesY;
        idealCoordinatesZ = coordinatesZ;
        invalidate();
    }

    public List<Float> getCurrentDataX() {
        return currentCoordinatesX;
    }

    public List<Float> getCurrentDataY() {
        return currentCoordinatesY;
    }

    public List<Float> getCurrentDataZ() {
        return currentCoordinatesZ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (idealCoordinatesX.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float stepX = width / (idealCoordinatesX.size() - 1);

        for (int i = 0; i < idealCoordinatesX.size() - 1; i++) {
            float startX = i * stepX;
            float startY = height - (idealCoordinatesX.get(i) * height / 10);
            float stopX = (i + 1) * stepX;
            float stopY = height - (idealCoordinatesX.get(i + 1) * height / 10);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }
}

 */


