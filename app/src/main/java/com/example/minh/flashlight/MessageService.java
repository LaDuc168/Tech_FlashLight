package com.example.minh.flashlight;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MessageService extends Service implements View.OnClickListener, View.OnTouchListener {
    private WindowManager mWindowManager;
    private MyGroupView mViewIcon;
    private MyGroupView mViewSms;
    private WindowManager.LayoutParams layoutParamsIcon;
    private WindowManager.LayoutParams layoutParamsSms;
    private int state;
    private static final int TYPE_SMS = 0;
    private static final int TYPE_ICON = 1;
    private int prviousX;
    private int prviousY;
    private float startX;
    private float startY;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initView();
        return START_STICKY;
    }

    private void initView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createIconView();
//        createSmsView();
        showIcon();
    }

    private void createSmsView() {
        mViewSms = new MyGroupView(this);
        View view = View.inflate(this, R.layout.view_sms, mViewSms);
//        ImageView imageView = view.findViewById(R.id.image);
        mViewSms.setOnTouchListener(this);
        layoutParamsSms = new WindowManager.LayoutParams();
        layoutParamsSms.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParamsSms.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParamsSms.gravity = Gravity.CENTER;
        layoutParamsSms.format = PixelFormat.TRANSLUCENT;
        layoutParamsSms.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParamsSms.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParamsSms.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParamsSms.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
    }

    private void showSms() {
        try {
            mWindowManager.removeView(mViewIcon);
        } catch (Exception e) {

        }
        mWindowManager.addView(mViewSms, layoutParamsSms);
        state = TYPE_SMS;
    }


    private void showIcon() {
        try {
            mWindowManager.removeView(mViewSms);
        } catch (Exception e) {
        }
        mWindowManager.addView(mViewIcon, layoutParamsIcon);
        state = TYPE_ICON;

    }

    private void createIconView() {
        mViewIcon = new MyGroupView(this);
        View view = View.inflate(this, R.layout.layout, mViewIcon);
        ImageView imageView = view.findViewById(R.id.image);
        Glide.with(this).asGif().load(R.drawable.ab).into(imageView);
        imageView.setOnClickListener(this);
        imageView.setOnTouchListener(this);
        layoutParamsIcon = new WindowManager.LayoutParams();
        layoutParamsIcon.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParamsIcon.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParamsIcon.gravity = Gravity.CENTER;
        layoutParamsIcon.format = PixelFormat.TRANSLUCENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParamsIcon.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParamsIcon.type = WindowManager.LayoutParams.TYPE_PHONE;
        }


//        layoutParamsIcon.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParamsIcon.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.translate);
        imageView.startAnimation(animAlpha);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                showSms();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (state == TYPE_ICON) {
                    prviousX = layoutParamsIcon.x;
                    prviousY = layoutParamsIcon.y;
                } else {
                    prviousX = layoutParamsSms.x;
                    prviousY = layoutParamsSms.y;
                }
                startX = motionEvent.getRawX();
                startY = motionEvent.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                double delX = motionEvent.getRawX() - startX;
                double delY = motionEvent.getRawY() - startY;
                if (state == TYPE_ICON) {
                    layoutParamsIcon.x = prviousX + (int) delX;
                    layoutParamsIcon.y = prviousY + (int) delY;
                    mWindowManager.updateViewLayout(mViewIcon,layoutParamsIcon);
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                if (state == TYPE_SMS) {
                    showIcon();
                }
                break;
        }
        return false;
    }
}
