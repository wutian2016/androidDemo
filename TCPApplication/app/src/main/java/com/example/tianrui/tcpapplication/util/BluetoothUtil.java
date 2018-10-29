package com.example.tianrui.tcpapplication.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 蓝牙工具包
 * 提供了四个方法：检查蓝牙开关，打开或关闭蓝牙，从输入流读取，像输出流写入
 */
public class BluetoothUtil {
    private final static String TAG = "BluetoothUtil";

    //获取蓝牙开关状态
    public static boolean getBlueToothStatus(Context context){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean enabled = false;
        switch(bluetoothAdapter.getState()){
            case BluetoothAdapter.STATE_ON:
            case BluetoothAdapter.STATE_TURNING_ON:
                enabled = true;
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
            default:
                enabled = false;
                break;
        }
        return enabled;
    }
    //打开或关闭蓝牙
    public static void setBluetoothStatus(Context context, boolean status){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(status == true){
            bluetoothAdapter.enable();//打开蓝牙
        }else{
            bluetoothAdapter.disable();
        }
    }
    //从输入流读取数据
    public static String readInputStream(InputStream instream){
        String result = "";
        try{
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = instream.read(buffer)) != -1){
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();//转换为字节数组？
            outStream.close();
            instream.close();
            result = new String(data, "utf8");//转换为utf8
        }catch(Exception e){
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }
    //利用socket进行数据传输
    public static void writeOutputStream(BluetoothSocket socket, String message){
        Log.d(TAG, "begin writeOutputstream message = "+ message);
        try{
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(message.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "end writeOutputStream");
    }
}
