package com.fdse.scontroller.hooks;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fdse.scontroller.constant.UrlConstant;
import com.fdse.scontroller.http.HttpUtil;
import com.fdse.scontroller.util.UtilsOfSDCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import okhttp3.Call;
import okhttp3.Response;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class SensorHook implements IXposedHookLoadPackage {

    private static String MI_PACKAGE_NAME = "com.xiaomi.smarthome";
    public static final String SP_NAME = "sp_name";
    public static final String PACKAGE_NAME = "com.forfan.bigbang.coolapk";
    public static final int NON_SELECTION = 3;
    private static String MT_PACKAGE_NAME = "com.sankuai";
    public static final String SP_DOBLUE_CLICK = "sp_doblue_click";
    private final List<Filter> mFilters = new ArrayList<>();
    private XSharedPreferences appXSP;
    private final TouchEventHandler mTouchHandler = new TouchEventHandler();


    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) {

//        if (lpparam.packageName.equals(MI_PACKAGE_NAME)) {
//            try {
////                getWorkflow(lpparam,"http://img3.imgtn.bdimg.com/it/u=979772111,3062137222&fm=26&gp=0.jpg");
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        }

//        appXSP = new XSharedPreferences(PACKAGE_NAME, SP_NAME);
//        appXSP.makeWorldReadable();

//        int type = appXSP.getInt(lpparam.packageName, NON_SELECTION);
        if (!"de.robv.android.xposed.installer".equals(lpparam.packageName) && !"com.android.systemui".equals(lpparam.packageName) && !"com.fdse.scontroller".equals(lpparam.packageName)) {
//            if (type!=NON_SELECTION){
//            Log.i("VirtualXposed_", lpparam.packageName);
            findAndHookMethod(Activity.class, "dispatchTouchEvent", MotionEvent.class, new ActivityTouchEvent());
            findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new ActivityWindowFocusChanged());
            Log.i("VirtualXposed_", "3");

//                findAndHookMethod(View.class, "dispatchTouchEvent", MotionEvent.class, new ViewTouchEvent(lpparam.packageName,type));
//                findAndHookMethod(View.class, "setOnClickListener", View.OnClickListener.class, new ViewOnClickListenerHooker(lpparam.packageName, type));
//                findAndHookMethod(View.class, "setOnLongClickListener", View.OnLongClickListener.class, new ViewOnLongClickListenerHooker(loadPackageParam.packageName,type));
//            }

//            findAndHookMethod(Activity.class, "onStart",  new UniversalCopyOnStartHook());
//            findAndHookMethod(Activity.class, "onStop",  new UniversalCopyOnStopHook());
        }

//        if (lpparam.packageName.contains(MT_PACKAGE_NAME)) {
//            try {
//                meituan(lpparam);
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        }

    }

    private class ViewOnClickListenerHooker extends XC_MethodHook {

        private final String packageName;

        public ViewOnClickListenerHooker(String packageName, int type) {
            this.packageName = packageName;
            setClickTypeToTouchHandler(type);
        }


        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            View view = (View) param.thisObject;
            final View.OnClickListener listener = (View.OnClickListener) param.args[0];
            if (isKeyBoardOrLauncher(view.getContext(), packageName))
                return;
            View.OnClickListener newListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTouchHandler.hookOnClickListener(v, mFilters);
                    if (listener == null) {
                        return;
                    } else {
                        listener.onClick(v);
                    }
                }
            };
            param.args[0] = newListener;
        }
    }

    private class ActivityTouchEvent extends XC_MethodHook {
        long startTime11 = System.currentTimeMillis();
        String className = "";

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            String state = UtilsOfSDCard.getState();
//            Log.e(TAG,"after->View:"+ view.getClass().getSimpleName()+ " viewTouchEvent: " + event);
            if ("Record".equals(state)) {
                MotionEvent event = (MotionEvent) param.args[0];
                String[] lastOperate = UtilsOfSDCard.getInfoLastOperate();
                String activityName = param.thisObject.toString();
                activityName = activityName.split("@")[0];

                if (lastOperate != null) {
                    if (!activityName.equals(className)) {
                        startTime11 = Long.parseLong(lastOperate[9]);
                    }
                    if("7".equals(lastOperate[4])){
                        startTime11 = System.currentTimeMillis();
                    }
                }
                className = activityName;
                long currentTime = System.currentTimeMillis();
                long time = currentTime - startTime11;

                String appName = getAppPackageName((Activity) param.thisObject);
                String data = appName + "&&" + activityName + "&&" + event.getDownTime() + "&&" + event.getEventTime() + "&&" + event.getAction() + "&&" + event.getRawX() + "&&" + event.getRawY() + "&&" + event.getMetaState() + "&&" + startTime11 + "&&" + currentTime + "&&" + time + "\r\n";
                UtilsOfSDCard.saveInfo(data);
//                Log.i("VirtualXposed_", "click333" + activityName);//com.xiaomi.smarthome.SmartHomeMainActivity@e25b533
//                Log.i("VirtualXposed_", "click333" + getRunningActivityName((Activity) param.thisObject));//SmartHomeMainActivity
//                Log.i("VirtualXposed_", "click333" + getAppPackageName((Activity) param.thisObject));//com.xiaomi.smarthome
                Log.i("VirtualXposed_", "click333" + data);//1579305452&&1579305452&&0&&135.87419&&336.82455&&0&&1561976578846&&1561980368845&&3789999
            }
        }
    }

    public static String getRunningActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//完整类名
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        String contextActivity = runningActivity.substring(runningActivity.lastIndexOf(".") + 1);
        return contextActivity;
    }

    public static String getAppPackageName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        Log.d("lixx", "当前应用:" + componentInfo.getPackageName());
        return componentInfo.getPackageName();
    }

    private class ActivityWindowFocusChanged extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            boolean hasFocus = (boolean) param.args[0];
            Log.i("VirtualXposed_", "ActivityWindowFocusChanged1");//

            //获取 activity
