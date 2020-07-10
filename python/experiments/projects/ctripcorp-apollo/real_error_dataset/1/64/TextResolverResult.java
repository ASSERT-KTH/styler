package com.ctrip.apollo.portal.service.txtresolver;

import com.ctrip.apollo.core.dto.ItemChangeSets;

public class TextResolverResult {

  private Code code;
  /**
   * extension msg. for example line number.
   */
  private String extensionMsg = "";
  private ItemChangeSets changeSets;


  public Code getCode() {
    return code;
  }

  public void setCode(Code code) {
    this.code = code;
  }

  public String getExtensionMsg() {
    return extensionMsg;
  }

  public void setExtensionMsg(String extensionMsg) {
    this.extensionMsg = extensionMsg;
  }

  public ItemChangeSets getChangeSets() {
    return changeSets;
  }

  public void setChangeSets(ItemChangeSets changeSets) {
    this.changeSets = changeSets;
  }

  public enum Code {
    OK(200, "success"), SIMPLTE_KVC_INVALID_FORMAT(40001, "item pattern must key:value##comment.pelease check!"),
    SIMPLE_KVC_TEXT_EMPTY(40002, "config text empty");

    private int value;
    private String baseMsg;

    Code(int value, String msg) {
      this.value = value;
      this.baseMsg = msg;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }

    public String getBaseMsg() {
      return baseMsg;
    }

    public void setBaseMsg(String baseMsg) {
      this.baseMsg = baseMsg;
    }
  }
}
