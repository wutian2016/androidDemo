package com.example.tianrui.tcpapplication.Adapter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.tianrui.tcpapplication.Activity.BlueToothAct;
import com.example.tianrui.tcpapplication.Bean.BlueDevice;
import com.example.tianrui.tcpapplication.R;
import com.example.tianrui.tcpapplication.Activity.BlueTooth_MSG;

import java.io.Serializable;
import java.util.List;

public class BlueDeviceAdapter extends ArrayAdapter<BlueDevice> {
    private int resourceId;

    public BlueDeviceAdapter(Context context, int textViewResourceId, List<BlueDevice> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        final BlueDevice device = getItem(position);
        View view;
        ViewHolder viewHolder;
        //获取界面控件
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);//这句什么意思？
            viewHolder = new ViewHolder();
            viewHolder.device_name = (TextView)view.findViewById(R.id.device_name);
            viewHolder.is_connect = (Button)view.findViewById(R.id.is_connect);
            viewHolder.send_msg = (Button) view.findViewById(R.id.send_MSG);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        //处理显示问题，将数据显示出来
        viewHolder.device_name.setText(device.getName());
        if(device.getBondState() == 10){//如果为建立连接，不显示
            viewHolder.send_msg.setVisibility(View.GONE);
            viewHolder.is_connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//开始设备连接
                    Intent intent = new Intent(getContext(), BlueTooth_MSG.class);
                    intent.putExtra("device_name", device.getName());
                    intent.putExtra("device_address", device.getAddress());
                    Log.d("BlueToothAct","点击了发送按钮:name="+ device.getName() + ",address=" + device.getAddress());
                    getContext().startActivity(intent);
                }
            });
        }else{
            viewHolder.is_connect.setVisibility(View.GONE);
            viewHolder.send_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//在以连接设备间开启数据交互，传递设备名称和地址
                    Intent intent = new Intent(getContext(), BlueTooth_MSG.class);
                    intent.putExtra("device_name", device.getName());
                    intent.putExtra("device_address", device.getAddress());
                    Log.d("BlueToothAct","点击了发送按钮:name="+ device.getName() + ",address=" + device.getAddress());
                    getContext().startActivity(intent);
                }
            });
        }
        //

        return view;
    }
    //连接两个蓝牙设备,完成配对功能，添加到已配对列表
    public void startConnect(String device_name, String device_address){

    }
    //item控件的内容
    class ViewHolder{
        TextView device_name;//设备名
        Button is_connect;//是否连接
        Button send_msg;//发送消息
    }
}
