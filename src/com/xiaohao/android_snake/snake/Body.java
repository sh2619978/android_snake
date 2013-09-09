package com.xiaohao.android_snake.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;

public class Body {

    private Shape rectangle; // 定义方块，模拟蛇的每一个身体
    private ShapeDrawable drawable;
    private int forward; // 定义每一个方块的运动方向
    public static final int SIZE = 30; // 方块的大小

    public Body() {
        rectangle = new RectShape();
        drawable = new ShapeDrawable(rectangle);
        drawable.setIntrinsicHeight(SIZE);
        drawable.setIntrinsicWidth(SIZE);

        Paint paint = drawable.getPaint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
    }

    public Shape getRectangle() {
        return rectangle;
    }

    public void setRectangle(Shape rectangle) {
        this.rectangle = rectangle;
    }

    public int getForward() {
        return forward;
    }

    public void setForward(int forward) {
        this.forward = forward;
    }

    public void setLocation(int x, int y) {
        drawable.setBounds(x, y, x + SIZE, y + SIZE);
    }

    public void setLocation(Rect bounds) {
        drawable.setBounds(bounds);
    }

    public int getX() {
        return drawable.getBounds().left;
    }

    public int getY() {
        return drawable.getBounds().top;
    }

    public Rect getLocation() {
        return drawable.getBounds();
    }

    public void draw(Canvas canvas) {
        drawable.draw(canvas);
    }

}
