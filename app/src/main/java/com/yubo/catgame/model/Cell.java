package com.yubo.catgame.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.yubo.catgame.R;

public class Cell {

    public static final int CELL_WIDTH = 120;
    private static final int BORDER_WIDTH = 1;
    private static final int BM_WIDTH = 60;
    private static final int TEXT_SIZE = 26;

    private Context context;
    private boolean isWall;
    private int posX, posY;
    private int wallColor = Color.BLACK;
    private int roadColor = Color.WHITE;
    private int borderColor = Color.GRAY;

    private Paint borderPaint;
    private Paint fillPaint;
    private Paint bitmapPaint;
    private Paint textPaint;

    private boolean isCat = false;
    private boolean isBone = false;

    // 是否已计算过G, H值
    private boolean isCalculated = false;

    private int G = 0, H = 0;

    public Cell(Context context, boolean isWall, int posX, int posY) {
        this.context = context;

        this.isWall = isWall;
        this.posX = posX;
        this.posY = posY;

        this.borderPaint = new Paint();
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setColor(borderColor);

        this.fillPaint = new Paint();
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setColor(isWall ? wallColor : roadColor);

        this.bitmapPaint = new Paint();
        this.bitmapPaint.setAntiAlias(true);

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(TEXT_SIZE);
    }

    public void setSelected() {
        this.fillPaint.setColor(Color.parseColor("#6699FF"));
    }

    public void drawMe(Canvas canvas) {
        int left = posX * CELL_WIDTH;
        int top = posY * CELL_WIDTH;
        int right = (posX + 1) * CELL_WIDTH;
        int bottom = (posY + 1) * CELL_WIDTH;
        canvas.drawRect(left, top, right, bottom, borderPaint);
        canvas.drawRect(left + BORDER_WIDTH, top + BORDER_WIDTH,
                right - BORDER_WIDTH, bottom - BORDER_WIDTH, fillPaint);
        if (isCat && !isWall) {
            drawImg(canvas, R.mipmap.cat);
        }
        if (isBone && !isWall) {
            drawImg(canvas, R.mipmap.fishbone);
        }
        if (G > 0 && H > 0) {
            canvas.drawText("F: " + (G + H), posX * CELL_WIDTH + 2, posY * CELL_WIDTH + TEXT_SIZE, textPaint);
            canvas.drawText("G: " + G, posX * CELL_WIDTH + 2, posY * CELL_WIDTH + TEXT_SIZE * 2 + 3, textPaint);
            canvas.drawText("H: " + H, posX * CELL_WIDTH + 2, posY * CELL_WIDTH + TEXT_SIZE * 3 + 3, textPaint);
        }
    }

    private void drawImg(Canvas canvas, int imgResId) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), imgResId);
        Rect srcRect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
        int deltaX = (CELL_WIDTH - BM_WIDTH) / 2;
        int deltaY = (CELL_WIDTH - BM_WIDTH) / 2;
        int destLeft = posX * CELL_WIDTH + deltaX;
        int destTop = posY * CELL_WIDTH + deltaY;
        Rect destRect = new Rect(destLeft, destTop, destLeft + BM_WIDTH, destTop + BM_WIDTH);
        canvas.drawBitmap(bm, srcRect, destRect, bitmapPaint);
    }

    public boolean isWall() {
        return isWall;
    }

    public void setWall(boolean wall) {
        isWall = wall;
    }

    public boolean isCat() {
        return isCat;
    }

    public void setCat(boolean cat) {
        isCat = cat;
    }

    public boolean isBone() {
        return isBone;
    }

    public void setBone(boolean bone) {
        isBone = bone;
    }

    public boolean isCalculated() {
        return isCalculated;
    }

    public void setCalculated(boolean calculated) {
        isCalculated = calculated;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getH() {
        return H;
    }

    public void setH(int h) {
        H = h;
    }
}
