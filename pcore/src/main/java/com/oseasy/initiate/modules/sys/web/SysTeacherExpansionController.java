package com.oseasy.initiate.modules.sys.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.sys.entity.SysTeacherExpansion;
import com.oseasy.initiate.modules.sys.service.SysTeacherExpansionService;

/**
 * 导师扩展信息表Controller
 * @author zj
 * @version 2017-03-25
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysTeacherExpansion")
public class SysTeacherExpansionController extends BaseController {

	@Autowired
	private SysTeacherExpansionService sysTeacherExpansionService;


}