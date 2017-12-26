package com.oseasy.initiate.modules.actyw.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.RepositoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.act.service.ActModelService;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwGtimeService;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowProjectType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowYwId;
import com.oseasy.initiate.modules.actyw.vo.MDSvl;
import com.oseasy.initiate.modules.gcontest.vo.GContestNodeVo;
import com.oseasy.initiate.modules.project.vo.ProjectNodeVo;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.proproject.service.ProProjectService;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.tpl.vo.Rstatus;

/**
 * 项目流程关联Controller.
 *
 * @author chenhao
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYw")
public class ActYwController extends BaseController {
  @Autowired
  private RepositoryService repositoryService;
  @Autowired
  private ActModelService actModelService;

  @Autowired
  private ActYwService actYwService;
  @Autowired
  private ActYwGtimeService actYwGtimeService;
  @Autowired
  private ActYwGroupService actYwGroupService;
  @Autowired
  private ActYwGnodeService actYwGnodeService;
  @Autowired
  private SystemService systemService;
  @Autowired
  private ProProjectService proProjectService;

  @ModelAttribute
  public ActYw get(@RequestParam(required = false) String id) {
    ActYw entity = null;
    if (StringUtil.isNotBlank(id)) {
      entity = actYwService.get(id);
    }
    if (entity == null) {
      entity = new ActYw();
    }
    return entity;
  }

  @RequiresPermissions("actyw:actYw:view")
  @RequestMapping(value = { "list", "" })
  public String list(ActYw actYw, HttpServletRequest request, HttpServletResponse response, Model model) {
    FlowType flowType = null;
    FlowProjectType[] flowProjectTypes = null;
    if((actYw != null)){
      if((actYw.getGroup() != null) && StringUtil.isNotEmpty(actYw.getGroup().getFlowType())){
        flowType = FlowType.getByKey(actYw.getGroup().getFlowType());
        flowProjectTypes = flowType.getType().getTypes();
      }

      Page<ActYw> page = actYwService.findPage(new Page<ActYw>(request, response), actYw);
      model.addAttribute("page", page);
      model.addAttribute("flowProjectTypes", flowProjectTypes);
      model.addAttribute("flowType", flowType);
      model.addAttribute("actYw", actYw);
    }
    model.addAttribute("ywpId", ProjectNodeVo.YW_ID);
    model.addAttribute("ywgId", GContestNodeVo.YW_ID);
    return "modules/actyw/actYwList";
  }

  @RequiresPermissions("actyw:actYw:view")
  @RequestMapping(value = "form")
  public String form(ActYw actYw, Model model, RedirectAttributes redirectAttributes) {
    FlowType flowType = null;
    ActYw projectActYw = null;
    ActYw gcontestActYw = null;
    FlowProjectType[] flowProjectTypes = null;
    if((actYw != null) && (actYw.getGroup() != null) && StringUtil.isNotEmpty(actYw.getGroup().getFlowType())){
      flowType = FlowType.getByKey(actYw.getGroup().getFlowType());
      flowProjectTypes = flowType.getType().getTypes();
    }else{
      flowProjectTypes = FlowProjectType.values();
    }

    List<ActYwGroup> actYwGroups = null;
    if(flowType == null){
      actYwGroups = actYwGroupService.findListByDeploy();
    }else{
      if((flowType).equals(FlowType.FWT_XM)){
        projectActYw = actYwService.get(ProjectNodeVo.YW_ID);

        if(projectActYw == null){
          addMessage(redirectAttributes, "项目数据不完整！");
          if(actYw.getGroup() != null){
            return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage&group.flowType="+actYw.getGroup().getFlowType();
          }
          return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
        }
      }else if((flowType).equals(FlowType.FWT_DASAI)){
        gcontestActYw = actYwService.get(GContestNodeVo.YW_ID);

        if(gcontestActYw == null){
          addMessage(redirectAttributes, "大赛数据不完整！");
          if(actYw.getGroup() != null){
            return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage&group.flowType="+actYw.getGroup().getFlowType();
          }
          return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
        }
      }

      actYwGroups = actYwGroupService.findListByDeploy(flowType);
    }

    /**
     * 新增项目时，不能选择固定的流程.
     */
    if(actYw.getIsNewRecord()){
      List<ActYwGroup> aygroups = Lists.newArrayList();
      for (ActYwGroup aygroup : actYwGroups) {
        FlowYwId curFlowYwId = FlowYwId.getByGid(aygroup.getId());
        if (curFlowYwId == null) {
          aygroups.add(aygroup);
        }
      }
      actYwGroups = aygroups;
    }

    /**
     * 当执行修改操作时，实现图片信息回显.
     */
    if(!actYw.getIsNewRecord()){
      if((actYw.getProProject() != null) && StringUtil.isNotEmpty(actYw.getProProject().getMenuRid())){
        Menu menu = systemService.getMenuById(actYw.getProProject().getMenuRid());
        if(menu != null){
          actYw.getProProject().setImgUrl(menu.getImgUrl());
        }
      }
    }
    if(actYw.getRelId()!=null &&actYw.getGroupId()!=null){
      ActYwGtime actYwGtimeOld = new ActYwGtime();
      actYwGtimeOld.setGrounpId(actYw.getGroupId());
      actYwGtimeOld.setProjectId(actYw.getRelId());
      List<ActYwGtime> actYwGtimeList = actYwGtimeService.findList(actYwGtimeOld);
      model.addAttribute("actYwGtimeList", actYwGtimeList);
    }

    model.addAttribute("projectActYw", projectActYw);
    model.addAttribute("gcontestActYw", gcontestActYw);
    model.addAttribute("flowYwId", FlowYwId.values());
    model.addAttribute("projectMarks", FlowProjectType.values());
    model.addAttribute("actYw", actYw);
    model.addAttribute("flowProjectTypes", flowProjectTypes);
    model.addAttribute("actYwGroups", actYwGroups);
    return "modules/actyw/actYwForm";
  }

  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "save")
  //TODO
  public String save(ActYw actYw, Model model, Boolean isUpdateYw, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    if (!beanValidator(model, actYw)) {
      return form(actYw, model, redirectAttributes);
    }
    if((actYw != null)){
      if(StringUtil.isEmpty(actYw.getId())){
        actYw.setIsNewRecord(true);
      }

      if((actYw.getGroup() != null) && StringUtil.isNotEmpty(actYw.getGroupId())){
        actYw.setGroup(actYwGroupService.get(actYw.getGroupId()));
      }

      if((actYw.getGroup() != null) && StringUtil.isNotEmpty(actYw.getGroup().getFlowType())){
        FlowType flowType = FlowType.getByKey(actYw.getGroup().getFlowType());
        StringBuffer proType = new StringBuffer();
        for (FlowProjectType fptype : flowType.getType().getTypes()) {
          proType.append(fptype.getKey());
          proType.append(",");
        }
        actYw.getProProject().setProType(proType.toString());
      }

      Boolean isTrue = actYwService.saveDeployTime(actYw, repositoryService, actModelService, isUpdateYw, request);
      if (isTrue) {
        addMessage(redirectAttributes, "保存成功");
      } else {
        addMessage(redirectAttributes, "保存失败");
      }

      if((actYw.getGroup() != null) && StringUtil.isNotEmpty(actYw.getGroup().getFlowType())){
        return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage&group.flowType="+actYw.getGroup().getFlowType();
      }
    }
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
  }

  /**
   * 项目发布.
   * @param actYw 实体
   * @param model 模型
   * @param request 请求
   * @param redirectAttributes 重定向
   * @return String
   */
  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "ajaxDeploy")
  public String ajaxDeploy(ActYw actYw, Model model, Boolean isUpdateYw, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    if(StringUtil.isNotEmpty(actYw.getId()) && (actYw.getIsDeploy() != null)){
      ActYw newActYw = actYwService.get(actYw.getId());
      newActYw.setIsDeploy(actYw.getIsDeploy());
      return save(newActYw, model, isUpdateYw, request, redirectAttributes);
    }

    if(actYw.getGroup() != null){
        return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage&group.flowType="+actYw.getGroup().getFlowType();
    }
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
  }

  /**
   * 项目显示到时间轴.
   * @param actYw 实体
   * @param model 模型
   * @param request 请求
   * @param redirectAttributes 重定向
   * @return String
   */
  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "ajaxShowAxis")
  public String ajaxShowAxis(ActYw actYw, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    if(StringUtil.isNotEmpty(actYw.getId()) && (actYw.getIsShowAxis() != null)){
      ActYw newActYw = actYwService.get(actYw.getId());

      /**
       * 修改使用中的证书,如果为true则修改其他的为false.
       */
      if(actYw.getIsShowAxis()){
        ActYw queryActYw = new ActYw();
        ActYwGroup group = new ActYwGroup(newActYw.getGroupId());
        group.setType(newActYw.getGroup().getType());
        queryActYw.setGroup(group);
        ProProject pproject = newActYw.getProProject();
        ProProject proProject = new ProProject();
        proProject.setProType(pproject.getProType());
        proProject.setType(pproject.getType());
        proProject.setProCategory(pproject.getProCategory());
        queryActYw.setProProject(proProject);
        queryActYw.setIsShowAxis(true);
        List<ActYw> actYws = actYwService.findList(queryActYw);
        if((actYws != null) && (actYws.size() > 0)){
          actYwService.updateIsShowAxisPL(actYws, false);
        }
      }

      newActYw.setIsShowAxis(actYw.getIsShowAxis());
      actYwService.save(newActYw);
      addMessage(redirectAttributes, "更新成功！");
    }else{
      addMessage(redirectAttributes, "更新失败！");
    }
    if(actYw.getGroup() != null){
      return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage&group.flowType="+actYw.getGroup().getFlowType();
    }
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
  }

  /**
   * 项目属性.
   * @param actYw 实体
   * @param model 模型
   * @param request 请求
   * @return String
   */
  @ResponseBody
  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "ajaxProp")
  public Rstatus ajaxProp(ActYw actYw, Model model, HttpServletRequest request) {
    if(StringUtil.isNotEmpty(actYw.getId())){
      ActYw newActYw = get(actYw.getId());
      if(newActYw == null){
        return new Rstatus(false, "数据库不存在该项目！");
      }

      if(actYw.getProProject() == null){
        return new Rstatus(false, "数据不完整（ProProject）！");
      }

      if(StringUtil.isEmpty(actYw.getGroupId())){
        return new Rstatus(false, "关联流程不能为空！");
      }

      Rstatus rstatus = new Rstatus(true, "数据更新成功！");
      if(!(actYw.getGroupId()).equals(newActYw.getGroupId())){
        newActYw.setGroupId(actYw.getGroupId());
        newActYw.setIsDeploy(false);
        rstatus.setMsg("数据更新成功(流程已更新，需要重新发布)！");
      }

      ProProject project = newActYw.getProProject();
      ProProject pproject = actYw.getProProject();
      if((pproject.getType()).equals(MDSvl.MD_PROJECY_TYPE)){
        newActYw.setKeyType(MDSvl.MD_TPL_PREFIX);
      }else{
        newActYw.setKeyType(null);
      }
      actYwService.save(newActYw);

      project.setProjectName(pproject.getProjectName());
      project.setProType(pproject.getProType());
      project.setType(pproject.getType());
      project.setProCategorys(pproject.getProCategorys());
      project.setLevel(pproject.getLevel());
      project.setFinalStatus(pproject.getFinalStatus());
      project.setRestCategory(pproject.isRestCategory());
      project.setRestMenu(pproject.isRestMenu());
      proProjectService.save(project);
      return rstatus;
    }
    return new Rstatus(false, "唯一标识不能为空！");
  }

  /**
   * 修改属性.
   * @param actYw 项目流程
   * @param model Model
   * @param request HttpServletRequest
   * @param redirectAttributes RedirectAttributes
   * @return String
   */
  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "formProp")
  public String formProp(ActYw actYw, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    FlowType flowType = null;
    FlowYwId flowYwId = null;
    FlowProjectType fpType = null;
    if((actYw.getGroup() != null) && StringUtil.isNotEmpty(actYw.getGroup().getFlowType())){
      flowType = FlowType.getByKey(StringUtil.removeLastDotH(actYw.getGroup().getFlowType()));
      model.addAttribute("flowType", flowType);
    }

    if((actYw.getGroup() == null) || (flowType == null)){
      addMessage(redirectAttributes, "数据异常，流程不能为空！");
      if(actYw.getGroup() != null){
        return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage&group.flowType="+actYw.getGroup().getFlowType();
      }
      return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
    }

    if((actYw.getProProject() != null) && StringUtil.isNotEmpty(actYw.getProProject().getProType())){
      fpType = FlowProjectType.getByKey(StringUtil.removeLastDotH(actYw.getProProject().getProType()));
      flowYwId = FlowYwId.getByFpType(fpType);
    }

    ActYw curActYw = null;
    boolean hasBase = false;
    if((flowType).equals(FlowType.FWT_XM)){
      curActYw = actYwService.get(FlowYwId.FY_P.getId());
      hasBase = true;
    }else if((flowType).equals(FlowType.FWT_DASAI)){
      curActYw = actYwService.get(FlowYwId.FY_G.getId());
      hasBase = true;
    }

    if(hasBase && (curActYw == null)){
      addMessage(redirectAttributes, "项目或大赛数据不完整！");
      return "redirect:" + Global.getAdminPath() + "/actyw/actYw/list?repage";
    }

    List<ActYwGroup>  actYwGroups = actYwGroupService.findListByDeploy(flowType);

    /**
     * 流程是否能更改.
     *  1、发布状态不能更换流程.
     *  2、固定的项目不能更换流程
     *  3、该类型流程没有发布或发布数量小于1
     */
    if((!actYw.getIsDeploy()) && (flowYwId == null) && (actYwGroups != null) && (actYwGroups.size() > 0)){
      model.addAttribute("showFlow", true);
    }else{
      model.addAttribute("showFlow", false);
    }

    model.addAttribute("curActYw", curActYw);
    model.addAttribute("fpType", fpType);
    model.addAttribute("flowYwId", flowYwId);
    model.addAttribute("actYwGroups", actYwGroups);
    model.addAttribute("actYw", actYw);
    return "modules/actyw/actYwPropForm";
  }

  /**
   * 修改审核时间.
   * @param actYw 实体
   * @param model 模型
   * @param request 请求
   * @return Rstatus 执行结果状态
   */
  @ResponseBody
  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "ajaxGtime")
  public Rstatus ajaxGtime(ActYw actYw, Model model, HttpServletRequest request) {
    if(StringUtil.isNotEmpty(actYw.getId())){
      ActYw newActYw = get(actYw.getId());
      if(newActYw == null){
        return new Rstatus(false, "数据库不存在该项目！");
      }

      actYw.setGroupId(newActYw.getGroupId());
      actYw.setRelId(newActYw.getRelId());
      Rstatus rstatus = actYwService.addGtime2(actYw, request);
      if(rstatus.getStatus()){
        actYwService.save(actYw);

        ProProject project = newActYw.getProProject();
        if(actYw.getProProject() != null){
          ProProject pproject = actYw.getProProject();
          project.setStartDate(pproject.getStartDate());
          project.setEndDate(pproject.getEndDate());
          project.setNodeState(pproject.getNodeState());
          project.setNodeStartDate(pproject.getNodeStartDate());
          project.setNodeEndDate(pproject.getNodeEndDate());
          proProjectService.save(project);
        }
      }
      return rstatus;
    }
    return new Rstatus(false, "项目唯一标识不能为空！");
  }

  /**
   * 跳转修改审核时间页面.
   * @param actYw 项目流程
   * @param model Model
   * @param request Http请求
   * @return String
   */
  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "formGtime")
  public String formGtime(ActYw actYw, Model model, HttpServletRequest request) {
    if(StringUtil.isNotEmpty(actYw.getGroupId())){
      model.addAttribute("actYwGroup", actYwGroupService.get(actYw.getGroupId()));
    }

    if((actYw.getProProject() != null) && StringUtil.isNotEmpty(actYw.getProProject().getProType())){
      FlowProjectType fpType = FlowProjectType.getByKey(StringUtil.removeLastDotH(actYw.getProProject().getProType()));
      model.addAttribute("fpType", fpType);
    }

    if(StringUtil.isNotEmpty(actYw.getRelId()) && StringUtil.isNotEmpty(actYw.getGroupId())){
      ActYwGtime actYwGtimeOld = new ActYwGtime();
      actYwGtimeOld.setGrounpId(actYw.getGroupId());
      actYwGtimeOld.setProjectId(actYw.getRelId());
      List<ActYwGtime> actYwGtimeList=actYwGtimeService.findList(actYwGtimeOld);
      model.addAttribute("actYwGtimeList", actYwGtimeList);
    }

    model.addAttribute("actYw", actYw);
    return "modules/actyw/actYwTimeForm";
  }

  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "deploy")
  public String deploy(ActYw actYw, Model model, Boolean isUpdateYw, RedirectAttributes redirectAttributes) {
    if (actYwService.deploy(actYw, repositoryService, actModelService, isUpdateYw)) {
      addMessage(redirectAttributes, "发布成功, 部署后才能启动申报流程");
    } else {
      addMessage(redirectAttributes, "发布失败");
    }
    if(actYw.getGroup() != null){
      return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage&group.flowType="+actYw.getGroup().getFlowType();
    }
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
  }

  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "delete")
  public String delete(ActYw actYw, RedirectAttributes redirectAttributes) {
    ActYw newActYw = new ActYw();
    newActYw.setGroup(actYw.getGroup());
    actYwService.deleteAll(actYw);
    addMessage(redirectAttributes, "删除成功");
    redirectAttributes.addAttribute("actYw", newActYw);
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/list?repage&group.flowType="+newActYw.getGroup().getFlowType();
  }

  @ResponseBody
  @RequestMapping(value = "changeModel")
  public List<ActYwGnode> findTeamPerson(@RequestParam(required = true) String id) {
    /*
     * List<Map<String,String>> list=new ArrayList<Map<String,String>>(); List<Map<String,String>>
     * list1=projectDeclareService.findTeamStudent(id); List<Map<String,String>>
     * list2=projectDeclareService.findTeamTeacher(id); for(Map<String,String> map:list1) {
     * list.add(map); } for(Map<String,String> map:list2) { list.add(map); }
     */
    List<ActYwGnode> sourcelist = new  ArrayList<ActYwGnode>() ;
    if(StringUtil.isNotEmpty(id)){
      ActYwGnode actYwGnode = new ActYwGnode();
      actYwGnode.setGroupId(id);
      sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
    }
    return sourcelist;
  }

  /**
   * 根据类型获取已发布流程.
   * 如果类型为空，返回所有已发布流程.
   * @param ftype 排除的ID
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "listData")
  public List<ActYwGroup> listData(@RequestParam(required=false) String ftype, HttpServletResponse response) {
    return actYwGroupService.listData(ftype);
  }

  /**
   * 根据类型获取已发布项目流程.
   * 如果类型为空，返回所有已发布流程.
   * @param ftype 排除的ID
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "listYwData")
  public List<ActYw> listYwData(@RequestParam(required=false) String ftype, HttpServletResponse response) {
    return actYwService.findListByDeploy(ftype);
  }


  /**
   * 获取所有的项目.
   * @param type 类型
   * @param isDeploy 是否发布
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "tree")
  public List<Map<String, Object>> tree(@RequestParam(required=false) String type, @RequestParam(required=false) String proType, @RequestParam(required=false) Boolean isDeploy, HttpServletResponse response) {
    List<Map<String, Object>> mapList = Lists.newArrayList();
    ActYw pactYw = new ActYw();
    ProProject proProject = new ProProject();
    if(StringUtil.isNotEmpty(type)){
      proProject.setType(type);
    }

    if(StringUtil.isNotEmpty(proType)){
      proProject.setProType(proType);
    }

    if(StringUtil.isEmpty(type) && StringUtil.isEmpty(proType)){
      pactYw.setProProject(proProject);
    }

    if(isDeploy != null){
      pactYw.setIsDeploy(isDeploy);
    }

    List<ActYw> list = actYwService.findList(pactYw);
    List<FlowProjectType> prolist = null;
    for (FlowProjectType pro : prolist) {
      Map<String, Object> dictMap = Maps.newHashMap();
      dictMap.put("id", pro.getValue());
      dictMap.put("pId", SysIds.SYS_TREE_PROOT.getId());
      dictMap.put("name", pro.getName());
      mapList.add(dictMap);
      for (ActYw actYw : list) {
        Map<String, Object> certRelMap = Maps.newHashMap();
        ProProject curProProject = actYw.getProProject();
        if ((curProProject == null) || StringUtil.isEmpty(curProProject.getProType())) {
          continue;
        }

        if((pro.getKey()).equals(StringUtil.replace(curProProject.getProType(), StringUtil.DOTH, StringUtil.EMPTY))){
          certRelMap.put("id", actYw.getId());
          certRelMap.put("pId", pro.getValue());
          certRelMap.put("name", curProProject.getProjectName());
          mapList.add(certRelMap);
        }
      }
    }
    return mapList;
  }
}