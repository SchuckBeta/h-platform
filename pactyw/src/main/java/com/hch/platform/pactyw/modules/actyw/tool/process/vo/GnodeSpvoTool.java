package com.oseasy.initiate.modules.actyw.tool.process.vo;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;

public class GnodeSpvoTool {

  /**
   * 验证是否为根节点.
   * @param spvo 节点参数.
   * @return ActYwRstatus
   */
  public static ActYwRstatus validateRoot(GnodeSpvo spvo) {
    ActYwRstatus ywstatus = new ActYwRstatus(true, "不是根节点!");
    if(spvo.getIsRoot()){
      if((spvo.getRoot() == null)){
        ywstatus.setStatus(false);
        ywstatus.setMsg("根节点为空!");
        return ywstatus;
      }
      ActYwGnode root = spvo.getRoot();
      if(!((root != null) && (root.getParentId()).equals(SysIds.SYS_TREE_ROOT.getId()))){
        ywstatus.setStatus(false);
        ywstatus.setMsg("不满足根节点父节点为顶级节点!");
        return ywstatus;
      }
      ywstatus.setMsg("根节验证成功!");
    }
    return ywstatus;
  }

  /**
   * 验证是否为子流程节点.
   * @param spvo 节点参数.
   * @return ActYwRstatus
   */
  public static ActYwRstatus validateProcess(GnodeSpvo spvo) {
    ActYwRstatus ywstatus = new ActYwRstatus(true, "不是子流程节点!");
    if(spvo.getIsProcess()){
      if((spvo.getProcess() == null)){
        ywstatus.setStatus(false);
        ywstatus.setMsg("子流程节点为空!");
        return ywstatus;
      }

      ywstatus.setMsg("子流程节点验证成功!");
    }
    return ywstatus;
  }

  /**
   * 验证是否为网关点.
   * @param spvo 节点参数.
   * @return ActYwRstatus
   */
  public static ActYwRstatus validateGateWay(GnodeSpvo spvo) {
    ActYwRstatus ywstatus = new ActYwRstatus(true, "没有网关节点!");
    if(spvo.getHasGateWay()){
      if((spvo.getGateWay() == null)){
        ywstatus.setStatus(false);
        ywstatus.setMsg("网关节点为空!");
        return ywstatus;
      }

      if((spvo.getYwSlibGnodes() == null) || (spvo.getYwSlibGnodes().size() != 1)){
        ywstatus.setStatus(false);
        ywstatus.setMsg("不满足网关节点执行条件(兄弟节点数为1)!");
        return ywstatus;
      }

      ywstatus.setMsg("网关节点验证成功!");
    }
    return ywstatus;
  }

  /**
   * 验证是否为驳回节点.
   * @param spvo 节点参数.
   * @return ActYwRstatus
   */
  public static ActYwRstatus validateReturnGnode(GnodeSpvo spvo) {
    ActYwRstatus ywstatus = new ActYwRstatus(true, "不是驳回节点!");
    if(spvo.getIsProcess()){
      if((spvo.getHasReturnGnode() == null)){
        ywstatus.setStatus(false);
        ywstatus.setMsg("子流程节点为空!");
        return ywstatus;
      }

      if((spvo.getReturnGnode() == null)){
        ywstatus.setStatus(false);
        ywstatus.setMsg("不满足驳回节点执行条件!");
        return ywstatus;
      }
      ywstatus.setMsg("驳回节点验证成功!");
    }
    return ywstatus;
  }
}
