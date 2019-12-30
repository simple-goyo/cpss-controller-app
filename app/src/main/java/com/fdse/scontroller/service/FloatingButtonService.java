package com.fdse.scontroller.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.cpacm.FloatingMusicButton;
import com.cpacm.FloatingMusicMenu;
import com.fdse.scontroller.R;
import com.fdse.scontroller.activity.devices.NewDeviceManageActivity;
import com.fdse.scontroller.util.UtilsOfSDCard;

/**
 * Created by dongzhong on 2018/5/30.
 */

public class FloatingButtonService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private Button button;
    private FloatingMusicMenu menuBtn;
    private FloatingMusicButton startBtn;
    private FloatingMusicButton pauseBtn;
    private FloatingMusicButton voiceBtn;
    private FloatingMusicButton videoBtn;
    private FloatingMusicButton endBtn;
    private Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        mContext=this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 300;
        layoutParams.y = 300;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            button = new Button(getApplicationContext());
            button.setText("录制");
            button.setBackgroundColor(Color.WHITE);
//            windowManager.addView(button, layoutParams);
            getApplicationContext().setTheme(R.style.AppTheme);

            startBtn = new FloatingMusicButton(getApplicationContext());
            startBtn.setCoverDrawable(getResources().getDrawable(R.drawable.ic_pause));
            pauseBtn = new FloatingMusicButton(getApplicationContext());
            pauseBtn.setCoverDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
            voiceBtn = new FloatingMusicButton(getApplicationContext());
            voiceBtn.setCoverDrawable(getResources().getDrawable(R.drawable.ic_audiotrack_black_24dp));
            videoBtn = new FloatingMusicButton(getApplicationContext());
            videoBtn.setCoverDrawable(getResources().getDrawable(R.drawable.ic_video_call_black_24dp));
            endBtn = new FloatingMusicButton(getApplicationContext());
            endBtn.setCoverDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            menuBtn = (FloatingMusicMenu) inflater.inflate(R.layout.item_device_floating_button, null);
//            menuBtn.setProgress(50);
            menuBtn.addButton(startBtn);
//            menuBtn.addButton(pauseBtn);
//            menuBtn.addButton(continueBtn);
//            menuBtn.addButton(voiceBtn);
//            menuBtn.addButton(videoBtn);
//            menuBtn.addButton(endBtn);
//            menuBtn.removeButton(endBtn);
            windowManager.addView(menuBtn, layoutParams);

            menuBtn.setOnTouchListener(new FloatingOnTouchListener());
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuBtn.start();
                    menuBtn.removeButton(startBtn);
                    menuBtn.addButton(pauseBtn);
                    menuBtn.addButton(endBtn);
                    UtilsOfSDCard.saveState("Record");
                    UtilsOfSDCard.deleteInfo();
                }
            });
            endBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuBtn.stop();
                    menuBtn.removeButton(pauseBtn);
                    menuBtn.removeButton(endBtn);
                    menuBtn.addButton(startBtn);
                    UtilsOfSDCard.saveState("Normal");
                    long downTime = SystemClock.uptimeMillis();
                    long eventTime = SystemClock.uptimeMillis() + 100;
                    float x = 0.0f;
                    float y = 0.0f;
//                        List<State>states=UtilsOfSDCard.getInfo();
                    String[] lastOperate = UtilsOfSDCard.getInfoLastOperate();
                    if (lastOperate == null) {
                        return;
                    }
                    long startTime = Long.parseLong(lastOperate[8]);
                    long currentTime = System.currentTimeMillis();
                    long time1 = currentTime - startTime;
                    String data = lastOperate[0] + "&&" + lastOperate[1] + "&&" + downTime + "&&" + eventTime + "&&" + 9 + "&&" + x + "&&" + y + "&&" + 0 + "&&" + startTime + "&&" + currentTime + "&&" + time1 + "\r\n";
                    UtilsOfSDCard.saveInfo(data);
                }
            });
            pauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuBtn.stop();
                    menuBtn.removeButton(pauseBtn);
                    menuBtn.removeButton(endBtn);
                    menuBtn.addButton(voiceBtn);
                    menuBtn.addButton(videoBtn);
                }
            });
            voiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuBtn.start();
                    menuBtn.removeButton(voiceBtn);
                    menuBtn.removeButton(videoBtn);
                    menuBtn.addButton(pauseBtn);
                    menuBtn.addButton(endBtn);
//                    Intent news = new Intent(mContext, NewDeviceManageActivity.class);
//                    news.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Service跳转到Activity 要加这个标记
//                    mContext.startActivity(news);
                    long downTime = SystemClock.uptimeMillis();
                    long eventTime = SystemClock.uptimeMillis() + 100;
                    float x = 0.0f;
                    float y = 0.0f;
//                        List<State>states=UtilsOfSDCard.getInfo();
                    String[] lastOperate = UtilsOfSDCard.getInfoLastOperate();
                    if (lastOperate == null) {
                        return;
                    }
                    long startTime = Long.parseLong(lastOperate[8]);
                    long currentTime = System.currentTimeMillis();
                    long time1 = currentTime - startTime;
                    String data = lastOperate[0] + "&&" + lastOperate[1] + "&&" + 1 + "&&" + 2 + "&&" + 7 + "&&" + x + "&&" + y + "&&" + 0 + "&&" + startTime + "&&" + currentTime + "&&" + time1 + "\r\n";
                    UtilsOfSDCard.saveInfo(data);
                }
            });
            videoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuBtn.start();
                    menuBtn.removeButton(voiceBtn);
                    menuBtn.removeButton(videoBtn);
                    menuBtn.addButton(pauseBtn);
                    menuBtn.addButton(endBtn);
                }
            });

//            button.setOnTouchListener(new FloatingOnTouchListener());
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String state = UtilsOfSDCard.getState();
//                    if ("Normal".equals(state) || "Playback".equals(state) || "Playbacking".equals(state)) {
//                        button.setText("停止录制");
//                        UtilsOfSDCard.saveState("Record");
//                        UtilsOfSDCard.deleteInfo();
//                    } else if ("Record".equals(state)) {
//                        button.setText("录制");
//                        UtilsOfSDCard.saveState("Normal");
//                        long downTime = SystemClock.uptimeMillis();
//                        long eventTime = SystemClock.uptimeMillis() + 100;
//                        float x = 0.0f;
//                        float y = 0.0f;
////                        List<State>states=UtilsOfSDCard.getInfo();
//                        String[] firstOperate = UtilsOfSDCard.getInfoFirstOperate();
//                        if (firstOperate == null) {
//                            return;
//                        }
//                        long startTime = Long.parseLong(firstOperate[8]);
//                        long currentTime = System.currentTimeMillis();
//                        long time1 = currentTime - startTime;
//                        String data = "END&&" + "END&&" + downTime + "&&" + eventTime + "&&" + 9 + "&&" + x + "&&" + y + "&&" + 0 + "&&" + startTime + "&&" + currentTime + "&&" + time1 + "\r\n";
//                        UtilsOfSDCard.saveInfo(data);
//                    }
//                }
//            });

        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
