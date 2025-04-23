package com.example.asfirstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class MazeGridView extends View {
    private static final int GRID_SIZE = 11;
    private float cellSize;
    private float offsetX, offsetY; // Offsets for centering
    private static final int BALL_RADIUS = 25;

    private int[][] maze;
    private float ballX, ballY;
    private Paint paint;
    private boolean gameStarted = false;
    private int countdown = 10;
    private Handler handler = new Handler();
    private Context context;

    public MazeGridView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MazeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

        // Maze: 1 = wall, 0 = path, 2 = goal (green square)
        maze = new int[][] {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        startCountdown();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Determine the maximum square cell size
        cellSize = Math.min(w / (float) GRID_SIZE, h / (float) GRID_SIZE);

        // Calculate offsets to center the maze if needed
        offsetX = (w - (cellSize * GRID_SIZE)) / 2;
        offsetY = (h - (cellSize * GRID_SIZE)) / 2;

        // Place the ball in the first open space
        ballX = offsetX + cellSize + BALL_RADIUS;
        ballY = offsetY + cellSize + BALL_RADIUS;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMaze(canvas);
        drawBall(canvas);
        if (!gameStarted) {
            drawCountdown(canvas);
        }
    }

    private void drawMaze(Canvas canvas) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (maze[row][col] == 1) {
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(
                            offsetX + col * cellSize,
                            offsetY + row * cellSize,
                            offsetX + (col + 1) * cellSize,
                            offsetY + (row + 1) * cellSize,
                            paint);
                } else if (maze[row][col] == 2) {
                    paint.setColor(Color.GREEN);
                    canvas.drawRect(
                            offsetX + col * cellSize,
                            offsetY + row * cellSize,
                            offsetX + (col + 1) * cellSize,
                            offsetY + (row + 1) * cellSize,
                            paint);
                }
            }
        }
    }

    private void drawBall(Canvas canvas) {
        paint.setColor(Color.RED);
        canvas.drawCircle(ballX, ballY, BALL_RADIUS, paint);
    }

    private void drawCountdown(Canvas canvas) {
        paint.setColor(Color.BLUE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(countdown), getWidth() / 2f, getHeight() / 2f, paint);
    }

    private void startCountdown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    countdown--;
                    invalidate();
                    handler.postDelayed(this, 1000);
                } else {
                    gameStarted = true;
                    invalidate();
                }
            }
        }, 1000);
    }

    public void updateBall(float tiltX, float tiltY) {
        if (!gameStarted) return;

        float speed = cellSize / 27; // Slower movement
        float newBallX = ballX - tiltX * speed;
        float newBallY = ballY + tiltY * speed;

        int col = (int) ((newBallX - offsetX) / cellSize);
        int row = (int) ((newBallY - offsetY) / cellSize);

        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            if (maze[row][col] == 0) {
                ballX = newBallX;
                ballY = newBallY;
            } else if (maze[row][col] == 2) {
                navigateToCorrectScreen();
            } else {
                navigateToFailureScreen();
            }
        }

        invalidate();
    }

    private void navigateToFailureScreen() {
        Intent intent = new Intent(context, Failure.class);
        context.startActivity(intent);
    }

    private void navigateToCorrectScreen() {
        Intent intent = new Intent(context, CorrectScreen6.class);
        context.startActivity(intent);
    }
}
