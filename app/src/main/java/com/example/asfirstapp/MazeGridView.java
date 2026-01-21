package com.example.asfirstapp;

import android.content.Context;         // Needed to start activities and access resources
import android.content.Intent;          // Used to navigate to new activities
import android.graphics.Canvas;         // Drawing surface for custom view
import android.graphics.Color;          // Colors for drawing
import android.graphics.Paint;          // Paint object for drawing shapes/text
import android.os.Handler;              // Handler for countdown timer
import android.util.AttributeSet;       // For XML inflation
import android.view.View;               // Base class for custom views

/**
 * MazeGridView is a custom View representing a tilt-controlled maze.
 * The user moves a red ball through the maze toward a green goal.
 * Hitting walls triggers a failure screen, reaching the goal triggers a success screen.
 */
public class MazeGridView extends View {
    // Constants
    private static final int GRID_SIZE = 11;     // Maze grid is 11x11 cells
    private static final int BALL_RADIUS = 25;   // Radius of the red ball

    // Maze layout: 0 = path, 1 = wall, 2 = goal
    private int[][] maze;

    // Ball position
    private float ballX, ballY;

    // Cell size and offsets to center maze
    private float cellSize;
    private float offsetX, offsetY;

    // Paint for drawing walls, ball, goal, and countdown
    private Paint paint;

    // Countdown and game state
    private boolean gameStarted = false;  // True after countdown ends
    private int countdown = 10;           // Countdown before game starts
    private Handler handler = new Handler();

    // Context to start activities (for navigating to success/failure screens)
    private Context context;

    // Constructors
    public MazeGridView(Context context) {
        super(context);
        this.context = context;
        init();  // Initialize maze, paint, and countdown
    }

    public MazeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    /**
     * Initializes the maze grid, paint object, and starts the countdown
     */
    private void init() {
        // Initialize paint
        paint = new Paint();
        paint.setAntiAlias(true);  // Smooth edges

        // Define the maze layout
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

        // Start 10-second countdown before allowing movement
        startCountdown();
    }

    /**
     * Calculates cell size and offsets when view size changes
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Determine maximum cell size that fits the view
        cellSize = Math.min(w / (float) GRID_SIZE, h / (float) GRID_SIZE);

        // Calculate offsets to center the maze
        offsetX = (w - (cellSize * GRID_SIZE)) / 2;
        offsetY = (h - (cellSize * GRID_SIZE)) / 2;

        // Place the ball at the first open path
        ballX = offsetX + cellSize + BALL_RADIUS;
        ballY = offsetY + cellSize + BALL_RADIUS;
    }

    /**
     * Draws the maze, ball, and countdown if applicable
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMaze(canvas);   // Draw walls and goal
        drawBall(canvas);   // Draw red ball

        if (!gameStarted) {
            drawCountdown(canvas);  // Draw countdown before game starts
        }
    }

    /**
     * Draws walls (black) and goal (green) squares
     */
    private void drawMaze(Canvas canvas) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (maze[row][col] == 1) {
                    paint.setColor(Color.BLACK); // Wall
                } else if (maze[row][col] == 2) {
                    paint.setColor(Color.GREEN); // Goal
                } else {
                    continue; // Path, no need to draw
                }
                canvas.drawRect(offsetX + col * cellSize,
                        offsetY + row * cellSize,
                        offsetX + (col + 1) * cellSize,
                        offsetY + (row + 1) * cellSize,
                        paint);
            }
        }
    }

    /**
     * Draws the red ball at its current coordinates
     */
    private void drawBall(Canvas canvas) {
        paint.setColor(Color.RED);
        canvas.drawCircle(ballX, ballY, BALL_RADIUS, paint);
    }

    /**
     * Draws the countdown number at the center of the screen
     */
    private void drawCountdown(Canvas canvas) {
        paint.setColor(Color.BLUE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(countdown), getWidth() / 2f, getHeight() / 2f, paint);
    }

    /**
     * Starts the 10-second countdown before the player can move the ball
     */
    private void startCountdown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    countdown--;
                    invalidate();           // Redraw countdown
                    handler.postDelayed(this, 1000); // Repeat every second
                } else {
                    gameStarted = true;     // Countdown finished, start the game
                    invalidate();
                }
            }
        }, 1000);
    }

    /**
     * Updates the ball position based on tilt input
     *
     * @param tiltX float representing horizontal tilt (accelerometer)
     * @param tiltY float representing vertical tilt (accelerometer)
     */
    public void updateBall(float tiltX, float tiltY) {
        if (!gameStarted) return;  // Ignore movement before countdown ends

        // Movement speed proportional to cell size
        float speed = cellSize / 50;
        float newBallX = ballX - tiltX * speed;
        float newBallY = ballY + tiltY * speed;

        // Determine the cell the ball is moving into
        int col = (int) ((newBallX - offsetX) / cellSize);
        int row = (int) ((newBallY - offsetY) / cellSize);

        // Bounds check
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            if (maze[row][col] == 0) {
                // Free path, move ball
                ballX = newBallX;
                ballY = newBallY;
            } else if (maze[row][col] == 2) {
                // Reached goal
                navigateToCorrectScreen();
            } else {
                // Hit wall
                navigateToFailureScreen();
            }
        }

        invalidate(); // Redraw ball at new position
    }

    /**
     * Navigates to the Failure screen
     */
    private void navigateToFailureScreen() {
        Intent intent = new Intent(context, Failure.class);
        context.startActivity(intent);
    }

    /**
     * Navigates to the CorrectScreen6 after completing the maze
     */
    private void navigateToCorrectScreen() {
        Intent intent = new Intent(context, CorrectScreen6.class);
        context.startActivity(intent);
    }
}
