package com.oseasy.initiate.modules.actyw.tool.project.impl;

import com.oseasy.initiate.modules.act.vo.ActRstatus;
import com.oseasy.initiate.modules.actyw.tool.project.IStrategy;

/**
 * 菜单生成策略.
 */
public class StyMenu2 implements IStrategy{

  @Override
  public ActRstatus deal() {

    return new ActRstatus(true, "StyMenu2 方案执行处理完成了！");
  }
}
