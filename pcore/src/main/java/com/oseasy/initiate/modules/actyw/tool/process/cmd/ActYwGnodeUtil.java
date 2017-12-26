package com.oseasy.initiate.modules.actyw.tool.process.cmd;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;

public class ActYwGnodeUtil {
  /**
   * 判断hasGroup属性是否为空，如果为空.
   * 若当前添加节点为子流程节点，则设置hasGroup=true,否则设置hasGroup=false。
   */
  public static ActYwGnode checkHasGroup(ActYwGnode gnode) {
    if((gnode.getHasGroup() == null)){
      if(((gnode.getType()).equals(GnodeType.GT_PROCESS.getId()))){
        gnode.setHasGroup(true);
      }else{
        gnode.setHasGroup(false);
      }
    }
    return gnode;
  }
}
