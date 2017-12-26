package com.hch.platform.pcore.modules.sco.web;

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
import com.hch.platform.pcore.modules.sco.entity.ScoScore;
import com.hch.platform.pcore.modules.sco.service.ScoScoreService;

/**
 * 学分汇总Controller.
 * @author chenh
 * @version 2017-07-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sco/scoScore")
public class ScoScoreController extends BaseController {

	@Autowired
	private ScoScoreService scoScoreService;

	@ModelAttribute
	public ScoScore get(@RequestParam(required=false) String id) {
		ScoScore entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = scoScoreService.get(id);
		}
		if (entity == null){
			entity = new ScoScore();
		}
		return entity;
	}

  @RequiresPermissions("sco:scoScore:view")
  @RequestMapping(value = {"listGbyUser", ""})
  public String listGbyUser(ScoScore scoScore, HttpServletRequest request, HttpServletResponse response, Model model) {
    Page<ScoScore> page = scoScoreService.findPageListGbyUser(new Page<ScoScore>(request, response), scoScore);
    model.addAttribute("page", page);

    return "modules/sco/scoScoreListGbyUser";
  }

	@RequiresPermissions("sco:scoScore:view")
	@RequestMapping(value = {"list"})
	public String list(ScoScore scoScore, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ScoScore> page = scoScoreService.findPage(new Page<ScoScore>(request, response), scoScore);
		model.addAttribute("page", page);
		return "modules/sco/scoScoreList";
	}

	@RequiresPermissions("sco:scoScore:view")
	@RequestMapping(value = "form")
	public String form(ScoScore scoScore, Model model) {
		model.addAttribute("scoScore", scoScore);
		return "modules/sco/scoScoreForm";
	}

	@RequiresPermissions("sco:scoScore:edit")
	@RequestMapping(value = "save")
	public String save(ScoScore scoScore, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, scoScore)){
			return form(scoScore, model);
		}
		scoScoreService.save(scoScore);
		addMessage(redirectAttributes, "保存学分查询成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoScore/?repage";
	}

	@RequiresPermissions("sco:scoScore:edit")
	@RequestMapping(value = "delete")
	public String delete(ScoScore scoScore, RedirectAttributes redirectAttributes) {
		scoScoreService.delete(scoScore);
		addMessage(redirectAttributes, "删除学分查询成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoScore/?repage";
	}

}