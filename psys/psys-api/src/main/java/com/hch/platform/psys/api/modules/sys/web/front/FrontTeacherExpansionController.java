package com.hch.platform.pcore.modules.sys.web.front;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.interactive.service.SysViewsService;
import com.hch.platform.pcore.modules.interactive.util.InteractiveUtil;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.project.vo.ProjectExpVo;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.Dict;
import com.hch.platform.pcore.modules.sys.entity.GContestUndergo;
import com.hch.platform.pcore.modules.sys.entity.Office;
import com.hch.platform.pcore.modules.sys.entity.User;
import com.hch.platform.pcore.modules.sys.service.BackTeacherExpansionService;
import com.hch.platform.pcore.modules.sys.service.OfficeService;
import com.hch.platform.pcore.modules.sys.service.StudentExpansionService;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.service.TeamService;
import com.hch.platform.pcore.modules.team.service.TeamUserHistoryService;

import net.sf.json.JSONObject;

/**
 * 导师扩展信息表Controller
 * @author l
 * @version 2017-03-31
 */
@Controller
@RequestMapping(value = "${frontPath}/sys/frontTeacherExpansion")
public class FrontTeacherExpansionController extends BaseController {
	@Autowired
	private OfficeService officeService;
	@Autowired
	private BackTeacherExpansionService backTeacherExpansionService;
	@Autowired
	private StudentExpansionService studentExpansionService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private UserService userService;
	@Autowired
	ProjectDeclareService projectDeclareService;
	@Autowired
	SysViewsService sysViewsService;
	
