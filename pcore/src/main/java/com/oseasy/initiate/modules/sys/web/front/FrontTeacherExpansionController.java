package com.oseasy.initiate.modules.sys.web.front;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
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
import com.oseasy.initiate.common.service.UoloadFtpService;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.BackTeacherExpansionService;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

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
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private UoloadFtpService uoloadFtpService;
	@Autowired
	private UserService userService;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	ProjectDeclareService projectDeclareService;


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
  		String curretId = UserUtils.getUser().getId();
//		if (StringUtil.isNotBlank(curretId)) {
//			if (backTeacherExpansion==null) {
//				backTeacherExpansion = new BackTeacherExpansion();
//			}
//		}

		backTeacherExpansion.setIsFront("1");
		backTeacherExpansion.setIsOpen("1");
		Page<BackTeacherExpansion> page = backTeacherExpansionService.findPage(new Page<BackTeacherExpansion>(request, response), backTeacherExpansion);

//		if (backTeacherExpansion != null && backTeacherExpansion.getUser() != null
//				&& backTeacherExpansion.getUser().getOffice() != null
//				&& StringUtil.isNotBlank(backTeacherExpansion.getUser().getOffice().getId())) {
//			model.addAttribute("xueyuanId",backTeacherExpansion.getUser().getOffice().getId() );
//		}
		//当前用户为团队发起者可邀请
		Team team=new Team();
		team.setSponsor(UserUtils.getUser().getId());
		List<Team> teams=teamService.findList(team);
		//List<Team> teams = backTeacherExpansionService.findTeamByUserId(UserUtils.getUser());
		model.addAttribute("canInvite",false);
		if (teams!=null&&teams.size()>0) {
			for (Team tm : teams) {
				if (tm!=null) {
					if (tm.getSponsor()!=null&&tm.getSponsor().equals(curretId)) {
						model.addAttribute("canInvite",true);

						if (page !=null) {
				        	List<BackTeacherExpansion>  teacherExp=null;
				        	teacherExp=page.getList();
				        	for (int i = 0; i < teacherExp.size(); i++) {
				        		//Integer result=oaNotifyService.findNotifyCount(teacherExp.get(i).getUser().getId(),tm.getId());
				        		Integer result= teamUserRelationService.findIsApplyTeam(tm.getId(), teacherExp.get(i).getUser().getId());
				        		if (result>0) {//已经邀请过
				        			BackTeacherExpansion backTeacherExpansion1= teacherExp.get(i);
				        			backTeacherExpansion1.setMsg("1");
				        			teacherExp.set(i, backTeacherExpansion1);
				        		}
				        	}
						}
						break;
					}
				}
			}
		}
//		List<Office> offices = officeService.findColleges();
//		model.addAttribute("offices",offices );
		model.addAttribute("page", page);
		return "modules/sys/front/frontTeacherExpansionList";
	}

	@RequestMapping(value = "form")
	public String form(BackTeacherExpansion backTeacherExpansion, Model model) {
		User user= userService.findUserById(backTeacherExpansion.getUser().getId());
		if (user!=null) {
			if (user.getPhoto()!=null&&!user.getPhoto().equals("")) {
				String img64= uoloadFtpService.downFtpByBase64(user.getPhoto());
				model.addAttribute("downImg64", img64);
			}
		}
			/*List<Dict> dictList = DictUtils.getDictList("technology_field");
			model.addAttribute("allDomains", dictList);*/
		  List<BackTeacherExpansion> teacherAwardInfo=backTeacherExpansionService.findTeacherAward(backTeacherExpansion.getUser().getId());
		  if (teacherAwardInfo!=null) {
			  model.addAttribute("teacherAwardInfo", teacherAwardInfo);
		  }
		List<Dict> dictList = DictUtils.getDictList("technology_field");
				model.addAttribute("allDomains", dictList);
			return "modules/sys/front/frontTeacherExpansionForm";
	}

	@RequestMapping(value = "save")
	public String save(BackTeacherExpansion backTeacherExpansion, Model model,RedirectAttributes redirectAttributes,HttpServletRequest request) {
		if (!beanValidator(model, backTeacherExpansion)) {
			return form(backTeacherExpansion, model);
		}
		if (StringUtil.isNotBlank(backTeacherExpansion.getId())) {
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
		return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/form?id="+backTeacherExpansion.getId();
	}

	@RequestMapping(value = "view")
	public String view(BackTeacherExpansion backTeacherExpansion, Model model) {
		List<ProjectExpVo> projectExpVo=projectDeclareService.getExpsByUserId(backTeacherExpansion.getUser().getId());//查询项目经历
		model.addAttribute("projectExpVo", projectExpVo);
		return "modules/sys/front/frontTeacherExpansionView";
	}

	@RequestMapping(value = "toInvite")
	@ResponseBody
	public JSONObject toInvite(StudentExpansion studentExpansion, Model model) {
		JSONObject json=new JSONObject();
		Team team=new Team();
		team.setSponsor(UserUtils.getUser().getId());
		List<Team> teamlist= teamService.findList(team);
		User applyUser= userService.findUserById(studentExpansion.getUser().getId());

		//插入申请记录
		if (teamUserRelationService.inseTeamUser(applyUser,teamlist.get(0).getId(),"2")<=0) {
			json.put("success", false);
			return json;
		}
		if (teamUserRelationService.inseOaNotify(UserUtils.getUser(), studentExpansion.getUser().getId(),(String)teamlist.get(0).getId())<=0) {
			json.put("success", false);
			return json;
		}
		json.put("success", true);
		return json;
	}

	@RequestMapping(value = "invite")
	public String invite(BackTeacherExpansion backTeacherExpansion,Model model) {
		backTeacherExpansionService.ivite(backTeacherExpansion);
		return "redirect:"+Global.getFrontPath()+"/sys/frontTeacherExpansion/?repage";
	}



}