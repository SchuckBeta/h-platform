package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;

/**
 * 所有节点处理结果状态.
 * @author chenhao
 *
 */
public class GnodeSvo {
  private ActYwRstatus rstatus;
  private List<GnodeStatus> gnodeStatus;

  public ActYwRstatus getRstatus() {
    return rstatus;
  }
  public void setRstatus(ActYwRstatus rstatus) {
    this.rstatus = rstatus;
  }
  public List<GnodeStatus> getGnodeStatus() {
    return gnodeStatus;
  }
  public void setGnodeStatus(List<GnodeStatus> gnodeStatus) {
    this.gnodeStatus = gnodeStatus;
  }

}
