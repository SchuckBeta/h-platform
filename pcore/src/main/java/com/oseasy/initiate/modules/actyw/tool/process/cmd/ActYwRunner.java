/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd
 * @Description [[_ActYwRunner_]]文件
 * @date 2017年6月18日 上午11:16:08
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootAdd;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootAddNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootAddNodeGate;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootDelete;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootDeleteNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootDeleteNodeGate;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwPgroot;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwRgroot;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;


/**
 * 流程引擎执行类.
 * @author chenhao
 * @date 2017年6月18日 上午11:16:08
 *
 */
@Service
public class ActYwRunner {
  public static final Boolean IS_ROOT = true;//是根节点操作
  public static final Boolean IS_ROOT_NOT = false;//不是根节点操作

  private ActYwEngineImpl engine;
  @Autowired
  private ActYwCnRootAdd actYwCnRootAdd;
  @Autowired
  private ActYwCnRootAddNode actYwCnRootAddNode;
  @Autowired
  private ActYwCnRootAddNodeGate actYwCnRootAddNodeGate;
  @Autowired
  private ActYwCnRootDelete actYwCnRootDelete;
  @Autowired
  private ActYwCnRootDeleteNode actYwCnRootDeleteNode;
  @Autowired
  private ActYwCnRootDeleteNodeGate actYwCnRootDeleteNodeGate;


