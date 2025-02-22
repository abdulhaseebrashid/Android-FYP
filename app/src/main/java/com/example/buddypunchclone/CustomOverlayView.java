package com.example.buddypunchclone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CustomOverlayView extends View {
    private Paint paint;
    private RectF rectangle;
    private boolean isRecognized = true;

    public CustomOverlayView(Context context) {
        super(context);
        init();
    }

    public CustomOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);  // Thinner line for more precise appearance
        rectangle = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate dimensions for a face-sized rectangle
        float rectWidth = w * 0.6f;       // 40% of screen width
        float rectHeight = h * 0.26f;     // 20% of screen height

        // Position the rectangle in the center of the screen
        float left = (w - rectWidth) / 2;
        // Move slightly below center for better face positioning
        float top = (h - rectHeight) / 2 + (h * 0.02f); // Offset down by 5% of screen height


        rectangle.set(left, top, left + rectWidth, top + rectHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Using slightly more transparent colors for better visibility
        int greenColor = Color.argb(160, 0, 255, 0);  // More transparent green
        int redColor = Color.argb(160, 255, 0, 0);    // More transparent red

        paint.setColor(isRecognized ? greenColor : redColor);

        // Draw the rectangle with rounded corners for a more professional look
        float cornerRadius = 10.0f;
        canvas.drawRoundRect(rectangle, cornerRadius, cornerRadius, paint);
    }

    public void setRecognitionStatus(boolean isRecognized) {
        this.isRecognized = isRecognized;
        invalidate();
    }
}