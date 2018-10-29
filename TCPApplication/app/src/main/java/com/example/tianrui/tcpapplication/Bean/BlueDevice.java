package com.example.tianrui.tcpapplication.Bean;

/**
 * 一个蓝牙实体
 */
public class BlueDevice {
    private String name;
    private String address;
    private int state;

    public BlueDevice(){
        name = "";
        address = "";
        state = 0;
    }

    public BlueDevice(String name, String address, int state){
        this.name = name;
        this.address = address;
        this.state = state;
    }
    public String getName(){
        return this.name;
    }
    public String getAddress(){
        return this.address;
    }
    public int getBondState(){
        return this.state;
    }
}
