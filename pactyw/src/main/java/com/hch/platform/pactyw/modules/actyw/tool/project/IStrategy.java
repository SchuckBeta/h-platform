package com.oseasy.initiate.modules.actyw.tool.project;

import com.oseasy.initiate.modules.act.vo.ActRstatus;

/**
 * 流程处理生成策略.
 * @author chenhao
 *
 */
public interface IStrategy {
  /**
   * 策略方法
   */
  public ActRstatus deal();
}
