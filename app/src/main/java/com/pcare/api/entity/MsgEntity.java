package com.pcare.api.entity;

public class MsgEntity {
    public static final int SEND_MSG = 1;//我
    public static final int RECV_MSG = 2;//机器人
    private String content;
    private int type;

    public MsgEntity(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
