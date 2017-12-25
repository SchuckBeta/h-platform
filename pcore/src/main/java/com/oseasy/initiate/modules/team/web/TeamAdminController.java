package com.oseasy.initiate.modules.team.web;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.gcontest.service.GContestService;
import org.apache.http.impl.io.HttpRequestWriter;
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
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.entity.UserInfo;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamDetails;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

import groovy.xml.StreamingSAXBuilder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.groovy.GJson;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 团队管理Controller
 * @author 刘波
 * @version 2017-03-30
 */
@Controller
@RequestMapping(value = "${adminPath}/team")
public class TeamAdminController extends BaseController {
	@Autowired
	private TeamService teamService;
	@Autowired
	private UserService userService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private GContestService gContestService;
	@Autowired
	private SystemService systemService;


	@ModelAttribute
	public Team get(@RequestParam(required=false) String id) {
		Team entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = teamService.get(id);
		}
		if (entity == null) {
			entity = new Team();
		}
		return entity;
	}

	//@RequiresPermissions("team:team:view")
	@RequestMapping(value = {"list", ""})
	public String list(Team team, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Team> teamList=null;
		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);
		User user = UserUtils.getUser();
		if (user!=null) {
			model.addAttribute("curUserId", user.getId());
		}else{
			model.addAttribute("curUserId", null);
		}
		if (page!=null) {
			teamList = page.getList();
			if (teamList!=null) {
				for(Team tt:teamList) {
					if (StringUtil.isNotBlank(tt.getSponsor())) {
						User usertmp = UserUtils.get(tt.getSponsor());
						if (usertmp!=null) {
						tt.setSponsor(usertmp.getName());
					}else {
							tt.setSponsor("");
						}
					}

					if (StringUtil.isNotBlank(tt.getLocalCollege())) {
						Office officetmp = officeService.get(tt.getLocalCollege());
						if (officetmp!=null) {
							tt.setLocalCollege(officetmp.getName());
						}else{
							tt.setLocalCollege("");
						}
					}

					String teamId = tt.getId();//获取teamId
					List<Team> teamUserName=teamService.findTeamUserName(teamId);
					int qyCount=0;
					int xyCount=0;
					int xsCount=0;
					StringBuffer xsbuffer = new StringBuffer();
					StringBuffer qybuffer = new StringBuffer();
					StringBuffer xybuffer = new StringBuffer();
					if (teamUserName!=null) {
					  	for (Team teamTm : teamUserName) {
						  	if (teamTm!=null) {
								if ("1".equals(teamTm.getTeamUserType())) {//代表类型是学生
									xsCount++;
									if (StringUtil.isNotBlank(teamTm.getuName())) {
										xsbuffer.append(teamTm.getuName() + "/");
									}
								}else if ("2".equals(teamTm.getTeamUserType())) {//否则类型就是导师
									if ("2".equals(teamTm.getTeacherType())) {//判断是否是企业导师//1是校园导师2是企业导师
										qyCount++;
										if (StringUtil.isNotBlank(teamTm.getuName())) {
											qybuffer.append(teamTm.getuName() + "/");
										}
									}else if ("1".equals(teamTm.getTeacherType())) {//否则就是校园导师
										xyCount++;
										if (StringUtil.isNotBlank(teamTm.getuName())) {
											xybuffer.append(teamTm.getuName() + "/");
										}
									}
								}
					 	 	}
					 	}

						if (xsbuffer.length()>0) {
							tt.setUserName(xsbuffer.substring(0, xsbuffer.lastIndexOf("/")));
						}
						tt.setUserCount(xsCount);
						if (qybuffer.length()>0) {
							tt.setEntName(qybuffer.substring(0,qybuffer.lastIndexOf("/")));
						}
						tt.setEnterpriseNum(qyCount);
						if (xybuffer.length()>0) {
						  tt.setSchName(xybuffer.substring(0,xybuffer.lastIndexOf("/")));
						}
					  	tt.setSchoolNum(xyCount);
					}
				}
			}
		}
		page.setList(teamList);
		model.addAttribute("page", page);
		model.addAttribute("org",UserUtils.getOffice(request.getParameter("localCollege")));
		return "modules/team/teamList";
	}

	//@RequiresPermissions("team:team:view")
	@RequestMapping(value = "form")
	public String form(Team team, Model model) {
		if (team.getId()!=null) {
			team= teamService.get(team.getId());
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
		return "redirect:"+Global.getAdminPath()+"/team/team/?repage";
	}

	//@RequiresPermissions("team:team:edit")
	@RequestMapping(value = "delete")
	public String delete(Team team, RedirectAttributes redirectAttributes) {
		//根据teamid判断团队状态
		Team teamTmp=teamService.get(team);
		if (!"2".equals(teamTmp.getState())) {//如果不是解散状态
			addMessage(redirectAttributes, "团队未解散,不可删除");
		}else{
			team.setDelFlag("1");
			teamService.delete(team);
			addMessage(redirectAttributes, "删除团队成功");
		}
		return "redirect:"+Global.getAdminPath()+"/team/?repage";

	}
	@RequestMapping(value = "batchDelete")
	public String batchDelete(Team team, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
		String idsre=request.getParameter("ids");
		String[] ids=idsre.split(",");
		String endString="";
		String delname="";
		String delfailname="";
		if (ids.length>0) {
			for (String string : ids) {
				Team delteam =teamService.get(string);
				if (delteam!=null) {
					if (delteam.getState().equals("2")) {
						delname=delname+delteam.getName()+",";
						teamService.delete(delteam);
					}else{
						delfailname=delfailname+delteam.getName()+",";
					}
				}
			}
		}
		if (delname.isEmpty()) {

		}else{
			endString=endString+delname.substring(0,delname.length()-1)+"删除团队成功。";
		}
		if (delfailname.isEmpty()) {

		}else{
			endString=endString+delfailname.substring(0,delfailname.length()-1)+"团队未解散,不可删除。";
		}
		addMessage(redirectAttributes, endString);
		return "redirect:"+Global.getAdminPath()+"/team/?repage";

	}
	@RequestMapping(value = "disTeam") //解散
	public String disTeam(Team team, RedirectAttributes redirectAttributes,HttpServletRequest request, HttpServletResponse response,Model model) {
		//根据teamid判断团队状态
		Team teamTmp=teamService.get(team);
		if ("2".equals(teamTmp.getState())) {//如果为解散状态
			addMessage(redirectAttributes,"团队已是解散状态");
			return "redirect:"+Global.getAdminPath()+"/team/?repage";
		}else{
			int projectCount=projectDeclareService.findByTeamId(team.getId());//根据teamid查询项目是否正在进行中
		    if (projectCount>0) {
		    	addMessage(redirectAttributes,"项目未完成,不可解散");
		    	return "redirect:"+Global.getAdminPath()+"/team/?repage";
		    }
			int gcontestCount=gContestService.findGcontestByTeamId(team.getId());
			if (gcontestCount>0) {
				addMessage(redirectAttributes,"大赛未完成,不可解散");
				return "redirect:"+Global.getAdminPath()+"/team/?repage";
			}
		}
		teamService.disTeam(team);//修改团队状态为解散状态
		addMessage(redirectAttributes, "解散团队成功");
//		team=new Team();
//		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);
//		model.addAttribute("page", page);
		return "redirect:"+Global.getAdminPath()+"/team/?repage";

	}

	@RequestMapping(value = "batchDis")
	public String batchDis(Team team, RedirectAttributes redirectAttributes) {
	  if(StringUtil.isNotEmpty(team.getId())){
  		String[] ids = team.getId().split(",");
  		if (ids.length > 0){
    		for (String string : ids) {
    			Team disteam=new Team(string);
    			teamService.disTeam(disteam);
    		}
  		}
  		addMessage(redirectAttributes, "解散团队成功");
    }else{
      addMessage(redirectAttributes, "团队为空，解散团队失败");
    }
		return "redirect:"+Global.getAdminPath()+"/team/?repage";

	}
	//项目负责人组建项目团队
//	@RequiresPermissions("team:team:view")
	@RequestMapping(value = "buildTeam")
	public String buildTeam(Team team, Model model) {
		model.addAttribute("team", team);
		//找到导师List
        User master=new User();
        master.setUserType("2");
		List<User> masterList=userService.findByType(master);
		model.addAttribute("masterList", masterList);

		//找到学生List
        User stu=new User();
        stu.setUserType("1");
        stu.setId(UserUtils.getUser().getId());
		List<User> studentList=userService.findByType(stu);
		model.addAttribute("studentList", studentList);

		return "modules/team/buildTeam";
	}


	//首页我的团队
	@RequestMapping(value ="indexMyTeamList")
	public String IndexMyTeamList(Team team, HttpServletRequest request, HttpServletResponse response, Model model) {
		//User sponsor = UserUtils.getUser();
		//team.setSponsor(sponsor.getId());
		List<Team> teamList=null;
		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);
		User user = UserUtils.getUser();
		if (user!=null) {
			model.addAttribute("curUserId", user.getId());
		}else{
			model.addAttribute("curUserId", null);
		}
		if (page!=null) {
			teamList = page.getList();
			if (teamList!=null) {
				for(Team tt:teamList) {
					if (StringUtil.isNotBlank(tt.getSponsor())) {
						User usertmp = UserUtils.get(tt.getSponsor());
						if (usertmp!=null) {
							tt.setSponsor(usertmp.getName());
						}
					}
					if (StringUtil.isNotBlank(tt.getLocalCollege())) {
						Office officetmp = officeService.get(tt.getLocalCollege());
						if (officetmp!=null) {
							tt.setLocalCollege(officetmp.getName());
						}
					}
					String teamId = tt.getId();//获取teamId
					List<Team> teamUserName=teamService.findTeamUserName(teamId);
					int qyCount=0;
					int xyCount=0;
					int xsCount=0;
					StringBuffer xsbuffer = new StringBuffer();
					StringBuffer qybuffer = new StringBuffer();
					StringBuffer xybuffer = new StringBuffer();
					if (teamUserName!=null) {
					  	for (Team teamTm : teamUserName) {
							if (teamTm.getTeamUserType().equals("1")) {//代表类型是学生
								xsCount++;
								xsbuffer.append(teamTm.getuName()+"/");
							}else if (teamTm.getTeamUserType().equals("2")) {//否则类型就是导师
								if (teamTm.getTeacherType()!=null && !teamTm.getTeacherType().equals("")) {//判断是否是企业导师
									if (teamTm.getTeacherType().equals("1")) {
										qyCount++;
										qybuffer.append(teamTm.getuName()+"/");
									}
								}else if (teamTm.getTeacherType()!=null && !teamTm.getTeacherType().equals("")) {//否则就是校园导师
									if (teamTm.getTeacherType().equals("2")) {
										xyCount++;
										xybuffer.append(teamTm.getuName()+"/");
									}
								}
							}
					  	}
					  	if (xsbuffer.length()>0) {
							 tt.setUserName(xsbuffer.substring(0, xsbuffer.lastIndexOf("/")));
					  	}
					  	tt.setUserCount(xsCount);
					  	if (qybuffer.length()>0) {
						  tt.setEntName(qybuffer.substring(0,qybuffer.lastIndexOf("/")));
					  	}
					  	tt.setEnterpriseNum(qyCount);
					  	if (xybuffer.length()>0) {
						  tt.setSchName(xybuffer.substring(0,xybuffer.lastIndexOf("/")));
					   	}
						tt.setSchoolNum(xyCount);
					}
				}
			}
			page.setList(teamList);
			model.addAttribute("page", page);
		}
		return "modules/team/indexTeam";
	}

	//首页保存
	//@RequiresPermissions("team:team:edit")
	@RequestMapping(value = "indexSave")
	public String indexSave(Team team, Model model, RedirectAttributes redirectAttributes,HttpServletRequest request, HttpServletResponse response) {
		/*addMessage(redirectAttributes, "发布团队信息成功");
		//return "redirect:"+Global.getAdminPath()+"/team/buildTeam?repage";
		team.setSponsor("");
		team.setName("");
		team.setLocalCollege("");
		Team team1=new Team();
		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team1);
		model.addAttribute("page", page);
		return "modules/team/indexTeam"; */
		if (team.getId()==null && team.getId().equals("")) {
			User curUser = UserUtils.getUser();
			team.setSponsor(curUser.getId());
			team.setLocalCollege(curUser.getOffice().getId());
			teamService.save(team);
			addMessage(redirectAttributes, "保存团队成功");
		}else{
			teamService.save(team);
		}
		return "redirect:"+Global.getAdminPath()+"/team/indexMyTeamList?repage";

	}

	@RequestMapping(value="findByTeamId")
	public String teamDetails(Model model,String id) {
		String stuType="1";
		List<TeamDetails> teamInfo=teamService.findTeamInfo(id,stuType);//查询学生list
		String masterType="2";
		List<TeamDetails> teamTeacherInfo=teamService.findTeamInfo(id,masterType);//查询导师list
		TeamDetails teamDetails=teamService.findTeamDetails(id);//查询团队详情
		for (TeamDetails te : teamInfo) {
			if (StringUtil.isNotBlank(te.getOfficeId())) {
				Office officeTmp=officeService.get(te.getOfficeId());
				if (officeTmp!=null) {
				te.setOfficeId(officeTmp.getName());
				}
			}
		}
		for (TeamDetails teacher : teamTeacherInfo) {
			if (StringUtil.isNotBlank(teacher.getOfficeId())) {
				Office officetmp=officeService.get(teacher.getOfficeId());
				if (officetmp!=null) {
				  teacher.setOfficeId(officetmp.getName());
				}
			}
		}
		User user = systemService.getUser(teamDetails.getSponsor());
		if (user!=null) {

      if (StringUtil.isNotEmpty(user.getName())) {
				teamDetails.setSponsor(user.getName());
			}
		}
		Office officeTeam=officeService.get(teamDetails.getLocalCollege());
		if (officeTeam!=null) {
			teamDetails.setLocalCollege(officeTeam.getName());
			model.addAttribute("teamDetails", teamDetails);//查询团队详情信息
		}
		model.addAttribute("teamInfo", teamInfo);
		model.addAttribute("teamTeacherInfo", teamTeacherInfo);
		return "modules/team/teamDetails_admin";
	}

    @RequestMapping(value="findById")
    public @ResponseBody Team findById(Team team, HttpServletRequest request, HttpServletResponse response, Model model,String id) throws IOException{
    	Team teamInfo=teamService.get(id);//根据id获取单条信息
		return teamInfo;

    }

    /**
     * 申请加入
     * @param model
     * @return
     */
	@RequestMapping(value="applyJoin")
	public String applyJoin(TeamUserRelation teamUserRelation,HttpServletRequest request, HttpServletResponse response, Model model,String teamId,
			User apply_User,User team_User,Team team,String post, RedirectAttributes redirectAttributes) {
		Date now = new Date();
		User curUser = UserUtils.getUser();//获取当前用户的信息
		teamUserRelation.setUser(curUser);
		List<TeamUserRelation> teamUserInfo=teamUserRelationService.findUserInfo(teamUserRelation);//根据用户id查询用户是否存在
			if (teamUserInfo.size()>0) {//如果用户存在并且状态是加入状态
				TeamUserRelation teamUserRelationTmp=teamUserInfo.get(0);//取获得到的第一条数据
				if ("0".equals(teamUserRelationTmp.getState())) {
					addMessage(redirectAttributes, "已加入过其它团队");
				  return "redirect:"+Global.getAdminPath()+"/team/indexMyTeamList?repage";
				}else if (!"0".equals(teamUserRelationTmp.getState())) {
					teamUserRelation.setUpdateDate(now);
					teamUserRelationService.updateByUserId(teamUserRelation);//根据用户id修改
				  return "redirect:"+Global.getAdminPath()+"/team/indexMyTeamList?repage";
				}
		}else{
			teamUserRelation.setTeamId(teamId);//添加teamid
			teamUserRelation.setId(IdGen.uuid());//添加id
			teamUserRelation.setState("1");//添加状态
			teamUserRelation.setUser(curUser);
			teamUserRelation.setCreateDate(now);
			teamUserRelation.setCreateBy(curUser);
			teamUserRelation.setUpdateDate(now);
			teamUserRelation.setUpdateBy(curUser);
			teamUserRelation.setDelFlag("0");
			teamUserRelation.setUserType(curUser.getUserType());
			teamService.saveTeamUserRelation(teamUserRelation);
			int result=teamService.sendOaNotify(apply_User, team_User, team, post);
			if (result>0) {
				addMessage(redirectAttributes, "申请成功");
			}else{
				addMessage(redirectAttributes, "申请失败");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/team/indexMyTeamList?repage";
	}


}













