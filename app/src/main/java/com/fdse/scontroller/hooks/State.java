package com.fdse.scontroller.hooks;

import android.view.MotionEvent;

/**
 * @author xuekai1
 * @date 2018/12/27
 */
public class State {
    /**
     * app名称
     */
    public String appName;
    /**
     * 类名称
     */
    public String className;
    /**
     * 事件
     */
    public MotionEvent event;
    /**
     * 从当前activity开始录制到该事件发生的时间
     */
    public long time;
}