//            Context context = (Context) XposedHelpers.callMethod(param.thisObject, "getActivity");
            String playState = UtilsOfSDCard.getState();
            if (!hasFocus && ("Playback".equals(playState) || "Playbacking".equals(playState) || playState.contains("PAUSE"))) {
                //do anything you want here
                int REQUEST_EXTERNAL_STORAGE = 1;
                String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                int permission = ActivityCompat.checkSelfPermission((Activity) param.thisObject, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            (Activity) param.thisObject,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<State> states = UtilsOfSDCard.getInfo();
                                for (final State state : states) {
                                    String activityName = param.thisObject.toString();
                                    activityName = activityName.split("@")[0];
                                    Log.i("VirtualXposed_", "click555" + activityName);
//                                    Log.i("VirtualXposed_", "click555" + state.className);
                                    Log.i("VirtualXposed_", "click555" + playState);

                                    if (!activityName.equals(state.className)) {
                                        continue;
                                    }
//                                    if (!"END".equals(state.className)) {
//                                        if (!activityName.equals(state.className)) {
//                                            continue;
//                                        }
//                                    } else {
//                                        if ("Playback".equals(playState)) {
//                                            UtilsOfSDCard.saveState("Playbacking");
//                                        } else {
//                                            continue;
//                                        }
//                                    }


                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (state.event.getAction() == 9) {
                                                //回放结束
                                                UtilsOfSDCard.saveState("Normal");
                                            } else if (state.event.getAction() == 8) {
                                                //简单粗暴的认为点击返回键就是finish
                                                XposedHelpers.callMethod(param.thisObject, "finish");
                                            } else if (state.event.getAction() == 7) {
                                                //暂停开始播放视频和音频
                                                //将state改为播放视频和音频，加上音频和视频地址
                                                UtilsOfSDCard.saveState("PAUSE__"+state.event.getDownTime() + "__" + state.event.getEventTime());
                                            } else {
                                                try {
//                                                    Class Activity = lpparam.classLoader.loadClass("android.app.Activity");
//                                                    Object activity = Activity.newInstance();
                                                    XposedHelpers.callMethod(param.thisObject, "dispatchTouchEvent", state.event);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }, state.time);

                                    //暂停节点，暂停部署后面的节点，等待状态改变
                                    if (state.event.getAction() == 7) {
                                        //当状态转为可继续时，继续执行后面步骤
                                        while (!UtilsOfSDCard.getState().equals("CONTINUE")) {
                                            try {
                                                Log.i("VirtualXposed_", "click555状态" + UtilsOfSDCard.getState());
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        UtilsOfSDCard.saveState("Playbacking");
                                    }

                                }
                            }
                        }).start();
                    }
                }, 1000);
            }
        }
    }


    //1. 为什么不hook住onTouch方法呢，而且非要dispatchTouchEvent返回true的时候才进行操作呢？
    private class ViewTouchEvent extends XC_MethodHook {
        long startTime = System.currentTimeMillis();
        private final String packageName;

        Class viewRootImplClass;

        public ViewTouchEvent(String packageName, int type) {
            this.packageName = packageName;
            try {
                viewRootImplClass = this.getClass().getClassLoader().loadClass("android.view.ViewRootImpl");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            setClickTypeToTouchHandler(type);
        }

//        @Override
//        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//            super.beforeHookedMethod(param);
//            View view = (View) param.thisObject;
//            if (isKeyBoardOrLauncher(view.getContext(), packageName))
//                return;
//            MotionEvent event = (MotionEvent) param.args[0];
//            Log.e(TAG,"before->View:"+ view.getClass().getSimpleName()+ " viewTouchEvent: " + event);
//        }


        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            View view = (View) param.thisObject;
            if (isKeyBoardOrLauncher(view.getContext(), packageName))
                return;
            MotionEvent event = (MotionEvent) param.args[0];
//            Log.e(TAG,"after->View:"+ view.getClass().getSimpleName()+ " viewTouchEvent: " + event);
            long currentTime = System.currentTimeMillis();
            long time = currentTime - startTime;
            String data = event.getDownTime() + "&&" + event.getEventTime() + "&&" + event.getAction() + "&&" + event.getRawX() + "&&" + event.getRawY() + "&&" + event.getMetaState() + "&&" + startTime + "&&" + currentTime + "&&" + time + "\r\n";
            Log.i("VirtualXposed_", "click222" + data);
            UtilsOfSDCard.saveInfo(data);
            if ((Boolean) param.getResult() || view.getParent() == null || (viewRootImplClass.isInstance(view.getParent()))) {
                mTouchHandler.hookTouchEvent(view, event, mFilters, true, appXSP.getInt(SP_DOBLUE_CLICK, 1000));
            }
        }
    }

    private void setClickTypeToTouchHandler(int type) {
        if (type == 0) {
            mTouchHandler.setUseClick(true);
        } else if (type == 1) {
            mTouchHandler.setUseLongClick(true);
        } else if (type == 2) {
            mTouchHandler.setUseDoubleClick(true);
        } else if (type == 3) {

        }
    }

    private boolean isKeyBoardOrLauncher = false;
    private boolean isKeyBoardOrLauncherChecked = false;

    private boolean isKeyBoardOrLauncher(Context context, String packageName) {
        if (isKeyBoardOrLauncherChecked) {
            return isKeyBoardOrLauncher;
        }
        if (context == null) {
            isKeyBoardOrLauncher = true;
            isKeyBoardOrLauncherChecked = true;
            return true;
        }
        for (String package_process : getInputMethodAsWhiteList(context)) {
            if (package_process.equals(packageName)) {
                isKeyBoardOrLauncher = true;
                isKeyBoardOrLauncherChecked = true;
                return true;
            }
        }
        for (String package_process : getLauncherAsWhiteList(context)) {
            if (package_process.equals(packageName)) {
                isKeyBoardOrLauncher = true;
                isKeyBoardOrLauncherChecked = true;
                return true;
            }
        }
        isKeyBoardOrLauncher = false;
        isKeyBoardOrLauncherChecked = true;
        return false;
    }

    private Set<String> getInputMethodAsWhiteList(Context context) {
        HashSet<String> packages = new HashSet<>();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> methodList = imm.getInputMethodList();
        for (InputMethodInfo info : methodList) {
            packages.add(info.getPackageName());
        }
        return packages;
    }

    private Set<String> getLauncherAsWhiteList(Context c) {
        HashSet<String> packages = new HashSet<>();
        PackageManager packageManager = c.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
//        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            packages.add(ri.activityInfo.packageName);
        }
        return packages;
    }


