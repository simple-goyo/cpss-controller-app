package com.fdse.scontroller.activity.devices;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cpacm.FloatingMusicButton;
import com.cpacm.FloatingMusicMenu;
import com.fdse.scontroller.R;
import com.fdse.scontroller.service.FloatingButtonService;
import com.fdse.scontroller.service.ReplayService;
import com.fdse.scontroller.util.MarketUtil;
import com.fdse.scontroller.util.UtilsOfSDCard;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewDeviceManageActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    //    private ImageView iv_device_photo;
//    private Button btn_download;
    private Button btn_hass;
    private Button btn_locate;
    private Button button;
    private FloatingMusicMenu button1;
    private FloatingMusicButton extraFab;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device_manage);
        Intent intent = getIntent();
        String url = (String) intent.getSerializableExtra("url");
        initView();
    }

    private void initView() {
        // 取得LinearLayout 物件
        LinearLayout ll = (LinearLayout) findViewById(R.id.viewObj);
        LayoutInflater inflater = (LayoutInflater)getSystemService( Context.LAYOUT_INFLATER_SERVICE);
//        button1 = (FloatingMusicMenu) inflater.inflate(R.layout.item_device_floating_button, null);
//        button1.setProgress(50);
//        button1.start();
//        ll.addView(button1);

//        button = new Button(getApplicationContext());
//        button.setText("录制");
//        button.setBackgroundColor(Color.WHITE);
//        ll.addView(button);
//        btn_download=findViewById(R.id.btn_download);
//        btn_download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 查询指定包名信息
//                try {
//                    String packageName = "com.xiaomi.smarthome";
//                    String className = "com.xiaomi.smarthome.SmartHomeMainActivity";
//                    Intent intent=new Intent();
//                    intent.setClassName(packageName, className);
//                    startActivity(intent);
//                }catch (Exception e){
//                    Toast.makeText(NewDeviceManageActivity.this,"没有找到对应软件，转到下载页面",Toast.LENGTH_LONG);
//                    MarketUtil.toXiaoMi(NewDeviceManageActivity.this,"com.xiaomi.smarthome");
//                }
//            }
//        });

//        btn_hass = findViewById(R.id.btn_hass);
//        btn_hass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UtilsOfSDCard.saveState("Playback");
//                // 查询指定包名信息
//                String[] infoAppName = UtilsOfSDCard.getInfoFirstOperate();
//                if (infoAppName == null) {
//                    return;
//                }
//                String packageName = infoAppName[0];
//                String className = infoAppName[1];
//                try {
////                    packageName = "com.xiaomi.smarthome";
////                    className = "com.xiaomi.smarthome.SmartHomeMainActivity";
//                    Intent intent = new Intent();
//                    intent.setClassName(packageName, className);
//                    startActivity(intent);
////                    startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
//                } catch (Exception e) {
//                    Toast.makeText(NewDeviceManageActivity.this, "没有找到对应软件，转到下载页面", Toast.LENGTH_LONG);
//                    MarketUtil.toXiaoMi(NewDeviceManageActivity.this, "com.xiaomi.smarthome");
//                }
//            }
//        });

        btn_locate = findViewById(R.id.btn_locate);
        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(NewDeviceManageActivity.this, MeasureDistActivity.class);
                startActivity(intent1);
            }
        });

        //初始化TTS
        tts = new TextToSpeech(this, this);
        //获取控件
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(NewDeviceManageActivity.this, FloatingButtonService.class));
            }
        }
    }

    public void startFloatingButtonService(View view) {
        if (FloatingButtonService.isStarted) {
            return;
        }

        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            startService(new Intent(NewDeviceManageActivity.this, FloatingButtonService.class));
        }
    }

    public void startReplayService(View view) {
        if (ReplayService.isStarted) {
            return;
        }
        tts.speak("Start playback", TextToSpeech.QUEUE_FLUSH, null);
        UtilsOfSDCard.saveState("Playback");
        //开始服务1
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            startService(new Intent(NewDeviceManageActivity.this, ReplayService.class));
        }

        //跳转界面
        // 查询指定包名信息
        String[] infoAppName = UtilsOfSDCard.getInfoFirstOperate();
        if (infoAppName == null) {
            return;
        }
        String packageName = infoAppName[0];
        String className = infoAppName[1];
        try {
//                    packageName = "com.xiaomi.smarthome";
//                    className = "com.xiaomi.smarthome.SmartHomeMainActivity";
            Intent intent = new Intent();
            intent.setClassName(packageName, className);
            startActivity(intent);
//                    startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
        } catch (Exception e) {
            Toast.makeText(NewDeviceManageActivity.this, "没有找到对应软件，转到下载页面", Toast.LENGTH_LONG);
            MarketUtil.toXiaoMi(NewDeviceManageActivity.this, "com.xiaomi.smarthome");
        }
    }

    @Override
    public void onInit(int status) {
        // 判断是否转化成功
        if (status == TextToSpeech.SUCCESS){
            //默认设定语言为中文，原生的android貌似不支持中文。
            int result = tts.setLanguage(Locale.CHINESE);
            Log.i("VirtualXposed_", "设置语音播报" + result);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(NewDeviceManageActivity.this, "不可用", Toast.LENGTH_SHORT).show();
                tts.setLanguage(Locale.US);
            }else{
                //不支持中文就将语言设置为英文
                tts.setLanguage(Locale.CHINESE);
            }
        }
    }
}
