package com.kathline.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Field;

public class BindingActivity extends Activity {
    LocalService mService;
    boolean mBound = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        setContentView(textView);
        //检查是否已经授予权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                //若未授权则请求权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }
        textView.setGravity(Gravity.CENTER);
        textView.setText("点击");
        textView.setOnClickListener(onButtonClick);
        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    /**
     * Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute)
     */
    View.OnClickListener onButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBound) {
                // Call a method from the LocalService.
                // However, if this call were something that might hang, then this request should
                // occur in a separate thread to avoid slowing down the activity performance.
                int num = mService.getRandomNumber();
//                Toast.makeText(BindingActivity.this, "number: " + num, Toast.LENGTH_SHORT).show();
                Toast.makeText(BindingActivity.this, "5秒后弹出页面", Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createSmallWindow(BindingActivity.this);
                    }
                }, 5000);
            }
        }
    };

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams smallWindowParams;
    private ViewGroup smallWindow;
    private static int statusBarHeight;
    private static int screenWidth;
    private static int screenHeight;
    public void createSmallWindow(final Context context) {
        mWindowManager = getWindowManager(context);
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        if (smallWindowParams == null) {
            smallWindowParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }else {
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            smallWindowParams.format = PixelFormat.RGBA_8888;
            smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
            int width;
            int height;
//            if(screenWidth > screenHeight) {
//                width = screenHeight / 2;
//                height = width * 9 / 16;
//            }else {
//                width = screenWidth / 2;
//                height = width * 9 / 16;
//            }
            width = screenWidth;
            height = screenHeight;
            //小窗口摆放的位置，手机屏幕中央
//            smallWindowParams.x = screenWidth / 2 - width / 2;
//            smallWindowParams.y = screenHeight / 2 - height / 2;
            smallWindowParams.width = width;
            smallWindowParams.height = height;
        }
        smallWindow = new FrameLayout(context);
        smallWindow = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_layout, null);
        smallWindow.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSmallWindow(context);
                MediaManager.release();
            }
        });
        final RelativeLayout relativeLayout = new RelativeLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.addView(smallWindow, params);
        relativeLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));
        mWindowManager.addView(relativeLayout, smallWindowParams);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {

            /**
             * 记录当前手指位置在屏幕上的横坐标值
             */
            private float xInScreen;

            /**
             * 记录当前手指位置在屏幕上的纵坐标值
             */
            private float yInScreen;

            /**
             * 记录手指按下时在屏幕上的横坐标的值
             */
            private float xDownInScreen;

            /**
             * 记录手指按下时在屏幕上的纵坐标的值
             */
            private float yDownInScreen;

            /**
             * 记录手指按下时在小悬浮窗的View上的横坐标的值
             */
            private float xInView;

            /**
             * 记录手指按下时在小悬浮窗的View上的纵坐标的值
             */
            private float yInView;

            /**
             * 按下的开始时间
             */
            private long startTime;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                        xInView = event.getX();
                        yInView = event.getY();
                        xDownInScreen = event.getRawX();
                        yDownInScreen = event.getRawY() - getStatusBarHeight(context);
                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY() - getStatusBarHeight(context);
                        // 手指移动的时候更新小悬浮窗的位置
                        if(smallWindowParams != null) {
                            smallWindowParams.x = (int) (xInScreen - xInView);
                            smallWindowParams.y = (int) (yInScreen - yInView);
                            mWindowManager.updateViewLayout(relativeLayout, smallWindowParams);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                        if (Math.abs(xDownInScreen - xInScreen) < 5 && Math.abs(yDownInScreen - yInScreen) < 5) {
                            long end = System.currentTimeMillis() - startTime;
                            // 双击的间隔在 300ms以下
                            if (end < 300) {
                                if(mWindowCallBack != null) {
                                    mWindowCallBack.removeSmallWindow();
                                }
                            }
                            startTime = System.currentTimeMillis();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        MediaManager.playRaw(getApplicationContext(), R.raw.call, true);
    }

    public void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView((View) smallWindow.getParent());
            smallWindow = null;
        }
    }

    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public interface WindowCallBack {
        void removeSmallWindow();
    }

    private WindowCallBack mWindowCallBack;

    public void setWindowCallBack(WindowCallBack callBack) {
        mWindowCallBack = callBack;
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
