package com.oseasy.initiate.modules.actyw.service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.service.SystemService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.dao.ActYwDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGnodeDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGroupDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGtimeDao;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwResult;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwTool;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtBounds;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtBoundsX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapes;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtLowerRight;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtLowerRightX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtModel;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtUpperLeft;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtUpperLeftX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.proproject.service.ProProjectService;

import net.sf.json.JSONObject;

/**
 * 项目流程关联Service.
 *
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwService extends CrudService<ActYwDao, ActYw> {

    protected static final Logger LOGGER = Logger.getLogger(ActYwService.class);
    @Autowired
    ActYwGroupDao actYwGroupDao;

    @Autowired
    ActYwGtimeDao actYwGtimeDao;

    @Autowired
    ActYwGnodeDao actYwGnodeDao;

    @Autowired
    private ProProjectService proProjectService;

    @Autowired
    private SystemService systemService;
  @Autowired
 	private CategoryService categoryService;

	public ActYw get(String id) {
		return super.get(id);
	}

	public List<ActYw> findList(ActYw actYw) {
		return super.findList(actYw);
	}

	public Page<ActYw> findPage(Page<ActYw> page, ActYw actYw) {
		return super.findPage(page, actYw);
	}

	@Transactional(readOnly = false)
	public void save(ActYw actYw) {
		super.save(actYw);
	}

	@Transactional(readOnly = false)
	public void saveDeploy(ActYw actYw, RepositoryService repositoryService,HttpServletRequest request) {

    if (actYw.getIsDeploy()) {
      deploy(actYw, repositoryService);
    }

    save(actYw);
    String[] gNodeId = request.getParameterValues("nodeId");
    String[] beginDate = request.getParameterValues("beginDate");
    String[] endDate = request.getParameterValues("endDate");
    String[] statuss = request.getParameterValues("status");
    if (beginDate != null && beginDate.length > 0) {
      for (int i = 0; i < beginDate.length; i++) {
        ActYwGtime actYwGtime = new ActYwGtime();
        actYwGtime.setGnodeId(actYw.getGroupId());
        actYwGtime.setProjectId(actYw.getRelId());
        actYwGtime.setGnodeId(gNodeId[i]);
        actYwGtime.setStatus(statuss[i]);
        actYwGtime.setBeginDate(DateUtil.parseDate(beginDate[i]));
        actYwGtime.setEndDate(DateUtil.parseDate(endDate[i]));
        actYwGtimeDao.insert(actYwGtime);
      }
    }
  }

  @Transactional(readOnly = false)
  public void saveDeploy(ActYw actYw, RepositoryService repositoryService) {
    if (actYw.getIsDeploy()) {
      deploy(actYw, repositoryService);
    }
    save(actYw);
  }

  @Transactional(readOnly = false)
  public Boolean editDeployTime(ActYw actYw, RepositoryService repositoryService, HttpServletRequest request) {
      if (actYw.getIsDeploy() == null) {
        return false;
      }

      ActYw actOld=get(actYw.getId());

      if (actYw.getIsDeploy()) {

        if(actYw.getGroupId()!=null && !actYw.getGroupId().equals(actOld.getGroupId())) {
            if (actOld.getGroupId() != null) {
                deleteModel(actOld);
            }
        }
        proProjectService.saveProProject(actYw, request);
        deploy(actYw, repositoryService);
      } else {
        proProjectService.save(actYw.getProProject());
      }

     // actYw.setRelId(actYw.getProProject().getId());
      save(actYw);
      return true;
  }

  @Transactional(readOnly = false)
  public Boolean saveDeployTime(ActYw actYw, RepositoryService repositoryService, HttpServletRequest request) {
      if (actYw.getIsDeploy() == null) {
        return false;
      }

      if ((actYw.getProProject() == null) || StringUtil.isEmpty(actYw.getProProject().getImgUrl())) {
        return false;
      }
      //新建
      if(actYw.getIsNewRecord()){
          //新建发布
          if (actYw.getIsDeploy()) {
              actYw.setId(IdGen.uuid());
              proProjectService.saveProProject(actYw,request);
              deploy(actYw, repositoryService);
          } else {
              proProjectService.save(actYw.getProProject());
          }
          actYw.setRelId(actYw.getProProject().getId());
          dao.insert(actYw);
      }else{
          //修改
          ActYw actOld=get(actYw.getId());
          //修改发布
          if (actYw.getIsDeploy()) {
              //修改发布 判断流程值是否相同
              if(actYw.getGroupId()!=null ){
                  if(!actYw.getGroupId().equals(actOld.getGroupId())){
                      if (actOld.getGroupId() != null) {
                          deleteModel(actOld);
                          proProjectService.changeProProjectModel(actYw,request);
                          deploy(actYw, repositoryService);
                      }else{
                          proProjectService.editProProject(actYw,request);
                      }
                  }else{
                      proProjectService.editProProject(actYw,request);
                  }
              }else{
                  proProjectService.editProProject(actYw,request);
              }
          } else {
              if(actOld.getGroupId() != null){
                  proProjectService.savedis(actYw.getProProject());
              }else{
                  proProjectService.save(actYw.getProProject());
              }
          }
          save(actYw);
      }
      return true;
  }

  @Transactional(readOnly = false)
  public void delete(ActYw actYw) {
    super.delete(actYw);
  }

  @Transactional(readOnly = false)
   public void deleteAll(ActYw actYw) {
    ProProject proProject =actYw.getProProject();
    Menu menu =systemService.getMenu(proProject.getMenuRid());
  //删除菜单
    if(menu!=null){
      systemService.deleteMenu(menu);
    }

  //删除栏目
    Category category= categoryService.get(proProject.getCategoryRid());
    if(category!=null){
      categoryService.delete(category);
    }

  //删除项目
    proProjectService.delete(proProject);
     super.delete(actYw);
   }

    @Transactional(readOnly = false)
    public void deleteModel(ActYw actYw) {
      ProProject proProject =actYw.getProProject();
         Menu menu =systemService.getMenu(proProject.getMenuRid());
       //删除菜单
         if(menu!=null){
           systemService.deleteMenu(menu);
         }

       //删除栏目
         Category category= categoryService.get(proProject.getCategoryRid());
         if(category!=null){
           categoryService.delete(category);
         }
    }
  /**
   * 发布项目流程.
   * 以流程标识和项目标识生成流程模板标识和版本（防止多项目共用一个流程是出现菜单、栏目重合）
   * @author chenhao
   * @param actYw
   *          项目流程
   * @return Boolean
   */
  @Transactional(readOnly = false)
  public Boolean deploy(ActYw actYw, RepositoryService repositoryService) {
    try {
      if ((actYw == null) || StringUtil.isEmpty(actYw.getId())) {
        return false;
      }

      ActYw actYwNew = super.get(actYw);
      if (actYwNew == null) {
        return false;
      }

      actYw.setGroup(actYwNew.getGroup());
      ActYwGroup actYwGroup = actYw.getGroup();
      if (actYwGroup == null) {
        return false;
      }

      ProProject proProject = actYw.getProProject();
      if (proProject == null) {
        return false;
      }

      if(StringUtil.isEmpty(actYwGroup.getKeyss()) || StringUtil.isEmpty(proProject.getProjectMark())){
        return false;
      }

      List<ActYwGnode> actYwGnodes = actYwGnodeDao.findList(new ActYwGnode(actYwGroup));

      RtModel rtModel = new RtModel(actYwGroup.getName(), actYwGroup.getKeyss()+"-"+proProject.getProjectMark(),
          actYwGroup.getRemarks(), actYwGroup.getType(), null, null);
      org.activiti.engine.repository.Model modelData = ActYwTool.genModelData(rtModel,
          repositoryService);
      repositoryService.saveModel(modelData);

      // String jsonXmlStr = genJsonByActYwGnodes(actYwGroup, actYwGnodes, rtModel);
      Model repModel = repositoryService.getModel(modelData.getId());
      // String jsonXmlStr =
      // "{\"resourceId\":\"4d32348b796e494299c37ddf342c5d5b\",\"properties\":{\"process_id\":\"process110\",\"name\":\"大赛流程110\",\"documentation\":\"\",\"process_author\":\"\",\"process_version\":\"\",\"process_namespace\":\"http://www.activiti.org/processdef\",\"executionlisteners\":\"{\\\"executionListeners\\\":\\\"[]\\\"}\",\"eventlisteners\":\"{\\\"eventListeners\\\":\\\"[]\\\"}\",\"signaldefinitions\":\"\\\"[]\\\"\",\"messagedefinitions\":\"\\\"[]\\\"\",\"messages\":[]},\"stencil\":{\"id\":\"BPMNDiagram\"},\"childShapes\":[{\"resourceId\":\"sid-E76260EF-151A-49A4-B235-825E54BAFF5B\",\"properties\":{\"overrideid\":\"sid-E76260EF-151A-49A4-B235-825E54BAFF5B\",\"name\":\"学生（项目负责人）\",\"documentation\":\"\",\"executionlisteners\":\"\",\"initiator\":\"\",\"formkeydefinition\":\"\",\"formproperties\":\"\"},\"stencil\":{\"id\":\"StartNoneEvent\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-FDE6ECD4-B683-434B-BC05-3BAF38B80B36\"}],\"bounds\":{\"lowerRight\":{\"x\":174.3333282470703,\"y\":115},\"upperLeft\":{\"x\":144.3333282470703,\"y\":85}},\"dockers\":[]},{\"resourceId\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\",\"properties\":{\"overrideid\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\",\"name\":\"学院审核（教学秘书）\",\"documentation\":\"\",\"asynchronousdefinition\":false,\"exclusivedefinition\":true,\"executionlisteners\":{\"executionListeners\":[]},\"multiinstance_type\":\"None\",\"multiinstance_cardinality\":\"\",\"multiinstance_collection\":\"\",\"multiinstance_variable\":\"\",\"multiinstance_condition\":\"\",\"isforcompensation\":\"false\",\"usertaskassignment\":\"\",\"formkeydefinition\":\"\",\"duedatedefinition\":\"\",\"prioritydefinition\":\"\",\"formproperties\":\"\",\"tasklisteners\":{\"taskListeners\":[]}},\"stencil\":{\"id\":\"UserTask\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-85C8FBB6-4298-4974-B2C6-F233D7543396\"}],\"bounds\":{\"lowerRight\":{\"x\":400,\"y\":140},\"upperLeft\":{\"x\":300,\"y\":60}},\"dockers\":[]},{\"resourceId\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\",\"properties\":{\"overrideid\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\",\"name\":\"审核结束（项目评级）\",\"documentation\":\"\",\"executionlisteners\":\"\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":645.3333282470703,\"y\":328},\"upperLeft\":{\"x\":617.3333282470703,\"y\":300}},\"dockers\":[]},{\"resourceId\":\"sid-FDE6ECD4-B683-434B-BC05-3BAF38B80B36\",\"properties\":{\"overrideid\":\"sid-FDE6ECD4-B683-434B-BC05-3BAF38B80B36\",\"name\":\"提交审核\",\"documentation\":\"\",\"conditionsequenceflow\":\"\",\"executionlisteners\":\"\",\"defaultflow\":\"false\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\"}],\"bounds\":{\"lowerRight\":{\"x\":299.8437486886978,\"y\":100},\"upperLeft\":{\"x\":175.22916197776794,\"y\":100}},\"dockers\":[{\"x\":15,\"y\":15},{\"x\":50,\"y\":40}],\"target\":{\"resourceId\":\"sid-35EB7BDF-E1A5-44F9-B00B-5F975A497D46\"}},{\"resourceId\":\"sid-85C8FBB6-4298-4974-B2C6-F233D7543396\",\"properties\":{\"overrideid\":\"sid-85C8FBB6-4298-4974-B2C6-F233D7543396\",\"name\":\"提交给学校\",\"documentation\":\"\",\"conditionsequenceflow\":\"\",\"executionlisteners\":\"\",\"defaultflow\":\"false\",\"showdiamondmarker\":false},\"stencil\":{\"id\":\"SequenceFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\"}],\"bounds\":{\"lowerRight\":{\"x\":618.9164207579244,\"y\":306.21570089208706},\"upperLeft\":{\"x\":400.5513526795757,\"y\":138.76476785791297}},\"dockers\":[{\"x\":50,\"y\":40},{\"x\":22.166671752929688,\"y\":22}],\"target\":{\"resourceId\":\"sid-6D43F4CC-A9EE-46FB-B40D-5765FF4E1D17\"}}],\"bounds\":{\"lowerRight\":{\"x\":1200,\"y\":1050},\"upperLeft\":{\"x\":0,\"y\":0}},\"stencilset\":{\"url\":\"stencilsets/bpmn2.0/bpmn2.0.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"ssextensions\":[]}";
      // rtModel.setJsonXml(jsonXmlStr);
      // rtModel.setJsonXml(genJsonByActYwGnodes(actYwGroup, actYwGnodes, rtModel));
      rtModel.setJsonXml(genJsonBySubProcessActYwGnodes(actYwGroup, actYwGnodes, rtModel));
      repositoryService.addModelEditorSource(repModel.getId(),
          rtModel.getJsonXml().getBytes(RtSvl.RtModelVal.UTF_8));

      return true;
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("不支持编码格式", e);
      return false;
    }
  }

  /**
   * 根据流程生成json .
   *
   * @author chenhao
   * @param actYwGroup
   *          流程对象
   * @param actYwGnodes
   *          流程节点对象
   * @param rtModel
   *          模型
   * @return String
   */
  @Transactional(readOnly = false)
  private String genJsonByActYwGnodes(ActYwGroup actYwGroup, List<ActYwGnode> actYwGnodes,
      RtModel rtModel) {
    ActYwResult rt = ActYwTool.initRtMin(new ActYwResult(),
        RtSvl.RtModelVal.M_PREFIX + IdGen.uuid(), rtModel.getKey(), rtModel.getName(),
        actYwGroup.getAuthor(), actYwGroup.getVersion());
    for (int i = 0; i < actYwGnodes.size(); i++) {
      ActYwGnode actYwGnode = actYwGnodes.get(i);
      ActYwNode node = actYwGnode.getNode();
      ActYwGnode preGnode = actYwGnode.getPreGnode();
      ActYwGnode nextGnode = actYwGnode.getNextGnode();
      if ((node != null) && node.getIsFlow()) {
        StenType stenType = StenType.getByKey(node.getNodeKey());
        if (stenType != null) {
          ActYwTool.addRtChildShapes(rt, addRtcsNode(rt, i, node));
        } else {
          LOGGER.warn("流程节点node[" + node.getName() + "]的流程为空！");
        }
      }
    }
    System.out.println(JSONObject.fromObject(rt).toString());
    return JSONObject.fromObject(rt).toString();
  }

  /**
   * 根据流程生成json .
   *
   * @author chenhao
   * @param actYwGroup
   *          流程对象
   * @param actYwGnodes
   *          流程节点对象
   * @param rtModel
   *          模型
   * @return String
   */
  @Transactional(readOnly = false)
  private String genJsonBySubProcessActYwGnodes(ActYwGroup actYwGroup, List<ActYwGnode> actYwGnodes, RtModel rtModel) {
    ActYwResult rt = ActYwTool.initRtMin(new ActYwResult(),
        RtSvl.RtModelVal.M_PREFIX + IdGen.uuid(), rtModel.getKey(), rtModel.getName(),
        actYwGroup.getAuthor(), actYwGroup.getVersion());

    /**
     * 过滤出一级节点和二级节点.
     */
    List<ActYwGnode> actYwGnodeProcess = Lists.newArrayList();
    List<ActYwGnode> actYwGnodePsubs = Lists.newArrayList();
    for (ActYwGnode actYgnode : actYwGnodes) {
      GnodeType gnodeType = GnodeType.getById(actYgnode.getType());
      if ((gnodeType).equals(GnodeType.GT_ROOT_START) || (gnodeType).equals(GnodeType.GT_ROOT_FLOW)
          || (gnodeType).equals(GnodeType.GT_ROOT_END)
          || (gnodeType).equals(GnodeType.GT_PROCESS)) {
        actYwGnodeProcess.add(actYgnode);
      } else if ((gnodeType).equals(GnodeType.GT_PROCESS_START)
          || (gnodeType).equals(GnodeType.GT_PROCESS_FLOW)
          || (gnodeType).equals(GnodeType.GT_PROCESS_GATEWAY)
          || (gnodeType).equals(GnodeType.GT_PROCESS_END)
          || (gnodeType).equals(GnodeType.GT_PROCESS_TASK)) {
        actYwGnodePsubs.add(actYgnode);
      }
    }

    /**
     * 节点序号最大值.
     */
    int count = 0;
    /**
     * 当前前一个业务节点.
     */
    RtChildShapes curRtCspPre = null;
    RtChildShapes curRtSubCspPre = null;
    /**
     * 当前父级节点.
     */
    RtChildShapes curRtCspParent = null;
    for (int i = 0; i < actYwGnodeProcess.size(); i++) {
      ActYwGnode actYwGnode = actYwGnodeProcess.get(i);
      ActYwNode node = actYwGnode.getNode();
      if ((node != null) && node.getIsFlow()) {
        if ((GnodeType.getById(actYwGnode.getType())).equals(GnodeType.GT_PROCESS)) {
          List<RtChildShapes> childShapes =Lists.newArrayList();

          /**
           * 获取子节点数量.
           */
          List<ActYwGnode> curChildGnode =Lists.newArrayList();
          for (int j = 0; j < actYwGnodePsubs.size(); j++) {
            ActYwGnode subActYwGnode = actYwGnodePsubs.get(j);
            ActYwNode subNode = subActYwGnode.getNode();

            if ((subActYwGnode.getParentId()).equals(actYwGnode.getId())) {
              if ((subNode != null) && subNode.getIsFlow()) {
                curChildGnode.add(subActYwGnode);
              }
            }
          }

          RtChildShapes subChildShapes = addRtcsNode(rt, i, curChildGnode.size(), node, curRtCspPre, null);
          curRtCspParent = subChildShapes;
          curRtSubCspPre = subChildShapes;
          count++;
          for (int j = 0; j < actYwGnodePsubs.size(); j++) {
            ActYwGnode subActYwGnode = actYwGnodePsubs.get(j);
            ActYwNode subNode = subActYwGnode.getNode();

            if ((subActYwGnode.getParentId()).equals(actYwGnode.getId()) && ((subNode != null) && subNode.getIsFlow())) {
              RtChildShapes subCshapes = addRtcsNode(rt, count, 0, subNode, curRtSubCspPre, curRtCspParent);
              childShapes.add(subCshapes);
              curRtSubCspPre = subCshapes;
              count++;
            }
          }

          subChildShapes.setChildShapes(childShapes);
          ActYwTool.addRtChildShapes(rt, subChildShapes);
          curRtCspPre = subChildShapes;
          curRtCspParent = null;
        } else {
          RtChildShapes rtCsp = addRtcsNode(rt, count, 0, node, curRtCspPre, null);
          ActYwTool.addRtChildShapes(rt, rtCsp);
          curRtCspPre = rtCsp;
          count++;
        }
      }
    }
    System.out.println(JSONObject.fromObject(rt).toString());
    return JSONObject.fromObject(rt).toString();
  }

  /**
   * 构建流程单个节点.
   *
   * @author chenhao
   * @param rt
   *          结果集对象
   * @param i
   *          序号
   * @param node
   *          当前结点
   * @return RtChildShapes
   */
  private RtChildShapes addRtcsNode(ActYwResult rt, int i, int childSize, ActYwNode node, RtChildShapes preCshap, RtChildShapes parentCshap) {
    List<Double[]> doSxys = Lists.newArrayList();
    RtChildShapes rtcs = new RtChildShapes();

    RtBounds rtb = rt.getBounds();
    RtUpperLeft rtbul = rtb.getUpperLeft();
    RtLowerRight rtblr = rtb.getLowerRight();

    Double maxx = (double) rtblr.getX();
    Double centerx = (double) ((rtblr.getX() - rtbul.getX()) / 2);
    Double zeroX = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
//    doSxys = sortPostionOrientation(i, childSize, node, preCshap, parentCshap, zeroX, maxx);
    doSxys = sortPostionPortrait(i, childSize, node, preCshap, parentCshap, zeroX);

    return ActYwTool.initRtChildShapesByName(rt, rtcs,
        RtSvl.RtModelVal.M_PREFIX + IdGen.uuid(), RtSvl.RtModelVal.M_PREFIX + IdGen.uuid(),
        node.getName(), StenType.getByKey(node.getNodeKey()), (i + 1),
        new String[] { RtSvl.RtModelVal.M_PREFIX + IdGen.uuid(),
            RtSvl.RtModelVal.M_PREFIX + IdGen.uuid() },
        RtSvl.RtModelVal.M_PREFIX + IdGen.uuid(), doSxys);
  }

  /**
   * 横向定位排序展示流程节点.
   * @param i
   * @param childSize
   * @param node
   * @param preCshap
   * @param parentCshap
   * @param zeroX
   * @return
   */
  private List<Double[]> sortPostionOrientation(int i, int childSize, ActYwNode node, RtChildShapes preCshap, RtChildShapes parentCshap, Double zeroX, Double maxx) {
    Double ulx, uly, lrx, lry;
    List<Double[]> doSxys;
    /**
     * 若前置节点为空且不是第一个节点！说明参数有误返回空.
     */
    if(preCshap == null){
      if(i == 0){
        ulx = zeroX;
        uly = RtSvl.RtBoundsVal.RT_ZERO;

        lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH;
        lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT;
      }else{
        return null;
      }
    }else{
      StenType stenType = StenType.getByKey(node.getNodeKey());

      if(parentCshap == null){
        //初始化一级节点
        RtBoundsX preBsx = preCshap.getBounds();
        RtUpperLeftX preUl = preBsx.getUpperLeft();
        RtLowerRightX preLr = preBsx.getLowerRight();

        Double preCenterX = (preUl.getX() + preLr.getX())/2;
        Double preCenterY = (preUl.getY() + preLr.getY())/2;

        if(stenType.equals(StenType.ST_JG_SUB_PROCESS)) {
          ulx = RtSvl.RtBoundsVal.RT_ZERO;
          uly = preCenterY + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
          lrx = maxx - RtSvl.RtBoundsVal.RT_ZERO;
          lry = uly + ((childSize - 1) * (RtSvl.RtBoundsVal.RT_RATE * RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2 + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 4)) + RtSvl.RtBoundsVal.RT_ZERO;
        } else if(stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
          ulx = preCenterX;
          uly = preLr.getY();
          lrx = ulx;
          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
          if(i == 1){
            uly = preCenterY - RtSvl.RtBoundsVal.RT_ZERO;
          }
        } else if(stenType.equals(StenType.ST_END_EVENT_NONE)) {
          ulx = preCenterX;
          uly = preLr.getY();
          lrx = ulx;
          lry = uly;
        } else {
          ulx = preCenterX;
          uly = preCenterY;
          lrx = ulx;
          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
        }
      }else{
        //初始化子级节点
        RtBoundsX preBsx = preCshap.getBounds();
        RtBoundsX parentBsx = parentCshap.getBounds();

        RtUpperLeftX preUl = preBsx.getUpperLeft();
        RtLowerRightX preLr = preBsx.getLowerRight();
        RtUpperLeftX parentUl = parentBsx.getUpperLeft();
        RtLowerRightX parentLr = parentBsx.getLowerRight();

        Double preCenterX = (preUl.getX() + preLr.getX())/2;
        Double preCenterY = (preUl.getY() + preLr.getY())/2;
        Double parentCenterX = (parentUl.getX() + parentLr.getX())/2;
        Double parentCenterY = (parentUl.getY() + parentLr.getY())/2;
        Double parentZeroX = parentUl.getX();
        Double parentZeroY = parentUl.getY();

        if(stenType.equals(StenType.ST_START_EVENT_NONE)) {
          ulx = RtSvl.RtBoundsVal.RT_ZERO;
          uly = preCenterY - parentZeroY;
          lrx = ulx;
          lry = uly;
        } else if(stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
//          ulx = RtSvl.RtBoundsVal.RT_ZERO+RtSvl.RtBoundsVal.RT_ZERO;
//          uly = preCenterY - parentZeroY;
//          lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH * RtSvl.RtBoundsVal.RT_RATE;
//          lry = uly;

//          ulx = zeroX - 100;
//          uly = parentZeroY + preLr.getY();
//          lrx = ulx + 150;
//          lry = uly + 100;

          ulx = 253.07421154834836;
          uly = 374.26890023433293;
          lrx = 91.85938220165163;
          lry = 188.9107872656671;
//          lrx = ulx + 150.0;
//          lry = uly;
//          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;

//          ulx = zeroX + preCenterX;
//          uly = parentZeroY + preLr.getY();
//          lrx = ulx;
//          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;

        } else if(stenType.equals(StenType.ST_END_EVENT_NONE)) {
          ulx = preCenterX - parentZeroX;
          uly = preLr.getY() - parentZeroY;
          lrx = ulx;
          lry = uly;
        } else {
          ulx = preCenterX - parentZeroX + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 8;
          uly = preLr.getY() - parentZeroY;
          lrx = ulx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 4;
          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
        }
      }
    }

    doSxys = Lists.newArrayList(Arrays.asList(new Double[][] { { lrx, lry }, { ulx, uly } }));
    LOGGER.info(node.getName() + "--> lrx=" + i + "-" + node.getName() + "--> lrx=" + lrx + " lry=" + lry + " ulx=" + ulx + " uly=" + uly);
    return doSxys;
  }

  /**
   * 纵向定位排序展示流程节点.
   * @param i
   * @param childSize
   * @param node
   * @param preCshap
   * @param parentCshap
   * @param zeroX
   * @return
   */
  private List<Double[]> sortPostionPortrait(int i, int childSize, ActYwNode node, RtChildShapes preCshap, RtChildShapes parentCshap, Double zeroX) {
    Double ulx, uly, lrx, lry;
    List<Double[]> doSxys;
    /**
     * 若前置节点为空且不是第一个节点！说明参数有误返回空.
     */
    if(preCshap == null){
      if(i == 0){
        ulx = zeroX;
        uly = RtSvl.RtBoundsVal.RT_ZERO;

        lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH;
        lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT;
      }else{
        return null;
      }
    }else{
      StenType stenType = StenType.getByKey(node.getNodeKey());

      if(parentCshap == null){
        //初始化一级节点
        RtBoundsX preBsx = preCshap.getBounds();
        RtUpperLeftX preUl = preBsx.getUpperLeft();
        RtLowerRightX preLr = preBsx.getLowerRight();

        Double preCenterX = (preUl.getX() + preLr.getX())/2;
        Double preCenterY = (preUl.getY() + preLr.getY())/2;

        if(stenType.equals(StenType.ST_JG_SUB_PROCESS)) {
          ulx = preCenterX - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2;
          uly = preCenterY + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
          lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH;
          lry = uly + ((childSize - 1) * (RtSvl.RtBoundsVal.RT_RATE * RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2 + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 4)) + RtSvl.RtBoundsVal.RT_ZERO;
        } else if(stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
          ulx = preCenterX;
          uly = preLr.getY();
          lrx = ulx;
          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
          if(i == 1){
            uly = preCenterY - RtSvl.RtBoundsVal.RT_ZERO;
          }
        } else if(stenType.equals(StenType.ST_END_EVENT_NONE)) {
          ulx = preCenterX;
          uly = preLr.getY();
          lrx = ulx;
          lry = uly;
        } else {
          ulx = preCenterX;
          uly = preCenterY;
          lrx = ulx;
          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
        }
      }else{
        //初始化子级节点
        RtBoundsX preBsx = preCshap.getBounds();
        RtBoundsX parentBsx = parentCshap.getBounds();

        RtUpperLeftX preUl = preBsx.getUpperLeft();
        RtLowerRightX preLr = preBsx.getLowerRight();
        RtUpperLeftX parentUl = parentBsx.getUpperLeft();
        RtLowerRightX parentLr = parentBsx.getLowerRight();

        Double preCenterX = (preUl.getX() + preLr.getX())/2;
        Double preCenterY = (preUl.getY() + preLr.getY())/2;
        Double parentCenterX = (parentUl.getX() + parentLr.getX())/2;
        Double parentCenterY = (parentUl.getY() + parentLr.getY())/2;
        Double parentZeroX = parentUl.getX();
        Double parentZeroY = parentUl.getY();

        if(stenType.equals(StenType.ST_START_EVENT_NONE)) {
          ulx = parentCenterX - parentZeroX;
          uly = RtSvl.RtBoundsVal.RT_ZERO;
          lrx = ulx;
          lry = uly;
        } else if(stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
          ulx = zeroX + preCenterX;
          uly = parentZeroY + preLr.getY();
          lrx = ulx;
          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
        } else if(stenType.equals(StenType.ST_END_EVENT_NONE)) {
          ulx = preCenterX - parentZeroX;
          uly = preLr.getY() - parentZeroY;
          lrx = ulx;
          lry = uly;
        } else {
          ulx = preCenterX - parentZeroX + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 8;
          uly = preLr.getY() - parentZeroY;
          lrx = ulx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 4;
          lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
        }
      }
    }

    doSxys = Lists.newArrayList(Arrays.asList(new Double[][] { { lrx, lry }, { ulx, uly } }));
    LOGGER.info(node.getName() + "--> lrx=" + i + "-" + node.getName() + "--> lrx=" + lrx + " lry=" + lry + " ulx=" + ulx + " uly=" + uly);
    return doSxys;
  }

  private RtChildShapes addRtcsNode(ActYwResult rt, int i, ActYwNode node) {
    return addRtcsNode(rt, i, 0, node, null, null);
  }
}