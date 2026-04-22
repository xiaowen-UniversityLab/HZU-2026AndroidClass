package com.example.smallcinema;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private MediaPlayer mPlayer;
    private SurfaceView sfv_show;
    private SurfaceHolder surfaceHolder;
    private Button  btn_start, btn_pause, btn_full,btn_horizontal;
    private SeekBar seekBar;


    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private float startY;

    private AudioManager.OnAudioFocusChangeListener focusChangeListener;

    private boolean isRunning = true;
    private boolean isFullScreen = false;
    private boolean isLandscape = false;


    //进度条自动更新
    private Handler mHandler=new Handler(Looper.getMainLooper());
    private Runnable updateProgressTsk= new Runnable() {
        @Override
        public void run() {
            if(mPlayer!=null && mPlayer.isPlaying()){
                seekBar.setProgress(mPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 100);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sfv_show = findViewById(R.id.sfv_show);
        btn_start = findViewById(R.id.btn_start);
        btn_pause = findViewById(R.id.btn_pause);
        btn_full = findViewById(R.id.btn_full);
        btn_horizontal = findViewById(R.id.btn_horizontal);
        seekBar = findViewById(R.id.seekBar);

        btn_start.setOnClickListener(v->startVideo());
        btn_pause.setOnClickListener(v->pauseVideo());
        btn_full.setOnClickListener(v->fullScreen());
        btn_horizontal.setOnClickListener(v->LandscapeScreen());


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        surfaceHolder = sfv_show.getHolder();
        surfaceHolder.addCallback(this);

        FocusListen();//音频焦点

        seekBarListen();//进度条拖动

        volumeListen();//音量调节
    }


    //播放音乐
    private void startVideo() {

        if(!mPlayer.isPlaying()){
            int result = audioManager.requestAudioFocus(focusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
            if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                mPlayer.start();
                mHandler.post(updateProgressTsk);
            }
        }

    }


    //暂停音乐
    private void pauseVideo() {

        if(mPlayer.isPlaying()){
            mPlayer.pause();
            mHandler.removeCallbacks(updateProgressTsk);
        }

    }


    //全屏
    private void fullScreen() {

        //不是全屏，按下按钮，进入全屏
        if(!isFullScreen){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());
            }
            else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            isFullScreen=true;
            btn_full.setText("退出全屏");

            //同步屏幕状态
            int currentOrientation = getResources().getConfiguration().orientation;
            if(currentOrientation== Configuration.ORIENTATION_LANDSCAPE){
                isLandscape=true;
            }else {
                isLandscape=false;
            }
        }

        //是全屏，变成退出全屏按钮，退出全屏
        else {

            if(isLandscape){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().getInsetsController().show(WindowInsets.Type.systemBars());
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            isFullScreen=false;
            btn_full.setText("全屏");
            int currentOrientation = getResources().getConfiguration().orientation;
            if(currentOrientation== Configuration.ORIENTATION_LANDSCAPE){
                isLandscape=true;
            }else {
                isLandscape=false;
            }

        }

        if(mPlayer!=null){
            adjustVideoSize(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels);

        }

    }



    //横屏竖屏切换
    private void LandscapeScreen() {

        //竖屏->横屏
        if(!isLandscape){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            btn_horizontal.setText("竖屏");
            isLandscape=true;
        }
        //横屏->竖屏
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            btn_horizontal.setText("横屏");
            isLandscape=false;
        }


        if(mPlayer!=null){
            adjustVideoSize(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels);
        }

    }



    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mPlayer = MediaPlayer.create(this,R.raw.my_video);
        mPlayer.setDisplay(surfaceHolder);
        mPlayer.setOnPreparedListener(mediaPlayer -> seekBar.setMax(mPlayer.getDuration()));
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        adjustVideoSize(i1,i2);

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        if(mPlayer!=null){
            mPlayer.release();
            mPlayer=null;
            isRunning=false;
        }
    }




    //音频焦点变化监听器
    private void FocusListen() {

        focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                switch (i) {

                    //获得焦点
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (mPlayer != null && !mPlayer.isPlaying()) {
                            mPlayer.start();
                            mHandler.post(updateProgressTsk);
                        }
                        break;

                    //失去焦点
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_LOSS:
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.pause();
                            mHandler.removeCallbacks(updateProgressTsk);
                        }
                        break;
                }
            }
        };

    }



    //拖动进度条监听
    private void seekBarListen() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //拖动进度条中
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b && mPlayer !=null){
                    mPlayer.seekTo(i);
                }
            }

            //用户开始拖动进度条，进度条停止自动更新
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(updateProgressTsk);

            }

            //结束拖动，恢复自动更新进度条
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mHandler.post(updateProgressTsk);
                }
            }
        });

    }


    //音量手势改变监听
    @SuppressLint("ClickableViewAccessibility")
    private void volumeListen() {

        sfv_show.setOnTouchListener((v, event) -> {
            int width = sfv_show.getWidth();
            float x = event.getX();
            float y = event.getY();

            if (x < width / 2) return false;
            switch (event.getAction()) {

                //手指按下瞬间
                case MotionEvent.ACTION_DOWN:
                    startY = y;
                    break;

                //手指滑动过程
                case MotionEvent.ACTION_MOVE:
                    float offset = startY - y;
                    if (offset > 15) {
                        currentVolume = Math.min(currentVolume + 1, maxVolume);
                        startY = y;
                    } else if (offset < -15) {
                        currentVolume = Math.max(currentVolume - 1, 0);
                        startY = y;
                    }
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    break;

                //手指松开瞬间
                case MotionEvent.ACTION_UP:
                    if (Math.abs(y - startY) < 5) {
                        v.performClick();
                    }
                    break;
            }
            return true;
        });
    }


    //调整视频比例
    private void adjustVideoSize(int surfaceWidth, int surfaceHeight) {
        if (mPlayer == null) return;

        int videoWidth = mPlayer.getVideoWidth();
        int videoHeight = mPlayer.getVideoHeight();

        if (videoWidth == 0 || videoHeight == 0) return;

        float surfaceRatio = (float) surfaceWidth / surfaceHeight;
        float videoRatio = (float) videoWidth / videoHeight;

        int newWidth, newHeight;
        if (videoRatio > surfaceRatio) {
            newWidth = surfaceWidth;
            newHeight = (int) (surfaceWidth / videoRatio);
        }
        else {
            newHeight = surfaceHeight;
            newWidth = (int) (surfaceHeight * videoRatio);
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sfv_show.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        params.gravity = android.view.Gravity.CENTER;
        sfv_show.setLayoutParams(params);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPlayer!=null){
            mPlayer.release();
            mPlayer=null;
        }

        if(mHandler!=null){
            mHandler.removeCallbacks(updateProgressTsk);
        }

        if(audioManager!=null){
            audioManager.abandonAudioFocus(focusChangeListener);
        }
    }
}