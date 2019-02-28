package com.yubo.catgame.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.yubo.catgame.model.Cell;

public class Utils {

    public static int[] getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
        }
        return null;
    }

    public static int[] getGamePanelSize(Context context) {
        int[] screenSize = getScreenSize(context);
        if (screenSize != null) {
            int cellCountHorizontal = (int) ((screenSize[0] * 0.9f) / Cell.CELL_WIDTH);
            int cellCountVertical = (int) ((screenSize[1] * 0.9f) / Cell.CELL_WIDTH);
            return new int[]{cellCountHorizontal, cellCountVertical};
        }
        return null;
    }

}
