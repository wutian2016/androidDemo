package com.example.tianrui.tcpapplication.Activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.tianrui.tcpapplication.Adapter.BlueDeviceAdapter;
import com.example.tianrui.tcpapplication.BaseActivity;
import com.example.tianrui.tcpapplication.Bean.BlueDevice;
import com.example.tianrui.tcpapplication.R;
import com.example.tianrui.tcpapplication.Adapter.Info_BluetoothAdapter;
import com.example.tianrui.tcpapplication.Thread.BlueConnectThread;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class BlueToothAct extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "BlueToothAct";


    private ArrayList<BlueDevice> devices_con = new ArrayList<>();//已连接存储设备
    private ArrayList<BlueDevice> devices_uncon = new ArrayList<>();//未连接存储设备
    //private ArrayList<String> deviceName = new ArrayList();//设备名

    //功能相关
    private IntentFilter intentFilter_blueTooth;
    private FoundReceiver foundReceiver;
    //private Info_BluetoothAdapter arrayAdapter;
    private BlueDeviceAdapter blue_deviceAdapter;//两个适配器，将设备显示到listView中
    private BlueDeviceAdapter blue_deviceAdapter_con;

    private BluetoothAdapter adapter;//这个是自定义的
    private BluetoothSocket bluetoothSocket;//
    //界面相关
    private Switch switch_onoff;

    private ListView blue_listview_con;
    private ListView blue_listview_uncon;



    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.blue_tooth);
        Log.d(TAG, "进入界面");

        bluetoothPermissions();//请求蓝牙权限

        adapter = BluetoothAdapter.getDefaultAdapter();//获取蓝牙适配器

        blue_listview_uncon = (ListView) findViewById(R.id.list_unconnect);
        blue_listview_con = (ListView)findViewById(R.id.list_connect);
        switch_onoff = (Switch) findViewById(R.id.switch_onoff);//蓝牙开关按钮
        test_blueStatus();//使页面显示与机器设备同步
        switch_onoff.setOnClickListener(new View.OnClickListener() {//实现蓝牙开关的功能
            public void onClick(View view) {
                if(adapter.isEnabled()){
                    adapter.disable();
                    switch_onoff.setChecked(false);
                }else{
                    adapter.enable();
                    switch_onoff.setChecked(true);
                }
            }
        });
        //查询配对设备
        Set<BluetoothDevice> bondedDevices= adapter.getBondedDevices();
        for(BluetoothDevice device: bondedDevices){
            BlueDevice blueDevice = new BlueDevice(device.getName(), device.getAddress(), device.getBondState());
            devices_con.add(blueDevice);
            Log.d(TAG, "添加设备"+ device.getName() + " length=" + devices_con.size());
        }
        adapter.startDiscovery();//开始发现设备

        intentFilter_blueTooth = new IntentFilter();//注册蓝牙发现监听程序，发现蓝牙后，调用foundReceive方法中的onReceive方法
        intentFilter_blueTooth.addAction(BluetoothDevice.ACTION_FOUND);
        foundReceiver = new FoundReceiver();
        registerReceiver(foundReceiver, intentFilter_blueTooth);

        //蓝牙设备发现列表，上面应该实现功能，填充devices,1)已连接设备， 2）未连接设备
        //先关掉点击事件
        //blue_listview_con.setOnItemClickListener(this);
        Log.d(TAG, "从适配器看="+ devices_con.size());
        blue_deviceAdapter_con = new BlueDeviceAdapter(BlueToothAct.this, R.layout.blue_item, devices_con);
        blue_listview_con.setAdapter(blue_deviceAdapter_con);

        //blue_listview_uncon.setOnItemClickListener(this);
        blue_deviceAdapter = new BlueDeviceAdapter(BlueToothAct.this, R.layout.blue_item, devices_uncon);
        blue_listview_uncon.setAdapter(blue_deviceAdapter);//填充设备列表
    }

//    /**
//     * function: 点击ListView中的一项时，出发该事件，
//     * 如果已经配对，进行发送消息；
//     * 如果未连接，尝试进行配对，连接；
//     */
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//        if(adapter.isDiscovering()) adapter.cancelDiscovery();//stop discovering the blue device
//        BlueDevice item = devices_uncon.get(position);
//        BluetoothDevice device = adapter.getRemoteDevice(item.getAddress());
//        try{
//            if(device.getBondState() == BluetoothDevice.BOND_NONE){//未配对
//               Boolean resultCreateBond = device.createBond();
//               Log.d(TAG, "配对的结果为： " + resultCreateBond);
//            }else if(device.getBondState() == BluetoothDevice.BOND_BONDED ){//未测试，不知道对不对
//                Log.d(TAG, "开始进行连接");
////                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(device.getUuids().toString()));
////                BlueConnectThread blueConnectThread = new BlueConnectThread(bluetoothSocket);
//
//            }
//
//
//            if(!bluetoothSocket.isConnected()) bluetoothSocket.connect();
//            Log.d(TAG, "连接完成");
//
//            //将该设备加入已配对设备
//            devices_uncon.remove(item);
//            blue_deviceAdapter.notifyDataSetChanged();
//            devices_con.add(item);
//            blue_deviceAdapter_con.notifyDataSetChanged();
//        }catch (Exception e){
//            Log.d(TAG, "配对异常");
//            e.printStackTrace();
//        }
//    }

    //获得启动蓝牙的权限
    private void bluetoothPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    /**
     *使设备与界面显示同步
     */
    public void test_blueStatus(){
        //蓝牙开关
        if(adapter.isEnabled()){
            switch_onoff.setChecked(true);
        }else{
            switch_onoff.setChecked(false);
        }
    }
    /**
     * 在活动注销后，取消监听程序
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        adapter.cancelDiscovery();//取消发现设备功能
        unregisterReceiver(foundReceiver);
    }

    @Override//点击事件的监听
    public void onClick(View view) {
        switch(view.getId()){
            default:
        }
    }

    /**
     * 内部类，当监听到蓝牙连接的消息
     */
    class FoundReceiver extends BroadcastReceiver{
        //检测到设备
        public void onReceive(Context context, Intent intent){
            Log.d(TAG, "接收到蓝牙消息");
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BlueDevice blueDevice;
                if(device.getName() != null){//不处理设备名为null的设备
                    blueDevice = new BlueDevice(device.getName(),device.getAddress(), device.getBondState());
                    Log.d(TAG, device.getName() + " " + device.getAddress() + "  " + device.getBondState() );
                    devices_uncon.add(blueDevice);
                }
                blue_deviceAdapter.notifyDataSetChanged();
            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Log.d("BlueToothAct", "发现设备结束");
            }
        }
    }
}
