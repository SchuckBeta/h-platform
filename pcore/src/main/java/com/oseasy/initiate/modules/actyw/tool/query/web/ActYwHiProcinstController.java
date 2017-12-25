package com.oseasy.initiate.modules.actyw.tool.query.web;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.query.entity.ActYwHiProcinst;
import com.oseasy.initiate.modules.actyw.tool.query.service.ActYwHiProcinstService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 流程历史实例Controller.
 *
 * @author chenhao
 * @version 2017-06-08
 */
@Controller
@RequestMapping(value = "${adminPath}/query/actYwHiProcinst")
public class ActYwHiProcinstController extends BaseController {

  @Autowired
  private ActYwHiProcinstService actYwHiProcinstService;

  @ModelAttribute
  public ActYwHiProcinst get(@RequestParam(required = false) String id) {
    ActYwHiProcinst entity = null;
    if (StringUtil.isNotBlank(id)) {
      entity = actYwHiProcinstService.get(id);
    }
    if (entity == null) {
      entity = new ActYwHiProcinst();
    }
    return entity;
  }

  @RequestMapping(value = { "list", "" })
  public String list(ActYwHiProcinst actYwHiProcinst, HttpServletRequest request,
      HttpServletResponse response, Model model) {
    Page<ActYwHiProcinst> page = actYwHiProcinstService
        .findPage(new Page<ActYwHiProcinst>(request, response), actYwHiProcinst);
    model.addAttribute("page", page);
    return "modules/actyw/actYwHiProcinstList";
  }
}