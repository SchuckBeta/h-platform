/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/oseasy/initiate">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.team.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.oa.dao.OaNotifyRecordDao;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

import net.sf.json.JSONObject;

/**
 * 团队人员关系表Controller
 * @author zhangzheng
 * @version 2017-03-06
 */
@Controller
@RequestMapping(value = "${adminPath}/team/teamUserRelation")
public class TeamUserRelationController extends BaseController {

	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private OaNotifyRecordDao oaNotifyRecordDao;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private TeamService teamService;
	
	@ModelAttribute
	public TeamUserRelation get(@RequestParam(required=false) String id) {
		TeamUserRelation entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = teamUserRelationService.get(id);
		}
		if (entity == null) {
			entity = new TeamUserRelation();
		}
		return entity;
	}

	//执行同意团建
	@RequestMapping(value = "changeState")
	public String changeState(TeamUserRelation teamUserRelation,
							  HttpServletRequest request,
							  HttpServletResponse response,
							  RedirectAttributes redirectAttributes,Model model) {
		System.out.println(teamUserRelation.getTeamId());
		System.out.println(teamUserRelation.getState());
		User user = UserUtils.getUser();
		teamUserRelation.setUser(user);
		//改变teamUserRelation 的state状态 改变team 的state状态
		teamUserRelationService.updateState(teamUserRelation);
		addMessage(redirectAttributes, "成功");
		return "redirect:" + adminPath + "/oa/oaNotify/self";
	}

	
//	@RequiresPermissions("team:teamUserRelation:view")
	@RequestMapping(value = {"list", ""})
	public String list(TeamUserRelation teamUserRelation, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TeamUserRelation> page = teamUserRelationService.findPage(new Page<TeamUserRelation>(request, response), teamUserRelation); 
		model.addAttribute("page", page);
		return "modules/team/teamUserRelationList";
	}

//	@RequiresPermissions("team:teamUserRelation:view")
	@RequestMapping(value = "form")
	public String form(TeamUserRelation teamUserRelation, Model model) {
		model.addAttribute("teamUserRelation", teamUserRelation);
		return "modules/team/teamUserRelationForm";
	}

//	@RequiresPermissions("team:teamUserRelation:edit")
	@RequestMapping(value = "save")
	public String save(TeamUserRelation teamUserRelation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, teamUserRelation)) {
			return form(teamUserRelation, model);
		}
		teamUserRelationService.save(teamUserRelation);
		addMessage(redirectAttributes, "保存团队人员关系成功");
		return "redirect:"+Global.getAdminPath()+"/team/teamUserRelation/?repage";
	}
	
//	@RequiresPermissions("team:teamUserRelation:edit")
	@RequestMapping(value = "delete")
	public String delete(TeamUserRelation teamUserRelation, RedirectAttributes redirectAttributes) {
		teamUserRelationService.delete(teamUserRelation);
		addMessage(redirectAttributes, "删除团队人员关系成功");
		return "redirect:"+Global.getAdminPath()+"/team/teamUserRelation/?repage";
	}
	
	//团队负责人发布
	@RequestMapping(value = "batInTeamUser")
	public void batInTeamUser(String offices,String userIds,String teamId, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		JSONObject json=new JSONObject();
		try {
			//查询所有用户
			List<String> userList=teamUserRelationService.findAllUserId(null,offices,userIds);
			logger.info("userIdList.size="+userList.size());
			
			
			//删除久通知  不稳定先注释
/*			OaNotify oaNotify=new OaNotify(); 
			oaNotify.setsId(teamId);
			oaNotify.setType("7");
			logger.info("teamId:"+teamId);
			List<OaNotify> oanList= oaNotifyService.findList(oaNotify);
			
			for (OaNotify oaNotify2 : oanList) {
				//oaNotifyRecordDao.deleteByOaNotifyId(oaNotify2.getId());
				oaNotifyService.delete(oaNotify2);
			}*/
			
			//插入发布通知
			if (userList.size()>0) {
				/*Team team=new Team();
				team.setId(teamId);
				User teamUser=  UserUtils.getUser();
				team.setSponsor(teamUser.getId());*/
				User teamUser=  UserUtils.getUser();
				Team team = teamService.get(teamId);
				teamUserRelationService.inseRelOaNo(team, teamUser, userList);
			}
			json.put("success", true);
		} catch (Exception e) {
			json.put("success", false);
			e.printStackTrace();
		}
		
		response.getWriter().write(json.toString());
		//return "redirect:"+Global.getAdminPath()+"/team/teamUserRelation/?repage";
	}
	
	
	//团队负责人批量邀请
	@RequestMapping(value = "toInvite")
	public void toInvite(String offices,String userIds,String userType,String teamId, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		JSONObject json=new JSONObject();
		try {
			//teamId="0a22f49819a14b41a76c1dd93bc61aea";
			//查询所有用户 
			List<String> userList=teamUserRelationService.findAllUserId(userType,offices,userIds);
			logger.info("userIdList.size="+userList.size());
			
			int ress=0;
			if (userList.size()>0) {
				for (String user1 : userList) {
					if (StringUtil.isNotBlank(user1)) {
		        			User user=userDao.get(user1);
		        			if (user!=null) {
		        				int res= teamUserRelationService.inseTeamUser(user,teamId,"2");
		        				//logger.info("++++++++++++++++++++++++++++res="+res);
		        				if (res==3) {
		        					ress++;
		        					teamUserRelationService.inseOaNotify(UserUtils.getUser(), user1,teamId);
		        				}
		        			}
						
						
					}
					//插入申请记录
				}
			}
			json.put("success", true);
			json.put("res", ress);
		} catch (Exception e) {
			json.put("success", false);
			e.printStackTrace();
		}
		
		response.getWriter().write(json.toString());
		//return "redirect:"+Global.getAdminPath()+"/team/teamUserRelation/?repage";
	}
	
	
	//团队负责人直接拉入
	@RequestMapping(value = "pullIn")
	public void pullIn(String offices,String userIds,String userType,String teamId, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		JSONObject json=new JSONObject();
		try {
			//teamId="0a22f49819a14b41a76c1dd93bc61aea";
			//查询所有用户
			List<String> userList=teamUserRelationService.findAllUserId(userType,offices,userIds);
			logger.info("userIdList.size="+userList.size());
			
			int ress=0;
			if (userList.size()>0) {
				for (String user1 : userList) {
					User user=userDao.get(user1);
					//插入申请记录
					int res= teamUserRelationService.pullIn(user,teamId);
					if (res>1) {
						ress++;
					}
					//teamUserRelationService.inseOaNotify(UserUtils.getUser(), user1);
				}
			}
			Team team= teamService.get(teamId);
			logger.info("teamId:"+teamId);
			if (team!=null) {
				TeamUserRelation teamUserRelation=new TeamUserRelation();
				teamUserRelation.setTeamId(teamId);
				teamUserRelationService.repTeamstate(teamUserRelation, team);
			}

			json.put("success", true);
			json.put("res", ress);
		} catch (Exception e) {
			json.put("success", false);
			e.printStackTrace();
		}
		
		response.getWriter().write(json.toString());
		//return "redirect:"+Global.getAdminPath()+"/team/teamUserRelation/?repage";
	}
	
	

	
}