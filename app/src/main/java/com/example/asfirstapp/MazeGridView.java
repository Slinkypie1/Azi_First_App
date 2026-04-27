package com.example.asfirstapp; // App package name

// ----- Android imports -----
import android.content.Context;          // Needed to access app resources and start activities
import android.content.Intent;           // Used to move between screens
import android.graphics.Canvas;          // Canvas for custom drawing
import android.graphics.Color;           // Color constants
import android.graphics.Paint;           // Used for drawing shapes and text
import android.os.Handler;               // Handles countdown timing
import android.util.AttributeSet;        // Used when the view is loaded from XML
import android.view.View;                // Base class for custom views

import java.util.Random;                 // Used to randomly select a maze

/**
 * MazeGridView
 * -------------
 * A custom View that displays a tilt-controlled maze game.
 * The player tilts the phone to move a red ball through a maze
 * and reach the green goal without touching walls.
 */
public class MazeGridView extends View {

    // ----- Constants -----

    private static final int GRID_SIZE = 11;   // Maze width and height (11x11 grid)
    private static final int BALL_RADIUS = 12; // Radius of the player ball in pixels

    // ----- Maze Library -----
    // Each maze is an 11x11 grid:
    // 0 = empty path (ball can move)
    // 1 = wall (game over if hit)
    // 2 = goal (level completed)
    private final int[][][] MAZE_LIBRARY = {

            // ===== Maze 1 =====
            {
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,2,1}, // Goal tile
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== Maze 2 =====
            {
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,1,0,0,0,0,0,0,0,1},
                    {1,0,1,0,1,1,1,1,1,0,1},
                    {1,0,1,0,1,0,0,0,1,0,1},
                    {1,0,0,0,1,0,1,0,1,0,1},
                    {1,1,1,0,1,0,1,0,1,0,1},
                    {1,0,0,0,0,0,1,0,0,0,1},
                    {1,0,1,1,1,1,1,1,1,0,1},
                    {1,0,0,0,0,0,0,0,1,0,1},
                    {1,1,1,1,1,1,1,0,0,2,1}, // Goal
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== Maze 3 =====
            {
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,1,0,0,0,1,0,0,0,1},
                    {1,0,1,0,0,0,1,0,0,0,1},
                    {1,0,1,0,1,0,1,0,1,0,1},
                    {1,0,1,0,1,0,1,0,1,0,1},
                    {1,0,1,0,1,0,1,0,1,0,1},
                    {1,0,1,0,1,0,1,0,1,0,1},
                    {1,0,1,0,1,0,1,0,1,0,1},
                    {1,0,0,0,1,0,0,0,1,0,1},
                    {1,0,0,0,1,0,0,0,1,2,1}, // Goal
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== Maze 4 =====
            {
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,0,1,1,1,1,1,1,1,1},
                    {1,1,0,1,1,1,1,1,1,1,1},
                    {1,1,0,0,0,0,0,0,0,1,1},
                    {1,1,1,1,1,1,1,1,0,1,1},
                    {1,1,0,0,0,0,0,0,0,1,1},
                    {1,1,0,1,1,1,1,1,1,1,1},
                    {1,1,0,0,0,0,0,0,0,1,1},
                    {1,1,1,1,1,1,1,1,0,1,1},
                    {1,1,1,1,1,1,1,1,2,1,1}, // Goal
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== Maze 5 =====
            {
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,0,1},
                    {1,0,0,0,0,0,0,0,1,0,1},
                    {1,0,1,1,1,1,1,0,1,0,1},
                    {1,0,1,1,1,2,1,0,1,0,1}, // Goal inside maze
                    {1,0,1,1,1,0,1,0,1,0,1},
                    {1,0,0,0,0,0,1,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== Maze 6 =====
            {
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,1,0,0,0,1,0,0,0,1},
                    {1,0,1,0,1,0,1,0,1,0,1},
                    {1,0,0,0,1,0,0,0,1,0,1},
                    {1,1,1,1,1,1,1,0,1,0,1},
                    {1,0,0,0,0,0,1,0,1,0,1},
                    {1,0,1,1,1,0,1,0,1,0,1},
                    {1,0,1,0,0,0,1,0,1,0,1},
                    {1,0,1,1,1,1,1,0,1,0,1},
                    {1,0,0,0,0,0,0,0,1,2,1}, // Goal
                    {1,1,1,1,1,1,1,1,1,1,1}
            }
    };

    // ----- Game Variables -----

    private int[][] maze;           // Currently active maze
    private float ballX, ballY;     // Ball position in pixels
    private float cellSize;         // Size of each maze cell
    private float offsetX, offsetY; // Offset to center maze on screen
    private Paint paint;            // Paint used for drawing
    private boolean gameStarted = false; // Prevents movement before countdown
    private boolean isNavigating = false; // Prevents multiple screen triggers
    private int countdown = 3;     // Countdown timer in seconds
    private Handler handler = new Handler(); // Handles countdown updates
    private Context context;        // Context for navigation
    private long startTime;         // Time when the game starts

