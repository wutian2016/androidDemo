package com.example.tianrui.tcpapplication.Thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用于蓝牙间通信
 */
public class BlueConnectThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public BlueConnectThread(BluetoothSocket socket){
        this.socket = socket;
        InputStream input = null;
        OutputStream output = null;
        try{
            input = socket.getInputStream();
            output = socket.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
        }
        this.inputStream = input;
        this.outputStream = output;
    }

    public void run(){
        byte[] buffer = new byte[1024];
        int bytes;

        //keep listening to the InputStream until exception
    }
    //send data
    public void write(byte [] bytes){
        try{
            outputStream.write(bytes);
        }catch (Exception e){}
    }
    // shut down connection
    public void cancel(){
        try{
            socket.close();
        }catch (Exception e){}
    }
}
