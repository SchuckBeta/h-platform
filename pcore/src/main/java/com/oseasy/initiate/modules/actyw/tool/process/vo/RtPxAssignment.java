package com.oseasy.initiate.modules.actyw.tool.process.vo;

import com.oseasy.initiate.modules.actyw.tool.process.ActYwTool;

public class RtPxAssignment {
  private String assignee;

  public RtPxAssignment() {
    super();
    this.assignee = ActYwTool.FLOW_PROP_OBJECT;
  }

  public RtPxAssignment(String assignee) {
    super();
    this.assignee = assignee;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }
}