    // ----- Constructors -----

    public MazeGridView(Context context) {
        super(context);
        this.context = context;
        init(); // Initialize game setup
    }

    public MazeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(); // Initialize game setup
    }

    // ----- Initialization -----

    private void init() {
        paint = new Paint();              // Create Paint object
        paint.setAntiAlias(true);         // Smooth drawing

        // Choose a random maze from the library
        Random random = new Random();
        maze = MAZE_LIBRARY[random.nextInt(MAZE_LIBRARY.length)];
    }

    /**
     * Public method to start the countdown and then the game.
     * This allows the activity to show an explanation before the timer begins.
     */
    public void beginGame() {
        startCountdown();
    }

    // ----- Size Handling -----

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate cell size so maze fits screen
        cellSize = Math.min(w / (float) GRID_SIZE, h / (float) GRID_SIZE);

        // Center maze on screen
        offsetX = (w - cellSize * GRID_SIZE) / 2;
        offsetY = (h - cellSize * GRID_SIZE) / 2;

        // Place ball on the first available path tile
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (maze[r][c] == 0) {
                    ballX = offsetX + c * cellSize + cellSize / 2;
                    ballY = offsetY + r * cellSize + cellSize / 2;
                    return;
                }
            }
        }
    }

    // ----- Drawing -----

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMaze(canvas); // Draw maze walls and goal
        drawBall(canvas); // Draw the red ball

        if (!gameStarted && !isNavigating && countdown > 0) {
            drawCountdown(canvas); // Show countdown before game starts
        }
    }

    private void drawMaze(Canvas canvas) {
        // Get the current background color setting to decide wall color
        String bgColor = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("bg_color", "white");
        int wallColor = bgColor.equals("black") ? Color.WHITE : Color.BLACK;

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (maze[r][c] == 1) paint.setColor(wallColor); // Wall
                else if (maze[r][c] == 2) paint.setColor(Color.GREEN); // Goal
                else continue; // Path (not drawn)

                canvas.drawRect(
                        offsetX + c * cellSize,
                        offsetY + r * cellSize,
                        offsetX + (c + 1) * cellSize,
                        offsetY + (r + 1) * cellSize,
                        paint
                );
            }
        }
    }

    private void drawBall(Canvas canvas) {
        paint.setColor(Color.RED);
        canvas.drawCircle(ballX, ballY, BALL_RADIUS, paint);
    }

    private void drawCountdown(Canvas canvas) {
        // Ensure countdown text is visible on black background
        String bgColor = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("bg_color", "white");
        int textColor = bgColor.equals("black") ? Color.YELLOW : Color.BLUE;

        paint.setColor(textColor);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(countdown),
                getWidth() / 2f,
                getHeight() / 2f,
                paint);
    }

    // ----- Countdown Logic -----

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

    // ----- Ball Movement -----

    public void updateBall(float tiltX, float tiltY) {
        if (!gameStarted || isNavigating) return; // Ignore movement during countdown or navigation

        float speed = cellSize / 200; // Movement speed
        float newX = ballX - tiltX * speed;
        float newY = ballY + tiltY * speed;

        // Bounding box of the ball for collision detection
        float left = newX - BALL_RADIUS;
        float right = newX + BALL_RADIUS;
        float top = newY - BALL_RADIUS;
        float bottom = newY + BALL_RADIUS;

        // Check all four corners of the ball's bounding box
        int[][] corners = {
                {(int) ((left - offsetX) / cellSize), (int) ((top - offsetY) / cellSize)},
                {(int) ((right - offsetX) / cellSize), (int) ((top - offsetY) / cellSize)},
                {(int) ((left - offsetX) / cellSize), (int) ((bottom - offsetY) / cellSize)},
                {(int) ((right - offsetX) / cellSize), (int) ((bottom - offsetY) / cellSize)}
        };

        boolean canMove = true;
        boolean hitGoal = false;
        boolean hitWall = false;

        for (int[] corner : corners) {
            int col = corner[0];
            int row = corner[1];

            if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                if (maze[row][col] == 1) {
                    hitWall = true;
                    canMove = false;
                } else if (maze[row][col] == 2) {
                    hitGoal = true;
                }
            } else {
                canMove = false; // Out of bounds
            }
        }

        if (canMove) {
            ballX = newX;
            ballY = newY;
            if (hitGoal) {
                navigateToCorrectScreen();
            }
        } else if (hitWall) {
            navigateToFailureScreen();
        }

        invalidate();
    }

    // ----- Navigation -----

    private void navigateToFailureScreen() {
        if (isNavigating) return;
        isNavigating = true;
        gameStarted = false;
        context.startActivity(new Intent(context, Failure.class));
    }

    private void navigateToCorrectScreen() {
        if (isNavigating) return;
        isNavigating = true;
        gameStarted = false;
        long timeTaken = System.currentTimeMillis() - startTime;
        Intent intent = new Intent(context, CorrectScreen6.class);
        intent.putExtra("TIME_TAKEN", timeTaken);
        context.startActivity(intent);
    }
}
