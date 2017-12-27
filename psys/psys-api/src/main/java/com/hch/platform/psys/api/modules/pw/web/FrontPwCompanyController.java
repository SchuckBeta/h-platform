package com.hch.platform.pcore.modules.pw.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.hch.platform.pcore.modules.pw.entity.PwCompany;
import com.hch.platform.pcore.modules.pw.entity.PwEnterRoom;
import com.hch.platform.pcore.modules.pw.service.PwCompanyService;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

/**
 * 入驻企业Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${frontPath}/pw/pwCompany")
public class FrontPwCompanyController extends BaseController {

	@Autowired
	private PwCompanyService pwCompanyService;

  /**
   * 获取入驻企业.
   * @param uid 创建人ID
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "ajaxPwCompany/{uid}")
  public ActYwRstatus<List<PwCompany>> ajaxPwCompany(@PathVariable(value = "uid") String uid) {
    PwCompany pwCompany = new PwCompany();
    pwCompany.setCreateBy(new AbsUser(uid));
    List<PwCompany> pwCompanys = pwCompanyService.findList(pwCompany);
    if((pwCompanys != null) && (pwCompanys.size() > 0)){
      return new ActYwRstatus<List<PwCompany>>(true, "查询成功！", pwCompanys);
    }else{
      return new ActYwRstatus<List<PwCompany>>(true, "查询结果为空！");
    }
  }
}