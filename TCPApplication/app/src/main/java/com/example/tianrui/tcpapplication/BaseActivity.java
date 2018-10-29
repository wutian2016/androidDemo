package com.example.tianrui.tcpapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by tianrui on 2018/8/3.
 */

public class BaseActivity extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", this.getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
