package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.sys.entity.Office;

public class GnodeSp {
  private Boolean isRoot;//是否有网关
  private Boolean isProcess;//是否有网关
  private Boolean hasGateWay;//是否有网关
  private Boolean hasReturnGnode;//是否有驳回

  private ActYwGnode root;//流程根节点(level=0)
  private ActYwGnode process;//子流程节点(level=1)
  private ActYwGnode gateWay;//网关节点(level=2)
  private ActYwGnode returnGnode;//驳回节点(level=2)
  private ActYwGnode ywGnode;//业务节点(level=2)
  private ActYwGnode ywPreGnode;//业务节点(level=2)-前一个


//
//  private static final long serialVersionUID = 1L;
//  private ActYwGnode parent; // 父级编号
//  private String parentIds; // 所有父级编号
//  private String groupId; // 自定义流程编号
//  private String type; // 流程节点类型
//  private String typefun; // 流程节点功能类型:0、不可选（子流程,结构-连接线），1、可选择（网关,任务）
//  private String nodeId; // 流程节点编号
//  private Boolean isShow; // 显示:1、默认（显示）；0、隐藏
//  private Boolean isForm; // 是否为表单节点:0、默认（否）；1、是
//  private String formId; // 表单标识
//  private Integer sort;   // 排序
//  private String flowGroup; // 流程执行用户或角色（默认用户）
//  private Office office; // 节点所属机构:1、默认（系统全局）；
//  private ActYwGroup group; // 自定义流程
//  private ActYwNode node; // 流程节点
//  private ActYwForm form; // 前一个流程节点
//
//  private String preId; // 前一个流程编号
//  private String preFunId; // 前一个业务流程编号
//  private String preIds; // 前一个流程编号
//  private String preIdss; // 前置所有流程编号
//
//  private String nextId; // 后一个自定义流程编号
//  private String nextFunId; // 后一个业务自定义流程编号
//  private String nextIds; // 后一个自定义流程编号
//  private String nextIdss; // 后置所有自定义流程编号
//
//  private ActYwGnode preGnode; // 前一个流程节点
//  private ActYwGnode nextGnode; // 后一个流程节点
//  private ActYwGnode preFunGnode; // 前一个业务流程节点
//  private ActYwGnode nextFunGnode; // 后一个业务流程节点
//
//  private ActYwGnode processGnode; // 子流程节点（当业务节点为二级节点时，该属性不为空）
//  private List<ActYwGnode> preGnodes; // 前一个流程节点列表
//  private List<ActYwGnode> nextGnodes; // 后一个流程节点列表
//  private List<ActYwGnode> preFunGnodes; // 前一个流程节点列表
//  private List<ActYwGnode> nextFunGnodes; // 后一个流程节点列表
//  private List<ActYwGnode> childGnodes; // 当前结点子节点
//
//  private Integer slibNum;//兄弟节点数量
//  private List<String> slibIds;//同级节点ID-不包含自己
}
