package com.oseasy.initiate.modules.sys.web.front;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.oseasy.initiate.common.service.UoloadFtpService;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.entity.gContestUndergo;
import com.oseasy.initiate.modules.sys.service.BackTeacherExpansionService;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.StudentExpansionService;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

/**
 * 学生扩展信息表Controller
 *
 * @author zy
 * @version 2017-03-27
 */
@Controller
@RequestMapping(value = "${frontPath}/sys/frontStudentExpansion")
public class FrontStudentExpansionController extends BaseController {
	@Autowired
	private OfficeService officeService;
	@Autowired
	private StudentExpansionService studentExpansionService;
	@Autowired
	private UserService  userService;
	@Autowired
	private TeamService  teamService;
	@Autowired
	private BackTeacherExpansionService teacherService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private ProjectDeclareService projectDeclareService;

	@Autowired
	private UoloadFtpService uoloadFtpService;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private BackTeacherExpansionService backTeacherExpansionService;
	@Autowired
	SystemService systemService;

	@ModelAttribute
	public StudentExpansion get(@RequestParam(required = false) String id,Model model) {
		StudentExpansion entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = studentExpansionService.get(id);
		}
		if (entity == null) {
			entity = new StudentExpansion();
		}
		return entity;
	}

	@RequestMapping(value = { "list", "" })
	public String list(StudentExpansion studentExpansion, HttpServletRequest request, HttpServletResponse response,Model model) {
		studentExpansion.setIsFront("1");
		studentExpansion.setIsOpen("1");
		Page<StudentExpansion> page = studentExpansionService.findPage(new Page<StudentExpansion>(request, response),studentExpansion);

		//当前用户为团队发起者可邀请
		String currentId = UserUtils.getUser().getId();
		Team team=new Team();
		team.setSponsor(UserUtils.getUser().getId());
		List<Team> teams=teamService.findListByCreatorId(team);

		 model.addAttribute("canInvite", false);
		if (teams!=null&&teams.size()>0) {
			for (Team tm : teams) {
				if (tm!=null&&tm.getSponsor()!=null) {
					if (tm.getSponsor().equals(currentId)) {
						model.addAttribute("canInvite",true);

				        if (page !=null) {
				        	List<StudentExpansion>  studentExp=null;
				        	studentExp=page.getList();
				        	for (int i = 0; i < studentExp.size(); i++) {
				        		//Integer result=oaNotifyService.findNotifyCount(studentExp.get(i).getUser().getId(),tm.getId());
				        		Integer result= teamUserRelationService.findIsApplyTeam(tm.getId(), studentExp.get(i).getUser().getId());
				        		if (result>0) {//已经邀请过
				        			StudentExpansion studentExpansion1= studentExp.get(i);
				        			studentExpansion1.setMsg("1");
				        			studentExp.set(i, studentExpansion1);
				        		}
							}
				        }
						break;
					}
				}
			}
		}
		model.addAttribute("page", page);
		return "modules/sys/front/frontStudentExpansionList";
	}

	@RequestMapping(value = "form")
	public String form(StudentExpansion studentExpansion, Model model) {
		List<ProjectExpVo> projectExpVo=projectDeclareService.getExpsByUserId(studentExpansion.getUser().getId());//查询项目经历
	    List<gContestUndergo> gContest=userService.findContestByUserId(studentExpansion.getUser().getId());
		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContest", gContest);
		String mobile=studentExpansion.getUser().getMobile();
//		mobile=mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
		model.addAttribute("mobile", mobile);
		return "modules/sys/front/frontStudentExpansionForm";
	}


	@RequestMapping(value = "toInvite")
	@ResponseBody
	public Map<String,Object> toInvite(StudentExpansion studentExpansion, Model model) {
		//JSONObject json=new JSONObject();
		Map<String, Object> rMap = new HashMap<String, Object>();
		Team team=new Team();
		team.setSponsor(UserUtils.getUser().getId());
		List<Team> teamlist=teamService.findList(team);
		User applyUser= userService.findUserById(studentExpansion.getUser().getId());

		//插入申请记录
		if (teamUserRelationService.inseTeamUser(applyUser,teamlist.get(0).getId(),"2")<=0) {
			//json.put("success", false);
			rMap.put("success", false);
			return rMap;
		}
		if (teamUserRelationService.inseOaNotify(UserUtils.getUser(), studentExpansion.getUser().getId(),(String)teamlist.get(0).getId())<=0) {
			/*json.put("success", false);
			return json;*/
			rMap.put("success", false);
			return rMap;
		}
		/*json.put("success", true);
		return json;*/
		rMap.put("success", true);
		return rMap;
	}


