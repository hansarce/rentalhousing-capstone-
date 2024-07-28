package com.example.rentalhousing;
// PinCircleView.java
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class pincirvleview extends View {
    private int numCircles = 4; // Default number of circles
    private int filledCircles = 0;
    private float radius = 20f; // Default radius of the circles
    private float spacing = 40f; // Default spacing between circles
    private Paint emptyCirclePaint;
    private Paint filledCirclePaint;

    public pincirvleview(Context context) {
        super(context);
        init();
    }

    public pincirvleview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public pincirvleview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public pincirvleview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        float cx = radius + getPaddingLeft();
        float cy = getHeight() / 2.0f;
        for (int i = 0; i < numCircles; i++) {
            if (i < filledCircles) {
                canvas.drawCircle(cx, cy, radius, filledCirclePaint);
            } else {
                canvas.drawCircle(cx, cy, radius, emptyCirclePaint);
            }
            cx += (radius * 2) + spacing;
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
}
