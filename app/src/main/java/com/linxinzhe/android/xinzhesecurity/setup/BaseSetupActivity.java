package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class BaseSetupActivity extends ActionBarActivity {

    private GestureDetector gestureDetector;
    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //方便继承的类获取sp
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //设置滑动切换设置页面
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getRawX() - e1.getRawX() > 200) {
                    goPrevSetup();
                    return true;
                }
                if (e1.getRawX() - e2.getRawX() > 200) {
                    goNextSetup();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void goNextSetup();


    public abstract void goPrevSetup();

}
