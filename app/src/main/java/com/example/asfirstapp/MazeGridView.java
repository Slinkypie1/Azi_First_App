package com.example.asfirstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * MazeGridView is a custom View representing a tilt-controlled maze.
 * The maze is randomly selected from a library each time the game starts.
 */
public class MazeGridView extends View {

    // ----- Constants -----
    private static final int GRID_SIZE = 11;
    private static final int BALL_RADIUS = 25;

    // ----- Maze Library (Add as many as you like here) -----
    private final int[][][] MAZE_LIBRARY = {
            {
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
            },
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1},
                    {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
                    {1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 0, 0, 2, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            },
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
                    {1, 0, 0, 0, 1, 0, 0, 0, 1, 2, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            },
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1},
                    {1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                    {1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            },
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                    {1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                    {1, 0, 1, 1, 1, 2, 1, 0, 1, 0, 1},
                    {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            },
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                    {1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            }
    };

    private int[][] maze;
    private float ballX, ballY;
    private float cellSize;
    private float offsetX, offsetY;
    private Paint paint;
    private boolean gameStarted = false;
    private int countdown = 10;
    private Handler handler = new Handler();
    private Context context;
    private long startTime;

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

        // Pick a random maze from our library
        Random random = new Random();
        int randomIndex = random.nextInt(MAZE_LIBRARY.length);
        maze = MAZE_LIBRARY[randomIndex];

        startCountdown();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        cellSize = Math.min(w / (float) GRID_SIZE, h / (float) GRID_SIZE);
        offsetX = (w - (cellSize * GRID_SIZE)) / 2;
        offsetY = (h - (cellSize * GRID_SIZE)) / 2;

        // Automatically find the first '0' (path) to place the ball
        boolean ballPlaced = false;
        for (int r = 0; r < GRID_SIZE && !ballPlaced; r++) {
            for (int c = 0; c < GRID_SIZE && !ballPlaced; c++) {
                if (maze[r][c] == 0) {
                    ballX = offsetX + (c * cellSize) + (cellSize / 2);
                    ballY = offsetY + (r * cellSize) + (cellSize / 2);
                    ballPlaced = true;
                }
            }
        }
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
                } else if (maze[row][col] == 2) {
                    paint.setColor(Color.GREEN);
                } else {
                    continue;
                }
                canvas.drawRect(offsetX + col * cellSize,
                        offsetY + row * cellSize,
                        offsetX + (col + 1) * cellSize,
                        offsetY + (row + 1) * cellSize,
                        paint);
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
                    startTime = System.currentTimeMillis();
                    invalidate();
                }
            }
        }, 1000);
    }

    public void updateBall(float tiltX, float tiltY) {
        if (!gameStarted) return;

        float speed = cellSize / 50;
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
            } else if (maze[row][col] == 1) {
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
        long timeTaken = System.currentTimeMillis() - startTime;
        Intent intent = new Intent(context, CorrectScreen6.class);
        intent.putExtra("TIME_TAKEN", timeTaken);
        context.startActivity(intent);
    }
}