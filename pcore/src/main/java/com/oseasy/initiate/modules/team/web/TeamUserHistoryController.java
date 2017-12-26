package com.oseasy.initiate.modules.team.web;

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
import com.oseasy.initiate.modules.team.entity.TeamUserHistory;
import com.oseasy.initiate.modules.team.service.TeamUserHistoryService;

/**
 * 团队历史纪录Controller.
 * @author chenh
 * @version 2017-11-14
 */
@Controller
@RequestMapping(value = "${adminPath}/team/teamUserHistory")
public class TeamUserHistoryController extends BaseController {

	@Autowired
	private TeamUserHistoryService teamUserHistoryService;

	@ModelAttribute
	public TeamUserHistory get(@RequestParam(required=false) String id) {
		TeamUserHistory entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = teamUserHistoryService.get(id);
		}
		if (entity == null){
			entity = new TeamUserHistory();
		}
		return entity;
	}

	@RequiresPermissions("team:teamUserHistory:view")
	@RequestMapping(value = {"list", ""})
	public String list(TeamUserHistory teamUserHistory, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TeamUserHistory> page = teamUserHistoryService.findPage(new Page<TeamUserHistory>(request, response), teamUserHistory);
		model.addAttribute("page", page);
		return "modules/team/teamUserHistoryList";
	}

	@RequiresPermissions("team:teamUserHistory:view")
	@RequestMapping(value = "form")
	public String form(TeamUserHistory teamUserHistory, Model model) {
		model.addAttribute("teamUserHistory", teamUserHistory);
		return "modules/team/teamUserHistoryForm";
	}

	@RequiresPermissions("team:teamUserHistory:edit")
	@RequestMapping(value = "save")
	public String save(TeamUserHistory teamUserHistory, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, teamUserHistory)){
			return form(teamUserHistory, model);
		}
		teamUserHistoryService.save(teamUserHistory);
		addMessage(redirectAttributes, "保存团队历史纪录成功");
		return "redirect:"+Global.getAdminPath()+"/team/teamUserHistory/?repage";
	}

	@RequiresPermissions("team:teamUserHistory:edit")
	@RequestMapping(value = "delete")
	public String delete(TeamUserHistory teamUserHistory, RedirectAttributes redirectAttributes) {
		teamUserHistoryService.delete(teamUserHistory);
		addMessage(redirectAttributes, "删除团队历史纪录成功");
		return "redirect:"+Global.getAdminPath()+"/team/teamUserHistory/?repage";
	}

}