package com.oseasy.initiate.modules.pw.vo;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.entity.PwEnter;

public class PwEnterExpireVo {
  private Integer totalNum; //总记录数
  private Integer ignorNum; //忽略记录数
  private Integer succNum; //成功记录数
  private Integer failNum; //失败记录数
  private String logFile; //日志文件路径
  private List<ActYwRstatus<PwEnter>> ignorEnters; //忽略记录
  private List<ActYwRstatus<PwEnter>> succEnters; //成功记录
  private List<ActYwRstatus<PwEnter>> failEnters; //失败记录


  public PwEnterExpireVo() {
    super();
    this.ignorEnters = Lists.newArrayList();
    this.succEnters = Lists.newArrayList();
    this.failEnters = Lists.newArrayList();
  }
  public Integer getTotalNum() {
    return totalNum;
  }
  public void setTotalNum(Integer totalNum) {
    this.totalNum = totalNum;
  }
  public Integer getSuccNum() {
    if(this.succNum == null){
      this.succNum = this.succEnters.size();
    }
    return succNum;
  }
  public void setSuccNum(Integer succNum) {
    this.succNum = succNum;
  }
  public Integer getFailNum() {
    if(this.failNum == null){
      this.failNum = this.failEnters.size();
    }
    return failNum;
  }
  public void setFailNum(Integer failNum) {
    this.failNum = failNum;
  }
  public Integer getIgnorNum() {
    if(this.ignorNum == null){
      this.ignorNum = this.ignorEnters.size();
    }
    return ignorNum;
  }
  public void setIgnorNum(Integer ignorNum) {
    this.ignorNum = ignorNum;
  }
  public List<ActYwRstatus<PwEnter>> getIgnorEnters() {
    return ignorEnters;
  }
  public void setIgnorEnters(List<ActYwRstatus<PwEnter>> ignorEnters) {
    this.ignorEnters = ignorEnters;
  }
  public List<ActYwRstatus<PwEnter>> getSuccEnters() {
    return succEnters;
  }
  public void setSuccEnters(List<ActYwRstatus<PwEnter>> succEnters) {
    this.succEnters = succEnters;
  }
  public List<ActYwRstatus<PwEnter>> getFailEnters() {
    return failEnters;
  }
  public void setFailEnters(List<ActYwRstatus<PwEnter>> failEnters) {
    this.failEnters = failEnters;
  }
  public String getLogFile() {
    return logFile;
  }
  public void setLogFile(String logFile) {
    this.logFile = logFile;
  }
}