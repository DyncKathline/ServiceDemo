package com.kathline.service;

import android.content.Intent;
import android.util.Log;

public class BackGroupService extends BaseService {

    private boolean isRun = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("xx", "current thread:" + Thread.currentThread().getName());
        //这里执行耗时操作
        new Thread() {
            @Override
            public void run() {
                while (isRun){
                    try {
                        Log.e("Service", "doSomething");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
    }
}
