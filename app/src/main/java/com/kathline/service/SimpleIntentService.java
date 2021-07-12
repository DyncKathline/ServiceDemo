package com.kathline.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SimpleIntentService extends IntentService {

    public SimpleIntentService() {
        super("name");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.getInstance().logCurrentMethod(this.getClass());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.getInstance().logCurrentMethod(this.getClass());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.getInstance().logCurrentMethod(this.getClass());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.getInstance().logCurrentMethod(this.getClass());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.getInstance().logCurrentMethod(this.getClass());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.getInstance().logCurrentMethod(this.getClass());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.getInstance().logCurrentMethod(this.getClass());
        return super.onBind(intent);
    }
}
