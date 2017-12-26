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
import com.hch.platform.pcore.modules.pw.entity.PwDesignerRoomAttr;
import com.hch.platform.pcore.modules.pw.service.PwDesignerRoomAttrService;

/**
 * 房间属性表Controller.
 * @author zy
 * @version 2017-12-18
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwDesignerRoomAttr")
public class PwDesignerRoomAttrController extends BaseController {

	@Autowired
	private PwDesignerRoomAttrService pwDesignerRoomAttrService;

	@ModelAttribute
	public PwDesignerRoomAttr get(@RequestParam(required=false) String id) {
		PwDesignerRoomAttr entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwDesignerRoomAttrService.get(id);
		}
		if (entity == null){
			entity = new PwDesignerRoomAttr();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwDesignerRoomAttr:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwDesignerRoomAttr pwDesignerRoomAttr, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwDesignerRoomAttr> page = pwDesignerRoomAttrService.findPage(new Page<PwDesignerRoomAttr>(request, response), pwDesignerRoomAttr);
		model.addAttribute("page", page);
		return "modules/pw/pwDesignerRoomAttrList";
	}

	@RequiresPermissions("pw:pwDesignerRoomAttr:view")
	@RequestMapping(value = "form")
	public String form(PwDesignerRoomAttr pwDesignerRoomAttr, Model model) {
		model.addAttribute("pwDesignerRoomAttr", pwDesignerRoomAttr);
		return "modules/pw/pwDesignerRoomAttrForm";
	}

	@RequiresPermissions("pw:pwDesignerRoomAttr:edit")
	@RequestMapping(value = "save")
	public String save(PwDesignerRoomAttr pwDesignerRoomAttr, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwDesignerRoomAttr)){
			return form(pwDesignerRoomAttr, model);
		}
		pwDesignerRoomAttrService.save(pwDesignerRoomAttr);
		addMessage(redirectAttributes, "保存房间属性表成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwDesignerRoomAttr/?repage";
	}

	@RequiresPermissions("pw:pwDesignerRoomAttr:edit")
	@RequestMapping(value = "delete")
	public String delete(PwDesignerRoomAttr pwDesignerRoomAttr, RedirectAttributes redirectAttributes) {
		pwDesignerRoomAttrService.delete(pwDesignerRoomAttr);
		addMessage(redirectAttributes, "删除房间属性表成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwDesignerRoomAttr/?repage";
	}

}