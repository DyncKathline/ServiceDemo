package com.kathline.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.renderscript.RenderScript;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

public class ForegroundService extends BaseService {

    public static final String CHANNEL_ID = "com.kathline.service.SampleService";
    public static final String CHANNEL_NAME = "com.kathline.service";

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        ForegroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ForegroundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerNotificationChannel();
        showNotification();
    }

    /**
     * 注册通知通道
     */
    private void registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = mNotificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                //是否在桌面icon右上角展示小红点
                channel.enableLights(true);
                //小红点颜色
                channel.setLightColor(Color.RED);
                //通知显示
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                //是否在久按桌面图标时显示此渠道的通知
                //channel.setShowBadge(true);
                mNotificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * 启动前台通知
     */
    private void showNotification() {
        int notifyId = 12;
        //创建通知详细信息
        Notification.Builder mBuilder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID);
        }
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("2020年11月24日")
                .setContentText("今天天气阴天，8到14度");
        //创建点击跳转Intent
        Intent intent = new Intent(this, MainActivity.class);
        //创建任务栈Builder
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        //设置跳转Intent到通知中
        mBuilder.setContentIntent(pendingIntent);
        //获取通知服务
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //构建通知
        Notification notification = mBuilder.build();
        //显示通知
        nm.notify(notifyId, notification);
        //启动为前台服务
        startForeground(notifyId, notification);
    }
}
