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
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class SurfaceFilterService extends Service {

    static boolean filterOn = false;
    static MainActivity activity;

    private WindowManager windowManager;
    private WindowManager.LayoutParams lp;
    private SurfaceFilterView surfaceFilterView;
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
        surfaceFilterView = new SurfaceFilterView(this, MainActivity.a);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nFlags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        } else {
            nFlags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, nFlags, PixelFormat.TRANSLUCENT);


        lp.gravity = Gravity.LEFT | Gravity.TOP;
//        lp.gravity = Gravity.FILL;

        windowManager.addView(surfaceFilterView, lp);


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
        if (surfaceFilterView != null) {
            surfaceFilterView.isDraw = false;
            windowManager.removeView(surfaceFilterView);
            surfaceFilterView = null;
        }
    }
}

class SurfaceFilterView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint mLoadPaint;
    private SurfaceHolder holder;
    private RenderThread renderThread;
    protected boolean isDraw = false;


    public SurfaceFilterView(Context context, MainActivity activity) {
        super(context);
        Toast.makeText(getContext(), "护眼功能已开启", Toast.LENGTH_SHORT).show();
        SurfaceFilterService.activity = activity;

        this.setZOrderOnTop(true);
        holder = this.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
        renderThread = new RenderThread();
        mLoadPaint = new Paint();
        mLoadPaint.setAntiAlias(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDraw = true;
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDraw = false;
    }


    private class RenderThread extends Thread {
        @Override
        public void run() {
            while (isDraw) {
                drawUI();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }


    public void drawUI() {
        Canvas canvas = holder.lockCanvas();
        try {
            drawCanvas(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }


    private void drawCanvas(Canvas canvas) {
        if (SurfaceFilterService.filterOn) {
            //System.out.println("alpha: " + MainActivity.alpha + "red: " + MainActivity.red);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mLoadPaint.setColor(Color.argb(MainActivity.alpha, MainActivity.red, MainActivity.green, MainActivity.blue));
            mLoadPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0 - (SurfaceFilterService.activity.getStatusBarHeight()), MainActivity.screenWidth, MainActivity.screenHeight + SurfaceFilterService.activity.getStatusBarHeight(), mLoadPaint);
        }
    }
}
