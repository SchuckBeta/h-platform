/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process
 * @Description [[_ActYwEngine_]]文件
 * @date 2017年6月19日 上午11:49:15
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process;

/**
 * 流程生成器数据库操作引擎.
 * @author chenhao
 * @date 2017年6月19日 上午11:49:15
 *
 */
public interface ActYwEngine<T, T1> {
  public T service();
  public T1 nodeservice();
}
