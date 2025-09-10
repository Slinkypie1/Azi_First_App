package com.example.asfirstapp; // Package declaration

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

// Custom View for the balance game where a ball moves based on device tilt
public class BalanceGameView extends View {

    private Paint paint;       // Paint object for drawing the ball
    private float ballX, ballY; // Current coordinates of the ball
    private float radius = 10;  // Radius of the ball
    private float maxX, maxY;   // Maximum X and Y positions (screen boundaries)

    // Constructor used when creating view programmatically
    public BalanceGameView(Context context) {
        super(context);
        init(); // Initialize paint and other properties
    }

    // Constructor used when inflating view from XML
    public BalanceGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(); // Initialize paint and other properties
    }

    // Initialize paint settings
    private void init() {
        paint = new Paint();       // Create a new Paint object
        paint.setColor(Color.BLACK); // Set color to black
        paint.setAntiAlias(true);  // Smooth edges of the ball
    }

    // Called when the view size changes (e.g., first layout or rotation)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        maxX = w - radius; // Maximum X coordinate so the ball doesn't go off-screen
        maxY = h - radius; // Maximum Y coordinate
        ballX = w / 2f;   // Start ball in the horizontal center
        ballY = h / 2f;   // Start ball in the vertical center
    }

    // Called to draw the view
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the ball at its current position
        canvas.drawCircle(ballX, ballY, radius, paint);
    }

    // Update ball position based on tilt input (tiltX and tiltY)
    public void updateBall(float tiltX, float tiltY) {
        float speed = 5; // Adjust sensitivity of the ball movement

        // Update ball coordinates based on tilt
        ballX -= tiltX * speed; // Tilt left/right affects horizontal position
        ballY += tiltY * speed; // Tilt forward/backward affects vertical position

        // Keep the ball within the screen boundaries
        ballX = Math.max(radius, Math.min(ballX, maxX));
        ballY = Math.max(radius, Math.min(ballY, maxY));

        invalidate(); // Redraw the view to reflect the new ball position
    }
}
