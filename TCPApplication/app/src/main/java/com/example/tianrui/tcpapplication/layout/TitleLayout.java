package com.example.tianrui.tcpapplication.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tianrui.tcpapplication.R;

import static android.app.Activity.RESULT_OK;
import com.example.tianrui.tcpapplication.Activity.ForthBaseActivity;
/**
 * Created by tianrui on 2018/8/3.
 */

public class TitleLayout extends LinearLayout {
    //当引用自定义控件时就会调用该方法加载动态布局文件
    Button button_bk;
    Button button_ed;
    TextView textView;
    public TitleLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        //from方法获取到inflate对象，this是titlelayout的容器，第三个参数默认false
        LayoutInflater.from(context).inflate(R.layout.title, this);
        button_bk = (Button) findViewById(R.id.title_back);
        button_ed = (Button) findViewById(R.id.title_edit);
        textView = (TextView) findViewById(R.id.title_text);
        button_bk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("data_return", "来自自定义控件的问候");
                ((Activity) getContext()).setResult(RESULT_OK, intent);
                ((Activity) getContext()).finish();
            }
        });
        button_ed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HelloAct", "准备开启活动");
                Intent intent = new Intent((getContext()), ForthBaseActivity.class);
                (getContext()).startActivity(intent);
                //Toast.makeText(getContext(), "you clicked edit Button.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setText(String str){
        textView.setText(str);
    }

}