//    private void getWorkflow(final LoadPackageParam lpparam, String url) {
//        final HashMap<String, String> postData = new HashMap<String, String>();
//        String serviceURL = UrlConstant.getPailitaoServiceURL(UrlConstant.PAILITAO_SEARCH_DEVICE_INFO);
//        postData.put("url", url);
//        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i("VirtualXposed_", "onFailure");
//                mijia(lpparam);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.i("VirtualXposed_", "onResponse");
//                mijia(lpparam);
//            }
//        });
//    }

    private void mijia(final LoadPackageParam lpparam) {
        try {
//            跳转到设备选择
            final Class deviceMainPageClazz = XposedHelpers.findClass("com.xiaomi.smarthome.newui.DeviceMainPage", lpparam.classLoader);
            findAndHookMethod(deviceMainPageClazz, "G", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 1");
                    //获取 activity
//                    Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                    Context context = (Context) XposedHelpers.callMethod(param.thisObject, "getActivity");
                    //调用方法
                    Class ChooseDeviceActivity = lpparam.classLoader.loadClass("com.xiaomi.smarthome.device.choosedevice.ChooseDeviceActivity");
                    Object chooseDeviceActivity = ChooseDeviceActivity.newInstance();
                    XposedHelpers.callMethod(chooseDeviceActivity, "openChooseDevice", context);
                }
            });

            //点击输入框
            final Class chooseDeviceManuallyClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.ChooseDeviceManually", lpparam.classLoader);
            findAndHookMethod(chooseDeviceManuallyClazz, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 2");
                    View comfirBtn = (View) XposedHelpers.findField(param.thisObject.getClass(), "i").get(param.thisObject);
                    comfirBtn.setEnabled(true);
                    comfirBtn.performClick();
                }
            });

            //输入设备名称
            final Class chooseDeviceActivityClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.ChooseDeviceActivity", lpparam.classLoader);
            findAndHookMethod(chooseDeviceActivityClazz, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 3");
                    EditText mSearchEt = (EditText) XposedHelpers.findField(param.thisObject.getClass(), "mSearchEt").get(param.thisObject);
                    mSearchEt.setEnabled(true);
                    Thread.sleep(10000);
                    mSearchEt.setText("米家门窗传感");
                }
            });

