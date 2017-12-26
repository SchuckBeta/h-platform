package com.oseasy.initiate.modules.actyw.tool.query.web;

import com.hch.platform.pcore.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.query.entity.ActYwHiTaskinst;
import com.oseasy.initiate.modules.actyw.tool.query.service.ActYwHiTaskinstService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 流程历史任务Controller.
 *
 * @author chenhao
 * @version 2017-06-08
 */
@Controller
@RequestMapping(value = "${adminPath}/query/actYwHiTaskinst")
public class ActYwHiTaskinstController extends BaseController {

  @Autowired
  private ActYwHiTaskinstService actYwHiTaskinstService;

  @ModelAttribute
  public ActYwHiTaskinst get(@RequestParam(required = false) String id) {
    ActYwHiTaskinst entity = null;
    if (StringUtil.isNotBlank(id)) {
      entity = actYwHiTaskinstService.get(id);
    }
    if (entity == null) {
      entity = new ActYwHiTaskinst();
    }
    return entity;
  }

  @RequestMapping(value = { "list", "" })
  public String list(ActYwHiTaskinst actYwHiTaskinst, HttpServletRequest request,
      HttpServletResponse response, Model model) {
    Page<ActYwHiTaskinst> page = actYwHiTaskinstService
        .findPage(new Page<ActYwHiTaskinst>(request, response), actYwHiTaskinst);
    model.addAttribute("page", page);
    return "modules/actyw/actYwHiTaskinstList";
  }
}