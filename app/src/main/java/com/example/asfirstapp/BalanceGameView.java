package com.example.asfirstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BalanceGameView extends View {
    private Paint paint;
    private float ballX, ballY;
    private float radius = 50; // Ball radius
    private float maxX, maxY; // Screen boundaries

    public BalanceGameView(Context context) {
        super(context);
        init();
    }

    public BalanceGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxX = w - radius;
        maxY = h - radius;
        ballX = w / 2f;
        ballY = h / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(ballX, ballY, radius, paint);
    }

    public void updateBall(float tiltX, float tiltY) {
        float speed = 5; // Adjust sensitivity

        // Move the ball based on tilt
        ballX -= tiltX * speed;
        ballY += tiltY * speed;

        // Keep ball within screen boundaries
        ballX = Math.max(radius, Math.min(ballX, maxX));
        ballY = Math.max(radius, Math.min(ballY, maxY));

        invalidate(); // Redraw view
    }
}
