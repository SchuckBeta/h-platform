package com.hch.platform.pcore.modules.pw.web;

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

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.pw.entity.PwCosMaterialUhistory;
import com.hch.platform.pcore.modules.pw.service.PwCosMaterialUhistoryService;

/**
 * 耗材使用记录Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwCosMaterialUhistory")
public class PwCosMaterialUhistoryController extends BaseController {

	@Autowired
	private PwCosMaterialUhistoryService pwCosMaterialUhistoryService;

	@ModelAttribute
	public PwCosMaterialUhistory get(@RequestParam(required=false) String id) {
		PwCosMaterialUhistory entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwCosMaterialUhistoryService.get(id);
		}
		if (entity == null){
			entity = new PwCosMaterialUhistory();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwCosMaterialUhistory:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwCosMaterialUhistory pwCosMaterialUhistory, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwCosMaterialUhistory> page = pwCosMaterialUhistoryService.findPage(new Page<PwCosMaterialUhistory>(request, response), pwCosMaterialUhistory);
		model.addAttribute("page", page);
		return "modules/pw/pwCosMaterialUhistoryList";
	}

	@RequiresPermissions("pw:pwCosMaterialUhistory:view")
	@RequestMapping(value = "form")
	public String form(PwCosMaterialUhistory pwCosMaterialUhistory, Model model) {
		model.addAttribute("pwCosMaterialUhistory", pwCosMaterialUhistory);
		return "modules/pw/pwCosMaterialUhistoryForm";
	}

	@RequiresPermissions("pw:pwCosMaterialUhistory:edit")
	@RequestMapping(value = "save")
	public String save(PwCosMaterialUhistory pwCosMaterialUhistory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwCosMaterialUhistory)){
			return form(pwCosMaterialUhistory, model);
		}
		pwCosMaterialUhistoryService.save(pwCosMaterialUhistory);
		addMessage(redirectAttributes, "保存耗材使用记录成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCosMaterialUhistory/?repage";
	}

	@RequiresPermissions("pw:pwCosMaterialUhistory:edit")
	@RequestMapping(value = "delete")
	public String delete(PwCosMaterialUhistory pwCosMaterialUhistory, RedirectAttributes redirectAttributes) {
		pwCosMaterialUhistoryService.delete(pwCosMaterialUhistory);
		addMessage(redirectAttributes, "删除耗材使用记录成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCosMaterialUhistory/?repage";
	}

}