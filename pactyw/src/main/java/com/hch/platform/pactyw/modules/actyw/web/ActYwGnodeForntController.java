package com.oseasy.initiate.modules.actyw.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeView;
/**
 *前台流程节点接口控制器.
 * @author chenhao
 */
@Controller
@RequestMapping(value = "${frontPath}/actyw/actYwGnode")
public class ActYwGnodeForntController extends BaseController {
  @Autowired
  private ActTaskService actTaskService;
  @Autowired
  private ActYwGroupService actYwGroupService;
  @Autowired
  private ActYwGnodeService actYwGnodeService;

  /**
   * 根据流程实例ID定位流程跟踪(自定义的项目、大赛使用).
   * @param groupId 流程ID
   * @param proInsId 流程实例ID
   * @param model 模型
   * @param request 请求
   * @return
   */
  @RequestMapping(value = "designView")
  public String designView(@RequestParam(required=true)String groupId, @RequestParam(required=true)String proInsId, String grade, Model model, HttpServletRequest request) {
      if(StringUtil.isNotEmpty(grade)){
        model.addAttribute("grade", grade);
      }

      if(StringUtil.isNotEmpty(groupId)){
        model.addAttribute("group", actYwGroupService.get(groupId));
        model.addAttribute("groupId", groupId);
      }
      if(StringUtil.isNotEmpty(proInsId)){
        model.addAttribute("proInsId", proInsId);
      }
      model.addAttribute("faUrl", "f");
      return "modules/actyw/actYwGnodeDesignView";
  }

  /**
   * 根据状态和流程ID 定位流程跟踪(旧的项目、大赛使用).
   * @param idx 状态
   * @param groupId 流程ID
   * @param model 模型
   * @return String
   */
  @RequestMapping(value = "designView/{idx}")
  public String designView(@PathVariable String idx, @RequestParam(required=true)String groupId, String grade, Model model) {
    if(StringUtil.isNotEmpty(grade)){
      model.addAttribute("grade", grade);
    }

    model.addAttribute("gnode", actYwGnodeService.getGnodeByStatus(idx, groupId, model));
    model.addAttribute("faUrl", "f");
    return "modules/actyw/actYwGnodeDesignViewPro";
  }

  /**
   * 根据流程标识查询带状态业务节点(旧的项目、大赛使用).
   * 1、得到ActYwGnode对应的节点
   * 2、根据findListByYwGroupAndPreIdss方法找到对应的已执行的节点列表（一级节点）
   * 3、根据findListByYwGroupAndPreIdss方法找到对应的全部的节点列表（一级节点）
   * @param groupId 流程标识
   * @param gnodeId 流程节点标识
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/queryStatusTreeByGnode/{groupId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<List<GnodeView>> queryStatusTreeByGnode(@PathVariable String groupId, @RequestParam(required=true) String gnodeId, @RequestParam(required=false) String grade) {
    return actYwGnodeService.queryStatusTreeByGnode(groupId, gnodeId, grade);
  }

  /**
   * 根据流程标识查询带状态业务节点(流程设计图运行状态查看).
   * 1、根据Activity的History接口查询流程进度，获取正在执行的节点
   * 2、得到ActYwGnode对应的节点
   * 3、根据findListByYwGroupAndPreIdss方法找到对应的已执行的节点列表（一级节点）
   * 4、根据findListByYwGroupAndPreIdss方法找到对应的全部的节点列表（一级节点）
   * @param groupId 流程标识
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/queryStatusTree/{groupId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<List<GnodeView>> queryStatusTree(@PathVariable String groupId, @RequestParam(required=false) String proInsId, @RequestParam(required=false) String grade) {
    return actYwGnodeService.queryStatusTree(groupId, proInsId, grade, actTaskService);
  }
}