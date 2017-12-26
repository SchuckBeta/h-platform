package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.Arrays;
import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;

/**
 * 流程执行状态实体.
 * @author chenhao
 */
public class GnodeView{
  public static final int GV_NO_START = 0;//0未开始/1执行中/2已执行结束
  public static final int GV_RUNNING = 1;//0未开始/1执行中/2已执行结束
  public static final int GV_END = 2;//0未开始/1执行中/2已执行结束
  private Integer rstatus; //0未开始/1执行中/2已执行结束
  private Boolean isFront; //是否前台节点
  private ActYwGnode gnode;

  public GnodeView() {
    super();
  }

  public GnodeView(ActYwGnode gnode) {
    super();
    this.rstatus = GV_NO_START;
    this.gnode = gnode;
  }

  public GnodeView(Integer rstatus, ActYwGnode gnode) {
    super();
    this.rstatus = rstatus;
    this.gnode = gnode;
  }

  public Integer getRstatus() {
    if(rstatus == null){
      rstatus = GV_NO_START;
    }
    return rstatus;
  }

  public Boolean getIsFront() {
    return isFront;
  }

  public void setIsFront(Boolean isFront) {
    this.isFront = isFront;
  }

  public void setRstatus(Integer rstatus) {
    this.rstatus = rstatus;
  }
  public ActYwGnode getGnode() {
    return gnode;
  }
  public void setGnode(ActYwGnode gnode) {
    this.gnode = gnode;
  }

  public static void main(String[] args) {
    String sss = "1, 19999d55bf1f4313ad3201019126b5b5, fe0c8ce5374445989daccd05fb2fe557, f781e5e9f58c475db2abf47f2aebf939, 4bfe23393e0848c39a386cb862e4ed0b, 37cc78dd6dcc4d0a920c695670ef5427, 5a44bc7ab17c45719c5e341078479cdf,";
    List<String> ss1 = Arrays.asList((sss).split(","));
    System.out.println(ss1);
    System.out.println(ss1.size());
  }
}
