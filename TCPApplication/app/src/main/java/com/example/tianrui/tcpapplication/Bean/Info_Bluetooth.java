package com.example.tianrui.tcpapplication.Bean;

public class Info_Bluetooth {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;
    public Info_Bluetooth(String content, int type) {
        this.content = content;
        this.type = type;
    }
    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }
}
