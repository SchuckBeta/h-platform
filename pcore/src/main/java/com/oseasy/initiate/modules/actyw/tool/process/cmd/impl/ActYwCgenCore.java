/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl
 * @Description [[_ActYwCgenCore_]]文件
 * @date 2017年6月18日 上午11:30:45
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl;

import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwResult;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwCommand;

/**
 * 业务流程根基本属性类.
 * 生成基本项目流程基本信息的Json结构
 *
 * @author chenhao
 * @date 2017年6月18日 上午11:30:45
 *
 */
public class ActYwCgenCore implements ActYwCommand<ActYwGroup>{
  @Override
  public ActYwResult execute(ActYwGroup tpl) {
    return new ActYwResult();
  }
}
