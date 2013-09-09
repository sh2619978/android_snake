package com.xiaohao.android_snake;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xiaohao.android_snake.snake.Body;
import com.xiaohao.android_snake.snake.Snake;

public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MyView myView = new MyView(this);
        setContentView(myView);

        // setContentView(R.layout.activity_second);

        // String text = getIntent().getStringExtra("message");
        //
        // TextView secondTextView = (TextView) findViewById(R.id.secondTextView);
        // secondTextView.setText("" + text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    public static class MyView extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {

        private SurfaceHolder surfaceHolder;
        private GestureDetector gestureDetector;

        private Snake snake; // 创建蛇的引用
        private Body food; // 创建食物的引用
        private Timer timer; // 定义一个定时器，用来控制蛇和食物的绘画
        private Random random = new Random();
        private boolean gameover;

        public static final int GAME_WIDTH = Body.SIZE * 12;
        public static final int GAME_HEIGHT = Body.SIZE * 20;
        public static final int FLING_UP = 1;
        public static final int FLING_DOWN = 2;
        public static final int FLING_LEFT = 3;
        public static final int FLING_RIGHT = 4;

        public MyView(Context context) {
            super(context);

            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            gestureDetector = new GestureDetector(context, this);

            snake = new Snake();
            timer = new Timer();
            snake.setTimer(timer);
            snake.init();

            food = new Body();
            randomPosition(food);
        }

        // 随机位置产生食物，蛇身之外
        public void randomPosition(Body food) {
            int x = random.nextInt(GAME_WIDTH / Body.SIZE) * Body.SIZE;
            int y = random.nextInt(GAME_HEIGHT / Body.SIZE) * Body.SIZE;
            while (snake.contains(x, y)) {
                x = random.nextInt(GAME_WIDTH / Body.SIZE) * Body.SIZE;
                y = random.nextInt(GAME_HEIGHT / Body.SIZE) * Body.SIZE;
            }
            food.setLocation(x, y);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.e("xiaohao", "onTouchEvent");
            return gestureDetector.onTouchEvent(event);
        }

        public void drawOnce() {
            Canvas canvas = surfaceHolder.lockCanvas(new Rect(0, 0, GAME_WIDTH, GAME_HEIGHT));
            canvas.drawColor(Color.WHITE);

            snake.draw(canvas);
            food.draw(canvas);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Canvas canvas = surfaceHolder.lockCanvas(new Rect(0, 0, GAME_WIDTH + 10, GAME_HEIGHT + 10));
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawRect(0, 0, GAME_WIDTH, GAME_HEIGHT, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);

            drawOnce();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.e("xiaohao", "timer draw");

                    if (snake.isEating(food)) {
                        snake.addBody(food);
                        food = new Body();
                        randomPosition(food);
                    } else {
                        snake.run();
                        if (snake.isHittingSelf()) {
                            timer.cancel();
                            gameover = true;
                            return;
                        }
                        if (snake.isHittingWall()) {
                            timer.cancel();
                            gameover = true;
                            return;
                        }
                    }
                    drawOnce();
                }
            }, 1000, Snake.SPEED);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            timer.cancel();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.e("xiaohao", "onDown");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("xiaohao", "onFling");

            float x1 = e1.getRawX();
            float y1 = e1.getRawY();
            float x2 = e2.getRawX();
            float y2 = e2.getRawY();

            Log.e("xiaohao", x1 + "," + y1 + " --> " + x2 + "," + y2);

            float xd = x2 - x1;
            float yd = y2 - y1;

            if (Math.abs(xd) > Math.abs(yd)) {
                yd = 0f;
            } else if (Math.abs(xd) < Math.abs(yd)) {
                xd = 0f;
            }

            int flingTurn = -1;

            if (yd == 0) {
                if (xd > 0) {
                    flingTurn = FLING_RIGHT;
                    Log.e("xiaohao", "右");
                } else if (xd < 0) {
                    flingTurn = FLING_LEFT;
                    Log.e("xiaohao", "左");
                }
            } else if (xd == 0) {
                if (yd > 0) {
                    flingTurn = FLING_DOWN;
                    Log.e("xiaohao", "下");
                } else if (yd < 0) {
                    flingTurn = FLING_UP;
                    Log.e("xiaohao", "上");
                }
            }

            switch (flingTurn) {
            case FLING_RIGHT:
                snake.turn(Snake.RIGHT);
                break;
            case FLING_DOWN:
                snake.turn(Snake.DOWN);
                break;
            case FLING_LEFT:
                snake.turn(Snake.LEFT);
                break;
            case FLING_UP:
                snake.turn(Snake.UP);
                break;
            default:
                break;
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("xiaohao", "onScroll");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }
    }

}
