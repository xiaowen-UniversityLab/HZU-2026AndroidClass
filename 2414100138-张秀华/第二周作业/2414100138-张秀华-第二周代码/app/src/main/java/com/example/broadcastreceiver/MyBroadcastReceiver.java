package com.example.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.TextView;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private TextView tvBattery;

    public void setTextView(TextView textView) {
        this.tvBattery = textView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            //获取当前电量
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int batteryPercent = (level * 100) / scale;
            if (tvBattery != null) {
                tvBattery.setText("当前电量：" + batteryPercent + "%");
            }
        }
    }
}