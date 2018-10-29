package com.example.tianrui.tcpapplication.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.tianrui.tcpapplication.Adapter.MsgAdapter;
import com.example.tianrui.tcpapplication.BaseActivity;
import com.example.tianrui.tcpapplication.Bean.Msg;
import com.example.tianrui.tcpapplication.R;
import com.example.tianrui.tcpapplication.layout.TitleLayout;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class BlueTooth_MSG extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "BlueTooth_MSG";
    private static final int STATE_NONE = 0; //初始状态//蓝牙的连接状态
    private static final int STATE_LISTEN = 1; //等待连接
    private static final int STATE_CONNECTING = 2; //正在连接
    private static final int STATE_CONNECTED= 3; //已连接
    private static int state = 0;
    //界面相关
    private TitleLayout titleLayout = null;
    private ListView listView = null;
    private EditText editText = null;
    private Button btn_send = null;
    //功能相关
    private static final int NOTICE_VIEW = 0;
    private static final int RCV_VIEW = 1;
    private ConnectedThread connectedThread = null;//两个线程用于连接后与连接前的数据交互
    private ConnectThread connectThread = null;

    private ArrayList<Msg> data = new ArrayList<Msg>();//用于与蓝牙交互的存放消息
    private MsgAdapter msgAdapter = null;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bluetooth_msg);
        Log.d(TAG, "进入页面");

        Intent intent = getIntent();
        String device_name = intent.getStringExtra("device_name");
        String device_address = intent.getStringExtra("device_address");

        titleLayout = (TitleLayout) findViewById(R.id.title_bluetooth_msg);//这个值获取到为null?
        titleLayout.setText(device_name);
        editText = (EditText)findViewById(R.id.edit_input);
        btn_send = (Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        data.add(new Msg("你好", Msg.TYPE_SENT));//模拟
        data.add(new Msg("hello", Msg.TYPE_RECEIVED));

        listView = (ListView)findViewById(R.id.msg_list_view);
        msgAdapter = new MsgAdapter(BlueTooth_MSG.this, R.layout.msg_item, data);
        listView.setAdapter(msgAdapter);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//获取蓝牙适配器
        if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();//关闭发现功能
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device_address);

        connectThread = new ConnectThread(bluetoothDevice);//初始化连接线程
        connectThread.start();
        Log.d(TAG, "完成启动");
    }
    //设置蓝牙的连接状态，只允许在该类内调用
    private static void setState(int mstate){
        state = mstate;
    }
    //连接完成后启动ConnectedThread
    public synchronized void connected(BluetoothSocket socket){
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(STATE_CONNECTED);
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    //蓝牙在线程里接受数据，传给该对象
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = null;
            switch (msg.what) {
                case RCV_VIEW://收到消息，显示到文件中
                    Log.d(TAG, "mhandler收到消息" + msg.obj.toString());
                    data.add(new Msg(msg.obj.toString(), Msg.TYPE_RECEIVED));
                    msgAdapter.notifyDataSetChanged();
                    break;
                case NOTICE_VIEW://收到提醒，用Toast提示
                    Log.d(TAG, "mhandler收到提醒"+ msg.obj.toString());
//                    bundle = msg.getData();//这么获取msg里面的值
//                    String notice = bundle.getString("notice");
                    break;
                default:
            }
        }};

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                String text_input = editText.getText().toString();
                Log.d(TAG, "点击了发送按钮，试图发送:"+ text_input);
                boolean result = connectedThread.write(text_input.getBytes());
                if(result){
                    Log.d(TAG, "点击发送成功：" + text_input);
                    data.add(new Msg(text_input, Msg.TYPE_SENT));
                    msgAdapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "点击发送失败：" + text_input);
                }
                break;
            default:
                    Log.d(TAG, "onClick方法接收到未知事件");
        }
    }

    //用于蓝牙连接
    private class ConnectThread extends Thread {
        private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                //尝试建立安全的连接
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            } catch (Exception e) {
                Log.i(TAG,"获取 BluetoothSocket失败");
                e.printStackTrace();
            }
            this.mmSocket = tmp;
            connected(mmSocket);
        }
        @Override
        public void run() {
            try {
                mmSocket.connect();
            } catch (Exception e) {
                Log.i(TAG,"socket连接失败");
                return;
            }
        }

        public void cancel(){
            setState(STATE_NONE);
            try {
                mmSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //蓝牙连接完成后进行输入输出
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (Exception e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            Log.d(TAG, "开始进行读取");
            //当连接状态为连接时，循环读取,问题出现在run方法里面了
            while(state == STATE_CONNECTED){
                try {
                    // 从InputStream中读取
                    Scanner in = new Scanner(mmInStream,"UTF-8");
                    String str = in.nextLine();
                    if(str != null){
                        Log.i(TAG,"在线程中，read: "+str);
                        Message msg = new Message();//利用handle传递数据，
                        msg.what = RCV_VIEW;
                        Bundle bundle = new Bundle();
                        bundle.putString("datarcv",str);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                    if(str.endsWith("#") || str == null){//如果发送以#结尾或空串，认为发送完成
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setState(STATE_NONE);//出现错误，关闭连接
                    Log.d(TAG, "device disconnected");
                }
            }
        }
        public boolean write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception during write", e);
                return false;
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}
