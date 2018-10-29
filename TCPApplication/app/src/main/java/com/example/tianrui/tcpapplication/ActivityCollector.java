package com.example.tianrui.tcpapplication;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianrui on 2018/8/3.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    //添加一个活动
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    //移除一个活动
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    //停止所有活动
    public static void finish_all(){
        for(Activity activity : activities){
            activity.finish();
        }
    }
}
