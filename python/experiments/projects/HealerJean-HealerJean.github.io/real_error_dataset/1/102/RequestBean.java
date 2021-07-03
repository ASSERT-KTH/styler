package com.hlj.netty.websocket.bean;

import java.io.Serializable;

/**
 * Created by j.sh on 20/03/2018.
 */
public class RequestBean implements Serializable{

    private static final long serialVersionUID = 6911183783207142064L;

    private String from; //来自谁
    private String to; //发给谁
    private String content; //内容是什么
    private Integer init = 0;   // 默认不是初始化

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getInit() {
        return init;
    }

    public void setInit(Integer init) {
        this.init = init;
    }
}
