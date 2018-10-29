package com.example.tianrui.tcpapplication.Bean;

/**
 * Created by tianrui on 2018/8/7.
 * 对于收到的消息，显示在list中
 * */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;
    public Msg(String content, int type) {
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
