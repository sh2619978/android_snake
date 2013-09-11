package com.xiaohao.android_snake;

import java.util.Random;

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
        private Thread timer; // 定义一个定时器，用来控制蛇和食物的绘画
        private Random random = new Random();
        private volatile boolean gameover;

        private volatile boolean userHasTurn; // 玩家一次转向手势是否完成

        public static final int GAME_WIDTH = Body.SIZE * 12;
        public static final int GAME_HEIGHT = Body.SIZE * 20;
        public static final int GESTURE_UP = 1;
        public static final int GESTURE_DOWN = 2;
        public static final int GESTURE_LEFT = 3;
        public static final int GESTURE_RIGHT = 4;

        public MyView(Context context) {
            super(context);

            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            gestureDetector = new GestureDetector(context, this);

            snake = new Snake();
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

            boolean result = gestureDetector.onTouchEvent(event);
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                userHasTurn = false;

                snake.setSpeedup(false);
                snake.setPeriod(Snake.SPEED);
            }
            return result;
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

            timer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Log.e("xiaohao", "timer draw");

                        if (snake.isEating(food)) {
                            snake.addBody(food);
                            food = new Body();
                            randomPosition(food);
                        } else {
                            snake.run();
                            if (snake.isHittingSelf()) {
                                gameover = true;
                                break;
                            }
                            if (snake.isHittingWall()) {
                                gameover = true;
                                break;
                            }
                        }
                        drawOnce();

                        try {
                            Thread.sleep(snake.getPeriod());
                        } catch (InterruptedException e) {

                        }
                    }
                }
            });
            timer.start();
            snake.setTimer(timer);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            timer.interrupt();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.e("xiaohao", "onDown");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("xiaohao", "onFling");
            Log.e("xiaohao", "velocityX: " + velocityX + ", velocityY: " + velocityY);

            // float xd = velocityX;
            // float yd = velocityY;
            //
            // int flingTurn = getGestureDirection(xd, yd);
            //
            // switch (flingTurn) {
            // case GESTURE_RIGHT:
            // snake.turn(Snake.RIGHT);
            // break;
            // case GESTURE_DOWN:
            // snake.turn(Snake.DOWN);
            // break;
            // case GESTURE_LEFT:
            // snake.turn(Snake.LEFT);
            // break;
            // case GESTURE_UP:
            // snake.turn(Snake.UP);
            // break;
            // default:
            // break;
            // }
            return true;
        }

        private int getGestureDirection(float xd, float yd) {
            if (Math.abs(xd) > Math.abs(yd)) {
                yd = 0f;
            } else if (Math.abs(xd) < Math.abs(yd)) {
                xd = 0f;
            }

            if (yd == 0) {
                if (xd > 0) {
                    Log.e("xiaohao", "右");
                    return GESTURE_RIGHT;
                } else if (xd < 0) {
                    Log.e("xiaohao", "左");
                    return GESTURE_LEFT;
                }
            } else if (xd == 0) {
                if (yd > 0) {
                    Log.e("xiaohao", "下");
                    return GESTURE_DOWN;
                } else if (yd < 0) {
                    Log.e("xiaohao", "上");
                    return GESTURE_UP;
                }
            }
            return -1;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.e("xiaohao", "onLongPress");
            snake.setSpeedup(true);
            snake.setPeriod(Snake.SPEED_UP);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("xiaohao", "onScroll");
            Log.e("xiaohao", "e1.getRawX(): " + e1.getRawX() + ", e1.getRawY(): " + e1.getRawY());
            Log.e("xiaohao", "e2.getRawX(): " + e2.getRawX() + ", e2.getRawY(): " + e2.getRawY());

            if (userHasTurn) {
                return true;
            }

            float x1 = e1.getRawX();
            float y1 = e1.getRawY();
            float x2 = e2.getRawX();
            float y2 = e2.getRawY();

            float xd = x2 - x1;
            float yd = y2 - y1;
            if (Math.abs(xd) < 5 && Math.abs(yd) < 5) {
                return true;
            }

            int scrollTurn = getGestureDirection(xd, yd);
            if (scrollTurn != -1) {
                userHasTurn = true;
            }

            switch (scrollTurn) {
            case GESTURE_RIGHT:
                snake.turn(Snake.RIGHT);
                break;
            case GESTURE_DOWN:
                snake.turn(Snake.DOWN);
                break;
            case GESTURE_LEFT:
                snake.turn(Snake.LEFT);
                break;
            case GESTURE_UP:
                snake.turn(Snake.UP);
                break;
            default:
                break;
            }

            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.e("xiaohao", "onShowPress");

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e("xiaohao", "onSingleTapUp");
            return true;
        }
    }

}
