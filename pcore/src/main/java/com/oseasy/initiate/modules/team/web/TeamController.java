package com.oseasy.initiate.modules.team.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oseasy.initiate.modules.gcontest.service.GContestService;
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
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.oa.dao.OaNotifyDao;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.dao.TeamUserRelationDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamDetails;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

/**
 * 团队管理Controller
 * @author 刘波
 * @version 2017-03-30
 */
@Controller
@RequestMapping(value = "${frontPath}/team")
public class TeamController extends BaseController {
	@Autowired
	private TeamService teamService;
	@Autowired
	private UserService userService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private GContestService gContestService;
	@Autowired
	private TeamUserRelationDao teamUserRelationDao;
	@Autowired
	private OaNotifyDao oaNotifyDao;
	@Autowired
	private OaNotifyService oaNotifyService;

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
						  if (teamTm.getTeamUserType()!=null && teamTm.getTeamUserType()!="") {
							  if ("1".equals(teamTm.getTeamUserType())) {//代表类型是学生
									xsCount++;
									if (StringUtil.isNotBlank(teamTm.getuName())) {
										xsbuffer.append(teamTm.getuName() + "/");
									}
								}else if ("2".equals(teamTm.getTeamUserType())) {//否则类型就是导师
									if ("2".equals(teamTm.getTeacherType())) {//判断是否是企业导师
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
	    page.setList(teamList);
		}
		model.addAttribute("page", page);
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
		teamService.delete(team);
		addMessage(redirectAttributes, "删除团队成功");
		return "redirect:"+Global.getFrontPath()+"/team/?repage";

	}

	@RequestMapping(value = "batchDelete")
	public String batchDelete(Team team, RedirectAttributes redirectAttributes) {
	  if(StringUtil.isNotEmpty(team.getId())){
  		String[] ids=team.getId().split(",");
  		if (ids.length > 0){
    		for (String string : ids) {
    			Team delteam=new Team(string);
    			teamService.delete(delteam);
    		}
  		}
  		addMessage(redirectAttributes, "删除团队成功");
	  }else{
      addMessage(redirectAttributes, "团队为空，删除团队失败");
	  }
		return "redirect:"+Global.getFrontPath()+"/team/?repage";

	}

	//解散团队不可见
	@RequestMapping(value = "hiddenDelete")
	@ResponseBody
	public String hiddenDelete(String teamId, RedirectAttributes redirectAttributes) {
		User curUser = UserUtils.getUser();//获取当前用户的信息
		String resStr = "1";
		if (curUser==null) {
			resStr = "请先登录！";
			return resStr;
		}
		try {
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			teamUserRelation.setUser(curUser);
			teamUserRelation.setTeamId(teamId);
			teamUserRelation.setState("4");
			teamUserRelationService.updateTeamUserRelation(teamUserRelation);
			Team team =teamService.get(teamId);
			if (team!=null && team.getSponsor().equals(curUser.getId())) {
				teamService.delete(team);
			}
		} catch (Exception e) {
			resStr = "操作异常";
			logger.error(e.getMessage(),e);
		}
		 return resStr;
	}

	/*@RequestMapping(value = "disTeam") //解散
	public String disTeam(Team team, RedirectAttributes redirectAttributes) {
		teamService.disTeam(team);
		return "redirect:"+Global.getFrontPath()+"/team/?repage";

	}*/

	@RequestMapping(value = "batchDis")
	public String batchDis(Team team, RedirectAttributes redirectAttributes) {
		String[] ids=team.getId().split(",");
		if (ids.length>0){
  		for (String string : ids) {
  			Team disteam=new Team(string);
  			teamService.disTeam(disteam);
  		}
		}
		addMessage(redirectAttributes, "解散团队成功");
		return "redirect:"+Global.getFrontPath()+"/team/?repage";

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
		String msg=request.getParameter("msg");
		model.addAttribute("msg", msg);
		String opType=request.getParameter("opType");
		model.addAttribute("opType", opType);
		if ("2".equals(opType)) {
			model.addAttribute("p1", request.getParameter("p1"));
			model.addAttribute("p2", request.getParameter("p2"));
			model.addAttribute("p3", request.getParameter("p3"));
			model.addAttribute("p4", request.getParameter("p4"));
			model.addAttribute("p5", request.getParameter("p5"));
			model.addAttribute("p6", request.getParameter("p6"));
			model.addAttribute("p7", request.getParameter("p7"));
			model.addAttribute("p8", request.getParameter("p8"));
			model.addAttribute("p9", request.getParameter("p9"));
		}

		List<Team> teamList=null;
		User user = UserUtils.getUser();
		team.setUser(user);
		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);
    if (StringUtil.isNotEmpty(team.getName())) {
			model.addAttribute("teamName", team.getName());
		}

    if (StringUtil.isNotEmpty(team.getCreator())) {
			model.addAttribute("teamCreator", team.getCreator());
		}
    if (StringUtil.isNotEmpty(team.getState())) {
			model.addAttribute("teamStatus", team.getState());
		}

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
					//	User usertmp = UserUtils.get(tt.getSponsor());
						User usertmp = userService.findUserById(tt.getSponsor());
						if (usertmp!=null) {
						tt.setSponsorId(tt.getSponsor());
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
						 	if (teamTm!=null) {
								if ("1".equals(teamTm.getTeamUserType())) {//代表类型是学生
									xsCount++;
									xsbuffer.append(teamTm.getuName()+"/");
								}else if ("2".equals(teamTm.getTeamUserType())) {//否则类型就是导师
									if ("2".equals(teamTm.getTeacherType())) {//判断是否是企业导师
										qyCount++;
										qybuffer.append(teamTm.getuName()+"/");
									}else if ("1".equals(teamTm.getTeacherType())) {//否则就是校园导师
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
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			User curUser = UserUtils.getUser();
			teamUserRelation.setUser(curUser);
			TeamUserRelation teamUserInfo=teamUserRelationService.findUserById(teamUserRelation);//根据用户id查询用户是否存在
			model.addAttribute("teamUserInfo", teamUserInfo);
			User userInfo = systemService.getUser(curUser.getId());//查询当前用户的信息
			if (userInfo!=null) {
				model.addAttribute("userType", userInfo.getUserType());
			}
		}
		return "modules/team/indexTeam";
	}

	//创建团队
	//@RequiresPermissions("team:team:edit")
	@RequestMapping(value = "indexSave")
	//@ResponseBody
	public String indexSave(Team team, Model model,RedirectAttributes redirectAttributes,HttpServletRequest request, HttpServletResponse response) {
		/*addMessage(redirectAttributes, "发布团队信息成功");
		//return "redirect:"+Global.getAdminPath()+"/team/buildTeam?repage";
		team.setSponsor("");
		team.setName("");
		team.setLocalCollege("");
		Team team1=new Team();
		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team1);
		model.addAttribute("page", page);
		return "modules/team/indexTeam"; */
		//Map<String,Object> rMap = new HashMap<String,Object>();

		String p1 = team.getName();
	/*	String p2 = DateUtil.formatDate(team.getValidDateStart(),"yyyy-MM-dd");
		String p3 = DateUtil.formatDate(team.getValidDateEnd(),"yyyy-MM-dd");*/
		Integer p4 = team.getMemberNum();
		Integer p5 = team.getSchoolTeacherNum();
		Integer p6 = team.getEnterpriseTeacherNum();
		String p7 = team.getMembership();
		String p8 = team.getProjectIntroduction();
		String p9 = team.getSummary();
		model.addAttribute("p1",p1);
	/*	model.addAttribute("p2",p2);
		model.addAttribute("p3",p3);*/
		model.addAttribute("p4",p4);
		model.addAttribute("p5",p5);
		model.addAttribute("p6",p6);
		model.addAttribute("p7",p7);
		model.addAttribute("p8",p8);
		model.addAttribute("p9",p9);
		if (p4==0) {
			model.addAttribute("message","要求必须有一位团队成员!");
			model.addAttribute("opType","2");
			return DefaultMyTeamList(request, response, model);
		}
		if (p5==0 && p6==0) {
			//addMessage(redirectAttributes, "要求必须有一位导师!");
			/*return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=2&p1="+p1+"&p2="+p2
					+"&p3="+p3+"&p4="+p4+"&p5="+p5+"&p6="+p6+"&p7="+
					p7+"&p8="+p8+"&p9="+p9;*/
			model.addAttribute("message","要求必须有一位导师!");
			model.addAttribute("opType","2");
			return DefaultMyTeamList(request, response, model);
		}

		if (StringUtil.isNotBlank(p7)&&p7.length()>500) {
			//addMessage(redirectAttributes, "组员要求字数不能超过500!");
			model.addAttribute("message","组员要求字数不能超过500!");
			model.addAttribute("opType","2");
			return DefaultMyTeamList(request, response, model);
		/*	return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=2&p1="+p1+"&p2="+p2
					+"&p3="+p3+"&p4="+p4+"&p5="+p5+"&p6="+p6+"&p7="+p7+"&p8="+p8+"&p9="+p9;*/
		}
		if (StringUtil.isNotBlank(p8)&&p8.length()>500) {
			/*addMessage(redirectAttributes, "项目简介字数不能超过500!");
			return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=2&p1="+p1+"&p2="+p2
					+"&p3="+p3+"&p4="+p4+"&p5="+p5+"&p6="+p6+"&p7="+p7+"&p8="+p8+"&p9="+p9;*/
			model.addAttribute("message","项目简介字数不能超过500!");
			model.addAttribute("opType","2");
			return DefaultMyTeamList(request, response, model);
		}
		if (StringUtil.isNotBlank(p9)&&p9.length()>500) {
			/*addMessage(redirectAttributes, "团队介绍字数不能超过500!");
			return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=2&p1="+p1+"&p2="+p2
					+"&p3="+p3+"&p4="+p4+"&p5="+p5+"&p6="+p6+"&p7="+p7+"&p8="+p8+"&p9="+p9;*/
			model.addAttribute("message","团队介绍字数不能超过500!");
			model.addAttribute("opType","2");
			return DefaultMyTeamList(request, response, model);
		}

		if (StringUtil.isNotEmpty(team.getId())) {
			teamService.save(team);
			//rMap.put("state", "1");
			//rMap.put("msg", "编辑团队信息成功!");
			//return rMap;
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			teamUserRelation.setTeamId(team.getId());
			teamUserRelationService.repTeamstate(teamUserRelation, team);

			/*addMessage(redirectAttributes, "编辑团队信息成功!");
			return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1&msg="+1;*/

			model.addAttribute("message","编辑团队信息成功!");
			model.addAttribute("opType","1");
			return DefaultMyTeamList(request, response, model);
		}else{
			User curUser = UserUtils.getUser();
			//logger.info(curUser.getId()+"=====================");

			if (StringUtil.isNotEmpty(curUser.getUserType())&&curUser.getUserType().equals("2")) {
		      	//addMessage(redirectAttributes, "导师暂时无法创建团队。");
		      	//model.addAttribute("msg", "导师暂时无法无法创建团队。");
		    	//return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1";
				model.addAttribute("message","导师暂时无法创建团队。");
				model.addAttribute("opType","1");
				return DefaultMyTeamList(request, response, model);

			}
			Long countValid =   teamService.countBuildByUserId(curUser);
			if (countValid!=null&&countValid>0) {
	        	/*addMessage(redirectAttributes, "你已创建团队，不能重复操作！");
	  			return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1";*/

				model.addAttribute("message","你已创建团队，不能重复操作！");
				model.addAttribute("opType","1");
				return DefaultMyTeamList(request, response, model);

			}
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			teamUserRelation.setUser(curUser);
			TeamUserRelation teamUserInfo=teamUserRelationService.findUserById(teamUserRelation);//根据用户id查询用户是否存在
            if (teamUserInfo!=null) {//判断是否创建团队
            	/*addMessage(redirectAttributes, "你已加入另一个项目团队！");
    			return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1";*/
				teamUserInfo.setState("3");
				teamUserRelationService.updateState(teamUserInfo);
				model.addAttribute("message","你已加入另一个项目团队！");
				model.addAttribute("opType","1");
				return DefaultMyTeamList(request, response, model);
            }
			team.setSponsor(curUser.getId());
			User userInfo = systemService.getUser(curUser.getId());
			String officeId = userInfo!=null&&userInfo.getOffice()!=null?userInfo.getOffice().getId():null;
			team.setLocalCollege(officeId);
			team.setState("0");
			teamService.save(team);
			Date now=new Date();
			teamUserRelation.setTeamId(team.getId());//添加teamid
			teamUserRelation.setId(IdGen.uuid());//添加id
			teamUserRelation.setState("0");//添加状态
			teamUserRelation.setCreateDate(now);
			teamUserRelation.setCreateBy(curUser);
			teamUserRelation.setUpdateDate(now);
			teamUserRelation.setUpdateBy(curUser);
			teamUserRelation.setDelFlag("0");
			teamUserRelation.setUserType(curUser.getUserType());
			teamService.saveTeamUserRelation(teamUserRelation);
			teamUserRelationService.repTeamstate(teamUserRelation, team);
			//addMessage(redirectAttributes, "创建团队成功!");
			//return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1&msg="+1;
			model.addAttribute("message","创建团队成功!");
			model.addAttribute("opType","1");
			model.addAttribute("msg","1");
			return DefaultMyTeamList(request, response, model);
		}

	}

	public String DefaultMyTeamList(HttpServletRequest request, HttpServletResponse response, Model model) {
			Team team=new Team();
			List<Team> teamList=null;
			User user = UserUtils.getUser();
			team.setUser(user);
			Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);
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
						//	User usertmp = UserUtils.get(tt.getSponsor());
							User usertmp = userService.findUserById(tt.getSponsor());
							if (usertmp!=null) {
							tt.setSponsorId(tt.getSponsor());
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
							  if (teamTm!=null) {
							  if ("1".equals(teamTm.getTeamUserType())) {//代表类型是学生
									xsCount++;
									xsbuffer.append(teamTm.getuName()+"/");
								}else if ("2".equals(teamTm.getTeamUserType())) {//否则类型就是导师
									if ("2".equals(teamTm.getTeacherType())) {//判断是否是企业导师
										qyCount++;
										qybuffer.append(teamTm.getuName()+"/");
									}else if ("1".equals(teamTm.getTeacherType())) {//否则就是校园导师
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
				TeamUserRelation teamUserRelation=new TeamUserRelation();
				User curUser = UserUtils.getUser();
				teamUserRelation.setUser(curUser);
				TeamUserRelation teamUserInfo=teamUserRelationService.findUserById(teamUserRelation);//根据用户id查询用户是否存在
				model.addAttribute("teamUserInfo", teamUserInfo);
				User userInfo = systemService.getUser(curUser.getId());//查询当前用户的信息
				if (userInfo!=null) {
					model.addAttribute("userType", userInfo.getUserType());
				}
			}
			return "modules/team/indexTeam";
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
		teamDetails.setSponsor(user.getName());
		Office officeTeam=officeService.get(teamDetails.getLocalCollege());
		if (officeTeam!=null) {
			teamDetails.setLocalCollege(officeTeam.getName());
			model.addAttribute("teamDetails", teamDetails);//查询团队详情信息
		}
		model.addAttribute("teamInfo", teamInfo);
		model.addAttribute("teamTeacherInfo", teamTeacherInfo);
		return "modules/team/teamDetails";
	}

    @RequestMapping(value="findById")
    public @ResponseBody Team findById(Team team, HttpServletRequest request, HttpServletResponse response, Model model,String id) throws IOException{
    	Team teamInfo=teamService.get(id);//根据id获取单条信息
		return teamInfo;

    }

    /**
     * 申请加入
     * @param model
     * @param teamId
     * @return
     */
	@RequestMapping(value="applyJoin")
	@ResponseBody
	public String applyJoin(TeamUserRelation teamUserRelation,HttpServletRequest request, HttpServletResponse response, Model model,String teamId,
			User apply_User,User team_User,Team team,String post,RedirectAttributes redirectAttributes) {

	   User curUser = UserUtils.getUser();//获取当前用户的信息
	   String resStr = "";
	   if (curUser==null) {
		   resStr = "请先登录！";
		   return resStr;
	   }

		team = teamService.get(teamId);
		team_User = team.getCreateBy();
		try {
		     int res=  teamUserRelationService.inseTeamUser(curUser, teamId, "1");
		     if (res==3) {
		    	teamService.sendOaNotify(curUser, team_User, team, post);
		    	resStr = "申请成功";
		     }else if (res>=2) {
				//addMessage(redirectAttributes, "申请成功");
				resStr = "已发出过申请或已经被邀请，请查看信息列表";
			}else if (res==1) {
				resStr = "申请失败,您已加入其他团队";
				//addMessage(redirectAttributes, "申请失败");
			}else{
				resStr = "申请失败";
			}
		} catch (Exception e) {
			resStr = "操作异常";
			logger.error(e.getMessage(),e);
		}
		 return resStr;
	}

	@RequestMapping(value="relTeam")
	public String fbTeam(Team team, HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/team/relTeam";
	}


	@RequestMapping(value = {"index"})
	public String index(User user, Model model) {
		return "modules/sys/userIndex";

	}

    //查询团队建设人员
	@RequestMapping(value="teambuild")
	public String teambuild(Model model,String id,HttpServletRequest request) {
		String stuType="1";
		List<TeamDetails> teamInfo=teamService.findTeamByTeamId(id,stuType);//查询学生list
		String masterType="2";
		List<TeamDetails> teamTeacherInfo=teamService.findTeamByTeamId(id,masterType);//查询导师list
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
		String teamId = request.getParameter("id");
		model.addAttribute("teamId", teamId);//查询团队组员
		model.addAttribute("teamInfo", teamInfo);//查询团队组员
		model.addAttribute("teamTeacherInfo", teamTeacherInfo);//查询团队组员
		//logger.info("==================teamIfno.size:"+teamInfo.size());
		//logger.info("==============teamTeacherInfo.size:"+teamTeacherInfo.size());

		return "modules/team/foundTeam";
	}


	//删除团队信息
	@RequestMapping(value="deleteTeamUserInfo")
	public String deleteTeamUserInfo(String userId,String teamId,RedirectAttributes redirectAttributes) {
		User curUser = UserUtils.getUser();//获取当前用户的信息
		Team team= teamService.get(teamId);
		User delUser=userService.findUserById(userId);
		teamUserRelationService.deleteTeamUserInfo(team,curUser, delUser);
		if (team!=null) {
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			teamUserRelation.setTeamId(teamId);
			teamUserRelationService.repTeamstate(teamUserRelation, team);
		}
		return "redirect:"+Global.getFrontPath()+"/team/teambuild?id="+teamId;
	}

	//同意
	@RequestMapping(value="checkInfo")
	public String checkInfo(String userId,String teamId,TeamUserRelation teamUserRelation,RedirectAttributes redirectAttributes) {
		teamUserRelation.setTeamId(teamId);
		User user=new User();
		user.setId(userId);
		teamUserRelation.setUser(user);
		teamService.checkInfo(teamUserRelation);
		return "redirect:"+Global.getFrontPath()+"/team/teambuild?id="+teamId;
	}

	@RequestMapping(value = "disTeam") //解散disTeam
	public String disTeam(Team team, RedirectAttributes redirectAttributes,HttpServletRequest request, HttpServletResponse response,Model model) {
		//根据teamid判断团队状态
		Team teamTmp=teamService.get(team);
		if ("2".equals(teamTmp.getState())) {//如果为解散状态
			addMessage(redirectAttributes,"团队已是解散状态");
			return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1&msg="+1;
		}else{
			int projectCount=projectDeclareService.findByTeamId(team.getId());//根据teamid查询项目是否正在进行中
		    if (projectCount>0) {
		    	addMessage(redirectAttributes,"项目未完成,不可解散");
		    	return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1&msg="+1;
		    }
			int gcontestCount=gContestService.findGcontestByTeamId(team.getId());
			if (gcontestCount>0) {
				addMessage(redirectAttributes,"大赛未完成,不可解散");
				return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1&msg="+1;
			}
		}
		teamService.disTeam(team);//修改团队状态为解散状态
		addMessage(redirectAttributes, "解散团队成功");
//		team=new Team();
//		Page<Team> page = teamService.findPage(new Page<Team>(request, response), team);
//		model.addAttribute("page", page);
		return "redirect:"+Global.getFrontPath()+"/team/indexMyTeamList?opType=1&msg="+1;

	}

	//团队组建情况拒绝申请
	@RequestMapping(value="refuseInviation")
	public String refuseInviation(HttpServletRequest request) {
		String turId = request.getParameter("turId");
		String teamId = request.getParameter("teamId");
		TeamUserRelation teamUserRelation = teamUserRelationService.get(turId);
		if (teamUserRelation != null) {
			teamUserRelation.setState("3");
			teamUserRelationDao.update(teamUserRelation);

			Team team = teamService.get(teamId);
	    User user = UserUtils.getUser();
	    User sentuser = teamUserRelation.getUser();
	    int t = teamUserRelationService.inseRefuseOaNo(team,"5", user, sentuser, null);

	    if (t>0) {
	      OaNotify oaNotify = oaNotifyDao.findOaNotifyByTeamID(user.getId(), teamId);
	      if (oaNotify!=null) {
	        oaNotifyService.updateReadFlag(oaNotify);
	        oaNotify.setType("10");
	        oaNotifyDao.update(oaNotify);
	      }

	    }
		}
		return "redirect:"+Global.getFrontPath()+"/team/teambuild?id="+teamId;
	}

	//团队组建情况接受申请
	@RequestMapping(value="acceptInviation")
	public String acceptInviation(HttpServletRequest request,RedirectAttributes redirectAttributes) {
		String teamId = request.getParameter("teamId");
		String userId = request.getParameter("userId");
		User user = userService.findUserById(userId);
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		teamUserRelation.setTeamId(teamId);
		teamUserRelation.setUser(user);
		int t = teamService.checkInfo(teamUserRelation);
		Team team = teamService.get(teamId);
		User acceptUser = UserUtils.getUser();
		OaNotify oaNotify = oaNotifyDao.findOaNotifyByTeamID(userId, teamId);
		if (t>0) {
			teamUserRelationService.inseAgreeOaNo(team, "5", acceptUser, user);
			oaNotifyService.updateReadFlag(oaNotify);
			oaNotify.setType("10");
			oaNotifyDao.update(oaNotify);
		}else{
			teamUserRelation.setUser(user);
			teamUserRelation=teamUserRelationService.findUserById(teamUserRelation);
			teamUserRelation.setState("3");
			teamUserRelationService.updateState(teamUserRelation);
			addMessage(redirectAttributes, "该用户已加入其它团队");
		}
		return "redirect:"+Global.getFrontPath()+"/team/teambuild?id="+teamId;

	}
}













