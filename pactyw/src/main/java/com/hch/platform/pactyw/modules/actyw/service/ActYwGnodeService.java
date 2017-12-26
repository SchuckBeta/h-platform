package com.oseasy.initiate.modules.actyw.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.service.TreeService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.dao.ActYwDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwFormDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGnodeDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGroupDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwNodeDao;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwEcmd;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwGnodeUtil;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRunner;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwPgroot;
import com.oseasy.initiate.modules.actyw.tool.process.rest.GnodeMargeVo;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowYwId;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FormClientType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodePvo;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeTool;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeView;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenEsubType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.gcontest.enums.GContestStatusEnum;
import com.oseasy.initiate.modules.gcontest.vo.GContestNodeVo;
import com.oseasy.initiate.modules.project.enums.ProjectStatusEnum;
import com.oseasy.initiate.modules.project.vo.ProjectNodeVo;
import com.oseasy.initiate.modules.sys.dao.RoleDao;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 项目流程Service.
 *
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwGnodeService extends TreeService<ActYwGnodeDao, ActYwGnode> {
  private static final String NO_PASS = "1";//审核不通过

  protected static final Logger LOGGER = Logger.getLogger(ActYwGnodeService.class);

  @Autowired
  private ActYwGroupDao actYwGroupDao;
  @Autowired
  private ActYwGnodeDao actYwGnodeDao;
  @Autowired
  private ActYwNodeDao actYwNodeDao;
  @Autowired
  private ActYwFormDao actYwFormDao;
  @Autowired
  private ActYwDao actYwDao;
  @Autowired
  private RoleDao roleDao;
  public ActYwGnode get(String id) {
    return super.get(id);
  }
  /**
   	 * 根据流程ID获取开始节点.
   	 * @param groupId
   	 * @return ActYwGnode
   	 */
    public ActYwGnode getStart(String groupId){
      return dao.getStart(groupId);
    }

    /**
     * 根据流程ID获取结束节点.
     * @param groupId
     * @return ActYwGnode
     */
    public ActYwGnode getEnd(String groupId){
      return dao.getEnd(groupId);
    }

    /**
     * 根据流程ID获取结束节点的前一业务节点.
     * 当
     * @param groupId
     * @param isProcess 是否一级节点
     * @return ActYwGnode
     */
    public ActYwGnode getEndPreFun(String groupId, Boolean isProcess){
      if(isProcess == null){
        isProcess = true;
      }

      ActYwGnode endPreFun = dao.getEndPreFun(groupId);

      if(isProcess){
        return endPreFun;
      }

      if((endPreFun != null) && StringUtil.isNotEmpty(endPreFun.getId())){
        return dao.getProcessEndPreFun(groupId, endPreFun.getId());
      }

      return null;
    }

    public ActYwGnode getEndPreFun(String groupId){
      return getEndPreFun(groupId, false);
    }

    /**
   	 * 根据流程ID获取开始节点.
     * @param groupId
     * @param id 流程ID
   	 * @return ActYwGnode
   	 */
    public ActYwGnode getProcessStart(String groupId, String id){
      return dao.getProcessStart(groupId, id);
    }

    /**
     * 根据流程ID获取结束节点.
     * @param groupId
     * @param id 流程ID
     * @return ActYwGnode
     */
    public ActYwGnode getProcessEnd(String groupId, String id){
      return dao.getProcessEnd(groupId, id);
    }

  public ActYwGnode getWithGtime(String id,String ppid) {
	    return actYwGnodeDao.getWithGtime(id, ppid);
  }
  public List<ActYwGnode> findByYwParentIdsLike(ActYwGnode entity){
    if (StringUtil.isNotBlank(entity.getParentId())) {
      entity.setParentIds("%," + entity.getParentId() + ",%");
    }
    return actYwGnodeDao.findByParentIdsLike(entity);
  }
  /**
   * 查找结束节点或子流程结束节点.
   *
   * @param groupId
   *          流程ID
   * @param gnodeTypeId
   *          //节点ID
   * @param parentId
   *          //父节点
   * @return ActYwGnode
   */
  public ActYwGnode getGnode(String groupId, String gnodeTypeId, String parentId) {
    if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(gnodeTypeId)) {
      return null;
    }

    ActYwGnode gnode = new ActYwGnode();
    gnode.setGroupId(groupId);
    gnode.setType(gnodeTypeId);

    if (StringUtil.isNotEmpty(parentId)) {
      gnode.setParent(new ActYwGnode(parentId));
    }

    List<ActYwGnode> gnodes = super.findList(gnode);
    if (gnodes == null) {
      return null;
    }

    if (gnodes.size() != 1) {
      return null;
    }

    return gnodes.get(0);
  }

  @SuppressWarnings("deprecation")
  public List<ActYwGnode> findAllList() {
    return actYwGnodeDao.findAllList();
  }

  public List<ActYwGnode> findAll() {
    return actYwGnodeDao.findAll();
  }

  public List<ActYwGnode> findListByinIds(String ids){
    if(StringUtil.isEmpty(ids)){
      return Lists.newArrayList();
    }
    return findListByinIds(Arrays.asList((ids).split(",")));
  }

  public List<ActYwGnode> findListByinIds(List<String> ids){
    if((ids != null) && (ids.size() > 0)){
      return dao.findListByinIds(ids);
    }
    return Lists.newArrayList();
  }

  public List<ActYwGnode> findNextByGpreIdsLike(ActYwGnode gnode){
    if((gnode == null) || StringUtil.isEmpty(gnode.getPreIds())){
      return Lists.newArrayList();
    }
    return dao.findNextByGpreIdsLike(gnode);
  }

  /**
   * 查找后置节点(不级联查询父子级).
   * @param gnode 节点
   * @return List
   * @return
   */
  public List<ActYwGnode> findNextByGpreIdssLike(ActYwGnode gnode){
    return findNextByGpreIdssLike(gnode, false, null);
  }

  /**
   * 查找后置节点(级联查询父子级).
   * @param gnode 节点
   * @param cascade 是否级联
   * @return List
   */
  public List<ActYwGnode> findNextByGpreIdssLike(ActYwGnode gnode, Boolean cascade, List<String> ids){
    if((gnode == null) || StringUtil.isEmpty(gnode.getPreIdss())){
      return Lists.newArrayList();
    }
    return dao.findNextByGpreIdssLike(gnode, cascade, ids);
  }

  public List<ActYwGnode> findPreByGnextIdsLike(ActYwGnode gnode){
    if((gnode == null) || StringUtil.isEmpty(gnode.getNextIds())){
      return Lists.newArrayList();
    }
    return dao.findPreByGnextIdsLike(gnode);
  }

  /**
   * 查找前置节点(不级联查询父子级).
   * @param gnode 节点
   * @return List
   */
  public List<ActYwGnode> findPreByGnextIdssLike(ActYwGnode gnode){
    return findPreByGnextIdssLike(gnode, false, null);
  }
  /**
   * 查找前置节点(级联查询父子级).
   * @param gnode 节点
   * @param cascade 是否级联
   * @return List
   */
  public List<ActYwGnode> findPreByGnextIdssLike(ActYwGnode gnode, Boolean cascade, List<String> ids){
    if((gnode == null) || StringUtil.isEmpty(gnode.getNextIdss())){
      return Lists.newArrayList();
    }
    return dao.findPreByGnextIdssLike(gnode, cascade, ids);
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
  public List<ActYwGnode> findListForTimeIndexByYw(ActYwGnode actYwGnode) {
	    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
	      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
	    }
	    return dao.findListForTimeIndexByYw(actYwGnode);
  }

  public List<ActYwGnode> findListByYwProcess(ActYwGnode actYwGnode) {
    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
    }
    return dao.findListByYwProcess(actYwGnode);
  }

  /**
   * 根据流程组获取流程.
   *
   * @param actYwGnode
   *          流程节点
   * @return List
   */
  public List<ActYwGnode> findListByGroup(ActYwGnode actYwGnode) {
    if (StringUtil.isEmpty(actYwGnode.getGroupId())) {
      return null;
    }

    if ((actYwGnode.getGroup() == null)) {
      actYwGnode.setGroup(new ActYwGroup(actYwGnode.getGroupId()));
    }

    if (StringUtil.isNotEmpty(actYwGnode.getNodeId())) {
      if ((actYwGnode.getNode() == null)) {
        actYwGnode.setNode(new ActYwNode(actYwGnode.getNodeId()));
      }
    }

    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
    }
    return findList(actYwGnode);
  }

  /**
   * 根据流程组获取流程,PreIdss升序.
   * @param actYwGnode 流程节点
   * @return List
   */
  public List<ActYwGnode> findListByGroupAscPreIdss(ActYwGnode actYwGnode) {
    return dao.findListByGroupAscPreIdss(actYwGnode);
  }

  /**
   * 根据流程组获取流程,NextIdss升序.
   * @param actYwGnode 流程节点
   * @return List
   */
  public List<ActYwGnode> findListByGroupAscNextIdss(ActYwGnode actYwGnode) {
    return dao.findListByGroupAscNextIdss(actYwGnode);
  }
  /**
   * 根据流程组获取流程所有节点.
   *
   * @param actYwGnode
   *          流程节点
   * @return List
   */
  public List<ActYwGnode> findPreNextByGroupAndIdss(ActYwGnode actYwGnode, Boolean isPreIdss) {
    if (StringUtil.isEmpty(actYwGnode.getGroupId())) {
      return null;
    }

    if ((actYwGnode.getGroup() == null)) {
      actYwGnode.setGroup(new ActYwGroup(actYwGnode.getGroupId()));
    }

    if (isPreIdss) {
      return dao.findPreByGroupAndIdss(actYwGnode);
    } else {
      return dao.findNextByGroupAndIdss(actYwGnode);
    }
  }

  /**
   * 根据流程组获取流程业务节点.
   *
   * @param actYwGnode
   *          流程节点
   * @return List
   */
  public List<ActYwGnode> findPreNextByYwGroupAndIdss(ActYwGnode actYwGnode, Boolean isPreIdss) {
    actYwGnode.setYw(true);
    return findPreNextByGroupAndIdss(actYwGnode, isPreIdss);
  }

  /**
   * 根据流程组获取流程.
   *
   * @param actYwGnode
   *          流程节点
   * @return List
   */
  public List<ActYwGnode> findListByYwGroup(ActYwGnode actYwGnode) {
    if (StringUtil.isEmpty(actYwGnode.getGroupId())) {
      return null;
    }

    if ((actYwGnode.getGroup() == null)) {
      actYwGnode.setGroup(new ActYwGroup(actYwGnode.getGroupId()));
    }

    if (StringUtil.isNotEmpty(actYwGnode.getNodeId())) {
      if ((actYwGnode.getNode() == null)) {
        actYwGnode.setNode(new ActYwNode(actYwGnode.getNodeId()));
      }
    }

    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
    }
    return findListByYw(actYwGnode);
  }
  public List<ActYwGnode> findListForTimeIndexByYwGroup(ActYwGnode actYwGnode) {
	    if (StringUtil.isEmpty(actYwGnode.getGroupId())) {
	      return null;
	    }

	    if ((actYwGnode.getGroup() == null)) {
	      actYwGnode.setGroup(new ActYwGroup(actYwGnode.getGroupId()));
	    }

	    if (StringUtil.isNotEmpty(actYwGnode.getNodeId())) {
	      if ((actYwGnode.getNode() == null)) {
	        actYwGnode.setNode(new ActYwNode(actYwGnode.getNodeId()));
	      }
	    }

	    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
	      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
	    }
	    return findListForTimeIndexByYw(actYwGnode);
  }

  /**
   * 根据流程组获取流程.
   *
   * @param actYwGnode
   *          流程节点
   * @return List
   */
  public List<ActYwGnode> findListByYwGroupAndParent(ActYwGnode actYwGnode) {
    if (StringUtil.isEmpty(actYwGnode.getParentId())) {
      return null;
    }
    if (StringUtil.isEmpty(actYwGnode.getGroupId())) {
      return null;
    }

    if ((actYwGnode.getNode() == null) || StringUtil.isEmpty(actYwGnode.getNode().getLevel())) {
      return null;
    }
    return findListByYwGroup(actYwGnode);
  }

  /**
   * 根据流程组获取流程(type=19).
   *
   * @param actYwGnode
   *          流程节点
   * @return List
   */
  public List<ActYwGnode> findListByYwProcessGroup(ActYwGnode actYwGnode) {
    if (StringUtil.isEmpty(actYwGnode.getGroupId())) {
      return null;
    }

    if ((actYwGnode.getGroup() == null)) {
      actYwGnode.setGroup(new ActYwGroup(actYwGnode.getGroupId()));
    }

    if (StringUtil.isNotEmpty(actYwGnode.getNodeId())) {
      if ((actYwGnode.getNode() == null)) {
        actYwGnode.setNode(new ActYwNode(actYwGnode.getNodeId()));
      }
    }

    if (StringUtil.isNotBlank(actYwGnode.getParentIds())) {
      actYwGnode.setParentIds("," + actYwGnode.getParentIds() + ",");
    }
    return findListByYwProcess(actYwGnode);
  }

  /**
   * 重新组装列表属性.
   * @param gnode 流程节点
   * @return List
   */
  @Transactional(readOnly = false)
  public Boolean recursionList(ActYwGnode gnode) {
    return recursionPreIdss(gnode) && recursionNextIdss(gnode);
  }

  @Transactional(readOnly = false)
  public Boolean recursionPreIdss(ActYwGnode gnode) {
    if(StringUtil.isEmpty(gnode.getPreId()) || (gnode.getPreId()).equals(SysIds.SYS_TREE_ROOT.getId())){
      return false;
    }
    ActYwGnode pregnode = get(gnode.getPreId());
    if(pregnode != null){
      pregnode.setNextIdss(pregnode.getNextIdss()+gnode.getId()+",");
      dao.updateGnextIdss(pregnode);
      recursionPreIdss(pregnode);
      return true;
    }
    return false;
  }

  @Transactional(readOnly = false)
  public Boolean recursionNextIdss(ActYwGnode gnode) {
    if(StringUtil.isEmpty(gnode.getNextId()) || (gnode.getNextId()).equals(SysIds.SYS_TREE_ROOT.getId())){
      return false;
    }
    ActYwGnode nextgnode = get(gnode.getNextId());
    if(nextgnode != null){
      nextgnode.setPreIdss(nextgnode.getPreIdss()+gnode.getId()+",");
      dao.updateGpreIdss(nextgnode);
      recursionNextIdss(nextgnode);
      return true;
    }
    return false;
  }

  /**
   * 重新组装列表属性.
   * @param gnode 流程节点
   * @return List
   */
  @Transactional(readOnly = false)
  public GnodeMargeVo margeList(ActYwGroup group, ActYwGnode gnode) {
    GnodeMargeVo margeVo = new GnodeMargeVo(gnode.getGroup(), false, "执行失败");
    gnode.setParent(get(gnode.getParent().getId()));
    gnode.setPreGnode(get(gnode.getPreGnode().getId()));
    gnode.setNextGnode(get(gnode.getNextGnode().getId()));

    if((gnode.getParent() == null) || (gnode.getPreGnode() == null) || (gnode.getNextGnode() == null)){
      return margeVo;
    }

    List<ActYwGnode> preLists = null;
    List<ActYwGnode> nextLists = null;

    /**
     * 当前结点为子流程节点(非子流程的开始、结束)时,会查不到父节点.
     */
    if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
      preLists = findPreByGnextIdssLike(gnode.getPreGnode(), true, null);
      if((gnode.getParent().getId()).equals(SysIds.SYS_TREE_ROOT.getId())){
        nextLists = findNextByGpreIdssLike(gnode.getNextGnode());
      }else{
        nextLists = findNextByGpreIdssLike(gnode.getNextGnode(), true, null);
      }
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
      preLists = findPreByGnextIdssLike(gnode.getPreGnode());
      nextLists = findNextByGpreIdssLike(gnode.getNextGnode());
      gnode.setNextGnodess(Lists.newArrayList());
      gnode.getNextGnodess().addAll(nextLists);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
      preLists = findPreByGnextIdssLike(gnode.getPreGnode(), true, null);
      nextLists = findNextByGpreIdssLike(gnode.getNextGnode());
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_TASK.getId()))){
      preLists = findPreByGnextIdssLike(gnode.getPreGnode());
      nextLists = findNextByGpreIdssLike(gnode.getNextGnode());

      ActYwGnode gparent = get(gnode.getParent().getId());
      List<ActYwGnode> newpreLists = Lists.newArrayList();
      newpreLists = findPreByGnextIdssLike(gparent);
      newpreLists.addAll(preLists);
      preLists = newpreLists;

      List<ActYwGnode> newnextLists = Lists.newArrayList();
      newnextLists = nextLists;
      newnextLists.addAll(findNextByGpreIdssLike(gparent));
      nextLists = newnextLists;
    }else{
      preLists = findPreByGnextIdssLike(gnode.getPreGnode());
      nextLists = findNextByGpreIdssLike(gnode.getNextGnode());
    }

    /**
     * 保存操作只能在查询之后，避免查询时把当前结点也查出来，出现重复.
     */
    save(gnode);

    /**************************************************************************
     * 更新前置节点,如果为流程线，取后一个节点
     */
    ActYwGnode prefun = get(gnode.getPreFunGnode().getId());
    if(((gnode.getType()).equals(GnodeType.GT_ROOT_FLOW.getId()))){
      prefun.setNextFunGnode(gnode.getNextGnode());
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
      prefun.setNextFunGnode(gnode.getNextGnode());
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
      //保持不变
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
      //保持不变
    }else{
      prefun.setNextFunGnode(gnode.getNextFunGnode());
    }
    prefun.setNextFunGnode(gnode);
    dao.updateGnextFunId(prefun);

    /**************************************************************************
     * 更新后置节点,如果为流程线，取后一个节点
     */
    ActYwGnode nextfun = get(gnode.getNextFunGnode().getId());
    if(((gnode.getType()).equals(GnodeType.GT_ROOT_FLOW.getId()))){
      nextfun.setPreFunGnode(gnode.getPreGnode());
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
      //保持不变(因为它的下一个节点为子流程节点)
      nextfun.setPreFunGnode(gnode.getPreGnode());
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
      //保持不变(因为它的下一个节点为子流程节点)
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
      //保持不变(因为它的下一个节点为子流程节点)
    }else{
      nextfun.setPreFunGnode(gnode.getPreFunGnode());
    }
    dao.updateGpreFunId(nextfun);

    /**************************************************************************
     * 更新上一节点的后置节点.
     */
    ActYwGnode pregn = gnode.getPreGnode();
    if(((gnode.getType()).equals(GnodeType.GT_ROOT_FLOW.getId()))){
      pregn.setNextFunGnode(gnode.getNextGnode());
      pregn.setNextGnode(gnode);
      pregn.setNextGnodes(Lists.newArrayList());
      pregn.getNextGnodes().add(gnode);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
      //NextFunGnode和NextGnode不变
      List<ActYwGnode> pregnNgnodes = Lists.newArrayList();
      pregnNgnodes.addAll(pregn.getNextGnodes());
      //TODO 子流程开始节点执行add操作，子流程节点preIds值有误（会带上Start节点）
      pregnNgnodes.add(gnode);
      pregn.setNextGnodes(pregnNgnodes);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
      pregn.setNextFunGnode(gnode.getNextGnode());
      pregn.setNextGnode(gnode);

      pregn.setNextGnodes(Lists.newArrayList());
      pregn.getNextGnodes().add(gnode);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
      //NextGnodes、NextFunGnode和NextGnode不变
      pregn.setNextFunGnode(gnode);
      pregn.setNextGnode(gnode);
      pregn.setNextGnodes(Lists.newArrayList());
      pregn.getNextGnodes().add(gnode);
    }else{
      pregn.setNextFunGnode(gnode);
      pregn.setNextGnode(gnode);
      pregn.setNextGnodes(Lists.newArrayList());
      pregn.getNextGnodes().add(gnode);
    }
    dao.updateGnextIds(pregn);
    dao.updateGnextFunId(pregn);

    /**************************************************************************
     * 更新下一节点的前置节点
     */
    ActYwGnode nextgn = gnode.getNextGnode();
    if(((gnode.getType()).equals(GnodeType.GT_ROOT_FLOW.getId()))){
      nextgn.setPreFunGnode(gnode.getPreGnode());
      nextgn.setPreGnode(gnode);
      nextgn.setPreGnodes(Lists.newArrayList());
      nextgn.getPreGnodes().add(gnode);
    }else if((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId())){
      //PreGnod和PreFunGnode保持不变
      List<ActYwGnode> nextgnPgnodes = Lists.newArrayList();
      nextgnPgnodes.addAll(nextgn.getPreGnodes());
      nextgnPgnodes.remove(gnode.getPreGnode());
      //TODO 子流程开始节点不执行add操作，会导致子流程节点nextIds值有误（会带上Flow节点）
      nextgnPgnodes.add(gnode);
      nextgn.setPreGnodes(nextgnPgnodes);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
      //PreGnod和PreFunGnode保持不变
      List<ActYwGnode> nextgnPgnodes = Lists.newArrayList();
      nextgnPgnodes.addAll(nextgn.getPreGnodes());
      //TODO 子流程开始节点不执行add操作，会导致子流程节点nextIds值有误（会带上Start节点）
      nextgnPgnodes.add(gnode);
      nextgn.setPreGnodes(nextgnPgnodes);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
      //PreGnode和PreFunGnode保持不变
      List<ActYwGnode> nextgnPgnodes = Lists.newArrayList();
      nextgnPgnodes.addAll(nextgn.getPreGnodes());
      nextgnPgnodes.remove(gnode.getPreGnode());
      //TODO 子流程开始节点不执行add操作，会导致子流程节点nextIds值有误（会带上End节点）
      nextgnPgnodes.add(gnode);
      nextgn.setPreGnodes(nextgnPgnodes);
    }else{
      nextgn.setPreFunGnode(gnode);
      nextgn.setPreGnode(gnode);
      nextgn.setPreGnodes(Lists.newArrayList());
      nextgn.getPreGnodes().add(gnode);
    }
    dao.updateGpreIds(nextgn);
    dao.updateGpreFunId(nextgn);

    List<ActYwGnode> tarGnodes = Lists.newArrayList();
    List<ActYwGnode> preListsClone = Lists.newArrayList();
    for (ActYwGnode pre : preLists) {
      if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
        //NextFunGnode和NextGnode不变
      }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
        //NextFunGnode和NextGnode不变，NextGnodess(遵循同级变更，父级保持不变)
        if((pre.getParent().getId()).equals(gnode.getParent().getId())){
          pre.setNextIdss(pre.getNextIdss().replace(gnode.getNextId(), gnode.getNextId()+"," + gnode.getId()));
          pre.setNextGnodess(null);
        }
      }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
        //NextFunGnode和NextGnode不变，NextGnodess(遵循同级变更，父级保持不变)
        if((pre.getParent().getId()).equals(gnode.getParent().getId()) && (pre.getParent().getId()).equals(gnode.getParent().getId())){
          pre.setNextIdss(pre.getNextIdss().replace(gnode.getNextId(), gnode.getNextId()+"," + gnode.getId()));
          pre.setNextGnodess(null);
        }
      }else{
        pre.setNextIdss(pre.getNextIdss().replace(gnode.getNextId(), gnode.getNextId()+"," + gnode.getId()));
        pre.setNextGnodess(null);
      }
      dao.updateGnextIdss(pre);

      /**
       * 克隆对象初始化NextGnodess节点(避免数据干扰).
       */
      ActYwGnode preClone = ActYwGnode.instance(pre);
      preClone.setNextGnodess(findListByinIds(pre.getNextIdss()));
      preListsClone.add(preClone);
    }
    tarGnodes.addAll(preListsClone);

    /**************************************************************************
     * 更新当前结点的前面节点列表.
     */
    if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
      gnode.setPreGnodess(Lists.newArrayList());
      gnode.getPreGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getPreGnodess().addAll(preLists);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
      gnode.setPreGnodess(Lists.newArrayList());
      gnode.getPreGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getPreGnodess().addAll(preLists);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
      gnode.setPreGnodess(Lists.newArrayList());
      gnode.getPreGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getPreGnodess().addAll(preLists);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_TASK.getId()))){
      gnode.setPreGnodess(Lists.newArrayList());
      gnode.getPreGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getPreGnodess().addAll(preLists);
    }else{
      gnode.setPreGnodess(Lists.newArrayList());
      gnode.getPreGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getPreGnodess().addAll(preLists);
    }
    dao.updateGpreIdss(gnode);

    /**************************************************************************
     * 更新当前结点的后面节点列表.
     */
    if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
      gnode.setNextGnodess(Lists.newArrayList());
      gnode.getNextGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getNextGnodess().addAll(nextLists);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
      gnode.setNextGnodess(Lists.newArrayList());
      gnode.getNextGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      List<ActYwGnode> ngnodes = ActYwGnode.sortLinks(nextLists, Lists.newArrayList(), SysIds.SYS_TREE_ROOT.getId(), false);
      gnode.getNextGnodess().addAll(ngnodes);

    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
      gnode.setNextGnodess(Lists.newArrayList());
      gnode.getNextGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getNextGnodess().addAll(nextLists);
    }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_TASK.getId()))){
      gnode.setNextGnodess(Lists.newArrayList());
      gnode.getNextGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      List<ActYwGnode> nextReverseLists = null;
      if((gnode.getParent().getId()).equals(SysIds.SYS_TREE_ROOT.getId())){
        nextReverseLists = nextLists;
        nextReverseLists = Lists.reverse(nextReverseLists);
      }else{
        nextReverseLists = ActYwGnode.sortLinks(nextLists, null, SysIds.SYS_TREE_ROOT.getId(), false);
      }
      gnode.getNextGnodess().addAll(nextReverseLists);
    }else{
      gnode.setNextGnodess(Lists.newArrayList());
      gnode.getNextGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      gnode.getNextGnodess().addAll(nextLists);
    }
    dao.updateGnextIdss(gnode);
    tarGnodes.add(gnode);

    List<ActYwGnode> nextListsClone = Lists.newArrayList();
    for (ActYwGnode next : nextLists) {
      if(((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))){
        //NextFunGnode和NextGnode不变
      }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))){
        //NextFunGnode和NextGnode不变
        next.setPreIdss(next.getPreIdss().replace(gnode.getPreId(), gnode.getPreId()+"," + gnode.getId()));
        next.setPreGnodess(null);
      }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))){
        //NextFunGnode和NextGnode不变
      }else if(((gnode.getType()).equals(GnodeType.GT_PROCESS_TASK.getId()))){
        //NextFunGnode和NextGnode不变
        next.setPreIdss(next.getPreIdss().replace(gnode.getPreId(), gnode.getPreId()+"," + gnode.getId()));
        next.setPreGnodess(null);
      }else{
        next.setPreIdss(next.getPreIdss().replace(gnode.getPreId(), gnode.getPreId()+"," + gnode.getId()));
        next.setPreGnodess(null);
      }
      dao.updateGpreIdss(next);

      /**
       * 克隆对象初始化PreGnodess节点(避免数据干扰).
       */
      ActYwGnode nextClone = ActYwGnode.instance(next);
      nextClone.setPreGnodess(findListByinIds(next.getPreIdss()));
      nextListsClone.add(nextClone);
    }
    tarGnodes.addAll(nextListsClone);

    /**************************************************************************
     * 如果节点的子节点nextidss，
     */
    updateNextidssByLv2PL(gnode);

    margeVo.setGnode(gnode);
    margeVo.setPreGnodes(preListsClone);
    margeVo.setNextGnodes(nextListsClone);
    margeVo.setAllGnodes(tarGnodes);
    margeVo.setStatus(true);
    margeVo.setMsg("执行成功");
    return margeVo;
  }

  /**
   * 更新当前节点前置节点中含有子节点，子节点的nextidss，没有更新当前结点的情况.
   * 如果节点的子节点nextidss中没有当前结点ID，则表示需要更新，
   * 更新规则：
   *  当前子节点的父节点的nextIdss+自己所处的位置.
   */
  public void updateNextidssByLv2PL(ActYwGnode gnode) {
    if(GnodeTool.checkNeedUpdateChild(gnode)){
      List<String> ids = Arrays.asList((((gnode.getPreIdss()).endsWith(StringUtil.DOTH))?(gnode.getPreIdss()).substring(0, gnode.getPreIdss().length()-1):gnode.getPreIdss()).split(StringUtil.DOTH));
      List<ActYwGnode> cgnodes = dao.findListByinPids(ids);
      List<ActYwGnode> ctargnodes = Lists.newArrayList();
      Integer idx = null;
      String cnextIdss = null;
      String cnIdssptfix = null;
      for (ActYwGnode cgnode : cgnodes) {
        /**
         * 非二级节点不需要更新.
         */
        if(!GnodeTool.checkIsLv2(cgnode)){
          continue;
        }
        cnextIdss = cgnode.getNextIdss();
        idx = cnextIdss.indexOf(cgnode.getParentId()+StringUtil.DOTH);
        if(idx != -1){
         cnIdssptfix = cnextIdss.substring(idx, cnextIdss.length());
         cgnode.setNextIdss(cgnode.getParent().getNextIdss() + cnIdssptfix);
         ctargnodes.add(cgnode);
        }
      }
      if((ctargnodes != null) && (ctargnodes.size() > 0)){
        dao.updateGnextIdssByPL(ctargnodes);
      }
    }
  }

  /**
   * 删除节点后重新组装列表属性.
   * @param gnode 流程节点
   * @return List
   */
  @Transactional(readOnly = false)
  public GnodeMargeVo margeDelList(ActYwGnode gnode) {
    GnodeMargeVo margeVo = new GnodeMargeVo(gnode.getGroup(), false, "执行失败");
    gnode = get(gnode);
    List<ActYwGnode> gnodes = findListByGroup(new ActYwGnode(gnode.getGroup()));
    if((gnodes == null) || (gnodes.size() <= 0)){
      return margeVo;
    }

    List<String> delgnodes = Lists.newArrayList();
    List<ActYwGnode> childgnodes = Lists.newArrayList();
    List<ActYwGnode> updategnodes = Lists.newArrayList();
    List<ActYwGnode> preListsClone = Lists.newArrayList();
    List<ActYwGnode> nextListsClone = Lists.newArrayList();
    List<ActYwGnode> tarGnodes = Lists.newArrayList();
    for (ActYwGnode curgnode : gnodes) {
      ActYwGnode gnodeClone = ActYwGnode.instance(curgnode, false);
      /**
       * 判断是否有子节点(如果有子节点，需要先删除子节点).
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getParentIds(), gnode)){
        childgnodes.add(gnodeClone);
        delgnodes.add(gnodeClone.getId());
        continue;
      }

      /**
       * 更新PreId.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getPreId(), gnode, null)){
        curgnode.setPreId(gnode.getPreId());
      }

      /**
       * 更新PreIds.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getPreIds(), gnode)){
        String curPreIds = GnodeTool.checkDelGnodeId(curgnode.getPreIds(), gnode);
        curgnode.setPreIds(StringUtil.isNotEmpty(curPreIds) ? curPreIds : gnode.getPreIds());
      }

      /**
       * 更新PreIdss.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getPreIdss(), gnode)){
        curgnode.setPreIdss(GnodeTool.checkDelGnodeId(curgnode.getPreIdss(), gnode));
        preListsClone.add(gnodeClone);
      }

      /**
       * 更新PreFunId.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getPreFunId(), gnode, null)){
        curgnode.setPreFunId(gnode.getPreFunId());
      }

      /**
       * 更新NextId.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getNextId(), gnode, null)){
        curgnode.setNextId(gnode.getNextId());
      }

      /**
       * 更新NextIds.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getNextIds(), gnode)){
        String curNextIds = GnodeTool.checkDelGnodeId(curgnode.getNextIds(), gnode);
        curgnode.setNextIds(StringUtil.isNotEmpty(curNextIds) ? curNextIds : gnode.getNextIds());
      }

      /**
       * 更新NextIdss.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getNextIdss(), gnode)){
        curgnode.setNextIdss(GnodeTool.checkDelGnodeId(curgnode.getNextIdss(), gnode));
        nextListsClone.add(gnodeClone);
      }

      /**
       * 更新NextFunId.
       */
      if(GnodeTool.checkHaveGnodeId(curgnode.getNextFunId(), gnode, null)){
        curgnode.setNextFunId(gnode.getNextFunId());
      }
      updategnodes.add(curgnode);
    }

    /**
     * 更新结点属性.
     */
    if(!updategnodes.isEmpty()){
      dao.updatePropPL(updategnodes);
    }

    if(!delgnodes.isEmpty()){
      /**
       * 删除子节点.
       */
      //dao.deletePLWL(delgnodes);
    }

    /**
     * 删除当前结点.
     */
    dao.deleteWL(gnode);

    margeVo.setGnode(gnode);
    margeVo.setPreGnodes(preListsClone);
    margeVo.setNextGnodes(nextListsClone);
    tarGnodes.addAll(preListsClone);
    tarGnodes.add(ActYwGnode.instance(gnode, false));
    tarGnodes.addAll(nextListsClone);
    margeVo.setAllGnodes(tarGnodes);
    margeVo.setStatus(true);
    margeVo.setMsg("执行成功");
    return margeVo;
  }

  @Transactional(readOnly = false)
  public ActYwRstatus<ActYwGnode> saveByGnodePvo(GnodePvo pvo, ActYwRunner runner) {
    ActYwGnode actYwGnode = GnodePvo.convert(pvo);
    if (actYwGnode == null) {
      return new ActYwRstatus<ActYwGnode>(false, "参数异常!");
    }

    return saveByGnodePvo(actYwGnode, runner);
  }

  @Transactional(readOnly = false)
  public ActYwRstatus<ActYwGnode> saveByGnodePvo(ActYwGnode actYwGnode, ActYwRunner runner) {
    if ((actYwGnode.getOffice() == null)) {
      actYwGnode.setOffice(UserUtils.getAdminOffice());
    }

    ActYwRstatus<ActYwGnode> result = null;
    if (actYwGnode.getIsNewRecord()) {
//      result = saveAutoPL(ActYwRunner.IS_ROOT_NOT, runner, actYwGnode.getGroup(), actYwGnode);
      result = saveAuto(ActYwRunner.IS_ROOT_NOT, runner, actYwGnode.getGroup(), actYwGnode);
      if (result.getStatus()) {
        saveAfterOper(actYwGnode);
      }
    } else {
      save(actYwGnode);
      saveAfterOper(actYwGnode);
      result = new ActYwRstatus<ActYwGnode>(true, "更新数据成功！");
      result.setDatas(actYwGnode);
    }

    /**
     * 设置返回的值(规避ActYwGnode序列化时报错问题).
     */
    if(result.getDatas() != null){
      ActYwGnode cgnode = result.getDatas();
      ActYwGnode rgnode = new ActYwGnode();
      rgnode.setId(cgnode.getId());
      rgnode.setPreId(cgnode.getPreId());
      rgnode.setPreFunId(cgnode.getPreFunId());
      rgnode.setNextId(cgnode.getNextId());
      rgnode.setNextFunId(cgnode.getNextFunId());
      List<ActYwGnode> childs = null;
      if((cgnode.getChildGnodes() != null) && (cgnode.getChildGnodes().size() > 0)){
        List<String> childIds = Lists.newArrayList();
        for (ActYwGnode child : cgnode.getChildGnodes()) {
          childIds.add(child.getId());
        }
        childs = findListByinIds(childIds);
      }
      if(childs == null){
        childs = Lists.newArrayList();
      }
      rgnode.setChildGnodes(childs);
      result.setDatas(rgnode);
    }
    return result;
  }

  /**
   * 执行节点更新后，更新其它数据操作.
   *
   * @param actYwGnode
   */
  private void saveAfterOper(ActYwGnode actYwGnode) {
    /**
     * 更新父节点ActYwGnode的ListId
     */
    if ((actYwGnode.getParent() != null) && StringUtil.isNotEmpty(actYwGnode.getParent().getId()) && !(SysIds.SYS_TREE_ROOT.getId()).equals(actYwGnode.getParent().getId())) {
      ActYwGnode pactYwGnode = get(actYwGnode.getParent().getId());

      ActYwForm pactYwForm = null;
      if (StringUtil.isNotEmpty(actYwGnode.getFormId())) {
        pactYwForm = actYwFormDao.get(actYwGnode.getFormId());
      }
      if ((pactYwForm == null) && ((actYwGnode.getForm() != null) && StringUtil.isEmpty(actYwGnode.getForm().getId()))) {
        pactYwForm = actYwFormDao.get(actYwGnode.getForm().getId());
      }
      if (pactYwForm != null) {
        pactYwGnode.setForm(pactYwForm.getListForm());
        pactYwGnode.setFormId(pactYwForm.getListId());
      }
      save(pactYwGnode);
    }

    /**
     * 更新ActYwGroup的datas
     */
    ActYwGroup curActYwGroup = actYwGroupDao.get(actYwGnode.getGroupId());
    curActYwGroup.setDatas(actYwGnode.getGroup().getDatas());
    actYwGroupDao.update(curActYwGroup);
  }

  @Transactional(readOnly = false)
  public ActYwRstatus<ActYwGnode> updateByGnodePvo(GnodePvo pvo) {
    if (StringUtil.isEmpty(pvo.getId())) {
      return new ActYwRstatus<ActYwGnode>(false, "ID参数不能为空！");
    }

    ActYwGnode actYwGnode = get(pvo.getId());
    if (actYwGnode == null) {
      return new ActYwRstatus<ActYwGnode>(false, "数据异常,ID=["+pvo.getId()+"]找不到!");
    }

    actYwGnode = GnodePvo.convert(pvo, actYwGnode, false);

    if ((actYwGnode.getOffice() == null)) {
      actYwGnode.setOffice(UserUtils.getAdminOffice());
    }

    save(actYwGnode);
    return new ActYwRstatus<ActYwGnode>(true, "更新数据成功！");
  }

  /**
   * 重置流程节点.
   * @param gnodeId 业务节点ID
   * @param runner ActYwRunner
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<ActYwGnode> resetGroup(String groupId, ActYwRunner runner) {
    ActYwRstatus<ActYwGnode> result = new ActYwRstatus<ActYwGnode>(false, "执行失败,节点数据不存在");
    if (StringUtil.isNotEmpty(groupId)) {
      ActYwGroup group = actYwGroupDao.get(groupId);
      if (StringUtil.isEmpty(groupId) || (group == null)) {
        result.setMsg("流程不存在或参数为空[type值未定义]");
        return result;
      }
      dao.deleteByGroup(group.getId());
      result = saveAuto(ActYwRunner.IS_ROOT, runner, group, null);
      result.setDatas(new ActYwGnode(group));
    }else{
      result = new ActYwRstatus<ActYwGnode>(false, "执行失败,节点数据不存在");
    }
    return result;
  }

  @Transactional(readOnly = false)
  public void save(ActYwGnode actYwGnode) {
    if ((actYwGnode != null)) {
      if (actYwGnode.getIsNewRecord()) {
        actYwGnode.setIsShow(true);
        actYwGnode.setSort(30);

        if ((actYwGnode.getParentId()).equals(SysIds.SYS_TREE_ROOT.getId())) {
          actYwGnode.setIsForm(false);
        }
      }

      if ((actYwGnode.getNode() != null)) {
        if (!(actYwGnode.getNode().getLevel()).equals(RtSvl.RtLevelVal.RT_LV2)) {
          actYwGnode.setFlowGroup(null);
        }
      }

      if (StringUtil.isNotEmpty(actYwGnode.getFormId())) {
        actYwGnode.setIsForm(true);
      } else {
        actYwGnode.setIsForm(false);
      }

      if(StringUtil.isEmpty(actYwGnode.getPosLux())){
        actYwGnode.setPosLux(ActYwGnode.DEFAULT_PLUX);
      }

      if(StringUtil.isEmpty(actYwGnode.getPosLuy())){
        actYwGnode.setPosLuy(ActYwGnode.DEFAULT_PLUY);
      }

      if(StringUtil.isEmpty(actYwGnode.getPosAlux())){
        actYwGnode.setPosAlux(ActYwGnode.DEFAULT_PLUX);
      }

      if(StringUtil.isEmpty(actYwGnode.getPosAluy())){
        actYwGnode.setPosAluy(ActYwGnode.DEFAULT_PLUY);
      }

      ActYwGnodeUtil.checkHasGroup(actYwGnode);
      super.save(actYwGnode);
    }
  }

  /**
   * 批量个业务节点添加（暂不支持）.
   * @param isRoot 是否根节点
   * @param runner ActYwRunner
   * @param actYwGoup 流程
   * @param actYwGnode 业务节点
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<ActYwGnode> saveAutoPL(Boolean isRoot, ActYwRunner runner, ActYwGroup actYwGoup, ActYwGnode actYwGnode) {
    List<Map<ActYwEcmd, ActYwPgroot>> cmdParams = Lists.newArrayList();
    List<ActYwGnode> nodes = null;
    if (actYwGnode == null) {
      nodes = dao.findListByGroup(new ActYwGnode(actYwGoup));
    } else {
      nodes = dao.findListByGroup(actYwGnode);
    }
    ActYwRstatus<ActYwGnode> results = new ActYwRstatus<ActYwGnode>();
    ActYwPgroot param = null;// 命令执行参数

    if ((nodes == null) || (nodes.isEmpty())) {// 没有任何节点时
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
    } else {
      /**
       * 判断是否为根节点操作.
       */
      if (isRoot) {
        results.setStatus(true);
        results.setMsg("自定义流程更新成功！");
        LOGGER.warn(results.getMsg());
        return results;
      }

      if ((actYwGnode == null)) {
        results.setStatus(false);
        results.setMsg("业务节点类型不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      }

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

      ActYwGnode gstartGnode = null;// 开始节点
      ActYwGnode gendGnode = null;// 结束节点
      ActYwGnode grootStartGnode = null;// 流程开始节点
      ActYwGnode grootEndGnode = null;// 流程结束节点
      ActYwGnode gprocessGnode = null;// 子流程节点
      ActYwGnode gprocessStartGnode = null;// 子流程开始节点
      ActYwGnode gprocessEndGnode = null;// 子流程结束节点

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
          } else {
            if ((curnode.getId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessGnode = curnode;
            }
          }
        } else if (((curgnodeType).equals(GnodeType.GT_PROCESS_START))) {
          if ((gnodePreFunType).equals(GnodeType.GT_PROCESS)) {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getId())) {
              gprocessStartGnode = curnode;
            }
          } else {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessStartGnode = curnode;
            }
          }
        } else if (((curgnodeType).equals(GnodeType.GT_PROCESS_END))) {
          if ((gnodePreFunType).equals(GnodeType.GT_PROCESS)) {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getId())) {
              gprocessEndGnode = curnode;
            }
          } else {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessEndGnode = curnode;
            }
          }
        }
      }

      if ((gnodeType).equals(GnodeType.GT_ROOT) || (gnodeType).equals(GnodeType.GT_ROOT_START)
          || (gnodeType).equals(GnodeType.GT_ROOT_END) || (gnodeType).equals(GnodeType.GT_ROOT_FLOW)
          || (gnodeType).equals(GnodeType.GT_PROCESS)) {
        gstartGnode = grootStartGnode;// 开始节点
        gendGnode = grootEndGnode;// 结束节点
      } else if ((gnodeType).equals(GnodeType.GT_PROCESS_START)
          || (gnodeType).equals(GnodeType.GT_PROCESS_END)
          || (gnodeType).equals(GnodeType.GT_PROCESS_FLOW)
          || (gnodeType).equals(GnodeType.GT_PROCESS_GATEWAY)
          || (gnodeType).equals(GnodeType.GT_PROCESS_TASK)) {
        gstartGnode = gprocessStartGnode;// 子流程开始节点
        gendGnode = gprocessEndGnode;// 子流程结束节点
      } else {
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

      if ((actYwGnode.getPreFunGnode() == null)
          || StringUtil.isEmpty((actYwGnode.getPreFunGnode().getId()))) {
        results.setStatus(false);
        results.setMsg("前置业务节点不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      } else {
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
        }
      }

      /**
       * 判断节点数大于3时，前置业务节点类型必须为TASK或GATEWAY或FLOW
       */
      if (nodes.size() > 3) {
        if ((actYwGnode.getPreFunGnode().getNode() != null)
            && StringUtil.isNotEmpty(actYwGnode.getPreFunGnode().getNode().getId())) {
          actYwGnode.setPreFunGnode(get(actYwGnode.getPreFunGnode().getId()));
        }
        StenType preStenType = StenType
            .getByKey(actYwGnode.getPreFunGnode().getNode().getNodeKey());
        if (!((preStenType.getSubtype()).equals(StenEsubType.SES_TASK)
            || (preStenType.getSubtype()).equals(StenEsubType.SES_GATEWAY)
            || (preStenType.getSubtype()).equals(StenEsubType.SES_JG)
            || (preStenType.getSubtype()).equals(StenEsubType.SES_EVENT_START))) {
          results.setStatus(false);
          results.setMsg("业务节点(gt 3)时,前置节点必须为[" + StenEsubType.SES_TASK.getRemark() + "或"
              + StenEsubType.SES_GATEWAY.getRemark() + "或" + StenEsubType.SES_JG.getRemark()
              + "]节点！");
          LOGGER.warn(results.getMsg());
          return results;
        }
      }

      if ((nodes.size() == 3)) {// 没有任何业务节点时
        actYwGnode.setPreFunGnode(gstartGnode);
        for (ActYwGnode node : nodes) {
          if ((node.getNodeId()).equals(StenType.ST_FLOW_SEQUENCE.getId())) {
            actYwGnode.setPreGnode(node);
          }
        }

        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));
        ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
      } else if ((nodes.size() == 5)) {// 有一个业务节点时
        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));
        ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
      } else if ((nodes.size() >= 7)) {// 有2个以上业务节点时
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
              if (!param.getGnode().isHasGateway()) {
                ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
              } else {
                ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODEGATE, param);
              }
            }
          } else if (param.getStartSflowGnode().size() > 1) {
            ActYwRunner.addCmdParam(cmdParams, ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
          }
        }
      }
    }

    ActYwRstatus<ActYwGnode> rstatus = runner.callExecutePL(cmdParams);
    results.setStatus(rstatus.getStatus());
    results.setMsg(rstatus.getMsg());
    results.setDatas(rstatus.getDatas());
    return results;
  }

  /**
   * 单个业务节点添加.
   * @param isRoot 是否根节点
   * @param runner ActYwRunner
   * @param actYwGoup 流程
   * @param actYwGnode 业务节点
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<ActYwGnode> saveAuto(Boolean isRoot, ActYwRunner runner, ActYwGroup actYwGoup, ActYwGnode actYwGnode) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>(false, "执行失败");
    List<ActYwGnode> nodes = null;
    if (actYwGnode == null) {
      nodes = dao.findListByGroup(new ActYwGnode(actYwGoup));
    } else {
      nodes = dao.findListByGroup(actYwGnode);
    }
    ActYwRstatus<ActYwGnode> results = new ActYwRstatus<ActYwGnode>();
    ActYwPgroot param = null;// 命令执行参数

    if ((nodes == null) || (nodes.isEmpty())) {// 没有任何节点时
      /**
       * 当没有任何节点，不论是否有业务节点，都需要新增根节点开始结束节点.
       */
      param = new ActYwPgroot(actYwGoup);
      rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_ADD, param);
      /**
       * 当没有任何节点，且业务节点不为空的时候,添加业务节点.
       */
      if ((actYwGnode != null) && rstatus.getStatus()) {
        param = ActYwPgroot.toActYwPgroot(actYwGnode, null, null);
        rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
        results.setStatus(rstatus.getStatus());
        results.setMsg(rstatus.getMsg());
        results.setDatas(rstatus.getDatas());
        return results;
      }
    } else {
      /**
       * 判断是否为根节点操作.
       */
      if (isRoot) {
        results.setStatus(true);
        results.setMsg("自定义流程更新成功！");
        LOGGER.warn(results.getMsg());
        return results;
      }

      if ((actYwGnode == null)) {
        results.setStatus(false);
        results.setMsg("业务节点类型不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      }

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

      ActYwGnode gstartGnode = null;// 开始节点
      ActYwGnode gendGnode = null;// 结束节点
      ActYwGnode grootStartGnode = null;// 流程开始节点
      ActYwGnode grootEndGnode = null;// 流程结束节点
      ActYwGnode gprocessGnode = null;// 子流程节点
      ActYwGnode gprocessStartGnode = null;// 子流程开始节点
      ActYwGnode gprocessEndGnode = null;// 子流程结束节点

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
          } else {
            if ((curnode.getId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessGnode = curnode;
            }
          }
        } else if (((curgnodeType).equals(GnodeType.GT_PROCESS_START))) {
          if ((gnodePreFunType).equals(GnodeType.GT_PROCESS)) {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getId())) {
              gprocessStartGnode = curnode;
            }
          } else {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessStartGnode = curnode;
            }
          }
        } else if (((curgnodeType).equals(GnodeType.GT_PROCESS_END))) {
          if ((gnodePreFunType).equals(GnodeType.GT_PROCESS)) {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getId())) {
              gprocessEndGnode = curnode;
            }
          } else {
            if ((curnode.getParentId()).equals(actYwGnode.getPreFunGnode().getParentId())) {
              gprocessEndGnode = curnode;
            }
          }
        }
      }

      if ((gnodeType).equals(GnodeType.GT_ROOT) || (gnodeType).equals(GnodeType.GT_ROOT_START)
          || (gnodeType).equals(GnodeType.GT_ROOT_END) || (gnodeType).equals(GnodeType.GT_ROOT_FLOW)
          || (gnodeType).equals(GnodeType.GT_PROCESS)) {
        gstartGnode = grootStartGnode;// 开始节点
        gendGnode = grootEndGnode;// 结束节点
      } else if ((gnodeType).equals(GnodeType.GT_PROCESS_START)
          || (gnodeType).equals(GnodeType.GT_PROCESS_END)
          || (gnodeType).equals(GnodeType.GT_PROCESS_FLOW)
          || (gnodeType).equals(GnodeType.GT_PROCESS_GATEWAY)
          || (gnodeType).equals(GnodeType.GT_PROCESS_TASK)) {
        gstartGnode = gprocessStartGnode;// 子流程开始节点
        gendGnode = gprocessEndGnode;// 子流程结束节点
      } else {
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

      if ((actYwGnode.getPreFunGnode() == null)
          || StringUtil.isEmpty((actYwGnode.getPreFunGnode().getId()))) {
        results.setStatus(false);
        results.setMsg("前置业务节点不能为空！");
        LOGGER.warn(results.getMsg());
        return results;
      } else {
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
        }
      }

      /**
       * 判断节点数大于3时，前置业务节点类型必须为TASK或GATEWAY或FLOW
       */
      if (nodes.size() > 3) {
        if ((actYwGnode.getPreFunGnode().getNode() != null)
            && StringUtil.isNotEmpty(actYwGnode.getPreFunGnode().getNode().getId())) {
          actYwGnode.setPreFunGnode(get(actYwGnode.getPreFunGnode().getId()));
        }
        StenType preStenType = StenType
            .getByKey(actYwGnode.getPreFunGnode().getNode().getNodeKey());
        if (!((preStenType.getSubtype()).equals(StenEsubType.SES_TASK)
            || (preStenType.getSubtype()).equals(StenEsubType.SES_GATEWAY)
            || (preStenType.getSubtype()).equals(StenEsubType.SES_JG)
            || (preStenType.getSubtype()).equals(StenEsubType.SES_EVENT_START))) {
          results.setStatus(false);
          results.setMsg("业务节点(gt 3)时,前置节点必须为[" + StenEsubType.SES_TASK.getRemark() + "或"
              + StenEsubType.SES_GATEWAY.getRemark() + "或" + StenEsubType.SES_JG.getRemark()
              + "]节点！");
          LOGGER.warn(results.getMsg());
          return results;
        }
      }

      if ((nodes.size() == 3)) {// 没有任何业务节点时
        actYwGnode.setPreFunGnode(gstartGnode);
        for (ActYwGnode node : nodes) {
          if ((node.getNodeId()).equals(StenType.ST_FLOW_SEQUENCE.getId())) {
            actYwGnode.setPreGnode(node);
          }
        }

        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));
        rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
        results.setStatus(rstatus.getStatus());
        results.setMsg(rstatus.getMsg());
        results.setDatas(rstatus.getDatas());
        return results;
      } else if ((nodes.size() == 5)) {// 有一个业务节点时
        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));
        rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
        results.setStatus(rstatus.getStatus());
        results.setMsg(rstatus.getMsg());
        results.setDatas(rstatus.getDatas());
        return results;
      } else if ((nodes.size() >= 7)) {// 有2个以上业务节点时
        param = ActYwPgroot.toActYwPgroot(actYwGnode, findPreListByGroup(actYwGnode), findNextListByGroup(actYwGnode));
        ActYwPgroot.initNextFunGnode(param, gendGnode, get(actYwGnode.getNextFunGnode()));
        if (GnodeTool.checkIsGateWay(param)) {
          rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_ADD_NODEGATE, param);
          results.setStatus(rstatus.getStatus());
          results.setMsg(rstatus.getMsg());
          results.setDatas(rstatus.getDatas());
          return results;
        }else{
          rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_ADD_NODE, param);
          results.setStatus(rstatus.getStatus());
          results.setMsg(rstatus.getMsg());
          results.setDatas(rstatus.getDatas());
          return results;
        }
      }
    }
    return results;
  }

  /**
   * 单个业务节点删除.
   * @param gnodeId 业务节点ID
   * @param runner ActYwRunner
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<ActYwGnode> deleteAuto(String gnodeId, ActYwRunner runner) {
    if (StringUtil.isEmpty(gnodeId)) {
      return new ActYwRstatus<ActYwGnode>(false, "执行失败,节点数据不存在");
    }

    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>(false, "执行失败,节点数据不存在");
    ActYwGnode gnode = dao.get(gnodeId);
    if (gnode == null) {
      return rstatus;
    }

    GnodeType gnodeType = GnodeType.getById(gnode.getType());
    if (StringUtil.isEmpty(gnode.getType()) || (gnodeType == null)) {
      rstatus.setMsg("节点数据有误[type值未定义]");
      return rstatus;
    }

    ActYwPgroot param = new ActYwPgroot(gnode);// 命令执行参数
    if((gnodeType).equals(GnodeType.GT_ROOT)){
      rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_DELETE, param);
    }else if((gnodeType).equals(GnodeType.GT_PROCESS)){
      rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_DELETE_NODE, param);
    }else if((gnodeType).equals(GnodeType.GT_PROCESS_TASK)){
      rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_DELETE_NODE, param);
    }else if((gnodeType).equals(GnodeType.GT_PROCESS_GATEWAY)){
      rstatus = runner.callExecute(ActYwEcmd.ECMD_ROOT_DELETE_NODEGATE, param);
    }else if((gnodeType).equals(GnodeType.GT_PROCESS_END) || (gnodeType).equals(GnodeType.GT_PROCESS_FLOW) || (gnodeType).equals(GnodeType.GT_PROCESS_START) || (gnodeType).equals(GnodeType.GT_ROOT_END) || (gnodeType).equals(GnodeType.GT_ROOT_FLOW) || (gnodeType).equals(GnodeType.GT_ROOT_START)){
      rstatus.setMsg("当前节点不能删除，只能删除一下节点类型[" + GnodeType.GT_ROOT.getRemark() + "|" + GnodeType.GT_PROCESS.getRemark() +  "|" + GnodeType.GT_PROCESS_TASK.getRemark() + "]");
      return rstatus;
    }
    return rstatus;
  }

  @Transactional(readOnly = false)
  public void delete(ActYwGnode actYwGnode) {
    super.delete(actYwGnode);
  }

  /**
   * 批量更新自定义流程节点排序 .
   *
   * @author chenhao
   * @param gnode
   */
  @Transactional(readOnly = false)
  public void updateSort(ActYwGnode gnode) {
    actYwGnodeDao.updateSort(gnode);
  }

  /**
   * 根据节点查询上一节点或下一节点. 查询所有前面节点时nextGnode 为空 查询所有后面节点时preGnode 为空
   *
   * @author chenhao
   * @param gnode
   *          实体.
   */
  public List<ActYwGnode> findPreNextListByGroup(ActYwGnode gnode) {
    return actYwGnodeDao.findPreNextListByGroup(gnode);
  }

  /**
   * 根据节点查询上一节点.
   * group不能为空 preFunGnode不能为空
   * @author chenhao
   * @param gnode
   *          实体.
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
   * 根据节点查询下一节点. group不能为空 nextFunGnode不能为空
   *
   * @author chenhao
   * @param gnode
   *          实体.
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
  public List<Map<String, Object>> treeDataForTimeIndexByYwId(String extId, String ywId,  String level,  Boolean isDetail, Integer isYw,Boolean isAll) {
	  List<Map<String, Object>> mapList = Lists.newArrayList();
	    if(StringUtil.isNotEmpty(ywId)){
	      ActYw actYw = actYwDao.get(ywId);
	      if((actYw != null) && StringUtil.isNotEmpty(actYw.getGroupId())){
	        mapList = treeDataForTimeIndexByGroup(actYw.getProProject().getId(),ywId,extId, actYw.getGroupId(), level,  isDetail, isYw, isAll);
	      }
	    }
	    return mapList;
  }
  public List<Map<String, Object>> treeDataByYwId(String extId, String ywId,  String level,  Boolean isDetail, Integer isYw,Boolean isAll) {
	  List<Map<String, Object>> mapList = Lists.newArrayList();
	    if(StringUtil.isNotEmpty(ywId)){
	      ActYw actYw = actYwDao.get(ywId);
	      if((actYw != null) && StringUtil.isNotEmpty(actYw.getGroupId())){
	        mapList = treeDataByGroup(extId, actYw.getGroupId(), level,  isDetail, isYw, isAll);
	      }
	    }
	    return mapList;
  }
  public List<Map<String, Object>> treeDataForTimeIndexByGroup(String ppid,String actywId,String extId, String groupId, String level,Boolean isDetail,Integer isYw, Boolean isAll) {
	  List<Map<String, Object>> mapList = Lists.newArrayList();

    List<ActYwGnode> list = queryForTimeIndexByYw(ppid,groupId, level, isYw);

    for (int i=0; i<list.size(); i++) {
      ActYwGnode e = list.get(i);
      if (StringUtil.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", e.getId());
        map.put("pId", e.getParentId());
        map.put("name", e.getName());
        map.put("formId", (e.getForm()!=null&&e.getForm().getId()!=null?e.getForm().getId():"javascript:void(0);"));
        map.put("formName", (e.getForm()==null?"":e.getForm().getName()));
        map.put("beginDate", (e.getActYwGtime()==null?null:e.getActYwGtime().getBeginDate()));
        map.put("endDate",  (e.getActYwGtime()==null?null:e.getActYwGtime().getEndDate()));
        if((isDetail != null) && isDetail){
          map.put("gnode", e);
        }
        mapList.add(map);
      }
    }
    return mapList;
  }
  public List<Map<String, Object>> treeDataByGroup(String extId, String groupId, String level,Boolean isDetail,Integer isYw, Boolean isAll) {
	  List<Map<String, Object>> mapList = Lists.newArrayList();

    List<ActYwGnode> list = queryByYw(groupId, level, isYw);
    for (int i=0; i<list.size(); i++) {
      ActYwGnode e = list.get(i);
      if (StringUtil.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", e.getId());
        map.put("pId", e.getParentId());
        map.put("name", e.getName());
        if((isDetail != null) && isDetail){
          map.put("gnode", e);
        }
        mapList.add(map);
      }
    }
    return mapList;
  }
  public List<ActYwGnode> queryForTimeIndexByYw(String ppid,String groupId, String level, Integer isYw) {
	    ActYwGnode pactYwGnode = new ActYwGnode();
	    if (StringUtil.isNotEmpty(groupId)) {
	      pactYwGnode.setGroupId(groupId);
	    }

	    if (isYw == null) {
	      isYw = ActYwGnode.L_YW;
	    }

	    if (StringUtil.isNotEmpty(level)) {
	      ActYwNode pactYwNode = new ActYwNode();
	      pactYwNode.setLevel(level);
	      pactYwGnode.setNode(pactYwNode);
	    }
	    ActYwGtime ayg=new ActYwGtime();
	    ayg.setProjectId(ppid);
	    pactYwGnode.setActYwGtime(ayg);
	    List<ActYwGnode> list = null;
	    if ((isYw).equals(ActYwGnode.L_PROCESS)) {
	      list = findListByYwProcessGroup(pactYwGnode);
	    } else if ((isYw).equals(ActYwGnode.L_YW)) {
	      list = findListForTimeIndexByYwGroup(pactYwGnode);
	    } else if ((isYw).equals(ActYwGnode.L_ALL)) {
	      list = findListByGroup(pactYwGnode);
	    } else {
	      logger.warn("业务类型未定义！！");
	      return Lists.newArrayList();
	    }
	    return list;
	  }
  /**
   * 根据业务类型和节点等级获取流程.
   *
   * @param groupId
   *          流程标识
   * @param level
   *          节点等级
   * @param isYw
   *          业务类型
   * @return
   */
  public List<ActYwGnode> queryByYw(String groupId, String level, Integer isYw) {
    ActYwGnode pactYwGnode = new ActYwGnode();
    if (StringUtil.isNotEmpty(groupId)) {
      pactYwGnode.setGroupId(groupId);
    }

    if (isYw == null) {
      isYw = ActYwGnode.L_YW;
    }

    if (StringUtil.isNotEmpty(level)) {
      ActYwNode pactYwNode = new ActYwNode();
      pactYwNode.setLevel(level);
      pactYwGnode.setNode(pactYwNode);
    }

    List<ActYwGnode> list = null;
    if ((isYw).equals(ActYwGnode.L_PROCESS)) {
      list = findListByYwProcessGroup(pactYwGnode);
    } else if ((isYw).equals(ActYwGnode.L_YW)) {
      list = findListByYwGroup(pactYwGnode);
    } else if ((isYw).equals(ActYwGnode.L_ALL)) {
      list = findListByGroup(pactYwGnode);
    } else {
      logger.warn("业务类型未定义！！");
      return Lists.newArrayList();
    }
    return list;
  }

  /**
   * 批量物理删除.
   *
   * @param deleteLists
   */
  @Transactional(readOnly = false)
  public Boolean deletePLWL(List<ActYwGnode> deleteLists) {
    List<String> ids = Lists.newArrayList();
    for (ActYwGnode actYwGnode : deleteLists) {
      ids.add(actYwGnode.getId());
    }
    dao.deletePLWL(ids);
    return true;
  }

  /**
   * 验证流程合法性.
   * 验证流程是否符合发布条件，过滤特定流程
   * @param groupId
   *          流程标识
   * @return boolean
   */
  public boolean validateProcess(String groupId) {
    List<ActYwGnode> processGnodes = findListByYwProcessGroup(
        new ActYwGnode(new ActYwGroup(groupId)));
    Boolean hasSubProcess = true;
    Boolean hasSubChilds = true;

    /** 忽略列表 */
    if(validateIgnoProcess(groupId)){
      return true;
    }

    if ((processGnodes == null) || (processGnodes.size() <= 0)) {
      hasSubProcess = false;
    } else {
      List<ActYwGnode> gnodes = findListByYwGroup(new ActYwGnode(new ActYwGroup(groupId)));
      for (ActYwGnode process : processGnodes) {
        List<ActYwGnode> subs = Lists.newArrayList();
        for (ActYwGnode subgnode : gnodes) {
          if ((process.getId()).equals(subgnode.getParent().getId())) {
            subs.add(subgnode);
          }
        }

        if (subs.size() <= 0) {
          hasSubChilds = false;
        }
      }
    }
    return (hasSubProcess && hasSubChilds);
  }

  /**
   * 验证流程是否符合发布条件，过滤特定流程.
   * @param groupId
   *          流程标识
   * @return
   */
  public boolean validateIgnoProcess(String groupId) {
    String[] ignorFilter = new String[]{ProjectNodeVo.YW_FID, GContestNodeVo.YW_FID};
    Boolean isTrue = false;
    for (String id : ignorFilter) {
      if(((groupId).equals(id))){
        isTrue = true;
      }
    }
    return isTrue;
  }


  /**
   * 根据前置业务节点ID查找兄弟业务节点.
   *
   * @param preFunId
   *          前置业务节点ID
   * @return List
   */
  public List<ActYwGnode> findFunSlibings(String groupId, String preFunId) {
    if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(preFunId)) {
      return null;
    }

    ActYwGnode preFunGnode = new ActYwGnode();
    preFunGnode.setPreFunId(preFunId);
    preFunGnode.setGroup(new ActYwGroup(groupId));
    return dao.findSlibings(preFunGnode);
  }

  /**
   * 根据前置节点ID查找兄弟节点.
   *
   * @param preId
   *          前置节点ID
   * @return List
   */
  public List<ActYwGnode> findSlibings(String groupId, String preId) {
    if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(preId)) {
      return null;
    }

    ActYwGnode preGnode = new ActYwGnode();
    preGnode.setPreId(preId);
    preGnode.setGroup(new ActYwGroup(groupId));
    return dao.findSlibings(preGnode);
  }

  /**
   *处理节点选中状态.
   * @param groupId 流程ID
   * @param selectId 选中节点ID
   * @param gnodeViews 视图节点集合
   * @param sortlist 所有节点
   * @param curRunningGnode 当前节点
   * @return ActYwRstatus
   */
  private ActYwRstatus<List<GnodeView>> dealGnodeViews(String groupId, String selectId, List<GnodeView> gnodeViews, List<ActYwGnode> sortlist, ActYwGnode curRunningGnode) {
    ActYwRstatus<List<GnodeView>> rstatus = new ActYwRstatus<List<GnodeView>>();
    if((curRunningGnode == null) || StringUtil.isEmpty(curRunningGnode.getId())){
      for (ActYwGnode gnode : sortlist) {
        gnodeViews.add(new GnodeView(gnode));
      }
      rstatus.setMsg("流程当前结点数据不存在[curId= "+selectId+"]！");
      rstatus.setDatas(gnodeViews);
      return rstatus;
    }else{
      /**
       * 查询流程历史获取当前执行的流程.
       * 根据流程执行状态，获取节点ID(将参数传递的ID改为流程获取,参数去掉)
       */
      if(!(groupId).equals(curRunningGnode.getGroupId())){
        rstatus.setStatus(false);
        rstatus.setMsg("groupId["+groupId+"]与id存在[groupId="+curRunningGnode.getGroupId()+"]冲突!");
        return rstatus;
      }

      /**
       *已执行完成节点.
       **/
      ActYwGnode pactYwGnodeEnd = new ActYwGnode();
      pactYwGnodeEnd.setGroupId(groupId);
      pactYwGnodeEnd.setPreidssx(Arrays.asList((curRunningGnode.getPreIdss()).split(",")));
      List<ActYwGnode> actYwGnodeEnds = findPreNextByGroupAndIdss(pactYwGnodeEnd, true);

      if((sortlist == null) || (sortlist.size() <= 0)){
        rstatus.setStatus(false);
        rstatus.setMsg("没有查询到节点");
        return rstatus;
      }

      if((actYwGnodeEnds != null) && ((sortlist.size() < actYwGnodeEnds.size()))){
        rstatus.setStatus(false);
        rstatus.setMsg("查询数据不正确");
        return rstatus;
      }


      /**
       * 流程节点更新用户信息.
       */
      List<String> roleIds = Lists.newArrayList();
      for (ActYwGnode agnode : sortlist) {
        if(StringUtil.isNotEmpty(agnode.getFlowGroup())){
          roleIds.add(agnode.getFlowGroup());
        }
      }

      if(roleIds.size() > 0){
        List<Role> roles = roleDao.findListByIds(roleIds);
        if((roles != null) && (roles.size() > 0)){
          for (ActYwGnode agnode : sortlist) {
            List<User> curUsers = Lists.newArrayList();
            if(StringUtil.isEmpty(agnode.getFlowGroup())){
              agnode.setUsers(curUsers);
              continue;
            }

            if((agnode != null) && (agnode.getForm() != null)){
              if((agnode.getForm().getClientType()).equals(FormClientType.FST_FRONT.getKey())){
                continue;
              }
            }

            for (Role role : roles) {
              if((SysIds.SYS_ROLE_USER.getId()).equals(role) || (role.getUser() == null) ){
                continue;
              }

              if((agnode.getFlowGroup()).contains(role.getId()) ){
                curUsers.add(role.getUser());
              }
            }
            agnode.setUsers(curUsers);
          }
        }
      }

      /**
       * 循环设置结束状态.
       */
      for (ActYwGnode gnode : sortlist) {
        GnodeView gview = new GnodeView(gnode);

        /**
         * 设置已完成的节点状态.
         **/
        if((actYwGnodeEnds).contains(gnode)){
          gview.setRstatus(GnodeView.GV_END);
        }

        /**
         * 设置前置节点中子节点完成状态.
         **/
        if((curRunningGnode.getParentId()).equals(SysIds.SYS_TREE_ROOT.getId()) || !(curRunningGnode.getParentId()).equals(gnode.getParent().getId())){
          if((actYwGnodeEnds).contains(gnode.getParent())){
            gview.setRstatus(GnodeView.GV_END);
          }
        }

        /**
         * 设置正在执行的节点状态.
         **/
        if((gnode.getId()).equals(curRunningGnode.getId())){
          gview.setRstatus(GnodeView.GV_RUNNING);
        }

        if(gnode.getUsers().size() <= 0){
          gview.getIsFront();
        }

        if((gnode != null) && (gnode.getForm() != null)){
          if((gnode.getForm().getClientType()).equals(FormClientType.FST_FRONT.getKey())){
            gview.setIsFront(true);
          }
        }
        gnodeViews.add(gview);
      }
    }
    rstatus.setDatas(gnodeViews);
    return rstatus;
  }

  /**
   * 自定义流程定位流程审核状态.
   * @param groupId 流程ID
   * @param proInsId 流程实例ID
   * @param actTaskService
   * @return ActYwRstatus
   */
  public ActYwRstatus<List<GnodeView>> queryStatusTree(String groupId, String proInsId, String grade, ActTaskService actTaskService) { ActYwRstatus<List<GnodeView>> rstatus = new ActYwRstatus<List<GnodeView>>();
    List<GnodeView> gnodeViews = Lists.newArrayList();

    /**
     *全部执行节点.
     **/
    ActYwGnode pactYwGnodeAll = new ActYwGnode();
    pactYwGnodeAll.setGroupId(groupId);
    List<ActYwGnode> actYwGnodeAll = findPreNextByGroupAndIdss(pactYwGnodeAll, true);
    List<ActYwGnode> sortlist = Lists.newArrayList();
    if(actYwGnodeAll == null){
      rstatus.setMsg("流程节点数据不存在[groupId= "+groupId+"]！");
      rstatus.setDatas(gnodeViews);
      return rstatus;
    }
    ActYwGnode.sortList(sortlist, actYwGnodeAll, ActYwGnode.getRootId(), true);
    ActYwGnode curRunningGnode = null;

    if(StringUtil.isEmpty(proInsId)){
      for (ActYwGnode gnode : sortlist) {
        gnodeViews.add(new GnodeView(gnode));
      }
      rstatus.setMsg("流程当前结点数据不存在[proInsId= "+proInsId+"]！");
      rstatus.setDatas(gnodeViews);
      return rstatus;
    }

    if(StringUtil.isNotEmpty(grade) && (grade).equals(NO_PASS)){
      curRunningGnode = getEnd(groupId);
    }else{
      /**
       * 若流程实例ID不为空，以流程实例ID获取流程节点.
       */
      if(StringUtil.isNotEmpty(proInsId)){
        curRunningGnode = actTaskService.getNodeByProInsIdByGroupId(groupId, proInsId);
      }
    }
    return dealGnodeViews(groupId, proInsId, gnodeViews, sortlist, curRunningGnode);
  }

  /**
   * 固定流程定位流程审核状态.
   * @param groupId 流程ID
   * @param gnodeId 流程节点ID
   * @return ActYwRstatus
   */
  public ActYwRstatus<List<GnodeView>> queryStatusTreeByGnode(String groupId, String gnodeId, String grade) {
    ActYwRstatus<List<GnodeView>> rstatus = new ActYwRstatus<List<GnodeView>>();
    List<GnodeView> gnodeViews = Lists.newArrayList();

    /**
     *全部执行节点.
     **/
    ActYwGnode pactYwGnodeAll = new ActYwGnode();
    pactYwGnodeAll.setGroupId(groupId);
    List<ActYwGnode> actYwGnodeAll = findPreNextByGroupAndIdss(pactYwGnodeAll, true);
    List<ActYwGnode> sortlist = Lists.newArrayList();
    if(actYwGnodeAll == null){
      rstatus.setMsg("流程节点数据不存在[groupId= "+groupId+"]！");
      rstatus.setDatas(gnodeViews);
      return rstatus;
    }
    ActYwGnode.sortList(sortlist, actYwGnodeAll, ActYwGnode.getRootId(), true);
    ActYwGnode curRunningGnode = null;

    if(StringUtil.isEmpty(gnodeId)){
      for (ActYwGnode gnode : sortlist) {
        gnodeViews.add(new GnodeView(gnode));
      }
      rstatus.setMsg("流程当前结点数据不存在[gnodeId= "+gnodeId+"]！");
      rstatus.setDatas(gnodeViews);
      return rstatus;
    }

    if(StringUtil.isNotEmpty(grade) && (grade).equals(NO_PASS)){
      curRunningGnode = getEnd(groupId);
    }else{
      /**
       * 若流程实例ID不为空，以流程实例ID获取流程节点.
       */
      if(StringUtil.isNotEmpty(gnodeId)){
        curRunningGnode = get(gnodeId);
      }
    }
    return dealGnodeViews(groupId, gnodeId, gnodeViews, sortlist, curRunningGnode);
  }

  /**
   * 根据固定流程的状态获取gnode节点。
   * @param idx 状态
   * @param groupId 流程
   * @param model
   * @param gnode 节点
   * @return
   */
  public ActYwGnode getGnodeByStatus(String idx, String groupId, Model model) {
    ActYwGnode gnode = null;
    ActYwGroup group = null;
    boolean isnodef = false;
    String gnodeId = null;
    if(StringUtil.isNotEmpty(groupId)){
      group = actYwGroupDao.get(groupId);

      if((groupId).equals(FlowYwId.FY_P.getGid())){
        ProjectStatusEnum pstatus = ProjectStatusEnum.getByValue(idx);
        if((pstatus != null)){
          isnodef = true;
          gnodeId = ProjectNodeVo.getGNodeIdByNodeId(pstatus.getGnodeId());
        }
      }else if((groupId).equals(FlowYwId.FY_G.getGid())){
        GContestStatusEnum gstatus = GContestStatusEnum.getByValue(idx);
        if((gstatus != null)){
          isnodef = true;
          gnodeId = GContestNodeVo.getGNodeIdByNodeId(gstatus.getGnodeId());
        }
      }
    }

    if((!isnodef) || StringUtil.isEmpty(gnodeId)){
      logger.warn("状态类型未定义，或数据不存在！");
    }else{
      gnode = get(gnodeId);
    }
    model.addAttribute("group", group);
    model.addAttribute("groupId", groupId);
    return gnode;
  }
  
  public List<ActYwGnode> findByPidsLike(ActYwGnode actYwGnode) {
	return dao.findByPidsLike(actYwGnode);
  }
}
