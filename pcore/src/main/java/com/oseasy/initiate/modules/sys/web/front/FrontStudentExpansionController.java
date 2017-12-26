package com.oseasy.initiate.modules.sys.web.front;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
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
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.interactive.service.SysViewsService;
import com.oseasy.initiate.modules.interactive.util.InteractiveUtil;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.GContestUndergo;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.BackTeacherExpansionService;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.StudentExpansionService;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.service.TeamService;

import net.sf.json.JSONObject;

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
	private BackTeacherExpansionService backTeacherExpansionService;
	@Autowired
	SystemService systemService;
	@Autowired
	SysViewsService sysViewsService;

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
		String currentId = UserUtils.getUser().getId();
		studentExpansion.setTeamLeaderId(currentId);
		Page<StudentExpansion> page = studentExpansionService.findPage(new Page<StudentExpansion>(request, response),studentExpansion);

		Team team=new Team();
		team.setSponsor(currentId);
		team.setState("0");
		List<Team> teams=teamService.findListByCreatorIdAndState(team);//建设中的团队
		String stuFullTeams=getFullStarffedTeams(teams, 1);//学生满员的
		if (StringUtil.isNotEmpty(currentId)&&teams!=null&&teams.size()>0) {
			model.addAttribute("canInvite",true);
        	for (StudentExpansion studentExp: page.getList()) {//每个被邀请人需单独判断
        		studentExp.setCanInviteTeamIds(removeFullStarffedTeams(stuFullTeams, studentExp.getCanInviteTeamIds()));//剔除满员的
        		if (StringUtil.isEmpty(studentExp.getCanInviteTeamIds())) {
        			studentExp.setCanInvite(false);
        		}else{
        			studentExp.setCanInvite(true);
        		}
			}
		}else{//没有可用于邀请的团队
			model.addAttribute("canInvite", false);
		}
		model.addAttribute("page", page);
		model.addAttribute("teams", teams);
		return "modules/sys/front/frontStudentExpansionList";
	}
	private String removeFullStarffedTeams(String stuFullTeams,String canInviteTeamIds){
		if(stuFullTeams==null){
			return canInviteTeamIds;
		}
		if(StringUtil.isEmpty(canInviteTeamIds)){
			return canInviteTeamIds;
		}
		List<String> canInviteTeamIdsList=new ArrayList<String>(Arrays.asList(canInviteTeamIds.split(",")));
		for(int i=0;i<canInviteTeamIdsList.size();i++){
			if(stuFullTeams.contains(canInviteTeamIdsList.get(i))){
				canInviteTeamIdsList.remove(i);
				i--;
			}
		}
		if(canInviteTeamIdsList!=null&&canInviteTeamIdsList.size()>0){
			canInviteTeamIds=org.apache.commons.lang3.StringUtils.join(canInviteTeamIdsList,",");
		}else{
			canInviteTeamIds=null;
		}
		return canInviteTeamIds;
	}
	private String getFullStarffedTeams(List<Team> teams,int type){
		if(teams!=null&&teams.size()>0){
			List<String> list=new ArrayList<String>();
			for(Team t:teams){
				if(type==1&&t.getUserCount()>=t.getMemberNum()){//学生满
					list.add(t.getId());
				}else if(type==2&&t.getSchoolNum()>=t.getSchoolTeacherNum()){//校内导师满
					list.add(t.getId());
				}else if(type==3&&t.getEnterpriseNum()>=t.getEnterpriseTeacherNum()){//企业导师满
					list.add(t.getId());
				}
			}
			if(list.size()>0){
				return org.apache.commons.lang3.StringUtils.join(list,",");
			}
		}
		return null;
	}
	@RequestMapping(value = "form")
	public String form(StudentExpansion studentExpansion, Model model,HttpServletRequest request) {
		List<ProjectExpVo> projectExpVo=studentExpansionService.findProjectByStudentId(studentExpansion.getUser().getId());//查询项目经历
		List<GContestUndergo> gContest=studentExpansionService.findGContestByStudentId(studentExpansion.getUser().getId()); //查询大赛经历
		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContestExpVo", gContest);
		model.addAttribute("cuser", studentExpansion.getUser().getId());
		String mobile=studentExpansion.getUser().getMobile();
		//判断studentExpansion与当前登录人是否在一个团队,如果不在一个团队，则电话号码隐藏中间四位
	    String studentId =	studentExpansion.getUser().getId();
		String userId = UserUtils.getUser().getId();
		if(!teamService.findTeamByUserId(userId,studentId)&&mobile!=null){
			mobile=mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
		}
		model.addAttribute("mobile", mobile);
		if(StringUtil.isEmpty(studentExpansion.getUser().getViews())){
			studentExpansion.getUser().setViews("0");
		}
		if(StringUtil.isEmpty(studentExpansion.getUser().getLikes())){
			studentExpansion.getUser().setLikes("0");
		}
		/*记录浏览量*/
		User user= UserUtils.getUser();
    	if(user!=null&&StringUtil.isNotEmpty(user.getId())&&!user.getId().equals(studentExpansion.getUser().getId())){
    		InteractiveUtil.updateViews(studentExpansion.getUser().getId(), request,CacheUtils.USER_VIEWS_QUEUE);
    	}
		/*记录浏览量*/
		/*查询谁看过它*/
		model.addAttribute("visitors", sysViewsService.getVisitors(studentExpansion.getUser().getId()));
		return "modules/sys/front/frontStudentExpansionForm";
	}


	@RequestMapping(value = "toInvite")
	@ResponseBody
	public JSONObject toInvite(String userIds,String userType,String teamId) {
		return studentExpansionService.toInvite(userIds, userType, teamId);
	}



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
		String operateType = request.getParameter("operateType");
		model.addAttribute("operateType", operateType);
		model.addAttribute("studentExpansion", studentExpansion);
		List<Dict> dictList = DictUtils.getDictList("technology_field");
		model.addAttribute("allDomains", dictList);
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
	    	if(StringUtil.isNotEmpty(studentExpansion.getUser().getName())){//反转义名字
		 		studentExpansion.getUser().setName(StringEscapeUtils.unescapeHtml4(studentExpansion.getUser().getName()));
		 	}
			studentExpansionService.updateAll(studentExpansion);
		}else {

		}
	    String[] arrUrl= request.getParameterValues("arrUrl");
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
	public String findUserInfoById(String custRedict,String okurl,String backurl,Model model,HttpServletRequest request) {
		if(StringUtil.isEmpty(custRedict)){
			custRedict=(String)model.asMap().get("custRedict");
			okurl=(String)model.asMap().get("okurl");
			backurl=(String)model.asMap().get("backurl");
		}
		if("1".equals(custRedict)){
			if(StringUtil.isEmpty(okurl)){
				String reqreferer=request.getHeader("referer");
				if(reqreferer.contains("/infoPerfect")){
					okurl="/f";
				}else{
					okurl=reqreferer;
				}
				backurl=reqreferer;
			}
			
		}
		model.addAttribute("custRedict",custRedict);
		model.addAttribute("okurl",okurl);
		model.addAttribute("backurl",backurl);
//		String reqreferer = referrer;
//		if(StringUtil.isEmpty(referrer)){
//			reqreferer=request.getHeader("referer");
//		}else{
//			try {
//				reqreferer=URLDecoder.decode(StringEscapeUtils.unescapeHtml4(reqreferer), "utf-8");
//			} catch (UnsupportedEncodingException e) {
//				logger.error(e);
//			}
//		}
		User user=UserUtils.getUser();
		if ("2".equals(user.getUserType())) {
			String teachId = backTeacherExpansionService.findTeacherIdByUser(user.getId());
			return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/form?custRedict=1&id="+teachId;
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
			if(office!=null){
				model.addAttribute("officeName", office.getName());
				model.addAttribute("officeId", office.getId());
			}
			}
		}
		List<ProjectExpVo> projectExpVo=studentExpansionService.findProjectByStudentId(user.getId());//查询项目经历
		List<GContestUndergo> gContest=studentExpansionService.findGContestByStudentId(user.getId()); //查询大赛经历
		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContestExpVo", gContest);
		model.addAttribute("cuser", user.getId());
		model.addAttribute("studentExpansion", studentExpansion);

		List<Dict> dictList = DictUtils.getDictList("technology_field");
		model.addAttribute("allDomains", dictList);

		if((studentExpansion != null) && (studentExpansion.getUser() != null)){
  		/*查询谁看过它*/
  		model.addAttribute("visitors", sysViewsService.getVisitors(user.getId()));
      /*查询我看过谁*/
      model.addAttribute("browse", sysViewsService.getBrowse(user.getId()));
		}
		return "modules/sys/front/frontUserInfo";
	}

	/**
	 * 修改密码跳转页面 addBy zhangzheng
	 * @return
	 */
	@RequestMapping(value="frontUserPassword")
	public String frontUserPassword(Model model) {
		User user=UserUtils.getUser();
		if("1".equals(user.getUserType())){
			StudentExpansion studentExpansion=studentExpansionService.getByUserId(String.valueOf(user.getId()));//查出用户基本信息
			if (studentExpansion!=null) {
				if (studentExpansion.getUser().getPhoto()!=null && !studentExpansion.getUser().getPhoto().equals("")) {
					model.addAttribute("user", studentExpansion.getUser());
				}
			}
			model.addAttribute("studentExpansion", studentExpansion);
		}
		if("2".equals(user.getUserType())){
			BackTeacherExpansion studentExpansion=backTeacherExpansionService.getByUserId(String.valueOf(user.getId()));//查出用户基本信息
			if (studentExpansion!=null) {
				if (studentExpansion.getUser().getPhoto()!=null && !studentExpansion.getUser().getPhoto().equals("")) {
					model.addAttribute("user", studentExpansion.getUser());
				}
			}
			model.addAttribute("studentExpansion", studentExpansion);
		}
		return "modules/sys/front/frontUserPassword";
	}

	/**
	 * 修改密码跳转页面 addBy chenh
	 * @return
	 */
	@RequestMapping(value="frontUserMobile")
	public String frontUserMobile(Model model) {
	  User user=UserUtils.getUser();
	  if("1".equals(user.getUserType())){
		  StudentExpansion studentExpansion=studentExpansionService.getByUserId(String.valueOf(user.getId()));//查出用户基本信息
		  if (studentExpansion!=null) {
		    if (studentExpansion.getUser().getPhoto()!=null && !studentExpansion.getUser().getPhoto().equals("")) {
		      model.addAttribute("user", studentExpansion.getUser());
		    }
		  }
		  model.addAttribute("studentExpansion", studentExpansion);
	  }
	  if("2".equals(user.getUserType())){
			BackTeacherExpansion studentExpansion=backTeacherExpansionService.getByUserId(String.valueOf(user.getId()));//查出用户基本信息
			if (studentExpansion!=null) {
				if (studentExpansion.getUser().getPhoto()!=null && !studentExpansion.getUser().getPhoto().equals("")) {
					model.addAttribute("user", studentExpansion.getUser());
				}
			}
			model.addAttribute("studentExpansion", studentExpansion);
		}
	  return "modules/sys/front/frontUserMobile";
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
				if(oldPassword.equals(newPassword)){
					redirectAttributes.addFlashAttribute("message","修改密码失败，新密码不能与原密码一致");
					redirectAttributes.addFlashAttribute("type","0");
					return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/frontUserPassword?repage";
				}
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				if(UserUtils.checkInfoPerfect(user)){
					redirectAttributes.addFlashAttribute("message","修改密码成功,请继续完善个人信息");
					redirectAttributes.addFlashAttribute("type","1");
				}else{
					redirectAttributes.addFlashAttribute("message","修改密码成功");
					redirectAttributes.addFlashAttribute("type","1");
				}
				if("1".equals(user.getUserType())){
					return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/findUserInfoById?repage";
				}else{
					return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/form?id="+backTeacherExpansionService.getByUserId(user.getId()).getId();
				}
			}else{
				redirectAttributes.addFlashAttribute("message","修改密码失败，旧密码错误");
				redirectAttributes.addFlashAttribute("type","0");
			}
		}
		return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/frontUserPassword?repage";
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
		if("1".equals(user.getUserType())){
			return "redirect:"+Global.getFrontPath()+"/sys/frontStudentExpansion/findUserInfoById?repage";
		}else{
			return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/form?id="+backTeacherExpansionService.getByUserId(user.getId()).getId();
		}
	}





	//修改信息
	@RequestMapping(value="updateUserInfo")
	public String updateUserInfo(String custRedict,String okurl,String backurl,HttpServletRequest request,Model model,StudentExpansion studentExpansion, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("custRedict", custRedict);
		redirectAttributes.addFlashAttribute("okurl", okurl);
		redirectAttributes.addFlashAttribute("backurl", backurl);
		if (StringUtil.isNotBlank(studentExpansion.getId())) {
			String enterdatedate=(String)request.getParameter("enterdate");
			DateFormat df = new SimpleDateFormat("yyyy");
			try {
				Date enDate = df.parse(enterdatedate);
				studentExpansion.setEnterdate(enDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String companyId = officeService.selelctParentId(studentExpansion.getUser().getOffice().getId());
		 	studentExpansion.getUser().setCompany(new Office());
		 	studentExpansion.getUser().getCompany().setId(companyId);
		 	if(StringUtil.isNotEmpty(studentExpansion.getUser().getName())){//反转义名字
		 		studentExpansion.getUser().setName(StringEscapeUtils.unescapeHtml4(studentExpansion.getUser().getName()));
		 	}
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