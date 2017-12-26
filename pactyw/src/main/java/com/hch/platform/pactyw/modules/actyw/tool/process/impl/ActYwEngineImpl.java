/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.impl
 * @Description [[_ActYwEngineImpl_]]文件
 * @date 2017年6月19日 上午11:52:00
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwNodeService;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwEngine;

/**
 * 流程生成器数据库操作引擎实现.
 * @author chenhao
 * @date 2017年6月19日 上午11:52:00
 *
 */
public class ActYwEngineImpl implements ActYwEngine<ActYwGnodeService, ActYwNodeService>{
  @Autowired
  ActYwGnodeService actYwGnodeService;
  @Autowired
  ActYwNodeService actYwNodeService;

  public ActYwEngineImpl(ActYwGnodeService actYwGnodeService, ActYwNodeService actYwNodeService) {
    super();
    this.actYwGnodeService = actYwGnodeService;
    this.actYwNodeService = actYwNodeService;
  }

  @Override
  public ActYwGnodeService service() {
    return actYwGnodeService;
  }

  @Override
  public ActYwNodeService nodeservice() {
    return actYwNodeService;
  }
}
