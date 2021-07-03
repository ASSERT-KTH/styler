package com.ctrip.apollo.core.entity;

/**
 * declare biz code and simple msg. maybe http response code is 200.
 */
public class SimpleRestfulResponse {

  private int code;
  private String msg;

  public SimpleRestfulResponse(int code, String msg){
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
