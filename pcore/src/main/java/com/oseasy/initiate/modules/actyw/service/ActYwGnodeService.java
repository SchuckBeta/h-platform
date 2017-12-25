package com.oseasy.initiate.modules.actyw.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.service.TreeService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.dao.ActYwGnodeDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwNodeDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwEcmd;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRunner;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwPgroot;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenEsubType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;

/**
 * 项目流程节点组Service.
 *
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwGnodeService extends TreeService<ActYwGnodeDao, ActYwGnode> {
  protected static final Logger LOGGER = Logger.getLogger(ActYwGnodeService.class);

  @Autowired
  private ActYwGnodeDao actYwGnodeDao;
  @Autowired
  private ActYwNodeDao actYwNodeDao;

  public ActYwGnode get(String id) {
    return super.get(id);
  }

  public List<ActYwGnode> findAllList() {
    return actYwGnodeDao.findAllList();
  }

  public List<ActYwGnode> findList(ActYwGnode actYwGnode) {
    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
    }
    return super.findList(actYwGnode);
  }

  public List<ActYwGnode> findListByYw(ActYwGnode actYwGnode) {
    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
    }
    return dao.findListByYw(actYwGnode);
  }
  public List<ActYwGnode> findListByYwProcess(ActYwGnode actYwGnode) {
     if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
       actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
     }
     return dao.findListByYwProcess(actYwGnode);
   }

/*  public List<ActYwGnode> findListByYwModel(ActYwGnode actYwGnode) {
    if (StringUtils.isNotBlank(actYwGnode.getParentIds())) {
      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
    }
    return findListByYw(actYwGnode);
  }*/

  @Transactional(readOnly = false)
  public void save(ActYwGnode actYwGnode) {
    super.save(actYwGnode);
  }

  @Transactional(readOnly = false)
  public ActYwRstatus saveAuto(ActYwRunner runner, ActYwGroup actYwGoup, ActYwGnode actYwGnode) {
    List<Map<ActYwEcmd, ActYwPgroot>> cmdParams = Lists.newArrayList();
    List<ActYwGnode> nodes = dao.findListByGroup(actYwGnode);
    ActYwRstatus results = new ActYwRstatus();
    ActYwPgroot param = null;//命令执行参数

    if ((nodes == null) || (nodes.isEmpty())) {//没有任何节点时
      /**
       * 当没有任何节点，不论是否有业务节点，都需要新增根节点开始结束节点.
       */
      param = new ActYwPgroot(actYwGoup);
      ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD, param);

      /**
       * 当没有任何节点，且业务节点不为空的时候,添加业务节点.
       */
      if (actYwGnode != null) {
        param = ActYwPgroot.toActYwPgroot(actYwGnode, null, null);
        ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
      }
    } else{
      if ((actYwGnode == null)) {
        results.setStatus(false);
        results.setMsg("业务节点类型不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      } else{
        if ((StringUtil.isNotEmpty(actYwGnode.getNodeId()))) {
          ActYwNode aywnode = actYwNodeDao.get(actYwGnode.getNodeId());
          if (aywnode != null) {
            actYwGnode.setNode(aywnode);
            actYwGnode.setType(GnodeType.getByActYwNode(aywnode, aywnode.getLevel()).getId());
          }
        }

        if ((StringUtil.isEmpty(actYwGnode.getType()))) {
          results.setStatus(false);
          results.setMsg("业务节点类型不能为空！");
          LOGGER.warn(results.getMsg());
          return results;
        }
      }

      if (StringUtil.isNotEmpty(actYwGnode.getPreFunId())) {
        ActYwGnode aywPreFunGnode = actYwGnodeDao.get(actYwGnode.getPreFunId());
        if (aywPreFunGnode != null) {
          actYwGnode.setPreFunGnode(aywPreFunGnode);
        }
      }

      if (actYwGnode.getPreFunGnode() == null) {
        results.setStatus(false);
        results.setMsg("业务节点前置业务节点不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      }


      ActYwGnode gstartGnode = null;//开始节点
      ActYwGnode gendGnode = null;//结束节点
      ActYwGnode grootStartGnode = null;//流程开始节点
      ActYwGnode grootEndGnode = null;//流程结束节点
      ActYwGnode gprocessGnode = null;//子流程节点
      ActYwGnode gprocessStartGnode = null;//子流程开始节点
      ActYwGnode gprocessEndGnode = null;//子流程结束节点

      GnodeType gnodeType = GnodeType.getById(actYwGnode.getType());
      GnodeType gnodePreFunType = GnodeType.getById(actYwGnode.getPreFunGnode().getType());
      for (ActYwGnode curnode : nodes) {
        GnodeType curgnodeType = GnodeType.getById(curnode.getType());
        if (((curgnodeType).equals(GnodeType.GT_ROOT_START))) {
          grootStartGnode = curnode;
        } else if (((curgnodeType).equals(GnodeType.GT_ROOT_END))) {
          grootEndGnode = curnode;
        } else if ((curgnodeType).equals(GnodeType.GT_PROCESS)) {
          if ((gnodePreFunType).equals(GnodeType.GT_PROCESS)) {
            if ((curnode.getId()).equals(actYwGnode.getPreFunGnode().getId())) {
              gprocessGnode = curnode;
            }
          } else{
            if ((curnode.getId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessGnode = curnode;
            }
          }
        } else if (((curgnodeType).equals(GnodeType.GT_PROCESS_START))) {
          if ((gnodePreFunType).equals(GnodeType.GT_PROCESS)) {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getId())) {
              gprocessStartGnode = curnode;
            }
          } else{
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessStartGnode = curnode;
            }
          }
        } else if (((curgnodeType).equals(GnodeType.GT_PROCESS_END))) {
          if ((gnodePreFunType).equals(GnodeType.GT_PROCESS)) {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getId())) {
              gprocessEndGnode = curnode;
            }
          } else{
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessEndGnode = curnode;
            }
          }
        }
      }

      if ((gnodeType).equals(GnodeType.GT_ROOT) || (gnodeType).equals(GnodeType.GT_ROOT_START) || (gnodeType).equals(GnodeType.GT_ROOT_END) || (gnodeType).equals(GnodeType.GT_ROOT_FLOW) || (gnodeType).equals(GnodeType.GT_PROCESS)) {
        gstartGnode = grootStartGnode;//开始节点
        gendGnode = grootEndGnode;//结束节点
      } else if ((gnodeType).equals(GnodeType.GT_PROCESS_START) || (gnodeType).equals(GnodeType.GT_PROCESS_END) || (gnodeType).equals(GnodeType.GT_PROCESS_FLOW) || (gnodeType).equals(GnodeType.GT_PROCESS_GATEWAY) || (gnodeType).equals(GnodeType.GT_PROCESS_TASK)) {
        gstartGnode = gprocessStartGnode;//子流程开始节点
        gendGnode = gprocessEndGnode;//子流程结束节点
      } else{
        results.setStatus(false);
        results.setMsg("业务节点类型未定义！");
        LOGGER.warn(results.getMsg());
        return results;
      }

      if ((gstartGnode == null) || (gendGnode == null)) {
        results.setStatus(false);
        results.setMsg("前、后节点不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      }

      if (actYwGnode.getProcessGnode() == null) {
        actYwGnode.setProcessGnode(gprocessGnode);
      }

      if ((actYwGnode.getPreFunGnode() == null) || StringUtil.isEmpty((actYwGnode.getPreFunGnode().getId()))) {
        results.setStatus(false);
        results.setMsg("前置业务节点不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      } else{
        actYwGnode.setPreFunGnode(get(actYwGnode.getPreFunGnode().getId()));
      }

      if (actYwGnode.getPreGnode() == null) {
        ActYwGnode preGnode = get(actYwGnode.getPreFunGnode().getNextId());
        if (preGnode == null) {
          results.setStatus(false);
          results.setMsg("前置节点不能为空！");
          LOGGER.warn(results.getMsg());
          return results;
        }
        actYwGnode.setPreGnode(preGnode);
      }

      if ((actYwGnode.getNextFunGnode() != null)) {
        if ((StringUtil.isNotEmpty(actYwGnode.getNextFunGnode().getId()))) {
          ActYwGnode nextFunGnode = get(actYwGnode.getNextFunGnode().getId());
          if (nextFunGnode == null) {
            results.setStatus(false);
            results.setMsg("后置业务节点对应的ID在数据库不存在！");
            LOGGER.warn(results.getMsg());
          }
          actYwGnode.setNextFunGnode(nextFunGnode);
//        } else{
//          results.setStatus(false);
//          results.setMsg("后置业务节点不为 null 时，ID不能为空！");
//          LOGGER.warn(results.getMsg());
//          return results;
        }
      }

      /**
       * 判断节点数大于3时，前置业务节点类型必须为TASK或GATEWAY或FLOW
       */
      if (nodes.size() > 3) {
        if ((actYwGnode.getPreFunGnode().getNode() != null) && StringUtil.isNotEmpty(actYwGnode.getPreFunGnode().getNode().getId())) {
          actYwGnode.setPreFunGnode(get(actYwGnode.getPreFunGnode().getId()));
        }
        StenType preStenType = StenType.getByKey(actYwGnode.getPreFunGnode().getNode().getNodeKey());
        if (!((preStenType.getSubtype()).equals(StenEsubType.SES_TASK) || (preStenType.getSubtype()).equals(StenEsubType.SES_GATEWAY) || (preStenType.getSubtype()).equals(StenEsubType.SES_JG) || (preStenType.getSubtype()).equals(StenEsubType.SES_EVENT_START))) {
          results.setStatus(false);
          results.setMsg("业务节点(gt 3)时,前置节点必须为["+StenEsubType.SES_TASK.getRemark()+"或"+StenEsubType.SES_GATEWAY.getRemark()+"或"+StenEsubType.SES_JG.getRemark()+"]节点！");
          LOGGER.warn(results.getMsg());
          return results;
        }
      }

      if ((nodes.size() == 3)) {//没有任何业务节点时
        actYwGnode.setPreFunGnode(gstartGnode);
        for (ActYwGnode node : nodes) {
          if ((node.getNodeId()).equals(StenType.ST_FLOW_SEQUENCE.getId())) {
              actYwGnode.setPreGnode(node);
          }
        }

        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));

        ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
      } else if ((nodes.size() == 5)) {//有一个业务节点时
        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));
        ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
      } else if ((nodes.size() >= 7)) {//有2个以上业务节点时
        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));

        if ((param.getStartSflowGnode() != null) && (!param.getStartSflowGnode().isEmpty())) {
          List<ActYwGnode> startSflowGnodes = param.getStartSflowGnode();
          if (startSflowGnodes.size() == 1) {
            ActYwGnode startsfGnode = startSflowGnodes.get(0);
            if (StringUtil.isNotEmpty(startsfGnode.getNextId()) || ((startsfGnode.getNextGnode() != null) && StringUtil.isNotEmpty(startsfGnode.getNextGnode().getId()))) {
              startsfGnode.setNextGnode(get(startsfGnode.getNextId()));
            }

            if (startsfGnode.getNextGnode().getNode() != null) {
              StenType startSflowNextStenType = StenType.getByKey(startsfGnode.getNextGnode().getNode().getNodeKey());
              if ((startsfGnode.getNextGnode()).equals(param.getNextFunGnode()) || (startsfGnode.getNextGnode()).equals(gendGnode) || (startSflowNextStenType.getSubtype()).equals(StenEsubType.SES_EVENT_END)) {//开始连接线的后置节点等于结束节点
                ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
              } else if (!(startsfGnode.getNextGnode()).equals(param.getNextFunGnode())) {
                ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODEGATE, param);
              }
            }
          } else if (param.getStartSflowGnode().size() > 1) {
            ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
          }
        }
      }
    }

    ActYwRstatus rstatus = runner.callExecute(cmdParams);
    results.setStatus(rstatus.getStatus());
    results.setMsg(rstatus.getMsg());
    return results;
  }

  @Transactional(readOnly = false)
  public void delete(ActYwGnode actYwGnode) {
    super.delete(actYwGnode);
  }

  /**
   * 批量更新流程组节点排序 .
   * @author chenhao
   * @param gnode
   */
  @Transactional(readOnly = false)
  public void updateSort(ActYwGnode gnode) {
    actYwGnodeDao.updateSort(gnode);
  }

  /**
   * 根据节点查询上一节点或下一节点.
   *  查询所有前面节点时nextGnode 为空
   *  查询所有后面节点时preGnode 为空
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findPreNextListByGroup(ActYwGnode gnode) {
    return actYwGnodeDao.findPreNextListByGroup(gnode);
  }

  /**
   * 根据节点查询上一节点.
   *  group不能为空
   *  preFunGnode不能为空
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findPreListByGroup(ActYwGnode gnode) {
    if ((gnode.getGroup() == null) || (gnode.getPreFunGnode() == null)) {
      return null;
    }
    ActYwGnode preParam = new ActYwGnode();
    preParam.setGroup(gnode.getGroup());
    preParam.setPreGnode(gnode.getPreFunGnode());
    return findPreNextListByGroup(preParam);
  }

  /**
   * 根据节点查询下一节点.
   *  group不能为空
   *  nextFunGnode不能为空
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findNextListByGroup(ActYwGnode gnode) {
    if ((gnode.getGroup() == null) || (gnode.getNextGnode() == null)) {
      return null;
    }
    ActYwGnode nextParam = new ActYwGnode();
    nextParam.setGroup(gnode.getGroup());
    nextParam.setNextGnode(gnode.getNextGnode());
    return findPreNextListByGroup(nextParam);
  }

}