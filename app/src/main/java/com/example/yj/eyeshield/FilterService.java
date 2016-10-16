package com.example.yj.eyeshield;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class FilterService extends Service {
    static boolean filterOn = false;
    static MainActivity activity;

    private WindowManager windowManager;
    private WindowManager.LayoutParams lp;
    private FilterView filterView;
    private int nFlags;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setFilterOn(boolean on) {
        filterOn = on;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        filterView = new FilterView(this, MainActivity.a);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nFlags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }else {
            nFlags =WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, nFlags, PixelFormat.TRANSLUCENT);


        lp.gravity = Gravity.LEFT | Gravity.TOP;
//        lp.gravity = Gravity.FILL;

        windowManager.addView(filterView, lp);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("护眼功能正在运行中");
        builder.setContentText("点击关闭后台服务");
        builder.setSmallIcon(R.drawable.eyeshield);

        Intent mainScreenIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, mainScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (filterView != null) {
            windowManager.removeView(filterView);
            filterView = null;
        }
    }
}

class FilterView extends View {
    private Paint mLoadPaint;

    public FilterView(Context context, Paint mLoadPaint) {
        super(context);
        this.mLoadPaint = mLoadPaint;
    }

    public FilterView(Context context, MainActivity activity) {
        super(context);
        Toast.makeText(getContext(), "护眼功能已开启", Toast.LENGTH_SHORT).show();
        FilterService.activity = activity;
        mLoadPaint = new Paint();
        mLoadPaint.setAntiAlias(true);
//        mLoadPaint.setTextSize(10);
//        mLoadPaint.setARGB(255, 255, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (FilterService.filterOn) {
            mLoadPaint.setColor(Color.argb(100, 255, 140, 0));
            mLoadPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0 - (FilterService.activity.getStatusBarHeight()), MainActivity.screenWidth, MainActivity.screenHeight + FilterService.activity.getStatusBarHeight(), mLoadPaint);
//           canvas.drawRect(0, 0 - 500, 100000000, 100000000, mLoadPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
