package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd;

import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.IcmdPtpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.IvalidatePtpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.IvalidateRtpl;

/**
 * 修改操作和新增操作时，验证数据是否完整或存在.
 * 当前节点完整性验证.
 * @author chenhao
 *
 */
public interface IGnodeValidate<T extends IvalidatePtpl, RT extends IvalidateRtpl, P extends IcmdPtpl>{

  /**
   * 当前节点数据是否符合新增条件.
   * @return
   */
  public ActYwRstatus<RT> isCanAdd(T vptl, P param);

  /**
   * 当前节点数据是否符合修改条件.
   * @return
   */
  public ActYwRstatus<RT> isCanUpdate(T vptl, P param);

  /**
   * 当前节点删除是否符合删除条件.
   * @return
   */
  public ActYwRstatus<RT> isCanDelete(T vptl, P param);
}
