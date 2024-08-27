package com.example.rentalhousing;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PinCircleView extends View {
    private int numCircles = 4; // Default number of circles
    private int filledCircles = 0;
    private float radius = 35f; // Default radius of the circles
    private float spacing = 60f; // Default spacing between circles
    private Paint emptyCirclePaint;
    private Paint filledCirclePaint;

    public PinCircleView(Context context) {
        super(context);
        init();
    }

    public PinCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PinCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        emptyCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyCirclePaint.setStyle(Paint.Style.STROKE);
        emptyCirclePaint.setStrokeWidth(5f);
        emptyCirclePaint.setColor(0xFFFFFFFF); // Light gray color for empty circles

        filledCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        filledCirclePaint.setStyle(Paint.Style.FILL);
        filledCirclePaint.setColor(0xFFFFFFFF); // Black color for filled circles
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the total width of all circles and spaces between them
        float totalWidth = (numCircles * (radius * 2)) + ((numCircles - 1) * spacing);

        // Calculate the starting x coordinate to center the circles
        float startX = (getWidth() - totalWidth) / 2.0f;

        // Vertical center
        float cy = getHeight() / 2.0f;

        float cx = startX + radius; // Starting position for the first circle
        for (int i = 0; i < numCircles; i++) {
            if (i < filledCircles) {
                canvas.drawCircle(cx, cy, radius, filledCirclePaint);
            } else {
                canvas.drawCircle(cx, cy, radius, emptyCirclePaint);
            }
            cx += (radius * 2) + spacing; // Move to the next circle position
        }
    }

    public void setNumCircles(int numCircles) {
        this.numCircles = numCircles;
        invalidate();
    }

    public void setFilledCircles(int filledCircles) {
        this.filledCircles = filledCircles;
        invalidate();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        invalidate();
    }
}