//            //输入设备名称
//            final Class verticalSlidingTabClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.VerticalSlidingTab", lpparam.classLoader);
//            findAndHookMethod(verticalSlidingTabClazz, "a", int.class, String.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    //com.xiaomi.smarthome.newui.DeviceMainPage
//                    super.afterHookedMethod(param);
//                    //调用方法
//                    Log.i("VirtualXposed_", "in after hooked method 4 +" + param.args[0].toString()+"+" +param.args[1].toString());
//                    LinearLayout linearLayout = (LinearLayout) XposedHelpers.findField(param.thisObject.getClass(), "e").get(param.thisObject);
//                    Log.i("VirtualXposed_", "in after hooked method 40 +" + linearLayout.getChildCount());
//                    LinearLayout b=(LinearLayout) linearLayout.getChildAt(2);
//                    b.performClick();
//
//
////                    Class AnimPageView = lpparam.classLoader.loadClass("com.xiaomi.smarthome.device.choosedevice.AnimPageView");
////                    Class  AnimPageView = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.AnimPageView", lpparam.classLoader);
////                    Log.i("VirtualXposed_", "in after hooked method 44");
////                    Object animPageView = AnimPageView.newInstance();
////                    Log.i("VirtualXposed_", "in after hooked method 444");
////                    Boolean b = false;
////                    while (!b) {
////                        XposedHelpers.callMethod(animPageView, "setPosition", 2);
////                        Log.i("VirtualXposed_", "in after hooked method 4444");
////                        b = true;
////                    }
//                }
//            });


            final Class loginPwdViewClazz = XposedHelpers.findClass("com.xiaomi.smarthome.frame.login.ui.view.LoginPwdView", lpparam.classLoader);
            findAndHookMethod(loginPwdViewClazz, "d", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    Log.i("VirtualXposed_", "5");
                    super.afterHookedMethod(param);
                    EditText username = (EditText) XposedHelpers.findField(param.thisObject.getClass(), "d").get(param.thisObject);
                    username.setEnabled(true);
                    username.setText("17317541547");
                    EditText password = (EditText) XposedHelpers.findField(param.thisObject.getClass(), "f").get(param.thisObject);
                    password.setEnabled(true);
                    password.setText("402fdsefdse");
                    TextView comfirBtn = (TextView) XposedHelpers.findField(param.thisObject.getClass(), "g").get(param.thisObject);
                    comfirBtn.setEnabled(true);
                    Boolean b = false;
                    while (!b) {
                        comfirBtn.performClick();
                        b = true;
                    }

                }
            });

            //点击类别,获取参数值
            final Class animPageViewClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.AnimPageView", lpparam.classLoader);
            findAndHookMethod(animPageViewClazz, "setPosition", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 6 +" + param.args[0].toString());
                }
            });


            findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, boolean.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i("VirtualXposed", "7");
//                    Log.i("VX param", param.method.getDeclaringClass().getName());
//                    XposedBridge.log("VirtualXposed sample: VirtualXposed");
                    if (param.args[0] != null) {
                        String stringArgs = param.args[0].toString();
//                        Log.i("VX param", "param " + stringArgs+"-"+param.thisObject.toString());
//                        XposedBridge.log("VirtualXposed sample: " + "param " + stringArgs);
//                        param.args[0] = stringArgs + "-1";
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void meituan(final LoadPackageParam lpparam) {
        try {
            Log.i("VirtualXposed_", "1");
//            跳转到设备选择
            final Class deviceMainPageClazz = XposedHelpers.findClass("com.meituan.android.cashier.fragment.MTCashierRevisionFragment", lpparam.classLoader);
            findAndHookMethod(deviceMainPageClazz, "onClick", View.class,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "付款");
                    getWorkflow(lpparam,"用户下单");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWorkflow(final LoadPackageParam lpparam, String pocId) {
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_USER_SendMessageToMPAll);
        postData.put("content", pocId);
        postData.put("url", "www.baidu.com");
        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("VirtualXposed_", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("VirtualXposed_", "onResponse");
            }
        });
    }
}
