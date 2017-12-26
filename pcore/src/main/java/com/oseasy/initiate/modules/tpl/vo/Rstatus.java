package com.hch.platform.pcore.modules.tpl.vo;

import net.sf.json.JSONObject;

public class Rstatus {
  private Boolean status;
  private String msg;
  private JSONObject datas;

  public Rstatus() {
    super();
    this.status = true;
    this.msg = "执行成功";
  }

  public Rstatus(Boolean status, String msg) {
    super();
    this.status = status;
    this.msg = msg;
  }

  public Rstatus(Boolean status, String msg, JSONObject datas) {
    super();
    this.status = status;
    this.msg = msg;
    this.datas = datas;
  }

  public Boolean getStatus() {
    return status;
  }
  public void setStatus(Boolean status) {
    this.status = status;
  }
  public String getMsg() {
    return msg;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Rstatus setDatas(JSONObject datas) {
    this.datas = datas;
    return this;
  }

  public JSONObject getDatas() {
    return datas;
  }
}
