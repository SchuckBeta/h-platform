package com.oseasy.initiate.modules.cms.web.back;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oseasy.initiate.common.web.BaseController;

/**
 * 后台静态页面路径
 * @author chenhao
 */
@Controller
@RequestMapping(value = "${adminPath}")
public class PageController extends BaseController{

  /*静态页面2*/
  @RequestMapping(value = "html-{pageName}")
  public String htmlviewPages(@PathVariable String pageName, Model model) {
    return "modules/website/back/"+pageName;
  }
}
