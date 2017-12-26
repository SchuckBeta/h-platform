package com.oseasy.initiate.modules.actyw.tool.project.impl;

import com.oseasy.initiate.modules.act.vo.ActRstatus;
import com.oseasy.initiate.modules.actyw.tool.project.IStrategy;

/**
 * 流程部署策略.
 */
public class StyDeploy implements IStrategy{

  @Override
  public ActRstatus deal() {
    return new ActRstatus(true, "StyDeploy1 方案执行处理完成了！");
  }
}
