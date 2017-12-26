package com.oseasy.initiate.modules.team.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.StudentExpansionService;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.sysconfig.utils.SysConfigUtil;
import com.oseasy.initiate.modules.sysconfig.vo.ConfRange;
import com.oseasy.initiate.modules.sysconfig.vo.PersonNumConf;
import com.oseasy.initiate.modules.sysconfig.vo.SysConfigVo;
import com.oseasy.initiate.modules.sysconfig.vo.TeamConf;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamDetails;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;
import com.oseasy.initiate.modules.team.vo.TeamStudentVo;
import com.oseasy.initiate.modules.team.vo.TeamTeacherVo;

import net.sf.json.JSONObject;

/**
 * 团队管理Controller
 *
 * @author 刘波
 * @version 2017-03-30
 */
@Controller
@RequestMapping(value = "${frontPath}/team")
public class FrontTeamController extends BaseController {
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private UserService userService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private StudentExpansionService studentExpansionService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
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


	@RequestMapping(value = "form")
	public String form(Team team, Model model) {
		if (team.getId() != null) {
			team = teamService.get(team.getId());
		}
		model.addAttribute("team", team);

		return "modules/team/teamForm";
	}

	@RequiresPermissions("team:team:edit")
	@RequestMapping(value = "save")
	public String save(Team team, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, team)) {
			return form(team, model);
		}
		teamService.save(team);
		addMessage(redirectAttributes, "保存团队成功");
		return "redirect:" + Global.getAdminPath() + "/team/team/?repage";
	}

	@RequestMapping(value = "delete")
	public String delete(Team team, RedirectAttributes redirectAttributes) {
		teamService.delete(team);
		addMessage(redirectAttributes, "删除团队成功");
		return "redirect:" + Global.getFrontPath() + "/team/?repage";

	}

	@RequestMapping(value = "batchDelete")
	public String batchDelete(Team team, RedirectAttributes redirectAttributes) {
		if (StringUtil.isNotEmpty(team.getId())) {
			teamService.batchDis(team);
			addMessage(redirectAttributes, "删除团队成功");
		} else {
			addMessage(redirectAttributes, "团队为空，删除团队失败");
		}
		return "redirect:" + Global.getFrontPath() + "/team/?repage";

	}

	// 解散团队不可见
	@RequestMapping(value = "hiddenDelete")
	@ResponseBody
	public String hiddenDelete(String teamId, RedirectAttributes redirectAttributes) {
		User curUser = UserUtils.getUser();// 获取当前用户的信息
		return teamService.hiddenDelete(teamId,curUser);
	}


	@RequestMapping(value = "batchDis")
	public String batchDis(Team team, RedirectAttributes redirectAttributes) {
		teamService.batchDis(team);
		addMessage(redirectAttributes, "解散团队成功");
		return "redirect:" + Global.getFrontPath() + "/team/?repage";

	}

	// 项目负责人组建项目团队
	@RequestMapping(value = "buildTeam")
	public String buildTeam(Team team, Model model) {
		model.addAttribute("team", team);
		// 找到导师List
		User master = new User();
		master.setUserType("2");
		List<User> masterList = userService.findByType(master);
		model.addAttribute("masterList", masterList);

		// 找到学生List
		User stu = new User();
		stu.setUserType("1");
		stu.setId(UserUtils.getUser().getId());
		List<User> studentList = userService.findByType(stu);
		model.addAttribute("studentList", studentList);

		return "modules/team/buildTeam";
	}

	// 首页我的团队
	@RequestMapping(value = "indexMyTeamList")
	public String indexMyTeamList(Team team, HttpServletRequest request, HttpServletResponse response, Model model) {
		String msg = request.getParameter("msg");
		model.addAttribute("msg", msg);
		String opType = request.getParameter("opType");
		model.addAttribute("opType", opType);
		String proType = request.getParameter("proType");
		if (proType != null) {
			if (proType.equals("1") || proType.equals("2")) {
				team.setMemberNum(5);
			}
			model.addAttribute("proType", proType);
		}
		return toTeamListPage(team, request, response, model);
	}
	@RequestMapping(value = "checkTeamCreateCdn")
	@ResponseBody
	public JSONObject checkTeamCreateCdn() {
		JSONObject js =new JSONObject();
		js.put("ret", 0);
		User curUser = UserUtils.getUser();
		if (StringUtil.isEmpty(curUser.getId())) {
			js.put("msg", "会话已失效，请重新登录");
			return js;
		}
		SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if (StringUtil.isNotEmpty(curUser.getUserType()) && curUser.getUserType().equals("2")) {
			js.put("msg", "导师暂时无法创建团队");
			return js;
		}
		if (UserUtils.checkInfoPerfect(UserUtils.getUser())) {
			js.put("ret", 2);
			js.put("msg", "个人信息未完善，立即完善个人信息？");
			return js;
		}
		Date date = studentExpansionService.getByUserId(curUser.getId()).getEnterdate();
		if (date != null) {
			// 入学时间
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			String dateString = formatter.format(date);
			// 今年
			Date newDate = new Date();
			String newda = formatter.format(newDate);
			// //去年
			if (dateString.equals(newda)) {
				js.put("msg", newda + "级学生不能创建团队");
				return js;
			}
		}
		Long countValid = teamService.countBuildByUserId(curUser);
		if(countValid==null){
			countValid=0L;
		}
		Long teamMax=null;
		if(scv!=null&&"1".equals(scv.getTeamConf().getMaxOnOff())){//团队创建数量限制
			teamMax=Long.valueOf(scv.getTeamConf().getMax());
			if (countValid >=teamMax) {
				js.put("msg","你创建的团队已经达到上限，无法继续创建");
				return js;
			}
		}
		js.put("ret", 1);
		return js;
	}
	@RequestMapping(value = "indexSave")
	public String indexSave(Team team, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			String p1 = team.getName();
			Integer p4 = team.getMemberNum();
			Integer p5 = team.getSchoolTeacherNum();
			Integer p6 = team.getEnterpriseTeacherNum();
			String p7 = team.getMembership();
			String p8 = team.getProjectIntroduction();
			String p9 = team.getSummary();
			String type = request.getParameter("type");
			String proType = request.getParameter("proType");
			SysConfigVo scv=SysConfigUtil.getSysConfigVo();
			if(StringUtil.isNotEmpty(proType)){//从项目页面跳转
				PersonNumConf pc =null;
				if(StringUtil.isNotEmpty(type)&&StringUtil.isNotEmpty(proType)){
					if(scv!=null){
						pc= SysConfigUtil.getProPersonNumConf(scv, type, proType);
					}
				}
				if (pc != null) {
					// 是否校验 1是有限制 0是无限制
					if (pc!=null&&"1".equals(pc.getTeamNumOnOff())) {
						ConfRange cr = pc.getTeamNum();
						int min = Integer.valueOf(cr.getMin());
						int max = Integer.valueOf(cr.getMax());
						if (p4 < min || p4 > max) {
							model.addAttribute("message", DictUtils.getDictLabel(proType, "project_type", "")+"项目人数为" + min + "-" + max + "人!");
							model.addAttribute("opType", "2");
							return toTeamListPage(team,request, response, model);
						}
					}

				}
			}
			model.addAttribute("p1", p1);
			model.addAttribute("p4", p4);
			model.addAttribute("p5", p5);
			model.addAttribute("p6", p6);
			model.addAttribute("p7", p7);
			model.addAttribute("p8", p8);
			model.addAttribute("p9", p9);
			User curUser = UserUtils.getUser();
			if (StringUtil.isNotEmpty(curUser.getUserType()) && curUser.getUserType().equals("2")) {
				redirectAttributes.addFlashAttribute("message", "导师暂时无法创建团队!");
				redirectAttributes.addFlashAttribute("opType", "1");
				return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
				
			}
			Date date = studentExpansionService.getByUserId(curUser.getId()).getEnterdate();
			if (date != null) {
				// 入学时间
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
				String dateString = formatter.format(date);
				// 今年
				Date newDate = new Date();
				String newda = formatter.format(newDate);
				// //去年
				if (dateString.equals(newda)) {
					redirectAttributes.addFlashAttribute("message",  newda + "级学生不能创建团队!");
					redirectAttributes.addFlashAttribute("opType", "1");
					return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
				}
			} else {
				redirectAttributes.addFlashAttribute("message", "请先完善个人信息入学年份!");
				redirectAttributes.addFlashAttribute("opType", "1");
				return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
			}
			if (p4 == 0) {
				model.addAttribute("message", "要求必须有一位团队成员!");
				model.addAttribute("opType", "2");
				return toTeamListPage(team,request, response, model);
			}
			if (scv != null) {
				if ("1".equals(scv.getTeamConf().getIntramuralValiaOnOff())) {
					ConfRange cr = scv.getTeamConf().getIntramuralValia();
					int min = Integer.valueOf(cr.getMin());
					int max = Integer.valueOf(cr.getMax());
					if (p5 < min || p5 > max) {
						if(min==max){
							model.addAttribute("message", "校内导师人数为" +  min+ "人!");
						}else{
							model.addAttribute("message", "校内导师人数为" +  min + "-" + max + "人!");
						}
						model.addAttribute("opType", "2");
						return toTeamListPage(team,request, response, model);
					}
				}
				String maxms=scv.getTeamConf().getMaxMembers();
				if(StringUtil.isNotEmpty(maxms)&&p4>Integer.valueOf(maxms)){
					model.addAttribute("message", "所需组员人数不能超过" + maxms + "人!");
					model.addAttribute("opType", "2");
					return toTeamListPage(team,request, response, model);
				}
			}
			if (StringUtil.isNotBlank(p7) && p7.length() > 200) {
				model.addAttribute("message", "组员要求字数不能超过200!");
				model.addAttribute("opType", "2");
				return toTeamListPage(team,request, response, model);
			}
			if (StringUtil.isNotBlank(p8) && p8.length() > 200) {
				model.addAttribute("message", "项目简介字数不能超过200!");
				model.addAttribute("opType", "2");
				return toTeamListPage(team,request, response, model);
			}
			if (StringUtil.isNotBlank(p9) && p9.length() > 500) {
				model.addAttribute("message", "团队介绍字数不能超过500!");
				model.addAttribute("opType", "2");
				return toTeamListPage(team,request, response, model);
			}

			if (StringUtil.isNotEmpty(team.getId())) {
				int projectCount = teamService.checkTeamIsInProject(team.getId());// 根据teamid查询项目是否正在进行中
				if (projectCount > 0) {
					redirectAttributes.addFlashAttribute("message", "该团队有正在进行的项目或者大赛，不能修改团队信息!");
					redirectAttributes.addFlashAttribute("opType", "1");
					return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
				}
				teamService.editTeam(team);
				redirectAttributes.addFlashAttribute("message", "编辑团队信息成功!");
				redirectAttributes.addFlashAttribute("opType", "1");
				return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
			} else {
				Long countValid = teamService.countBuildByUserId(curUser);
				if(countValid==null){
					countValid=0L;
				}
				Long teamMax=null;
				if(scv!=null&&"1".equals(scv.getTeamConf().getMaxOnOff())){//团队创建数量限制
					teamMax=Long.valueOf(scv.getTeamConf().getMax());
					if (countValid >=teamMax) {
						redirectAttributes.addFlashAttribute("message", "你创建的团队已经达到上限，无法继续创建！");
						redirectAttributes.addFlashAttribute("opType", "1");
						return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
					}
				}
				team.setSponsor(curUser.getId());
				String officeId = curUser != null && curUser.getOffice() != null ? curUser.getOffice().getId() : null;
				team.setLocalCollege(officeId);
				team.setState("0");
				if (team.getEnterpriseTeacherNum() == null) {
					team.setEnterpriseTeacherNum(0);
				}
				teamService.saveTeam(team, curUser);
				if(teamMax!=null){
					redirectAttributes.addFlashAttribute("message", "创建团队成功,你还能再创建"+(teamMax-countValid-1)+"个团队!");
				}else{
					redirectAttributes.addFlashAttribute("message", "创建团队成功!");
				}
				redirectAttributes.addFlashAttribute("opType", "1");
				redirectAttributes.addFlashAttribute("msg", "1");
				return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
			}
		} catch (Exception e) {
			logger.error("团队保存出错",e.getMessage());
			redirectAttributes.addFlashAttribute("message", "团队保存出现系统错误");
			redirectAttributes.addFlashAttribute("opType", "1");
			return  "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList";
		}

	}

	private String toTeamListPage(Team team,HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		team.setUser(user);
		if (user != null) {
			model.addAttribute("curUserId", user.getId());
		} else {
			model.addAttribute("curUserId", null);
		}
		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);

		model.addAttribute("page", page);
		User curUser = UserUtils.getUser();
		if (curUser != null && curUser.getUserType() != null) {
			model.addAttribute("userType", curUser.getUserType());
		}
		model.addAttribute("qryForm", team);
		SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv!=null){
			TeamConf tc=scv.getTeamConf();
			if(tc!=null){
				model.addAttribute("teamCheckOnOff", tc.getTeamCheckOnOff());
			}
		}
		return "modules/team/indexTeam";
	}

	@RequestMapping(value = "findByTeamId")
	public String teamDetails(Model model, String id,HttpServletRequest request) {
		String cuid=UserUtils.getUser().getId();
		String stuType = "1";
		List<TeamDetails> teamInfo = teamService.findTeamInfo(id, stuType);// 查询学生list
		String masterType = "2";
		List<TeamDetails> teamTeacherInfo = teamService.findTeamInfo(id, masterType);// 查询导师list
		TeamDetails teamDetails = teamService.findTeamDetails(id);// 查询团队详情
		if(!teamDetails.getSponsor().equals(cuid)){
			model.addAttribute("notSponsor", true);
		}else{
			model.addAttribute("notSponsor", false);
		}
		if(teamUserRelationService.findUserHasJoinTeam(cuid,id)!=null){
			model.addAttribute("hasJoin", true);
		}else{
			model.addAttribute("hasJoin", false);
		}

		User user = systemService.getUser(teamDetails.getSponsor());
		teamDetails.setLeaderid(teamDetails.getSponsor());
		teamDetails.setSponsor(user.getName());
		Office officeTeam = officeService.get(teamDetails.getLocalCollege());
		if (officeTeam != null) {
			teamDetails.setLocalCollege(officeTeam.getName());
			model.addAttribute("teamDetails", teamDetails);// 查询团队详情信息
		}
		model.addAttribute("teamInfo", teamInfo);
		model.addAttribute("teamTeacherInfo", teamTeacherInfo);
		String from=request.getParameter("from");
		String notifyId=request.getParameter("notifyId");
		if("notify".equals(from)){
			OaNotify on=oaNotifyService.get(notifyId);
			TeamUserRelation param=new TeamUserRelation();
			param.setUser(UserUtils.getUser());
			param.setTeamId(id);
			if(on!=null&&teamUserRelationService.findUserHasJoinTeam(param)==null){
				model.addAttribute("notifyType", on.getType());
				model.addAttribute("from", from);
				model.addAttribute("notifyId", notifyId);
			}
		}
		return "modules/team/teamDetails";
	}

	@RequestMapping(value = "findById")
	@ResponseBody
	public Team findById(Team team, HttpServletRequest request, HttpServletResponse response, Model model, String id)
			throws IOException {
		Team teamInfo = teamService.get(id);// 根据id获取单条信息
		return teamInfo;

	}

	/**
	 * 申请加入
	 *
	 * @param model
	 * @param teamId
	 * @return
	 */
	@RequestMapping(value = "applyJoin")
	@ResponseBody
	public String applyJoin(HttpServletRequest request, HttpServletResponse response,
			Model model, String teamId, RedirectAttributes redirectAttributes) {
		return teamService.applyJoin(teamId);
	}

	@RequestMapping(value = "relTeam")
	public String fbTeam(Team team, HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/team/relTeam";
	}

	@RequestMapping(value = { "index" })
	public String index(User user, Model model) {
		return "modules/sys/userIndex";

	}

	// 查询团队建设人员
	@RequestMapping(value = "teambuild")
	public String teambuild(Model model, String id, HttpServletRequest request) {
		String stuType = "1";
		List<TeamDetails> teamInfo = teamService.findTeamByTeamId(id, stuType);// 查询学生list
		String masterType = "2";
		List<TeamDetails> teamTeacherInfo = teamService.findTeamByTeamId(id, masterType);// 查询导师list
		String teamId = request.getParameter("id");
		model.addAttribute("teamId", teamId);// 查询团队组员
		model.addAttribute("teamInfo", teamInfo);// 查询团队组员
		model.addAttribute("teamTeacherInfo", teamTeacherInfo);// 查询团队组员
		return "modules/team/foundTeam";
	}

	// 删除团队信息
	@RequestMapping(value = "deleteTeamUserInfo")
	public String deleteTeamUserInfo(String userId, String teamId, String turId,RedirectAttributes redirectAttributes) {
		TeamUserRelation tt=teamUserRelationService.get(turId);
		if(tt!=null){
			if("0".equals(tt.getState())||"4".equals(tt.getState())){
				int projectCount = teamService.checkTeamIsInProject(teamId);// 根据teamid查询项目是否正在进行中
				if (projectCount > 0) {
					addMessage(redirectAttributes, "删除失败,该团队有正在进行的项目或者大赛!");
					return "redirect:" + Global.getFrontPath() + "/team/teambuild?id=" + teamId;
				}
			}
			teamService.deleteTeamUserInfo(tt);
		}
		return "redirect:" + Global.getFrontPath() + "/team/teambuild?id=" + teamId;
	}

	// 同意
	@RequestMapping(value = "checkInfo")
	public String checkInfo(String userId, String teamId, TeamUserRelation teamUserRelation,
			RedirectAttributes redirectAttributes) {
		teamUserRelation.setTeamId(teamId);
		User user = new User();
		user.setId(userId);
		teamUserRelation.setUser(user);
		teamService.acceptJoinTeam(teamUserRelation);
		return "redirect:" + Global.getFrontPath() + "/team/teambuild?id=" + teamId;
	}

	@RequestMapping(value = "disTeam") // 解散disTeam
	public String disTeam(Team team, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 根据teamid判断团队状态
		Team teamTmp = teamService.get(team);
		if ("2".equals(teamTmp.getState())) {// 如果为解散状态
			addMessage(redirectAttributes, "团队已是解散状态");
			return "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList?opType=1&msg=" + 1;
		} else {
			int projectCount = teamService.checkTeamIsInProject(team.getId());// 根据teamid查询项目是否正在进行中
			if (projectCount > 0) {
				addMessage(redirectAttributes, "该团队有未完成的项目或者大赛,不可解散");
				return "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList?opType=1&msg=" + 1;
			}
			int cyjdCount = teamService.checkTeamIsInCyjd(team.getId());// 根据teamid查询项目是否正在进行中
			if (cyjdCount > 0) {
				addMessage(redirectAttributes, "该团队已入驻创业基地,不可解散");
				return "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList?opType=1&msg=" + 1;
			}
		}
		teamService.disTeam(team);// 修改团队状态为解散状态
		addMessage(redirectAttributes, "解散团队成功");
		return "redirect:" + Global.getFrontPath() + "/team/indexMyTeamList?opType=1&msg=" + 1;

	}

	// 团队组建情况拒绝申请
	@RequestMapping(value = "refuseInviation")
	public String refuseInviation(HttpServletRequest request) {
		String turId = request.getParameter("turId");
		String teamId = request.getParameter("teamId");
		teamService.refuseInviation(turId, teamId);
		return "redirect:" + Global.getFrontPath() + "/team/teambuild?id=" + teamId;
	}

	// 团队组建情况接受申请
	@RequestMapping(value = "acceptInviation")
	public String acceptInviation(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		String teamId = request.getParameter("teamId");
		String userId = request.getParameter("userId");
		JSONObject ret = teamService.acceptInviation(userId, teamId);
		if (ret != null) {
			addMessage(redirectAttributes, ret.getString("msg"));
		}
		return "redirect:" + Global.getFrontPath() + "/team/teambuild?id=" + teamId;

	}
	//从消息框接受邀请
	@RequestMapping(value="acceptInviationByNotify")
	@ResponseBody
	public JSONObject acceptInviationByNotify(HttpServletRequest request) {
		String oaNotifyId = request.getParameter("send_id");
		return teamService.acceptInviationByNotify(oaNotifyId);
	}
	//从消息框拒绝邀请
	@RequestMapping(value="refuseInviationByNotify")
	@ResponseBody
	public JSONObject refuseInviationByNotify(HttpServletRequest request) {
		String oaNotifyId = request.getParameter("send_id");
		return teamService.refuseInviationByNotify(oaNotifyId);
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
