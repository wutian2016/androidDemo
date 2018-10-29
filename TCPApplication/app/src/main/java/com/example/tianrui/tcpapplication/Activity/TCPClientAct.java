package com.example.tianrui.tcpapplication.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.example.tianrui.tcpapplication.BaseActivity;
import com.example.tianrui.tcpapplication.Bean.Msg;
import com.example.tianrui.tcpapplication.R;
import com.example.tianrui.tcpapplication.Adapter.*;

import static java.lang.Thread.*;

/**
 * Created by tianrui on 2018/8/3.
 */

public class TCPClientAct extends BaseActivity {

    public static final String TAG = "TCPClient";
    public static final int UPDATE_TEXT=1;
    public static final int UPDATE_BTN=2;
    Button btn_connect;
    Button btn_send;
    EditText edit_ip;
    EditText edit_port;
    EditText edit_input;
    //TextView text_result;
    String str_ip;
    int port;
    String content;//要发送的数据
    ListView msgListview;
    Socket msocket;
    BufferedWriter bw;
    BufferedReader bf;

    //用于消息记录
    private List msgList = new ArrayList<Msg>();
    private MsgAdapter adapter;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case UPDATE_TEXT:
                    Log.d(TAG, "连接成功获得返回结果" + msg.toString());
                    btn_connect.setText("Disconnect");
                    Msg info = new Msg(msg.obj.toString(), Msg.TYPE_RECEIVED);
                    msgList.add(info);
                    adapter.notifyDataSetChanged();
                    msgListview.setSelection(msgList.size());
                    break;
                case UPDATE_BTN:
                    Log.d(TAG, "改变button内容");
                    if("Disconnect" == btn_connect.getText().toString()){
                        btn_connect.setText("Connect");
                    }else{
                        btn_connect.setText("Disconnect");
                    }
                    break;
                default:
                    Log.d(TAG, "连接失败");
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tcp_client);

        edit_ip = (EditText)findViewById(R.id.edit_ip);
        edit_port = (EditText)findViewById(R.id.edit_port);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        //建立连接
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取端口号与IP地址
                str_ip = edit_ip.getText().toString();
                port = Integer.parseInt(edit_port.getText().toString());
                //如果为连接状态，关闭连接。否则，开启连接
                if(btn_connect.getText().toString() == "Disconnect"){
                    try{
                        msocket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    btn_connect.setText("Connect");
                }else {
                    //new Thread(new MyThread()).start();//发送hello的线程
                    ceShiConnection();
                }
            }
        });

        msgListview = (ListView) findViewById(R.id.msg_list_view);//消息列表部分
        edit_input = (EditText)findViewById(R.id.edit_input);//输入框
        adapter = new MsgAdapter(TCPClientAct.this, R.layout.msg_item, msgList);//建立适配器
        msgListview.setAdapter(adapter);//列表使用新的适配器。
        btn_send = (Button)findViewById(R.id.btn_send);
        //发送数据
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = edit_input.getText().toString();
                if(!"".equals(content)){
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);//添加到消息列表中
                    adapter.notifyDataSetChanged();//适配器通知数据变化
                    msgListview.setSelection(msgList.size());//列表选择最后一行
                    edit_input.setText("");//清空输入
                    //发送该数据,应该开启一个新线程来发送。
                    new Thread(new MyThread()).start();
                }
            }
        });
    }
    public void ceShiConnection(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               try{
                   msocket = new Socket(str_ip, port);
                   if(msocket.isConnected()){
                       Message msg = handler.obtainMessage(UPDATE_BTN, 0, 0, null);
                       handler.sendMessage(msg);
                   }
                   msocket.setSoTimeout(4000);//无数据4s后断开
              }catch (IOException e){
                   e.printStackTrace();
               }

           }
       }).start();
    }
    //学习以下线程的用法+handle/message机制
    public class MyThread implements Runnable{
        public void run(){

            try{
                //String str = "hello";
                if(!msocket.isConnected()){
                    msocket = new Socket(str_ip, port);
                    msocket.setSoTimeout(4000);//
                }
                Log.d(TAG, "开始建立socket");
                //int result = msocket.isConnected() == true? 1:0;
                bw = new BufferedWriter(new OutputStreamWriter(msocket.getOutputStream()));
                bw.write(content);
                bw.flush();
                bf = new BufferedReader(new InputStreamReader(msocket.getInputStream()));
                Log.d(TAG, "bf is ok");
                String str;
                while((str = bf.readLine()) != null){
                    Log.d(TAG, "获取的内容为" + str);
                    Message message = handler.obtainMessage(UPDATE_TEXT, 0, 0, str);
                    handler.sendMessage(message);
                }
                //Message message = handler.obtainMessage(UPDATE_TEXT, -1, 0, str);
                Log.d(TAG, "读取数据结束，关闭输入输出流");
                bw.close();
                bf.close();
                msocket.close();
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}
