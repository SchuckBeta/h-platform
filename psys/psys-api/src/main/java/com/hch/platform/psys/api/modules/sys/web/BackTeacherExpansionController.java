package com.hch.platform.pcore.modules.sys.web;

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
import com.hch.platform.pcore.modules.project.vo.ProjectExpVo;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.Dict;
import com.hch.platform.pcore.modules.sys.entity.GContestUndergo;
import com.hch.platform.pcore.modules.sys.entity.Office;
import com.hch.platform.pcore.modules.sys.entity.TeacherKeyword;
import com.hch.platform.pcore.modules.sys.entity.User;
import com.hch.platform.pcore.modules.sys.service.BackTeacherExpansionService;
import com.hch.platform.pcore.modules.sys.service.OfficeService;
import com.hch.platform.pcore.modules.sys.service.TeacherKeywordService;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.entity.TeamUserRelation;
import com.hch.platform.pcore.modules.team.service.TeamUserHistoryService;
import com.hch.platform.pcore.modules.team.service.TeamUserRelationService;

/**
 * 导师扩展信息表Controller
 * @author l
 * @version 2017-03-31
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/backTeacherExpansion")
public class BackTeacherExpansionController extends BaseController {

	@Autowired
	private BackTeacherExpansionService backTeacherExpansionService;
	@Autowired
	private UserService  userService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private TeamUserHistoryService teamUserHistoryService;
	
	@Autowired
	private TeacherKeywordService teacherKeywordService;
	@ModelAttribute
	public BackTeacherExpansion get(@RequestParam(required=false) String id,Model model) {
		BackTeacherExpansion entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = backTeacherExpansionService.get(id);
		}
		if (entity == null) {
			entity = new BackTeacherExpansion();
		}
		List<Office> offices = officeService.findAll();
		model.addAttribute("offices", offices);
		return entity;
	}

	@RequiresPermissions("sys:backTeacherExpansion:view")
	@RequestMapping(value = {"list", ""})
	public String list(BackTeacherExpansion backTeacherExpansion, HttpServletRequest request, HttpServletResponse response, Model model) {
    if(backTeacherExpansion == null){
      backTeacherExpansion = new BackTeacherExpansion();
    }

		Page<BackTeacherExpansion> page = backTeacherExpansionService.findPage(new Page<BackTeacherExpansion>(request, response), backTeacherExpansion);
		if (backTeacherExpansion != null && backTeacherExpansion.getUser() != null
				&& backTeacherExpansion.getUser().getOffice() != null
				&& StringUtil.isNotBlank(backTeacherExpansion.getUser().getOffice().getId())) {
			model.addAttribute("xueyuanId",backTeacherExpansion.getUser().getOffice().getId() );
		}
		model.addAttribute("page", page);
		return "modules/sys/backTeacherExpansionList";
	}

	@RequiresPermissions("sys:backTeacherExpansion:view")
	@RequestMapping(value = "form")
	public String form(BackTeacherExpansion backTeacherExpansion, Model model,HttpServletRequest request) {
		String operateType = request.getParameter("operateType");
		model.addAttribute("operateType", operateType);
		model.addAttribute("backTeacherExpansion", backTeacherExpansion);

		if(backTeacherExpansion.getId()!=null){
			List <TeacherKeyword> tes=teacherKeywordService.getKeywordByTeacherid(backTeacherExpansion.getId());
			if(tes.size()>0){
				model.addAttribute("tes", tes);
			}
		}
		List<Dict> dictList = DictUtils.getDictList("technology_field");
		model.addAttribute("allDomains", dictList);
		//导师参评信息
		List<ProjectExpVo> projectExpVo=backTeacherExpansionService.findProjectByTeacherId(backTeacherExpansion.getUser().getId());//查询项目经历
		List<GContestUndergo> gContest=backTeacherExpansionService.findGContestByTeacherId(backTeacherExpansion.getUser().getId()); //查询大赛经历
		model.addAttribute("projectExpVo", projectExpVo);
		model.addAttribute("gContestExpVo", gContest);
		model.addAttribute("cuser", backTeacherExpansion.getUser().getId());
		return "modules/sys/backTeacherForm";

	}

	@RequiresPermissions("sys:backTeacherExpansion:edit")
	@RequestMapping(value = "save")
	public String save(BackTeacherExpansion backTeacherExpansion, Model model,RedirectAttributes redirectAttributes,HttpServletRequest request) {
		if (!beanValidator(model, backTeacherExpansion)) {
			return form(backTeacherExpansion, model,request);
		}

		if (StringUtil.isNotBlank(backTeacherExpansion.getId())) {
			User user=backTeacherExpansion.getUser();
			if(StringUtil.isNotEmpty(user.getId())&&teamUserHistoryService.getBuildingCountByUserId(user.getId())>0){//修改时有正在进行的项目大赛
				User old=UserUtils.get(user.getId());
				if(old!=null&&StringUtil.isNotEmpty(old.getId())){
					if(old.getUserType()!=null&&!old.getUserType().equals(user.getUserType())){//用户类型变化了
						addMessage(model, "保存失败，该用户有正在进行的项目或大赛，不能修改用户类型");
						return form(backTeacherExpansion, model,request);
					}else if(old.getUserType()!=null&&old.getUserType().equals(user.getUserType())&&"2".equals(old.getUserType())){//导师类型
						BackTeacherExpansion bte=backTeacherExpansionService.getByUserId(old.getId());
						if(bte!=null&&bte.getTeachertype()!=null&&!bte.getTeachertype().equals(backTeacherExpansion.getTeachertype())){//导师类型的用户导师来源发生变化
							addMessage(model, "保存失败，该用户有正在进行的项目或大赛，不能修改导师来源");
							return form(backTeacherExpansion, model,request);
						}
					}
				}
			}
			if(backTeacherExpansion.getTopShow().equals("1")){
				BackTeacherExpansion backTeacherExpansionNew =backTeacherExpansionService.findTeacherByTopShow(backTeacherExpansion.getTeachertype());
				if(backTeacherExpansionNew!=null){
					backTeacherExpansionNew.setTopShow("0");
					backTeacherExpansionService.updateAll(backTeacherExpansionNew);
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
			if(backTeacherExpansion.getTopShow().equals("1")){
				BackTeacherExpansion backTeacherExpansionNew =backTeacherExpansionService.findTeacherByTopShow(backTeacherExpansion.getTeachertype());
				if(backTeacherExpansionNew!=null){
					backTeacherExpansionNew.setTopShow("0");
					backTeacherExpansionService.updateAll(backTeacherExpansionNew);
				}
			}
		    String companyId = officeService.selelctParentId(backTeacherExpansion.getUser().getOffice().getId());
		    backTeacherExpansion.getUser().setCompany(new Office());
		    backTeacherExpansion.getUser().getCompany().setId(companyId);//设置学校id
			backTeacherExpansionService.saveAll(backTeacherExpansion);
		}
		backTeacherExpansionService.updateKeyword(backTeacherExpansion);
		addMessage(redirectAttributes, "保存导师扩展信息成功");
		return "redirect:"+Global.getAdminPath()+"/sys/backTeacherExpansion/?repage";
	}

	@RequiresPermissions("sys:backTeacherExpansion:edit")
	@RequestMapping(value = "delete")
	public String delete(BackTeacherExpansion backTeacherExpansion, RedirectAttributes redirectAttributes) {
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		teamUserRelation.setUser(backTeacherExpansion.getUser());
		teamUserRelation = teamUserRelationService.findUserById(teamUserRelation);
		if (teamUserRelation!=null) {
			addMessage(redirectAttributes, "该导师已加入团队，不能删除!");
			return "redirect:"+Global.getAdminPath()+"/sys/backTeacherExpansion/?repage";
		}
		backTeacherExpansionService.delete(backTeacherExpansion);
		User user= UserUtils.get(backTeacherExpansion.getUser().getId());
		userService.delete(user);
		addMessage(redirectAttributes, "删除导师扩展信息成功");
		return "redirect:"+Global.getAdminPath()+"/sys/backTeacherExpansion/?repage";
	}

	//批量删除 返回成功删除数，失败删除数
	@RequestMapping(value = "deleteBatch")
	@ResponseBody
	public String deleteBatch(String ids) {
		String[] idStr=ids.split(",");
		int successCount=0;
		int failCount=0;
		for (int i=0;i<idStr.length;i++) {
			BackTeacherExpansion backTeacherExpansion = backTeacherExpansionService.get(idStr[i]);
			TeamUserRelation teamUserRelation = new TeamUserRelation();
			teamUserRelation.setUser(backTeacherExpansion.getUser());
			teamUserRelation = teamUserRelationService.findUserById(teamUserRelation);
			if (teamUserRelation!=null) {
				failCount++;
			}else{
				successCount++;
				backTeacherExpansionService.delete(backTeacherExpansion);
				User user= UserUtils.get(backTeacherExpansion.getUser().getId());
				userService.delete(user);
			}
		}
		String message;
		if (failCount==0) {
			message="成功删除"+successCount+"个导师。";
		}else{
			message="成功删除"+successCount+"个导师。"+failCount+"个导师已加入团队，不能删除!";
		}
		return message;
	}



}