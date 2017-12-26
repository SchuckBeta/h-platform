package com.oseasy.initiate.modules.actyw.tool.process.vo;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;

public class GnodeTypeRt {
  private String rootId;
  private String rootStartId;
  private String rootEndId;
  private String subId;
  private String subStartId;
  private String subEndId;

  Boolean isRoot;
  Boolean isRootStart;
  Boolean isRootEnd;
  Boolean isRootFlow;
  Boolean isRootFlowFirst;//是否根节点第一个流程线
  Boolean isSub;
  Boolean isSubFirst;//是否第一个子节点
  Boolean isSubRootStart;
  Boolean isSubRootEnd;
  Boolean isSubRootFlow;
  Boolean isSubRootFlowFirst;//是否子节点第一个流程线
  Boolean isSubRootTask;
  Boolean isSubRootTaskFirst;//是否子节点第一个任务
  Boolean isSubRootGateway;

  public Boolean getIsRootStart() {
    return isRootStart;
  }

  public void setIsRootStart(Boolean isRootStart) {
    this.isRootStart = isRootStart;
  }

  public Boolean getIsRootEnd() {
    return isRootEnd;
  }

  public void setIsRootEnd(Boolean isRootEnd) {
    this.isRootEnd = isRootEnd;
  }

  public Boolean getIsSubRootStart() {
    return isSubRootStart;
  }

  public void setIsSubRootStart(Boolean isSubRootStart) {
    this.isSubRootStart = isSubRootStart;
  }

  public Boolean getIsSubRootEnd() {
    return isSubRootEnd;
  }

  public void setIsSubRootEnd(Boolean isSubRootEnd) {
    this.isSubRootEnd = isSubRootEnd;
  }

  public Boolean getIsRoot() {
    return isRoot;
  }

  public void setIsRoot(Boolean isRoot) {
    this.isRoot = isRoot;
  }

  public Boolean getIsRootFlow() {
    return isRootFlow;
  }

  public void setIsRootFlow(Boolean isRootFlow) {
    this.isRootFlow = isRootFlow;
  }

  public Boolean getIsSub() {
    return isSub;
  }

  public void setIsSub(Boolean isSub) {
    this.isSub = isSub;
  }

  public Boolean getIsSubRootFlow() {
    return isSubRootFlow;
  }

  public void setIsSubRootFlow(Boolean isSubRootFlow) {
    this.isSubRootFlow = isSubRootFlow;
  }

  public Boolean getIsRootFlowFirst() {
    return isRootFlowFirst;
  }

  public void setIsRootFlowFirst(Boolean isRootFlowFirst) {
    this.isRootFlowFirst = isRootFlowFirst;
  }

  public Boolean getIsSubFirst() {
    return isSubFirst;
  }

  public void setIsSubFirst(Boolean isSubFirst) {
    this.isSubFirst = isSubFirst;
  }

  public Boolean getIsSubRootFlowFirst() {
    return isSubRootFlowFirst;
  }

  public void setIsSubRootFlowFirst(Boolean isSubRootFlowFirst) {
    this.isSubRootFlowFirst = isSubRootFlowFirst;
  }

  public Boolean getIsSubRootTaskFirst() {
    return isSubRootTaskFirst;
  }

  public void setIsSubRootTaskFirst(Boolean isSubRootTaskFirst) {
    this.isSubRootTaskFirst = isSubRootTaskFirst;
  }

  public Boolean getIsSubRootTask() {
    return isSubRootTask;
  }

  public void setIsSubRootTask(Boolean isSubRootTask) {
    this.isSubRootTask = isSubRootTask;
  }

  public Boolean getIsSubRootGateway() {
    return isSubRootGateway;
  }

  public void setIsSubRootGateway(Boolean isSubRootGateway) {
    this.isSubRootGateway = isSubRootGateway;
  }

  public String getRootId() {
    return rootId;
  }

  public void setRootId(String rootId) {
    this.rootId = rootId;
  }

  public String getRootStartId() {
    return rootStartId;
  }

  public void setRootStartId(String rootStartId) {
    this.rootStartId = rootStartId;
  }

  public String getRootEndId() {
    return rootEndId;
  }

  public void setRootEndId(String rootEndId) {
    this.rootEndId = rootEndId;
  }

  public String getSubId() {
    return subId;
  }

  public void setSubId(String subId) {
    this.subId = subId;
  }

  public String getSubStartId() {
    return subStartId;
  }

  public void setSubStartId(String subStartId) {
    this.subStartId = subStartId;
  }

  public String getSubEndId() {
    return subEndId;
  }

  public void setSubEndId(String subEndId) {
    this.subEndId = subEndId;
  }

  public static GnodeTypeRt validate(ActYwGnode actYwGnode) {
    GnodeTypeRt gnodeTypeRt = new GnodeTypeRt();
    GnodeType gnodeGnodeType = GnodeType.getById(actYwGnode.getType());

    gnodeTypeRt.setIsRoot(((gnodeGnodeType).equals(GnodeType.GT_ROOT)) ? true : false);
    gnodeTypeRt.setIsRootStart(((gnodeGnodeType).equals(GnodeType.GT_ROOT_START)) ? true : false);
    gnodeTypeRt.setIsRootEnd(((gnodeGnodeType).equals(GnodeType.GT_ROOT_END)) ? true : false);
    gnodeTypeRt.setIsRootFlow(((gnodeGnodeType).equals(GnodeType.GT_ROOT_FLOW)) ? true : false);

    gnodeTypeRt.setIsSub(((gnodeGnodeType).equals(GnodeType.GT_PROCESS)) ? true : false);
    gnodeTypeRt.setIsSubRootStart(((gnodeGnodeType).equals(GnodeType.GT_PROCESS_START)) ? true : false);
    gnodeTypeRt.setIsSubRootEnd(((gnodeGnodeType).equals(GnodeType.GT_PROCESS_END)) ? true : false);
    gnodeTypeRt.setIsSubRootFlow(((gnodeGnodeType).equals(GnodeType.GT_PROCESS_FLOW)) ? true : false);
    gnodeTypeRt.setIsSubRootTask(((gnodeGnodeType).equals(GnodeType.GT_PROCESS_TASK)) ? true : false);
    gnodeTypeRt.setIsSubRootGateway(((gnodeGnodeType).equals(GnodeType.GT_PROCESS_GATEWAY)) ? true : false);
    return gnodeTypeRt;
  }
}
