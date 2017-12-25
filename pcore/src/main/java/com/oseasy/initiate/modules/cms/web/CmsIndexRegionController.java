package com.oseasy.initiate.modules.cms.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.oseasy.initiate.modules.cms.entity.CmsIndexRegion;
import com.oseasy.initiate.modules.cms.service.CmsIndexRegionService;

/**
 * 首页区域管理Controller
 * @author daichanggeng
 * @version 2017-04-06
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/cmsIndexRegion")
public class CmsIndexRegionController extends BaseController {

	@Autowired
	private CmsIndexRegionService cmsIndexRegionService;

	@ModelAttribute
	public CmsIndexRegion get(@RequestParam(required=false) String id) {
		CmsIndexRegion entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = cmsIndexRegionService.get(id);
		}
		if (entity == null) {
			entity = new CmsIndexRegion();
		}
		return entity;
	}

	@RequiresPermissions("cms:cmsIndexRegion:view")
	@RequestMapping(value = {"list", ""})
	public String list(CmsIndexRegion cmsIndexRegion, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CmsIndexRegion> page = cmsIndexRegionService.findPage(new Page<CmsIndexRegion>(request, response), cmsIndexRegion);
		model.addAttribute("page", page);
		return "modules/cms/cmsIndexRegionList";
	}

	@RequiresPermissions("cms:cmsIndexRegion:view")
	@RequestMapping(value = "form")
	public String form(CmsIndexRegion cmsIndexRegion, Model model) {
		model.addAttribute("cmsIndexRegion", cmsIndexRegion);
		return "modules/cms/cmsIndexRegionForm";
	}

	@RequiresPermissions("cms:cmsIndexRegion:edit")
	@RequestMapping(value = "save")
	public String save(CmsIndexRegion cmsIndexRegion, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, cmsIndexRegion)) {
			return form(cmsIndexRegion, model);
		}
		cmsIndexRegionService.save(cmsIndexRegion);
		addMessage(redirectAttributes, "保存首页区域成功");
		return "redirect:"+Global.getAdminPath()+"/cms/cmsIndexRegion/?repage";
	}

	@RequiresPermissions("cms:cmsIndexRegion:edit")
	@RequestMapping(value = "delete")
	public String delete(CmsIndexRegion cmsIndexRegion, RedirectAttributes redirectAttributes) {
		cmsIndexRegionService.delete(cmsIndexRegion);
		addMessage(redirectAttributes, "删除首页区域成功");
		return "redirect:"+Global.getAdminPath()+"/cms/cmsIndexRegion/?repage";
	}

	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(CmsIndexRegion cmsIndexRegion, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		return cmsIndexRegionService.getRegionTrees(cmsIndexRegion, extId);
	}
}