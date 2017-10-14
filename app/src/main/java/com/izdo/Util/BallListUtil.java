package com.izdo.Util;

import com.izdo.Bean.Ball;
import com.izdo.R;

import java.util.ArrayList;

/**
 * Created by iZdo on 2017/10/13.
 */

public class BallListUtil {

    //    private String[] colors = {"默认灰", "浅蓝色", "浅红色", "浅绿色", "什么黄", "基佬紫"};
    private static String[] colors = {"#696969", "#d7433a", "#8dbe2e", "#ef8750", "#00aaff"};
    private static int[] colorImages = {R.drawable.ball_696969, R.drawable.ball_d7433a, R.drawable.ball_8dbe2e, R.drawable.ball_ef8750, R.drawable.ball_00aaff};
    private static String[] colorsText = {"默认灰", "浅红色", "浅绿色", "黄色", "蓝"};
    private static int[] colorCheckedImages = {R.drawable.ball_checked_696969, R.drawable.ball_checked_d7433a, R.drawable.ball_checked_8dbe2e, R.drawable.ball_checked_ef8750, R.drawable.ball_checked_00aaff};

    private static ArrayList<Ball> ballList;

    public static void init() {

        ballList = new ArrayList<>();

        for (int i = 0; i < colors.length; i++) {

            Ball ball = new Ball();
            ball.setColor(colors[i]);
            ball.setColorImage(colorImages[i]);
            ball.setColorText(colorsText[i]);
            ball.setColorCheckedImage(colorCheckedImages[i]);

            ballList.add(ball);

        }
    }

    public static ArrayList<Ball> getBallList() {
        return ballList;
    }
}
