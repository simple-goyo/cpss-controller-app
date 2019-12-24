package com.fdse.scontroller.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cpacm.FloatingMusicButton;
import com.cpacm.FloatingMusicMenu;
import com.fdse.scontroller.R;
import com.fdse.scontroller.activity.devices.NewDeviceManageActivity;
import com.fdse.scontroller.util.UtilsOfSDCard;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by dongzhong on 2018/5/30.
 */

public class ReplayService extends Service implements TextToSpeech.OnInitListener{
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
//    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;

//    final FloatingMusicMenu menuBtn = null;
//    final FloatingMusicButton continueBtn = null;



    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
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
        //初始化TTS
        tts = new TextToSpeech(this, this);
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
//            windowManager.addView(button, layoutParams);
            getApplicationContext().setTheme(R.style.AppTheme);

            final  FloatingMusicButton  continueBtn = new FloatingMusicButton(getApplicationContext());
            continueBtn.setCoverDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final FloatingMusicMenu  menuBtn = (FloatingMusicMenu) inflater.inflate(R.layout.item_device_floating_button, null);
            menuBtn.setProgress(50);
            menuBtn.addButton(continueBtn);
            windowManager.addView(menuBtn, layoutParams);
            menuBtn.start();
            menuBtn.setOnTouchListener(new FloatingOnTouchListener());

            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilsOfSDCard.saveState("CONTINUE");
                }
            });

//            musicPrepared();
//            mediaPlayer.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if(UtilsOfSDCard.getState().contains("PAUSE")){
                            Log.i("VirtualXposed_", "回放暂停，播放音视频1" + UtilsOfSDCard.getState());
//                            mediaPlayer.start();
                            menuBtn.stop();
                            tts.speak("Please operate by yourself.", TextToSpeech.QUEUE_FLUSH, null);
                        }
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

//    private void musicPrepared() {
//        try {
//            AssetFileDescriptor afd = getAssets().openFd("99nights.mp3");
//            if (mediaPlayer != null) {
//                mediaPlayer.reset();
//                mediaPlayer = null;
//            }
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//            mediaPlayer.prepareAsync();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onInit(int status) {
        // 判断是否转化成功
        if (status == TextToSpeech.SUCCESS){
            //默认设定语言为中文，原生的android貌似不支持中文。
            int result = tts.setLanguage(Locale.CHINESE);
            Log.i("VirtualXposed_", "设置语音播报" + result);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
//                Toast.makeText(NewDeviceManageActivity.this, "不可用", Toast.LENGTH_SHORT).show();
                tts.setLanguage(Locale.US);
            }else{
                //不支持中文就将语言设置为英文
                tts.setLanguage(Locale.CHINESE);
            }
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
