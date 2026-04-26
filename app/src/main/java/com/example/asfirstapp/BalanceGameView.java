package com.example.asfirstapp; // Defines the package this class belongs to

import android.content.Context;      // Provides access to application-specific resources
import android.graphics.Canvas;      // Used to draw graphics on the screen
import android.graphics.Color;       // Provides color constants
import android.graphics.Paint;       // Used to define how shapes are drawn (color, style, etc.)
import android.util.AttributeSet;    // Used to read XML attributes
import android.view.View;            // Base class for all UI components

// Custom View that draws a ball and moves it based on device tilt
public class BalanceGameView extends View {

    private Paint paint;       // Paint object used to style and draw the ball
    private float ballX, ballY; // Current X and Y position of the ball
    private float radius = 10;  // Radius (size) of the ball
    private float maxX, maxY;   // Maximum allowed X and Y positions on the screen

    // Constructor used when the view is created in Java code
    public BalanceGameView(Context context) {
        super(context); // Call the parent View constructor
        init();         // Initialize paint and settings.svg
    }

    // Constructor used when the view is created from an XML layout
    public BalanceGameView(Context context, AttributeSet attrs) {
        super(context, attrs); // Call the parent View constructor with XML attributes
        init();                // Initialize paint and settings.svg
    }

    // Initializes drawing settings.svg
    private void init() {
        paint = new Paint();             // Create a new Paint object
        paint.setColor(Color.BLACK);     // Set the ball color to black
        paint.setAntiAlias(true);        // Smooth the edges of the ball
    }

    // Called when the view size is determined or changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh); // Call parent method

        maxX = w - radius; // Set right boundary so the ball stays on screen
        maxY = h - radius; // Set bottom boundary so the ball stays on screen
        ballX = w / 2f;    // Place the ball in the horizontal center
        ballY = h / 2f;    // Place the ball in the vertical center
    }

    // Called whenever the view needs to be drawn
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); // Call parent drawing method

        // Draw a circle (the ball) at its current position
        canvas.drawCircle(ballX, ballY, radius, paint);
    }

    // Updates the ball position based on tilt sensor values
    public void updateBall(float tiltX, float tiltY) {
        float speed = 5; // Controls how fast the ball moves

        // Move the ball horizontally based on left/right tilt
        ballX -= tiltX * speed;

        // Move the ball vertically based on forward/backward tilt
        ballY += tiltY * speed;

        // Prevent the ball from going off the screen horizontally
        ballX = Math.max(radius, Math.min(ballX, maxX));

        // Prevent the ball from going off the screen vertically
        ballY = Math.max(radius, Math.min(ballY, maxY));

        invalidate(); // Forces the view to redraw with the new position
    }
}
