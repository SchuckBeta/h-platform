/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd
 * @Description [[_ActYwCommand_]]文件
 * @date 2017年6月18日 上午11:13:07
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd;

/**
 * 业务流程命令接口.
 * @author chenhao
 * @date 2017年6月18日 上午11:13:07
 *
 */
public interface ActYwCommand<T extends ActYwPtpl> {
  public abstract ActYwRtpl execute(T tpl);
}
