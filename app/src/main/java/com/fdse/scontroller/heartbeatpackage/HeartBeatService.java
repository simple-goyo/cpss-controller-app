package com.fdse.scontroller.heartbeatpackage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.fdse.scontroller.TokenMessage;
import com.fdse.scontroller.web.WebService;

public class HeartBeatService extends Service implements Runnable {

    private Thread mThread;

    public int count = 0;

    private boolean isConnect = true;

    private static String mRestMsg;

    private static String KEY_REST_MSG = "KEY_REST_MSG";
    //Context context = getApplicationContext();


    @Override

    public void run()

    {

        while (isConnect)

        {

            try

            {

                if (count < 3) {

                    //向服务器发送心跳包

                    sendHeartbeatPackage(mRestMsg);

                } else {
                    isConnect = false;
                }


                /*Thread.sleep(1000 * 3);
                if (!isConnect) {
                    String reConnect = WebService.login(TokenMessage.getInstance().getUserToken(context, "Token"), "login");
                    if (reConnect != null) {
                        count = 0;
                        isConnect = true;
                    }
                }*/

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }


    private void sendHeartbeatPackage(String msg)

    {

        String message = null;
        try

        {

            message = WebService.login("000", "heartbeat");

        } catch (Exception e)

        {

            e.printStackTrace();

        }
        // 处理返回结果

        if (message != null)

        {

            //只要服务器有回应就OK

            count = 0;


        } else {

            count++;

        }


    }


    @Override

    public IBinder onBind(Intent intent)

    {

        return null;

    }


    @Override

    public void onCreate()

    {

        super.onCreate();

    }


    @Override

    public void onDestroy()

    {

        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new Thread(this);

        mThread.start();

        count = 0;
        return super.onStartCommand(intent, flags, startId);
    }

}