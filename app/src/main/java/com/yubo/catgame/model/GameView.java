package com.yubo.catgame.model;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Toast;

import com.yubo.catgame.utils.Utils;

public class GameView extends View {

    private int step = 1;
    private Cell[][] dataArr;
    private int rows, cols;
    private int gamePanelWidth, gamePanelHeight;

    // 猫的起始坐标
    private int catStartX, catStartY;
    // 骨头的坐标
    private int boneX, boneY;
    // 当前放方块的坐标
    private int curCellX, curCellY;

    private boolean isFindingPath = false;

    public GameView(Context context) {
        super(context);
        init();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startFindPath();
            }
        });
    }

    private void init() {
        int[] gamePanelSize = Utils.getGamePanelSize(getContext());
        if (gamePanelSize != null) {
            gamePanelWidth = Cell.CELL_WIDTH * gamePanelSize[0];
            gamePanelHeight = Cell.CELL_WIDTH * gamePanelSize[1];
            generateArr(gamePanelSize);
        }
    }

    public int getGamePanelWidth() {
        return gamePanelWidth;
    }

    public int getGamePanelHeight() {
        return gamePanelHeight;
    }

    private void generateArr(int[] gamePanelSize) {
        rows = gamePanelSize[1]; // 行数
        cols = gamePanelSize[0]; // 列数
        dataArr = new Cell[rows][cols];
        for (int row = 0; row < rows; row++) {
            dataArr[row] = new Cell[cols];
            for (int col = 0; col < cols; col++) {
                boolean isWall = false;
                if (col == 5 && row > 0 && row < rows - 1) {
                    isWall = true;
                }
                Cell cell = new Cell(getContext(), isWall, col, row);
                if (row == 2 && col == 2) {
                    cell.setCat(true);
                    catStartX = col;
                    catStartY = row;
                }
                if (row == rows - 4 && col == cols - 2) {
                    cell.setBone(true);
                    boneX = col;
                    boneY = row;
                }
                dataArr[row][col] = cell;
            }
        }
    }

    /* 开始寻找路径
     * 公式：F = G + H
     * G: 从开始点到当前方块的移动量
     * H: 从当前方块到目标点的移动量估算值
     */
    public void startFindPath() {
        if (isFindingPath) return;
        isFindingPath = true;
        curCellX = catStartX;
        curCellY = catStartY;
        dataArr[curCellY][curCellX].setSelected();

        findPath();
    }

    private void findPath() {
        if (curCellX == boneX && curCellY == boneY) {
            isFindingPath = false;
            Toast.makeText(getContext(), "over!", Toast.LENGTH_SHORT).show();
            return;
        }
        // 计算上下左右四个方向的Cell的G, H值
        int min = Integer.MAX_VALUE;
        int nextX = curCellX, nextY = curCellY, f;
        if (curCellX + 1 < cols) {
            // right
            f = calculate(curCellX + 1, curCellY);
            if (min > f) {
                min = f;
                nextX = curCellX + 1;
                nextY = curCellY;
            }
        }
        if (curCellY + 1 < rows) {
            // bottom
            f = calculate(curCellX, curCellY + 1);
            if (min > f) {
                min = f;
                nextX = curCellX;
                nextY = curCellY + 1;
            }
        }
        if (curCellX > 0) {
            // left
            f = calculate(curCellX - 1, curCellY);
            if (min > f) {
                min = f;
                nextX = curCellX - 1;
                nextY = curCellY;
            }
        }
        if (curCellY > 0) {
            // top
            f = calculate(curCellX, curCellY - 1);
            if (min > f) {
                min = f;
                nextX = curCellX;
                nextY = curCellY - 1;
            }
        }
        curCellX = nextX;
        curCellY = nextY;
        dataArr[curCellY][curCellX].setSelected();
        ++step;
        invalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                findPath();
            }
        }, 1000);
    }

    private int calculate(int x, int y) {
        Cell cell = dataArr[y][x];
        if (cell.isWall() || cell.isCalculated()) {
            return Integer.MAX_VALUE;
        }
        int G = step;
        int H = getH(x, y);
        cell.setG(G);
        cell.setH(H);
        cell.setCalculated(true);
        return G + H;
    }

    private int getH(int x, int y) {
        return Math.abs(x - boneX) + Math.abs(y - boneY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                dataArr[row][col].drawMe(canvas);
            }
        }
    }
}
