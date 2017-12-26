package com.hch.platform.pcore.modules.team.web;

import java.util.List;

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

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.project.vo.ProjectExpVo;
import com.hch.platform.pcore.modules.sys.entity.GContestUndergo;
import com.hch.platform.pcore.modules.sys.entity.Office;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.OfficeService;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.sysconfig.utils.SysConfigUtil;
import com.hch.platform.pcore.modules.sysconfig.vo.SysConfigVo;
import com.hch.platform.pcore.modules.sysconfig.vo.TeamConf;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.entity.TeamDetails;
import com.hch.platform.pcore.modules.team.service.TeamService;
import com.hch.platform.pcore.modules.team.vo.TeamStudentVo;
import com.hch.platform.pcore.modules.team.vo.TeamTeacherVo;

import net.sf.json.JSONObject;

/**
 * 团队管理Controller
 *
 * @author 刘波
 * @version 2017-03-30
 */
@Controller
@RequestMapping(value = "${adminPath}/team")
public class TeamController extends BaseController {
	@Autowired
	private TeamService teamService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private ProjectDeclareService projectDeclareService;

	@ModelAttribute
	public Team get(@RequestParam(required = false) String id) {
		Team entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = teamService.get(id);
		}
		if (entity == null) {
			entity = new Team();
		}
		return entity;
	}
	@RequiresPermissions("team:audit:edit")
	@RequestMapping(value = "checkTeam")
	@ResponseBody
	public JSONObject checkTeam(HttpServletRequest request) {
		String teamId=request.getParameter("teamId");
		String res=request.getParameter("res");
		return teamService.checkTeam(teamId,res);
	}
	@RequiresPermissions("team:audit:edit")
	@RequestMapping(value = "unAutoCheck")
	@ResponseBody
	public JSONObject unAutoCheck() {
		return teamService.unAutoCheck();
	}
	@RequiresPermissions("team:audit:edit")
	@RequestMapping(value = "autoCheck")
	@ResponseBody
	public JSONObject autoCheck() {
		return teamService.autoCheck();
	}

	@RequestMapping(value = { "list", "" })
	public String list(Team team, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);
		model.addAttribute("page", page);
		model.addAttribute("org", UserUtils.getOffice(request.getParameter("localCollege")));
		SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv!=null){
			TeamConf tc=scv.getTeamConf();
			if(tc!=null){
				model.addAttribute("teamCheckOnOff", tc.getTeamCheckOnOff());
			}
		}
		return "modules/team/teamList";
	}

