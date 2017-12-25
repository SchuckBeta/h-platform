/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd
 * @Description [[_ActYwRunner_]]文件
 * @date 2017年6月18日 上午11:16:08
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootAdd;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootAddNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.impl.ActYwCnRootAddNodeGate;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwPgroot;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;


/**
 * 流程引擎执行类.
 * @author chenhao
 * @date 2017年6月18日 上午11:16:08
 *
 */
@Service
public class ActYwRunner {
  private ActYwEngineImpl engine;
  @Autowired
  private ActYwCnRootAdd actYwCnRootAdd;
  @Autowired
  private ActYwCnRootAddNode actYwCnRootAddNode;
  @Autowired
  private ActYwCnRootAddNodeGate actYwCnRootAddNodeGate;

  public ActYwRstatus callExecute(List<Map<ActYwEcmd, ActYwPgroot>> cmdParams) {
    ActYwRstatus rstatus = new ActYwRstatus();
    if (cmdParams != null) {
      List<ActYwRstatus> isFales = Lists.newArrayList();

      for (Map<ActYwEcmd, ActYwPgroot> cmdParam : cmdParams) {
        Iterator<ActYwEcmd> itor = cmdParam.keySet().iterator();
        while (itor.hasNext()) {
          ActYwEcmd cmd = (ActYwEcmd) itor.next();
          ActYwPgroot param = cmdParam.get(cmd);
          if ((cmd == null) || (param == null)) {
             continue;
          }

          ActYwRstatus isCurStatus = null;
          if ((ActYwEcmd.ECMD_ROOT_ADD).equals(cmd)) {
            isCurStatus = saveRoot(param.getGroup());
          }else if ((ActYwEcmd.ECMD_ROOT_ADD_NODE).equals(cmd)) {
            isCurStatus = saveRootNode(param);
          }else if ((ActYwEcmd.ECMD_ROOT_ADD_NODEGATE).equals(cmd)) {
            isCurStatus = saveRootNodeGate(param);
          }else{
            isCurStatus = new ActYwRstatus(false, "当前命令["+cmd.getKey()+"]未定义！");
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

  public ActYwRstatus callExecute(ActYwEcmd cmd, ActYwPgroot param) {
    return callExecute(addCmdParam(Lists.newArrayList(), cmd, param));
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

  public ActYwRstatus saveRoot(ActYwGroup group) {
    actYwCnRootAdd.setEngine(engine);
    return actYwCnRootAdd.execute(group);
  }

  public boolean saveRootNodeStatus(ActYwPgroot actYwPgroot) {
    return saveRootNode(actYwPgroot).getStatus();
  }

  public ActYwRstatus saveRootNode(ActYwPgroot actYwPgroot) {
    actYwCnRootAddNode.setEngine(engine);
    return actYwCnRootAddNode.execute(actYwPgroot);
  }

  public boolean saveRootNodeGateStatus(ActYwPgroot actYwPgroot) {
    return saveRootNodeGate(actYwPgroot).getStatus();
  }

  public ActYwRstatus saveRootNodeGate(ActYwPgroot actYwPgroot) {
    return actYwCnRootAddNodeGate.execute(actYwPgroot);
  }

  public ActYwEngineImpl getEngine() {
    return engine;
  }

  public ActYwRunner setEngine(ActYwEngineImpl engine) {
    this.engine = engine;
    return this;
  }
}
