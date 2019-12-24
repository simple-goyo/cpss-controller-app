package com.fdse.scontroller.hooks;

import android.view.MotionEvent;

public class MotionEventInfo {
    /**
     * 事件
     */
    MotionEvent event;
    /**
     * 从当前activity开始录制到该事件发生的时间
     */
    long time;
}
