package com.oseasy.initiate.modules.gcontesthots.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
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
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.gcontesthots.entity.GcontestHots;
import com.oseasy.initiate.modules.gcontesthots.service.GcontestHotsKeywordService;
import com.oseasy.initiate.modules.gcontesthots.service.GcontestHotsService;
import com.oseasy.initiate.modules.interactive.util.InteractiveUtil;

/**
 * 大赛热点Controller.
 * @author 9527
 * @version 2017-07-12
 */
@Controller
public class GcontestHotsController extends BaseController {
	public static final String FRONT_URL = Global.getConfig("frontUrl");
	@Autowired
	private GcontestHotsKeywordService gcontestHotsKeywordService;
	@Autowired
	private GcontestHotsService gcontestHotsService;

	@ModelAttribute
	public GcontestHots get(@RequestParam(required=false) String id) {
		GcontestHots entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = gcontestHotsService.get(id);
		}
		if (entity == null){
			entity = new GcontestHots();
		}
		return entity;
	}

	@RequiresPermissions("gcontesthots:gcontestHots:view")
	@RequestMapping(value = {"${adminPath}/gcontesthots/list"})
	public String list(GcontestHots gcontestHots, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<GcontestHots> page = gcontestHotsService.findPage(new Page<GcontestHots>(request, response), gcontestHots);
		model.addAttribute("page", page);
		return "modules/gcontesthots/gcontestHotsList";
	}

	@RequiresPermissions("gcontesthots:gcontestHots:view")
	@RequestMapping(value = "${adminPath}/gcontesthots/form")
	public String form(GcontestHots gcontestHots, Model model) {
		if(StringUtil.isNotEmpty(gcontestHots.getId())){
			gcontestHots.setKeywords(gcontestHotsKeywordService.findListByEsid(gcontestHots.getId()));
		}
		if(StringUtil.isNotEmpty(gcontestHots.getContent())){
			gcontestHots.setContent(StringEscapeUtils.unescapeHtml4(gcontestHots.getContent()));
			gcontestHots.setContent(gcontestHots.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
		}
		model.addAttribute("gcontestHots", gcontestHots);
		model.addAttribute("front_url", FRONT_URL);
		return "modules/gcontesthots/gcontestHotsForm";
	}

	@RequiresPermissions("gcontesthots:gcontestHots:edit")
	@RequestMapping(value = "${adminPath}/gcontesthots/save")
	public String save(GcontestHots gcontestHots, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, gcontestHots)){
			return form(gcontestHots, model);
		}
		gcontestHotsService.save(gcontestHots);
		addMessage(redirectAttributes, "保存大赛热点成功");
		return "redirect:"+Global.getAdminPath()+"/gcontesthots/list?repage";
	}

	@RequiresPermissions("gcontesthots:gcontestHots:edit")
	@RequestMapping(value = "${adminPath}/gcontesthots/delete")
	public String delete(GcontestHots gcontestHots, RedirectAttributes redirectAttributes) {
		gcontestHotsService.delete(gcontestHots);
		addMessage(redirectAttributes, "删除大赛热点成功");
		return "redirect:"+Global.getAdminPath()+"/gcontesthots/list?repage";
	}
	@RequestMapping(value = "${frontPath}/gcontesthots/view")
	public String view(GcontestHots gcontestHots, Model model,HttpServletRequest request) {
		if(StringUtil.isEmpty(gcontestHots.getId())){
			gcontestHots=gcontestHotsService.getTop();
		}
		if(StringUtil.isNotEmpty(gcontestHots.getId())){
			gcontestHots.setKeywords(gcontestHotsKeywordService.findListByEsid(gcontestHots.getId()));
		}
		if(StringUtil.isNotEmpty(gcontestHots.getContent())){
			gcontestHots.setContent(StringEscapeUtils.unescapeHtml4(gcontestHots.getContent()));
			gcontestHots.setContent(gcontestHots.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
		}
		if(StringUtil.isNotEmpty(gcontestHots.getId())){
			model.addAttribute("more",gcontestHotsService.getMore(gcontestHots.getId(),gcontestHots.getKeywords()));
		}
		if(StringUtil.isNotEmpty(gcontestHots.getId())){
			InteractiveUtil.updateViews(gcontestHots.getId(), request,CacheUtils.GCONTESTHOTS_VIEWS_QUEUE);
		}
		model.addAttribute("gcontestHots", gcontestHots);
		return "modules/gcontesthots/gcontestHotsView";
	}
	@RequestMapping(value = "${frontPath}/gcontesthots/preView")
	public String preView(GcontestHots gcontestHots, Model model,HttpServletRequest request) {
		if(StringUtil.isNotEmpty(gcontestHots.getContent())){
			gcontestHots.setContent(StringEscapeUtils.unescapeHtml4(gcontestHots.getContent()));
			gcontestHots.setContent(gcontestHots.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
		}
		model.addAttribute("more",gcontestHotsService.getMore(null,gcontestHots.getKeywords()));
		model.addAttribute("gcontestHots", gcontestHots);
		return "modules/gcontesthots/gcontestHotsView";
	}
}