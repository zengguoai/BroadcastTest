package com.example.localreceivertest;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiguanghua on 18-1-18.
 */
//活动管理类，专门负责添加/移除activity
public class ActivityCollector  {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
