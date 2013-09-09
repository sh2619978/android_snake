package com.xiaohao.android_snake.snake;

import java.util.ArrayList;
import java.util.Timer;

import android.graphics.Canvas;

import com.xiaohao.android_snake.SecondActivity;

public class Snake {
    private ArrayList<Body> bodyList; // 定义数组列表存储Body类，即整个蛇
    private Timer timer; // 定时器，同MainFrame中的timer
    private boolean speedup; // 定义布尔类型，表示蛇是否处于加速状态
    private int delay;
    private static final int INIT_SIZE = 3; // 定义蛇初始长度
    public static final int UP = 1; // 定义向上常量
    public static final int RIGHT = 2; // 定义向右常量
    public static final int DOWN = 3; // 定义向下常量
    public static final int LEFT = 4; // 定义向左常量
    public static final int SPEED = 300; // 蛇普通速度时两次走动的时间间隔，单位ms
    public static final int SPEED_UP = 50; // 蛇加速时两次走动的时间间隔，单位ms

    public Snake() {
        bodyList = new ArrayList<Body>();
    }

    // 初始化蛇，个数与运动方向
    public void init() {
        for (int i = 0; i < INIT_SIZE; i++) {
            Body body = new Body();
            body.setLocation(i * Body.SIZE + 3 * Body.SIZE, 3 * Body.SIZE);
            body.setForward(RIGHT);
            bodyList.add(body);
        }
    }

    // 吃食物，增加蛇长度
    public void addBody(Body food) {
        Body head = getHead();
        food.setForward(head.getForward());
        bodyList.add(food);
    }

    // 设置每一个方块的运动方向，让每一个方块的运动方向和前一个方块的一致
    private void setEachForward() {
        for (int i = 1; i < bodyList.size(); i++) {
            int beforeForward = bodyList.get(i).getForward();
            bodyList.get(i - 1).setForward(beforeForward);
        }
    }

    // 使整个方块组向前移动一格，并调用setEachForward函数对每个方块重新设置方向
    public void run() {
        for (int i = bodyList.size() - 1; i >= 0; i--) {
            Body body = bodyList.get(i);
            int forward = body.getForward();

            switch (forward) {
            case RIGHT:
                body.setLocation(body.getX() + Body.SIZE, body.getY());
                break;
            case DOWN:
                body.setLocation(body.getX(), body.getY() + Body.SIZE);
                break;
            case LEFT:
                body.setLocation(body.getX() - Body.SIZE, body.getY());
                break;
            case UP:
                body.setLocation(body.getX(), body.getY() - Body.SIZE);
                break;
            default:
                break;
            }
        }
        setEachForward(); // 对每个方块重新设置方向
    }

    // 根据指定方向来设置头方块的运动方向，如果是同方向则进行加速
    public void turn(int forward) {
        int currentForward = getHead().getForward();
        if (currentForward == forward) // 同方向进行加速
        {
            delay = SPEED_UP;
            setSpeedup(true);
        } else if (Math.abs(currentForward - forward) != 2) // 不同方向则拐弯
        {
            getHead().setForward(forward);
        }
        // 相反方向则不进行任何改变，因为不能倒退
    }

    // 判断snake是否包含坐标(x, y)
    public boolean contains(int x, int y) {
        for (Body b : bodyList) {
            if (b.getX() == x && b.getY() == y) {
                return true;
            }
        }
        return false;
    }

    // 判断蛇是否将要碰到食物
    public boolean isEating(Body food) {
        Body head = getHead();
        switch (head.getForward()) {
        case Snake.RIGHT:
            if ((head.getX() + Body.SIZE) == food.getX() && head.getY() == food.getY()) {
                return true;
            }
            break;
        case Snake.DOWN:
            if ((head.getY() + Body.SIZE) == food.getY() && head.getX() == food.getX()) {
                return true;
            }
            break;
        case Snake.LEFT:
            if ((head.getX() - Body.SIZE) == food.getX() && head.getY() == food.getY()) {
                return true;
            }
            break;
        case Snake.UP:
            if ((head.getY() - Body.SIZE) == food.getY() && head.getX() == food.getX()) {
                return true;
            }
            break;
        default:
            break;
        }
        return false;
    }

    // 判断蛇是否将要撞到自己
    public boolean isHittingSelf() {
        for (int i = 0; i < getSize() - 1; i++) {
            if (getHead().getX() == bodyList.get(i).getX() && getHead().getY() == bodyList.get(i).getY()) {
                return true;
            }
        }
        return false;
    }

    // 判断蛇是否将要撞到墙
    public boolean isHittingWall() {
        if (getHead().getX() < 0 || getHead().getX() >= SecondActivity.MyView.GAME_WIDTH) {
            return true;
        }
        if (getHead().getY() < 0 || getHead().getY() >= SecondActivity.MyView.GAME_HEIGHT) {
            return true;
        }
        return false;
    }

    public ArrayList<Body> getBodyList() {
        return bodyList;
    }

    public void setBodyList(ArrayList<Body> bodyList) {
        this.bodyList = bodyList;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    // 得到蛇的头部
    public Body getHead() {
        return bodyList.get(getSize() - 1);
    }

    // 得到蛇的总长度
    public int getSize() {
        return bodyList.size();
    }

    public boolean isSpeedup() {
        return speedup;
    }

    public void setSpeedup(boolean speedup) {
        this.speedup = speedup;
    }

    public int getDelay() {
        return delay;
    }

    public void draw(Canvas canvas) {
        for (Body body : bodyList) {
            body.draw(canvas);
        }
    }

}
