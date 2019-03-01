package com.yubo.catgame.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yubo.catgame.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameView extends View {

    // 放到openList中的方块用蓝色表示
    private static final int OPEN_COLOR = Color.parseColor("#6699FF");
    // 放到closedList中的方块用红色表示
    private static final int CLOSED_COLOR = Color.parseColor("#CC3366");
    // 找到的路径用蓝色表示
    private static final int PATH_COLOR = Color.BLUE;

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

    private List<Cell> openList = new ArrayList<>();
    private List<Cell> closeList = new ArrayList<>();

    private Cell currentStep;

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

        Cell startStep = dataArr[curCellY][curCellX];
        startStep.setG(0);
        startStep.setH(getH(curCellX, curCellY));

        openList.clear();
        closeList.clear();

        // 起点加入openList
        addToOpenList(startStep);

        findPath();
    }

    private void findPath() {
        // 当前步是openList中F值最小的一步
        currentStep = getMinFCellInOpenList();

        Log.d("yubo-test", String.format(Locale.getDefault(), "[%d, %d]", currentStep.getPosX(), currentStep.getPosY()));

        if (currentStep.isBone()) {
            Toast.makeText(getContext(), "find path!", Toast.LENGTH_SHORT).show();
            showPath();
            return;
        }

        // 将当前步加入closeList
        addToCloseList(currentStep);

        // 获取当前步的四个方向的Cell并加入openList
        // left
        Cell currentStepLeftCell = getCellAround(currentStep.getPosX() - 1, currentStep.getPosY());
        if (currentStepLeftCell != null) {
            if (!openList.contains(currentStepLeftCell)) {
                // 不在openList中
                currentStepLeftCell.setParent(currentStep);
                addToOpenList(currentStepLeftCell);
            } else {
                // 已在openList中，从当前路径走到该节点时，该节点的G值是否更小
                checkIfGIsSmaller(currentStepLeftCell);
            }
        }
        // top
        Cell currentStepTopCell = getCellAround(currentStep.getPosX(), currentStep.getPosY() - 1);
        if (currentStepTopCell != null) {
            if (!openList.contains(currentStepTopCell)) {
                // 不在openList中
                currentStepTopCell.setParent(currentStep);
                addToOpenList(currentStepTopCell);
            } else {
                // 已在openList中，从当前路径走到该节点时，该节点的G值是否更小
                checkIfGIsSmaller(currentStepTopCell);
            }
        }
        // right
        Cell currentStepRightCell = getCellAround(currentStep.getPosX() + 1, currentStep.getPosY());
        if (currentStepRightCell != null) {
            if (!openList.contains(currentStepRightCell)) {
                // 不在openList中
                currentStepRightCell.setParent(currentStep);
                addToOpenList(currentStepRightCell);
            } else {
                // 已在openList中，从当前路径走到该节点时，该节点的G值是否更小
                checkIfGIsSmaller(currentStepRightCell);
            }
        }
        // bottom
        Cell currentStepBottomCell = getCellAround(currentStep.getPosX(), currentStep.getPosY() + 1);
        if (currentStepBottomCell != null) {
            if (!openList.contains(currentStepBottomCell)) {
                // 不在openList中
                currentStepBottomCell.setParent(currentStep);
                addToOpenList(currentStepBottomCell);
            } else {
                // 已在openList中，从当前路径走到该节点时，该节点的G值是否更小
                checkIfGIsSmaller(currentStepBottomCell);
            }
        }

        invalidate();

        if (!openList.isEmpty()) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    findPath();
                }
            }, 600);
        } else {
            Toast.makeText(getContext(), "not found!", Toast.LENGTH_SHORT).show();
        }
    }

    // 显示找到的路径
    private void showPath() {
        // 先把所有的格子都重置颜色
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                dataArr[row][col].resetColor();
            }
        }
        // 再从终点开始根据parent一级级绘制
        Cell cell = dataArr[boneY][boneX];
        drawPathColor(cell);
    }

    private void drawPathColor(Cell cell) {
        cell.setBgColor(PATH_COLOR);
        invalidate();
        if (!cell.isCat()) {
            final Cell preCell = cell.getParent();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawPathColor(preCell);
                }
            }, 100);
        }
    }

    /**
     * 将cell添加到openList中
     * @param cell 当前处理的方块
     */
    private void addToOpenList(Cell cell) {
        if (!openList.contains(cell)) {
            cell.setBgColor(OPEN_COLOR);
            openList.add(cell);
        }
    }

    /**
     * 将cell添加到closeList中
     * @param cell 当前处理的方块
     */
    private void addToCloseList(Cell cell) {
        if (openList.contains(cell)) {
            openList.remove(cell);
        }
        if (!closeList.contains(cell)) {
            cell.setBgColor(CLOSED_COLOR);
            closeList.add(cell);
        }
    }

    /**
     * 检查从当前路径走到cell时，cell是否有更小的G值
     *
     * @param cell cell为当前要检查的方块，一定在openList中
     */
    private void checkIfGIsSmaller(Cell cell) {
        int g = currentStep.getG() + 1;
        if (g < cell.getG()) {
            // 从当前路径走到cell有更小的G值，更新cell的parent
            cell.setParent(currentStep);
        }
    }

    /**
     * 获取openList中最小F值的方块
     * @return 拥有最小F值的方块
     */
    public Cell getMinFCellInOpenList() {
        if (openList.size() == 1) {
            return openList.get(0);
        }
        Cell first = openList.get(0);
        Cell result = first;
        int min = first.getF();
        for (int i = 1; i < openList.size(); i++) {
            Cell item = openList.get(i);
            if (min >= item.getF()) {
                min = item.getF();
                result = item;
            }
        }
        return result;
    }

    // 获取周围的Cell， x, y为这个Cell的坐标
    private Cell getCellAround(int x, int y) {
        if (x < 0 || x >= cols) {
            return null;
        }
        if (y < 0 || y >= rows) {
            return null;
        }
        Cell cell = dataArr[y][x];
        // 如果是墙壁或者在closeList中，则忽略
        if (cell.isWall() || closeList.contains(cell)) {
            return null;
        }
        int G = currentStep.getG() + 1;
        int H = getH(x, y);
        cell.setG(G);
        cell.setH(H);
        return cell;
    }

    // 计算曼哈顿距离（听起来好牛批，其实就是两个点的横向与纵向距离和）
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
