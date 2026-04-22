package com.example.lyricfloatingwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class LyricWindow implements View.OnTouchListener {

    private Context mContext;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    private View rootLayout;

    private TextView tv_lyric;

    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;

    private boolean isInit = false;
    private boolean isMoving = false;


    //私有静态实例，私有构造方法
    private static LyricWindow INSTANCE;
    private LyricWindow(Context context) {
        mContext = context;
        initLyricWindow();
        isInit = true;
    }
    //获取实例方法
    public static synchronized LyricWindow getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LyricWindow(context.getApplicationContext());
        }
        return INSTANCE;
    }

    //初始化悬浮窗
    private void initLyricWindow() {

        rootLayout = LayoutInflater.from(mContext).inflate(R.layout.lyric_window_layout, null);
        tv_lyric = rootLayout.findViewById(R.id.tv_lyric);

        rootLayout.setOnTouchListener(this);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// 不抢占焦点，点击穿透至下方应用
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        defaultPosition();//设置默认位置
    }

    private void defaultPosition() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mWindowParams.x = 0;
        mWindowParams.y = 50;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        switch (motionEvent.getAction()) {

            //按下：记录初始坐标
            case MotionEvent.ACTION_DOWN:
                mInViewX = motionEvent.getX();
                mInViewY = motionEvent.getY();
                mDownInScreenX = motionEvent.getRawX();
                mDownInScreenY = motionEvent.getRawY();
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY();
                isMoving = false;
                break;

            //滑动：计算新位置，更新悬浮窗
            case MotionEvent.ACTION_MOVE:
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY();
                // 拖拽公式：悬浮窗左上角坐标 = 手指当前屏幕坐标 - 手指按下时在悬浮窗内的偏移量
                mWindowParams.x = (int) (mInScreenX - mInViewX);
                mWindowParams.y = (int) (mInScreenY - mInViewY);
                // 更新悬浮窗位置
                mWindowManager.updateViewLayout(rootLayout, mWindowParams);
                isMoving = true;
                break;

            //抬起：区分点击与滑动
            case MotionEvent.ACTION_UP:
                isMoving = false;
                if (Math.abs(mInScreenX - mDownInScreenX) < 5 && Math.abs(mInScreenY - mDownInScreenY) < 5) {
                    Toast.makeText(mContext, "点击了歌词~", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    // 移除悬浮窗
    public void remove() {
        if (isInit && rootLayout.getParent() != null) {
            mWindowManager.removeView(rootLayout);
            isInit = false;
        }
    }

    // 显示悬浮窗
    public void showFloatWindow() {
        if (!isInit) return;
        if (rootLayout.getParent() == null) {
            mWindowManager.addView(rootLayout, mWindowParams);
        }
    }


}
