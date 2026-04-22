package com.example.floatingwindow;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatingService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. 获取窗口管家
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 2. 加载你的歌词布局
        floatingView = LayoutInflater.from(this).inflate(R.layout.lyric, null);

        params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; // 设为悬浮层级
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不抢占输入焦点
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.START; // 习惯性从左上角开始计算坐标

        // 实现拖拽逻辑

        initDragListener();

        // 3. 正式把 View 添加到屏幕上
        windowManager.addView(floatingView, params);
    }

    private void initDragListener() {
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // 计算移动了多少
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        // 通过 WindowManager 告诉系统 View 移动了
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}