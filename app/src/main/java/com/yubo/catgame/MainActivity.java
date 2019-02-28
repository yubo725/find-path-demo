package com.yubo.catgame;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yubo.catgame.model.GameView;
import com.yubo.catgame.utils.Utils;

public class MainActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout rootView = findViewById(R.id.root_view);
        gameView = new GameView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int[] screenSize = Utils.getScreenSize(this);
        if (screenSize != null) {
            params.leftMargin = (screenSize[0] - gameView.getGamePanelWidth()) / 2;
            params.topMargin = (screenSize[1] - gameView.getGamePanelHeight()) / 2;
            gameView.setLayoutParams(params);
        }
        rootView.addView(gameView);
    }
}