//	@RequiresPermissions("team:team:edit")
	@RequestMapping(value = "delete")
	public String delete(Team team, RedirectAttributes redirectAttributes) {
		// 根据teamid判断团队状态
		Team teamTmp = teamService.get(team);
		if (!"2".equals(teamTmp.getState())) {// 如果不是解散状态
			addMessage(redirectAttributes, "团队未解散,不可删除");
		} else {
			team.setDelFlag("1");
			teamService.delete(team);
			addMessage(redirectAttributes, "删除团队成功");
		}
		return "redirect:" + Global.getAdminPath() + "/team/?repage";

	}

	@RequestMapping(value = "batchDelete")
	public String batchDelete(Team team, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response) {
		String idsre = request.getParameter("ids");
		String endString =teamService.batchDelete(team, idsre);
		addMessage(redirectAttributes, endString);
		return "redirect:" + Global.getAdminPath() + "/team/?repage";
	}

	@RequestMapping(value = "disTeam") // 解散
	public String disTeam(Team team, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 根据teamid判断团队状态
		Team teamTmp = teamService.get(team);
		if ("2".equals(teamTmp.getState())) {// 如果为解散状态
			addMessage(redirectAttributes, "团队已是解散状态");
			return "redirect:" + Global.getAdminPath() + "/team/?repage";
		} else {
			int projectCount = teamService.checkTeamIsInProject(team.getId());// 根据teamid查询项目是否正在进行中
			if (projectCount > 0) {
				addMessage(redirectAttributes, "该团队有未完成的项目或者大赛,不可解散");
				return "redirect:" + Global.getAdminPath() + "/team/?repage";
			}
		}
		teamService.disTeam(team);// 修改团队状态为解散状态
		addMessage(redirectAttributes, "解散团队成功");
		return "redirect:" + Global.getAdminPath() + "/team/?repage";

	}

	@RequestMapping(value = "batchDis")
	public String batchDis(Team team, RedirectAttributes redirectAttributes) {
		if (StringUtil.isNotEmpty(team.getId())) {
			teamService.batchDis(team);
			addMessage(redirectAttributes, "解散团队成功");
		} else {
			addMessage(redirectAttributes, "团队为空，解散团队失败");
		}
		return "redirect:" + Global.getAdminPath() + "/team/?repage";

	}

	@RequestMapping(value = "toTeamAudit")
	@RequiresPermissions("team:audit:edit")
	public String toTeamAudit(Model model, String id) {
		String stuType = "1";
		List<TeamDetails> teamInfo = teamService.findTeamInfo(id, stuType);// 查询学生list
		String masterType = "2";
		List<TeamDetails> teamTeacherInfo = teamService.findTeamInfo(id, masterType);// 查询导师list
		TeamDetails teamDetails = teamService.findTeamDetails(id);// 查询团队详情
		AbsUser user = systemService.getUser(teamDetails.getSponsor());
		if (user != null) {

			if (StringUtil.isNotEmpty(user.getName())) {
				teamDetails.setSponsor(user.getName());
			}
		}
		Office officeTeam = officeService.get(teamDetails.getLocalCollege());
		if (officeTeam != null) {
			teamDetails.setLocalCollege(officeTeam.getName());
			model.addAttribute("teamDetails", teamDetails);// 查询团队详情信息
		}
		model.addAttribute("teamInfo", teamInfo);
		model.addAttribute("teamTeacherInfo", teamTeacherInfo);
		return "modules/team/teamAudit";
	}
	@RequestMapping(value = "findByTeamId")
	public String teamDetails(Model model, String id) {
		String stuType = "1";
		List<TeamDetails> teamInfo = teamService.findTeamInfo(id, stuType);// 查询学生list
		String masterType = "2";
		List<TeamDetails> teamTeacherInfo = teamService.findTeamInfo(id, masterType);// 查询导师list
		TeamDetails teamDetails = teamService.findTeamDetails(id);// 查询团队详情
		AbsUser user = systemService.getUser(teamDetails.getSponsor());
		if (user != null) {

			if (StringUtil.isNotEmpty(user.getName())) {
				teamDetails.setSponsor(user.getName());
			}
		}
		Office officeTeam = officeService.get(teamDetails.getLocalCollege());
		if (officeTeam != null) {
			teamDetails.setLocalCollege(officeTeam.getName());
			model.addAttribute("teamDetails", teamDetails);// 查询团队详情信息
		}
		List<ProjectExpVo> projectExpVo=teamService.findProjectByTeamId(id);//查询项目经历
		List<GContestUndergo> gContest=teamService.findGContestByTeamId(id);//查询大赛经历

		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContestExpVo", gContest);
		model.addAttribute("teamInfo", teamInfo);
		model.addAttribute("teamTeacherInfo", teamTeacherInfo);
		return "modules/team/teamDetails_admin";
	}

	@RequestMapping(value = "findById")
	public @ResponseBody Team findById(Team team, HttpServletRequest request, HttpServletResponse response, Model model,
			String id){
		Team teamInfo = teamService.get(id);// 根据id获取单条信息
		return teamInfo;
	}

	/**
	 * 查询团队学生成员.
	 * @param teamid 团队ID
	 * @param proId 项目ID
	 * @return ActYwRstatus
	 */
  @ResponseBody
  @RequestMapping(value = "ajaxTeamStudent")
  public ActYwRstatus<List<TeamStudentVo>> ajaxTeamStudent(@RequestParam(required = true) String teamid, @RequestParam(required = false) String proId) {
    List<TeamStudentVo> list = projectDeclareService.findTeamStudentByTeamId(teamid, proId);
    if((list == null) || (list.size() <= 0)){
      return new ActYwRstatus<List<TeamStudentVo>>(true, "查询结果为空！");
    }
    return new ActYwRstatus<List<TeamStudentVo>>(true, "查询成功", list);
  }

  /**
   * 查询团队导师成员.
   * @param teamid 团队ID
   * @param proId 项目ID
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "ajaxTeamTeacher")
  public ActYwRstatus<List<TeamTeacherVo>> ajaxTeamTeacher(@RequestParam(required = true) String teamid, @RequestParam(required = false) String proId) {
    List<TeamTeacherVo> list = projectDeclareService.findTeamTeacherByTeamId(teamid, proId);
    if((list == null) || (list.size() <= 0)){
      return new ActYwRstatus<List<TeamTeacherVo>>(true, "查询结果为空！");
    }
    return new ActYwRstatus<List<TeamTeacherVo>>(true, "查询成功", list);
  }
}
