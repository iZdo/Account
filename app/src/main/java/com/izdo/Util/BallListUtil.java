package com.izdo.Util;

import com.izdo.Bean.Ball;
import com.izdo.R;

import java.util.ArrayList;

/**
 * Created by iZdo on 2017/10/13.
 */

public class BallListUtil {

    private static String[] colors = {"#696969", "#d7433a", "#ef8750", "#f9f154","#8dbe2e", "#00aaff","#285afb","#b44dee"};
    private static int[] colorImages = {R.drawable.ball_696969, R.drawable.ball_d7433a, R.drawable.ball_ef8750, R.drawable.ball_f9f154,
            R.drawable.ball_8dbe2e,R.drawable.ball_00aaff,R.drawable.ball_285afb,R.drawable.ball_b44dee};
    private static String[] colorsText = {"默认灰", "大娃", "二娃","三娃", "四娃", "五娃","六娃","七娃"};
    private static int[] colorCheckedImages = {R.drawable.ball_checked_696969, R.drawable.ball_checked_d7433a, R.drawable.ball_checked_ef8750, R.drawable.ball_checked_f9f154,
            R.drawable.ball_checked_8dbe2e,R.drawable.ball_checked_00aaff,R.drawable.ball_checked_285afb,R.drawable.ball_checked_b44dee};

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
