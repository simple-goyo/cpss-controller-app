package com.fdse.scontroller.util;

import android.os.Environment;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.fdse.scontroller.hooks.State;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilsOfSDCard {
    /**
     * 向sd卡中存入数据
     */
    public static boolean saveInfo(String data){
        if(!isHaveSDCard()){
            return false;
        }
        //如果已经安装了sd卡
        File file=new File(Environment.getExternalStorageDirectory(),"info.txt");
        try {
            FileOutputStream fos=new FileOutputStream(file,true);
//            String data=number+"##"+password;
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<State> getInfo(){

        //判断设备是否装了sd卡
        if(!isHaveSDCard()){
            return null;
        }
        //如果装了sd卡，则从sd卡中取出数据
        File file=new File(Environment.getExternalStorageDirectory(),"info.txt");
        try {
            FileInputStream fis=new FileInputStream(file);
            BufferedReader bf=new BufferedReader(new InputStreamReader(fis));
            String info;
            List<State> states=new ArrayList<>();
            while (!TextUtils.isEmpty(info=bf.readLine())) {
                String[] data=info.split("&&");
                int i=2;
                long downTime = Long.parseLong(data[i+0]);
                long eventTime = Long.parseLong(data[i+1]);
                int action = Integer.parseInt(data[i+2]);
                float x = Float.parseFloat(data[i+3]);
                float y = Float.parseFloat(data[i+4]);
                int metaState = Integer.parseInt(data[i+5]);
                MotionEvent obtain = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
                State state = new State();
                state.event = obtain;
                state.time =  Long.parseLong(data[i+8]);
                state.appName=data[0];
                state.className=data[1];
                states.add(state);
            }
            bf.close();
            return states;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除相对路径
     * */
    public static void deleteInfo() {
        File file;
        if (isHaveSDCard()) {
            file = Environment.getExternalStorageDirectory();
        } else {
            file = Environment.getDataDirectory();
        }
        file = new File(file.getPath() + "/info.txt");
        RecursionDeleteFile(file);
    }


    /**
     * 从sd卡读取数据
     */

    public static String[] getInfoFirstOperate(){

        //判断设备是否装了sd卡
        if(!isHaveSDCard()){
            return null;
        }
        //如果装了sd卡，则从sd卡中取出数据
        File file=new File(Environment.getExternalStorageDirectory(),"info.txt");
        try {
            FileInputStream fis=new FileInputStream(file);
            BufferedReader bf=new BufferedReader(new InputStreamReader(fis));
            String info;
            List<State> states=new ArrayList<>();
            if (!TextUtils.isEmpty(info=bf.readLine())) {
                String[] data=info.split("&&");
//                String appName = data[0];
//                String classname = data[1];
//                bf.close();
//                List<String> app=new ArrayList<>();
//                app.add(appName);
//                app.add(classname);
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String[] getInfoLastOperate(){

        //判断设备是否装了sd卡
        if(!isHaveSDCard()){
            return null;
        }
        //如果装了sd卡，则从sd卡中取出数据
        File file=new File(Environment.getExternalStorageDirectory(),"info.txt");
        try {
            FileInputStream fis=new FileInputStream(file);
            BufferedReader bf=new BufferedReader(new InputStreamReader(fis));
            String info;
            String[] data = null;
            while (!TextUtils.isEmpty(info=bf.readLine())) {
                 data=info.split("&&");
            }
            bf.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveState(String data){
        if(!isHaveSDCard()){
            return false;
        }
        //如果已经安装了sd卡
        File file=new File(Environment.getExternalStorageDirectory(),"state.txt");
        try {
            FileOutputStream fos=new FileOutputStream(file);
//            String data=number+"##"+password;
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getState(){
        //判断设备是否装了sd卡
        if(!isHaveSDCard()){
            return "Normal";
        }
        //如果装了sd卡，则从sd卡中取出数据
        File file=new File(Environment.getExternalStorageDirectory(),"state.txt");
        try {
            FileInputStream fis=new FileInputStream(file);
            BufferedReader bf=new BufferedReader(new InputStreamReader(fis));
            String info=bf.readLine();
            if(!TextUtils.isEmpty(info)){
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Normal";
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file
     *            要删除的根目录
     */
    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    /** 是否有SD卡 */
    public static boolean isHaveSDCard() {
        //判断设备是否已经安装了sd卡
        String status= Environment.getExternalStorageState();
        //如果没有sd则返回false
        if(!Environment.MEDIA_MOUNTED.equals(status)){
            return false;
        }else {
            return true;
        }
    }
}