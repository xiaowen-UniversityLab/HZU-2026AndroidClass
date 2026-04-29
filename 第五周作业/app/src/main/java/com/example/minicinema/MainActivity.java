package com.example.minicinema;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SeekBar seekBar;
    private TextView tvTime;
    private AudioManager audioManager;

    private static final int REQUEST_CODE_PICK_VIDEO = 1;
    private Handler handler = new Handler();
    private float lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        surfaceView = findViewById(R.id.surfaceView);
        seekBar = findViewById(R.id.seekBar);
        tvTime = findViewById(R.id.tv_time);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // 初始化播放器并让用户选文件
                initMediaPlayer(holder);
                openFilePicker();
            }

            @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int h1) {}
            @Override public void surfaceDestroyed(SurfaceHolder h) {}
        });

        //  进度条手动拖动
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void initMediaPlayer(SurfaceHolder holder) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setDisplay(holder);
    }

    // 调用文件夹
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Uri videoUri = data.getData();
            startPlayback(videoUri);
    }

    private void startPlayback(Uri uri) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mp.getDuration());
                mp.start();
                updateProgressTask();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProgressTask() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int current = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(current);
            tvTime.setText(formatTime(current) + " / " + formatTime(mediaPlayer.getDuration()));
            handler.postDelayed(this::updateProgressTask, 1000);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = lastY - event.getY();
                if (Math.abs(deltaY) > 30) {
                    int direction = deltaY > 0 ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER;
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI);
                    lastY = event.getY();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private String formatTime(int ms) {
        int totalSeconds = ms / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}