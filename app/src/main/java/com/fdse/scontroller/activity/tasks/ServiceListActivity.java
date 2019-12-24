package com.fdse.scontroller.activity.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fdse.scontroller.R;
import com.fdse.scontroller.constant.UrlConstant;
import com.fdse.scontroller.http.HttpUtil;
import com.fdse.scontroller.model.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ServiceListActivity extends AppCompatActivity {
    private View view;
    private ListView listView;
    private String[] listViewData;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_service_list);
        listView = (ListView) findViewById(R.id.lv_tasks_service_list);
        listViewData = new String[2];
        listViewData[0] = "喝水";
        listViewData[1] = "踢球";
        //item的点击事件，里面可以设置跳转并传值
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //开始传值
                String taskName=adapter.getItem(i);
                taskName="调用"+taskName;
                saveTaskInfo(taskName);
                ServiceListActivity.this.finish();
            }
        });
        adapter = new ArrayAdapter<String>(
                ServiceListActivity.this, android.R.layout.simple_list_item_1, listViewData);
        listView.setAdapter(adapter);
    }

    //保存任务信息
    private void saveTaskInfo(String taskName) {
        //发送post数据
        final HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("taskName", taskName);
        postData.put("puid", "");

        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_TASKS_SAVE_TASK);
        HttpUtil.doPost(serviceURL, postData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responceData = response.body().string();
//                Map<String ,String> result = (Map<String, String>) JSONObject.parseObject(responceData, Task.class);
                //获取到任务id之后再把owls丢给流程执行引擎
            }
        });
    }

}
