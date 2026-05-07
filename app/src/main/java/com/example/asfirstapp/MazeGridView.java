package com.example.asfirstapp; // שם החבילה של האפליקציה

// ----- ייבוני אנדרואיד -----
import android.content.Context;          // דרוש לגישה למשאבי אפליקציה והפעלת אקטיביטיז
import android.content.Intent;           // משמש למעבר בין מסכים
import android.graphics.Canvas;          // קנבס לציור מותאם אישית
import android.graphics.Color;           // קבועי צבע
import android.graphics.Paint;           // משמש לציור צורות וטקסט
import android.os.Handler;               // מטפל בתזמון ספירה לאחור
import android.util.AttributeSet;        // משמש כאשר התצוגה נטענת מ-XML
import android.view.View;                // מחלקת בסיס לתצוגות מותאמות אישית

import java.util.Random;                 // משמש לבחירה אקראית של מבוך

/**
 * MazeGridView
 * -------------
 * תצוגה מותאמת אישית המציגה משחק מבוך הנשלט על ידי הטיית המכשיר.
 * השחקן מטה את הטלפון כדי להזיז כדור אדום דרך מבוך
 * ולהגיע ליעד הירוק מבלי לגעת בקירות.
 */
public class MazeGridView extends View {

    // ----- קבועים -----

    private static final int GRID_SIZE = 11;   // רוחב וגובה המבוך (רשת של 11x11)
    private static final int BALL_RADIUS = 12; // רדיוס כדור השחקן בפיקסלים

    // ----- ספריית מבוכים -----
    // כל מבוך הוא רשת של 11x11:
    // 0 = נתיב פנוי (הכדור יכול לזוז)
    // 1 = קיר (סיום המשחק אם נוגעים)
    // 2 = יעד (השלמת השלב)
    private final int[][][] MAZE_LIBRARY = {

            // ===== מבוך 1 =====
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
                    {1,0,0,0,0,0,0,0,0,2,1}, // משבצת יעד
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== מבוך 2 =====
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
                    {1,1,1,1,1,1,1,0,0,2,1}, // יעד
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== מבוך 3 =====
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
                    {1,0,0,0,1,0,0,0,1,2,1}, // יעד
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== מבוך 4 =====
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
                    {1,1,1,1,1,1,1,1,2,1,1}, // יעד
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== מבוך 5 =====
            {
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,0,1},
                    {1,0,0,0,0,0,0,0,1,0,1},
                    {1,0,1,1,1,1,1,0,1,0,1},
                    {1,0,1,1,1,2,1,0,1,0,1}, // יעד בתוך המבוך
                    {1,0,1,1,1,0,1,0,1,0,1},
                    {1,0,0,0,0,0,1,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1}
            },

            // ===== מבוך 6 =====
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
                    {1,0,0,0,0,0,0,0,1,2,1}, // יעד
                    {1,1,1,1,1,1,1,1,1,1,1}
            }
    };

    // ----- משתני משחק -----

    private int[][] maze;           // המבוך הפעיל כעת
    private float ballX, ballY;     // מיקום הכדור בפיקסלים
    private float cellSize;         // גודל כל משבצת במבוך
    private float offsetX, offsetY; // היסט למרכוז המבוך במסך
    private Paint paint;            // אובייקט Paint המשמש לציור
    private boolean gameStarted = false; // מונע תנועה לפני הספירה לאחור
    private boolean isNavigating = false; // מונע הפעלה כפולה של מעבר מסכים
    private int countdown = 3;     // טיימר ספירה לאחור בשניות
    private Handler handler = new Handler(); // מטפל בעדכוני ספירה לאחור
    private Context context;        // הקשר לצורך ניווט
    private long startTime;         // זמן תחילת המשחק

    // ----- בנאים -----

    public MazeGridView(Context context) {
        super(context);
        this.context = context;
        init(); // אתחול הגדרות המשחק
    }

    public MazeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(); // אתחול הגדרות המשחק
    }

    // ----- אתחול -----

    private void init() {
        paint = new Paint();              // יצירת אובייקט Paint
        paint.setAntiAlias(true);         // ציור חלק

        // בחירת מבוך אקראי מהספרייה
        Random random = new Random();
        maze = MAZE_LIBRARY[random.nextInt(MAZE_LIBRARY.length)];
    }

    /**
     * שיטה ציבורית להתחלת הספירה לאחור ואז המשחק.
     * מאפשרת לאקטיביטי להציג הסבר לפני שהטיימר מתחיל.
     */
    public void beginGame() {
        startCountdown();
    }

    // ----- טיפול בגודל -----

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // חישוב גודל משבצת כך שהמבוך יתאים למסך
        cellSize = Math.min(w / (float) GRID_SIZE, h / (float) GRID_SIZE);

        // מרכוז המבוך במסך
        offsetX = (w - cellSize * GRID_SIZE) / 2;
        offsetY = (h - cellSize * GRID_SIZE) / 2;

        // מיקום הכדור במשבצת הפנויה הראשונה
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

    // ----- ציור -----

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMaze(canvas); // ציור קירות המבוך והיעד
        drawBall(canvas); // ציור הכדור האדום

        if (!gameStarted && !isNavigating && countdown > 0) {
            drawCountdown(canvas); // הצגת ספירה לאחור לפני תחילת המשחק
        }
    }

    private void drawMaze(Canvas canvas) {
        // קבלת הגדרת צבע הרקע הנוכחית כדי להחליט על צבע הקיר
        String bgColor = ProgressStorage.getAppPrefs(context)
                .getString("bg_color", "white");
        int wallColor = bgColor.equals("black") ? Color.WHITE : Color.BLACK;

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (maze[r][c] == 1) paint.setColor(wallColor); // קיר
                else if (maze[r][c] == 2) paint.setColor(Color.GREEN); // יעד
                else continue; // נתיב (לא מצויר)

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
        // לוודא שטקסט הספירה לאחור נראה על רקע שחור
        String bgColor = ProgressStorage.getAppPrefs(context)
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

    // ----- לוגיקת ספירה לאחור -----

    private void startCountdown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    countdown--;
                    invalidate(); // ציור מחדש של התצוגה
                    handler.postDelayed(this, 1000);
                } else {
                    gameStarted = true;
                    startTime = System.currentTimeMillis();
                    invalidate();
                }
            }
        }, 1000);
    }

    // ----- תנועת הכדור -----

    public void updateBall(float tiltX, float tiltY) {
        if (!gameStarted || isNavigating) return; // התעלמות מתנועה בזמן ספירה לאחור או ניווט

        float speed = cellSize / 200; // מהירות תנועה
        float newX = ballX - tiltX * speed;
        float newY = ballY + tiltY * speed;

        // תיבת התחימה של הכדור לזיהוי התנגשות
        float left = newX - BALL_RADIUS;
        float right = newX + BALL_RADIUS;
        float top = newY - BALL_RADIUS;
        float bottom = newY + BALL_RADIUS;

        // בדיקת כל ארבעת הפינות של תיבת התחימה של הכדור
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
                canMove = false; // מחוץ לגבולות
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

        invalidate(); // עדכון התצוגה
    }

    private void navigateToFailureScreen() {
        if (isNavigating) return;
        isNavigating = true;
        gameStarted = false;

        // תיעוד פגיעה בקיר עבור הישג פרפקציוניסט
        ProgressStorage.recordWallHit();

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