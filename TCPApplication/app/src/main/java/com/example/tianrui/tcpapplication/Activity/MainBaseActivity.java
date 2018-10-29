package com.example.tianrui.tcpapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.tianrui.tcpapplication.BaseActivity;
import com.example.tianrui.tcpapplication.R;

/**
 * Created by tianrui on 2018/8/3.
 */

public class MainBaseActivity extends BaseActivity {
    private final static String TAG = "MainBaseActivity";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Button btn_tcpclient = (Button)findViewById(R.id.btn_tcpclient);
        btn_tcpclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainBaseActivity.this, TCPClientAct.class);
                Log.d(TAG, "start tcpClient");
                startActivity(intent);
            }
        });
        Button btn_bluetooth = (Button)findViewById(R.id.btn_bluetooth);
        btn_bluetooth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainBaseActivity.this, BlueToothAct.class);
                Log.d(TAG, "start blueTooth");
                startActivity(intent);
            }
        });
    }
}
