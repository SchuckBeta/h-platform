package com.oseasy.initiate.modules.actyw.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.tool.process.vo.ProjectMarkType;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.proproject.service.ProProjectService;
import org.activiti.engine.RepositoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;

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
  private ActYwService actYwService;
  @Autowired
  private ActYwGroupService actYwGroupService;
  @Autowired
  private ActYwGnodeService actYwGnodeService;
  @Autowired
  private ProjectDeclareService projectDeclareService;
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
  public String list(ActYw actYw, HttpServletRequest request, HttpServletResponse response,
      Model model) {

    Page<ActYw> page = actYwService.findPage(new Page<ActYw>(request, response), actYw);
    model.addAttribute("page", page);
    return "modules/actyw/actYwList";
  }

  @RequiresPermissions("actyw:actYw:view")
  @RequestMapping(value = "form")
  public String form(ActYw actYw, Model model) {
    List<ActYwGroup> actYwGroups = actYwGroupService.findList(new ActYwGroup());
    // List<ProjectDeclare> projectDeclares = projectDeclareService.findList(new ProjectDeclare());
    List<ProProject> proProjectList = new ArrayList<ProProject>();
    proProjectList = proProjectService.findList(new ProProject());

    model.addAttribute("projectMarks", ProjectMarkType.values());
    model.addAttribute("proProjects", proProjectList);
    model.addAttribute("actYw", actYw);
    model.addAttribute("actYwGroups", actYwGroups);
    model.addAttribute("proProjectList", proProjectList);
    return "modules/actyw/actYwForm";
  }

  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "save")
  public String save(ActYw actYw, Model model, HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    if (!beanValidator(model, actYw)) {
      return form(actYw, model);
    }
    Boolean isTrue = false;
    /*if(StringUtil.isNotEmpty(actYw.getId())){
      isTrue=actYwService.editDeployTime(actYw, repositoryService, request);
    }else {
      isTrue=actYwService.saveDeployTime(actYw, repositoryService, request);
    }*/
    isTrue=actYwService.saveDeployTime(actYw, repositoryService, request);
    if (isTrue) {
      addMessage(redirectAttributes, "保存项目流程成功");
    } else {
      addMessage(redirectAttributes, "保存项目流程失败");
    }
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
  }

  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "deploy")
  public String deploy(ActYw actYw, Model model, RedirectAttributes redirectAttributes) {
    if (actYwService.deploy(actYw, repositoryService)) {
      addMessage(redirectAttributes, "项目流程发布成功");
    } else {
      addMessage(redirectAttributes, "项目流程发布失败");
    }
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
  }

  @RequiresPermissions("actyw:actYw:edit")
  @RequestMapping(value = "delete")
  public String delete(ActYw actYw, RedirectAttributes redirectAttributes) {
    actYwService.deleteAll(actYw);
    addMessage(redirectAttributes, "删除项目流程成功");
    return "redirect:" + Global.getAdminPath() + "/actyw/actYw/?repage";
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
}