	@Autowired
	private TeamUserHistoryService teamUserHistoryService;
	@ModelAttribute
	public BackTeacherExpansion get(@RequestParam(required=false) String id,Model model) {
		BackTeacherExpansion entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = backTeacherExpansionService.get(id);
		}
		if (entity == null) {
			entity = new BackTeacherExpansion();
		}
		List<Office> offices = officeService.findList(true);
		model.addAttribute("offices", offices);
		return entity;
	}

	@RequestMapping(value = {"list", ""})
	public String list(BackTeacherExpansion backTeacherExpansion, HttpServletRequest request, HttpServletResponse response, Model model) {
		String currentId = UserUtils.getUser().getId();
		backTeacherExpansion.setTeamLeaderId(currentId);

		backTeacherExpansion.setIsFront("1");
		backTeacherExpansion.setIsOpen("1");
		Page<BackTeacherExpansion> page = backTeacherExpansionService.findPage(new Page<BackTeacherExpansion>(request, response), backTeacherExpansion);

		Team team=new Team();
		team.setSponsor(currentId);
		team.setState("0");
		List<Team> teams=teamService.findListByCreatorIdAndState(team);
		String steaFullTeams=getFullStarffedTeams(teams, 2);//校园导师满员的
		String eteaFullTeams=getFullStarffedTeams(teams, 3);//企业导师满员的
		if (StringUtil.isNotEmpty(currentId)&&teams!=null&&teams.size()>0) {
			model.addAttribute("canInvite",true);
        	for (BackTeacherExpansion studentExp: page.getList()) {//每个被邀请人需单独判断
        		if("2".equals(studentExp.getTeachertype())){
        			studentExp.setCanInviteTeamIds(removeFullStarffedTeams(eteaFullTeams, studentExp.getCanInviteTeamIds()));//剔除满员的
        		}else{
        			studentExp.setCanInviteTeamIds(removeFullStarffedTeams(steaFullTeams, studentExp.getCanInviteTeamIds()));//剔除满员的
        		}
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
		return "modules/sys/front/frontTeacherExpansionList";
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
	public String form(BackTeacherExpansion backTeacherExpansion,String custRedict,String okurl,String backurl,Model model,HttpServletRequest request) {
		if(!UserUtils.getUser().getId().equals(backTeacherExpansion.getUser().getId())){
			return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/view?id="+backTeacherExpansion.getId();
		}
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
		List<BackTeacherExpansion> teacherAwardInfo=backTeacherExpansionService.findTeacherAward(backTeacherExpansion.getUser().getId());
		if (teacherAwardInfo!=null) {
			 model.addAttribute("teacherAwardInfo", teacherAwardInfo);
		}
		List<Dict> dictList = DictUtils.getDictList("technology_field");
				model.addAttribute("allDomains", dictList);
		/*查询谁看过它*/
		model.addAttribute("visitors", sysViewsService.getVisitors(backTeacherExpansion.getUser().getId()));
		/*查询我看过谁*/
		model.addAttribute("browse", sysViewsService.getBrowse(backTeacherExpansion.getUser().getId()));
		//导师参评信息
		List<ProjectExpVo> projectExpVo=backTeacherExpansionService.findProjectByTeacherId(backTeacherExpansion.getUser().getId());//查询项目经历
		List<GContestUndergo> gContest=backTeacherExpansionService.findGContestByTeacherId(backTeacherExpansion.getUser().getId()); //查询大赛经历
		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContestExpVo", gContest);
		model.addAttribute("cuser", backTeacherExpansion.getUser().getId());
		return "modules/sys/front/editTeacher";
//		return "modules/sys/front/frontTeacherExpansionForm";
		
	}

	@RequestMapping(value = "save")
	public String save(BackTeacherExpansion backTeacherExpansion, Model model,String custRedict,String okurl,String backurl,RedirectAttributes redirectAttributes,HttpServletRequest request) {
		if (!beanValidator(model, backTeacherExpansion)) {
			return form(backTeacherExpansion, custRedict, okurl, backurl, model, request);
		}
		if (StringUtil.isNotBlank(backTeacherExpansion.getId())) {
			User user=backTeacherExpansion.getUser();
			if(StringUtil.isNotEmpty(user.getId())&&teamUserHistoryService.getBuildingCountByUserId(user.getId())>0){//修改时有正在进行的项目大赛
				User old=UserUtils.get(user.getId());
				if(old!=null&&StringUtil.isNotEmpty(old.getId())){
					if(old.getUserType()!=null&&!old.getUserType().equals(user.getUserType())){//用户类型变化了
						addMessage(model, "保存失败，该用户有正在进行的项目或大赛，不能修改用户类型");
						return form(backTeacherExpansion, custRedict, okurl, backurl, model, request);
					}else if(old.getUserType()!=null&&old.getUserType().equals(user.getUserType())&&"2".equals(old.getUserType())){//导师类型
						BackTeacherExpansion bte=backTeacherExpansionService.getByUserId(old.getId());
						if(bte!=null&&bte.getTeachertype()!=null&&!bte.getTeachertype().equals(backTeacherExpansion.getTeachertype())){//导师类型的用户导师来源发生变化
							addMessage(model, "保存失败，该用户有正在进行的项目或大赛，不能修改导师来源");
							return form(backTeacherExpansion, custRedict, okurl, backurl, model, request);
						}
					}
				}
			}
			backTeacherExpansionService.updateAll(backTeacherExpansion);
		}else {
			User exitUser = userService.getByMobile(backTeacherExpansion.getUser());
			if (exitUser != null) {
				List<Dict> dictList = DictUtils.getDictList("technology_field");
				model.addAttribute("allDomains", dictList);
				model.addAttribute("loginNameMessage","手机号已经存在!");
				model.addAttribute("operateType", "1");
				return "modules/sys/backTeacherForm";
			}
			String companyId = officeService.selelctParentId(backTeacherExpansion.getUser().getOffice().getId());
			backTeacherExpansion.getUser().setCompany(new Office());
			backTeacherExpansion.getUser().getCompany().setId(companyId);//设置学校id
			backTeacherExpansionService.saveAll(backTeacherExpansion);
		}

		addMessage(redirectAttributes, "保存导师扩展信息成功");
		redirectAttributes.addFlashAttribute("custRedict", custRedict);
		redirectAttributes.addFlashAttribute("okurl", okurl);
		redirectAttributes.addFlashAttribute("backurl", backurl);
		return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/form?id="+backTeacherExpansion.getId();
	}

	@RequestMapping(value = "view")
	public String view(BackTeacherExpansion backTeacherExpansion, Model model,HttpServletRequest request) {
		List<ProjectExpVo> projectExpVo=backTeacherExpansionService.findProjectByTeacherId(backTeacherExpansion.getUser().getId());//查询项目经历
		List<GContestUndergo> gContest=backTeacherExpansionService.findGContestByTeacherId(backTeacherExpansion.getUser().getId()); //查询大赛经历
		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContestExpVo", gContest);
		model.addAttribute("cuser", backTeacherExpansion.getUser().getId());
		if(StringUtil.isEmpty(backTeacherExpansion.getUser().getViews())){
			backTeacherExpansion.getUser().setViews("0");
		}
		if(StringUtil.isEmpty(backTeacherExpansion.getUser().getLikes())){
			backTeacherExpansion.getUser().setLikes("0");
		}
		String teaId = backTeacherExpansion.getUser().getId();
		String userId = UserUtils.getUser().getId();
		String mobile=backTeacherExpansion.getUser().getMobile();
		if(!teamService.findTeamByUserId(userId,teaId)&&mobile!=null){
			mobile=mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
		}
		model.addAttribute("mobile", mobile);
		/*记录浏览量*/
		User user= UserUtils.getUser();
    	if(user!=null&&StringUtil.isNotEmpty(user.getId())&&!user.getId().equals(backTeacherExpansion.getUser().getId())){
    		InteractiveUtil.updateViews(backTeacherExpansion.getUser().getId(), request,CacheUtils.USER_VIEWS_QUEUE);
    	}
		/*记录浏览量*/
		/*查询谁看过它*/
		model.addAttribute("visitors", sysViewsService.getVisitors(backTeacherExpansion.getUser().getId()));
		return "modules/sys/front/frontTeacherExpansionView";
	}

	@RequestMapping(value = "toInvite")
	@ResponseBody
	public JSONObject toInvite(String userIds,String userType,String teamId) {
		return studentExpansionService.toInvite(userIds, userType, teamId);
	}

	@RequestMapping(value = "invite")
	public String invite(BackTeacherExpansion backTeacherExpansion,Model model) {
		backTeacherExpansionService.ivite(backTeacherExpansion);
		return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/?repage";
	}
	@RequestMapping(value = "addTeacherByStu")
	@ResponseBody
	public JSONObject addTeacherByStu(String name,String no,String mobile,String type,String office,String profes) {
		try {
			return backTeacherExpansionService.addTeacherByStu(name, no, mobile, type,office,profes);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JSONObject js=new JSONObject();
			js.put("ret", 0);
			js.put("msg", "发生了未知的错误");
			return js;
		}
	}


}