/*	@RequestMapping(value ="findNotifyCount")
	@ResponseBody
	public String findNotifyCount(StudentExpansion studentExpansion) {
	    logger.info("+++++++++++++++++++++++++++++++======"+UserUtils.getUser().getId());
	    logger.info("+++++++++++++++++++++++++++++++======"+studentExpansion.getUser().getId());
		Integer result=oaNotifyService.findNotifyCount(UserUtils.getUser().getId(),studentExpansion.getUser().getId());
		String userId=studentExpansion.getUser().getId();
		if (result>0) {
			return "2";//已经邀请过
		}else{
			return userId;//未邀请
		}
	}*/

	@RequestMapping(value = "invite")
	public String invite(StudentExpansion studentExpansion, Model model) {
		studentExpansionService.invite(studentExpansion);
		return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/?repage";
	}


	/**
	 * 个人信息查看和修改表单
	 * @param studentExpansion
	 * @param model
	 * @param request
	 * @return
	 */
	//@RequiresPermissions("sys:studentExpansion:view")
	@RequestMapping(value = "formPersonal")
	public String formPersonal(StudentExpansion studentExpansion, Model model,HttpServletRequest request) {
		/*if (studentExpansion.getUser()!=null) {
			User user = studentExpansion.getUser();
			if (user!=null) {
				if (StringUtil.isNotBlank(user.getPhoto())) {
					model.addAttribute("user", user);
				}
			}
		}*/
		/*
		if (StringUtil.isNotBlank(studentExpansion.getId())) {
			model.addAttribute("studentExpansion", studentExpansion);
			return "modules/sys/studentExpansionForm";
		}else {
			return "modules/sys/studentExpansionAdd";
		}*/
		String operateType = request.getParameter("operateType");
		model.addAttribute("operateType", operateType);
		model.addAttribute("studentExpansion", studentExpansion);
		List<Dict> dictList = DictUtils.getDictList("technology_field");
		model.addAttribute("allDomains", dictList);
		//return "modules/sys/studentExpansionForm";
		return "modules/sys/studentForm";
	}


	/**
	 * 个人信息保存表单
	 * @param studentExpansion
	 * @param model
	 * @param request
	 * @return
	 */
	//@RequiresPermissions("sys:studentExpansion:edit")
	@RequestMapping(value = "savePersonal")
	public String savePersonal(StudentExpansion studentExpansion,HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, studentExpansion)) {
			return formPersonal(studentExpansion, model,request);
		}
	    if (StringUtil.isNotBlank(studentExpansion.getId())) {
			studentExpansionService.updateAll(studentExpansion);
		}else {

		}
	    String[] arrUrl= request.getParameterValues("arrUrl");
		String[] arrNames= request.getParameterValues("arrName");
		if (arrUrl!=null&&arrUrl.length>0) {
			for(int i=0;i<arrUrl.length;i++) {
				 FtpUtil t = new FtpUtil();
				 try {
					String moveEnd=t.moveFile(t.getftpClient(), arrUrl[i]);
					arrUrl[i]=moveEnd;
				} catch (IOException e) {
					e.printStackTrace();
				}
				User user=studentExpansion.getUser();
				if (user!=null) {
					user.setPhoto(arrUrl[i]);
					userService.updateUser(user);
				}
			}
		}
		return "success";

	}
	/**
	 * 查看当前登录 学生信息
	 * @return
	 */
	@RequestMapping(value="findUserInfoById")
	public String findUserInfoById(Model model) {
		User user=UserUtils.getUser();
		if ("2".equals(user.getUserType())) {
			String teachId = backTeacherExpansionService.findTeacherIdByUser(user.getId());
			return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/form?id="+teachId;
		}
		StudentExpansion studentExpansion=studentExpansionService.getByUserId(String.valueOf(user.getId()));//查出用户基本信息
		if (studentExpansion!=null) {
			studentExpansion.setUser(UserUtils.get(studentExpansion.getUser().getId()));
			if (studentExpansion.getUser()!=null && studentExpansion.getUser().getPhoto()!=null && !studentExpansion.getUser().getPhoto().equals("")) {
				model.addAttribute("user", studentExpansion.getUser());
			}
			if (studentExpansion.getUser()!=null&&
					studentExpansion.getUser().getOffice()!=null&&StringUtil.isNotEmpty(studentExpansion.getUser().getOffice().getId())) {
			Office office = officeService.get(studentExpansion.getUser().getOffice().getId());
			model.addAttribute("officeName", office.getName());
			model.addAttribute("officeId", office.getId());
			}
		}
		List<ProjectExpVo> projectExpVo=projectDeclareService.getExpsByUserId(user.getId());//查询项目经历
	    List<gContestUndergo> gContest=userService.findContestByUserId(user.getId());
		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContest", gContest);
		model.addAttribute("studentExpansion", studentExpansion);

		List<Dict> dictList = DictUtils.getDictList("technology_field");
		model.addAttribute("allDomains", dictList);
		return "modules/sys/front/frontUserInfo";
	}

	/**
	 * 修改密码跳转页面 addBy zhangzheng
	 * @return
	 */
	@RequestMapping(value="frontUserPassword")
	public String frontUserPassword(Model model) {
		User user=UserUtils.getUser();
		StudentExpansion studentExpansion=studentExpansionService.getByUserId(String.valueOf(user.getId()));//查出用户基本信息
		if (studentExpansion!=null) {
			if (studentExpansion.getUser().getPhoto()!=null && !studentExpansion.getUser().getPhoto().equals("")) {
				model.addAttribute("user", studentExpansion.getUser());
			}
		}
		model.addAttribute("studentExpansion", studentExpansion);
		return "modules/sys/front/frontUserPassword";
	}

	/**
	 * 修改密码 addBy zhangzheng
	 * @return
	 */
	@RequestMapping(value="updatePassWord")
	public String updatePassWord(String oldPassword, String newPassword, Model model,RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		if (StringUtil.isNotBlank(oldPassword) && StringUtil.isNotBlank(newPassword)) {
			if (SystemService.validatePassword(oldPassword, user.getPassword())) {
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				redirectAttributes.addFlashAttribute("message","修改密码成功");
				redirectAttributes.addFlashAttribute("type","1");
			}else{
				redirectAttributes.addFlashAttribute("message","修改密码失败，旧密码错误");
				redirectAttributes.addFlashAttribute("type","0");
			}
		}
		return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/frontUserPassword/?repage";
	}

	/**
	 * 修改手机号 addBy zhangzheng
	 * @return
	 */
	@RequestMapping(value="updateMobile")
	public String updateMobile(String mobile ,Model model,RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		if (StringUtil.isNotBlank(mobile) ) {
			user.setMobile(mobile);
			userService.updateMobile(user);
			redirectAttributes.addFlashAttribute("message","修改手机号成功");
			redirectAttributes.addFlashAttribute("type","1");
		}
		return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/frontUserPassword/?repage";
	}





	//修改信息
	@RequestMapping(value="updateUserInfo")
	public String updateUserInfo(HttpServletRequest request,Model model,StudentExpansion studentExpansion, RedirectAttributes redirectAttributes) {
		if (StringUtil.isNotBlank(studentExpansion.getId())) {
		 	String companyId = officeService.selelctParentId(studentExpansion.getUser().getOffice().getId());
		 	studentExpansion.getUser().setCompany(new Office());
		 	studentExpansion.getUser().getCompany().setId(companyId);
		 	studentExpansionService.updateAll(studentExpansion);
			addMessage(redirectAttributes, "修改学生信息成功");
		}

	 	User user=UserUtils.getUser();
	 	if (user!=null) {
	 		CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_ID_ + user.getId());
			CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		}
		return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/findUserInfoById/?repage";
	}

	@RequestMapping(value="uploadFTP")
	public String uploadFTP(HttpServletRequest request) {
		String arrUrl = request.getParameter("arrUrl");
		User user=UserUtils.getUser();
		if (user!=null) {
			user.setPhoto(arrUrl);
			userService.updateUser(user);
		}
		return "1";

	}

}