/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd
 * @Description [[_ActYwEcmd_]]文件
 * @date 2017年6月18日 上午11:39:24
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd;

/**
 * 流程生成命令枚举.
 * @author chenhao
 * @date 2017年6月18日 上午11:39:24
 *
 */
public enum ActYwEcmd {
  ECMD_ROOT_ADD("rootAdd", "新增根节点"),
  ECMD_ROOT_ADD_NODE("rootAddNode", "添加节点"),
  ECMD_ROOT_ADD_NODEGATE("rootAddNodeGate", "添加网关节点"),
  ECMD_ROOT_ADD_NODEJG("rootAddNodeJG", "添加结构节点(子流程)");

  private String key;
  private String remark;

  private ActYwEcmd(String key, String remark) {
    this.key = key;
    this.remark = remark;
  }

  /**
   * 根据关键字获取枚举 .
   *
   * @author chenhao
   * @param key
   *          关键字
   * @return StenEtype
   */
  public static ActYwEcmd getByKey(String key) {
    ActYwEcmd[] entitys = ActYwEcmd.values();
    for (ActYwEcmd entity : entitys) {
      if ((key).equals(entity.getKey())) {
        return entity;
      }
    }
    return null;
  }

  public String getKey() {
    return key;
  }

  public String getRemark() {
    return remark;
  }
}