  /**
   * 执行批量命令.
   * @param cmd 命令
   * @param param 参数
   * @return ActYwRstatus
   */
  public ActYwRstatus<ActYwGnode> callExecutePL(List<Map<ActYwEcmd, ActYwPgroot>> cmdParams) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    if (cmdParams != null) {
      List<ActYwRstatus<ActYwGnode>> isFales = Lists.newArrayList();

      for (Map<ActYwEcmd, ActYwPgroot> cmdParam : cmdParams) {
        Iterator<ActYwEcmd> itor = cmdParam.keySet().iterator();
        while (itor.hasNext()) {
          ActYwEcmd cmd = (ActYwEcmd) itor.next();
          ActYwPgroot param = cmdParam.get(cmd);
          if ((cmd == null) || (param == null)) {
             continue;
          }

          ActYwRstatus<ActYwGnode> isCurStatus = null;
          if ((ActYwEcmd.ECMD_ROOT_ADD).equals(cmd)) {
            isCurStatus = saveRoot(param.getGroup());
          }else if ((ActYwEcmd.ECMD_ROOT_ADD_NODE).equals(cmd)) {
            isCurStatus = saveRootNode(param);
          }else if ((ActYwEcmd.ECMD_ROOT_ADD_NODEGATE).equals(cmd)) {
            isCurStatus = saveRootNodeGate(param);
          }else{
            isCurStatus = new ActYwRstatus<ActYwGnode>(false, "当前命令["+cmd.getKey()+"]未定义！");
          }

          if (!isCurStatus.getStatus()) {
            isFales.add(isCurStatus);
          }
        }
      }

      if (!isFales.isEmpty()) {
        return isFales.get(0);
      }
    }else{
      rstatus.setStatus(false);
      rstatus.setMsg("命令执行参数不能为空！");
    }
    return rstatus;
  }

  /**
   * 执行批量命令.
   * @param cmd 命令
   * @param param 参数
   * @return ActYwRstatus
   */
  public ActYwRstatus<ActYwGnode> callExecutePL(ActYwEcmd cmd, ActYwPgroot param) {
    return callExecutePL(addCmdParam(Lists.newArrayList(), cmd, param));
  }

  /**
   * 执行单个命令.
   * @param cmd 命令
   * @param param 参数
   * @return ActYwRstatus
   */
  public ActYwRstatus<ActYwGnode> callExecute(ActYwEcmd cmd, ActYwPgroot param) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>(false, "执行失败");
    if ((cmd == null) || (param == null)) {
      rstatus.setMsg("执行失败，命令或参数不能为空！");
      return rstatus;
    }

    if ((ActYwEcmd.ECMD_ROOT_ADD).equals(cmd)) {
      rstatus = saveRoot(param.getGroup());
    }else if ((ActYwEcmd.ECMD_ROOT_ADD_NODE).equals(cmd)) {
      rstatus = saveRootNode(param);
    }else if ((ActYwEcmd.ECMD_ROOT_ADD_NODEGATE).equals(cmd)) {
      rstatus = saveRootNodeGate(param);
    }else if ((ActYwEcmd.ECMD_ROOT_DELETE).equals(cmd)) {
      rstatus = deleteRoot(param.getGroup());
    }else if ((ActYwEcmd.ECMD_ROOT_DELETE_NODE).equals(cmd)) {
      rstatus = deleteRootNode(param);
    }else if ((ActYwEcmd.ECMD_ROOT_DELETE_NODEGATE).equals(cmd)) {
      rstatus = deleteRootNodeGate(param);
    }else{
      rstatus = new ActYwRstatus<ActYwGnode>(false, "当前命令["+cmd.getKey()+"]未定义！");
    }
    return rstatus;
  }

  /**
   * 添加CMD参数到命令列表 .
   * @author chenhao
   * @param cmd 命令
   * @param param 参数
   * @param cmdParams 集合
   * @return List
   */
  public static List<Map<ActYwEcmd, ActYwPgroot>> addCmdParam(List<Map<ActYwEcmd, ActYwPgroot>> cmdParams, ActYwEcmd cmd, ActYwPgroot param) {
    Map<ActYwEcmd, ActYwPgroot> cmdParam = new HashMap<ActYwEcmd, ActYwPgroot>();
    cmdParam.put(cmd, param);
    cmdParams.add(cmdParam);
    return cmdParams;
  }

  public boolean saveRootStatus(ActYwGroup group) {
    return saveRoot(group).getStatus();
  }

  public ActYwRstatus<ActYwGnode> saveRoot(ActYwGroup group) {
    actYwCnRootAdd.setEngine(engine);
    ActYwRgroot actYwRgroot = actYwCnRootAdd.execute(group);
    return new ActYwRstatus<ActYwGnode>(actYwRgroot.getStatus(), actYwRgroot.getMsg(), actYwRgroot.getDatas());
  }

  public boolean deleteRootStatus(ActYwGroup group) {
    return deleteRoot(group).getStatus();
  }

  public ActYwRstatus<ActYwGnode> deleteRoot(ActYwGroup group) {
    actYwCnRootDelete.setEngine(engine);
    return actYwCnRootDelete.execute(group);
  }

  public boolean saveRootNodeStatus(ActYwPgroot actYwPgroot) {
    return saveRootNode(actYwPgroot).getStatus();
  }

  public ActYwRstatus<ActYwGnode> saveRootNode(ActYwPgroot actYwPgroot) {
    actYwCnRootAddNode.setEngine(engine);
    return actYwCnRootAddNode.execute(actYwPgroot);
  }

  public boolean saveRootNodeGateStatus(ActYwPgroot actYwPgroot) {
    return saveRootNodeGate(actYwPgroot).getStatus();
  }

  public ActYwRstatus<ActYwGnode> saveRootNodeGate(ActYwPgroot actYwPgroot) {
    actYwCnRootAddNodeGate.setEngine(engine);
    return actYwCnRootAddNodeGate.execute(actYwPgroot);
  }

  public boolean deleteRootNodeStatus(ActYwPgroot actYwPgroot) {
    return deleteRootNode(actYwPgroot).getStatus();
  }

  public ActYwRstatus<ActYwGnode> deleteRootNode(ActYwPgroot actYwPgroot) {
    actYwCnRootDeleteNode.setEngine(engine);
    return actYwCnRootDeleteNode.execute(actYwPgroot);
  }

  public boolean deleteRootNodeGateStatus(ActYwPgroot actYwPgroot) {
    return deleteRootNodeGate(actYwPgroot).getStatus();
  }

  public ActYwRstatus<ActYwGnode> deleteRootNodeGate(ActYwPgroot actYwPgroot) {
    actYwCnRootDeleteNodeGate.setEngine(engine);
    return actYwCnRootDeleteNodeGate.execute(actYwPgroot);
  }

  public ActYwEngineImpl getEngine() {
    return engine;
  }

  public ActYwRunner setEngine(ActYwEngineImpl engine) {
    this.engine = engine;
    return this;
  }
}
