package com.oseasy.initiate.modules.attachment.web;

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
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;

/**
 * 附件信息表Controller
 * @author zy
 * @version 2017-03-23
 */
@Controller
@RequestMapping(value = "${adminPath}/attachment/sysAttachment")
public class SysAttachmentController extends BaseController {

	@Autowired
	private SysAttachmentService sysAttachmentService;
	
	@ModelAttribute
	public SysAttachment get(@RequestParam(required=false) String id) {
		SysAttachment entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = sysAttachmentService.get(id);
		}
		if (entity == null) {
			entity = new SysAttachment();
		}
		return entity;
	}
	
	@RequiresPermissions("attachment:sysAttachment:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysAttachment sysAttachment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysAttachment> page = sysAttachmentService.findPage(new Page<SysAttachment>(request, response), sysAttachment); 
		model.addAttribute("page", page);
		return "modules/attachment/sysAttachmentList";
	}

	@RequiresPermissions("attachment:sysAttachment:view")
	@RequestMapping(value = "form")
	public String form(SysAttachment sysAttachment, Model model) {
		model.addAttribute("sysAttachment", sysAttachment);
		return "modules/attachment/sysAttachmentForm";
	}

	@RequiresPermissions("attachment:sysAttachment:edit")
	@RequestMapping(value = "save")
	public String save(SysAttachment sysAttachment, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysAttachment)) {
			return form(sysAttachment, model);
		}
		sysAttachmentService.save(sysAttachment);
		addMessage(redirectAttributes, "保存附件信息表成功");
		return "redirect:"+Global.getAdminPath()+"/attachment/sysAttachment/?repage";
	}
	
	@RequiresPermissions("attachment:sysAttachment:edit")
	@RequestMapping(value = "delete")
	public String delete(SysAttachment sysAttachment, RedirectAttributes redirectAttributes) {
		sysAttachmentService.delete(sysAttachment);
		addMessage(redirectAttributes, "删除附件信息表成功");
		return "redirect:"+Global.getAdminPath()+"/attachment/sysAttachment/?repage";
	}

}