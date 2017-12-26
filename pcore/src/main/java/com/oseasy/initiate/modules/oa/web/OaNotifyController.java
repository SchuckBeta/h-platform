/**
 *
 */
package com.hch.platform.pcore.modules.oa.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hch.platform.pcore.modules.actyw.entity.ActYw;
import com.hch.platform.pcore.modules.actyw.service.ActYwService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.oa.entity.OaNotify;
import com.hch.platform.pcore.modules.oa.entity.OaNotifyRecord;
import com.hch.platform.pcore.modules.oa.service.OaNotifyKeywordService;
import com.hch.platform.pcore.modules.oa.service.OaNotifyService;
import com.hch.platform.pcore.modules.project.entity.ProjectAnnounce;
import com.hch.platform.pcore.modules.project.service.ProjectAnnounceService;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.service.TeamService;
import com.hch.platform.pcore.modules.team.service.TeamUserRelationService;

/**
 * 通知通告Controller
TODO
 * @version 2014-05-16
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/oaNotify")
public class OaNotifyController extends BaseController {
	@Autowired
	private OaNotifyKeywordService oaNotifyKeywordService;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	TeamService teamService;
	@Autowired
	TeamUserRelationService  teamUserRelationService;
	@Autowired
	private ProjectAnnounceService projectAnnounceService;
	@Autowired
	ActYwService actYwService;

	@ModelAttribute
	public OaNotify get(@RequestParam(required=false) String id) {
		OaNotify entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = oaNotifyService.get(id);
		}
		if (entity == null) {
			entity = new OaNotify();
		}
		return entity;
	}

	@RequiresPermissions("oa:oaNotify:view")
	@RequestMapping(value = {"list", ""})
	public String list(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
		AbsUser currUser = UserUtils.getUser();
		//logger.info("curre========="+currUser.getId());
		if (currUser!=null&&currUser.getId()!=null&&!"1".equals(currUser.getId())) {
			oaNotify.setUserId(String.valueOf(currUser.getId()));
		}
		oaNotify.setIsSelf(true);
		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify);
		model.addAttribute("page", page);
		return "modules/oa/oaNotifyList";
	}



	@RequiresPermissions("oa:oaNotify:view")
	@RequestMapping(value = {"broadcastList"})
	public String broadcastList(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
		oaNotify.setSendType("1");
		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify);
		model.addAttribute("page", page);
		return "modules/oa/oaNotifyListBroadcast";
	}

	@RequiresPermissions("oa:oaNotify:view")
	@RequestMapping(value = {"assignList"})
	public String assignList(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
		oaNotify.setSendType("2");
		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify);
		model.addAttribute("page", page);
		return "modules/oa/oaNotifyListAssign";
	}

	@RequiresPermissions("oa:oaNotify:view")
	@RequestMapping(value = "form")
	public String form(OaNotify oaNotify, Model model) {
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			oaNotify = oaNotifyService.getRecordList(oaNotify);
		}
		model.addAttribute("oaNotify", oaNotify);
		if ("1".equals(oaNotify.getType())) {  //团建通知
			String teamId=oaNotify.getContent();
			Team team=teamService.get(teamId);
			model.addAttribute("team", team);
			return "modules/oa/oaNotifyTeam";
		}

		return "modules/oa/oaNotifyForm";
	}

	@RequiresPermissions("oa:oaNotify:view")
	@RequestMapping(value = "formAssign")
	public String formAssign(OaNotify oaNotify, Model model) {
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			oaNotify = oaNotifyService.getRecordList(oaNotify);
		}
		model.addAttribute("oaNotify", oaNotify);
		/*if ("1".equals(oaNotify.getType())) {  //团建通知
			String teamId=oaNotify.getContent();
			Team team=teamService.get(teamId);
			model.addAttribute("team", team);
			return "modules/oa/oaNotifyTeamAssign";
		}*/
		return "modules/oa/oaNotifyFormAssign";
	}

	@RequiresPermissions("oa:oaNotify:view")
	@RequestMapping(value = "formBroadcast")
	public String formBroadcast(OaNotify oaNotify, Model model,HttpServletRequest request) {
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			oaNotify = oaNotifyService.getRecordList(oaNotify);
		}

		if(StringUtil.isNotBlank(oaNotify.getId())){
			oaNotify=oaNotifyService.getRecordList(oaNotify);
			List<String> officeIdList= Lists.newArrayList();
			List<String> officeNameList = Lists.newArrayList();
			for(OaNotifyRecord onr:oaNotify.getOaNotifyRecordList()){
				if(!officeIdList.contains(onr.getUser().getOffice().getId())){
					officeIdList.add(onr.getUser().getOffice().getId());
				}
				if(!officeNameList.contains(onr.getUser().getOffice().getName())){
					officeNameList.add(onr.getUser().getOffice().getName());
				}
			}
			model.addAttribute("officeIdList", StringUtil.join(officeIdList, ","));
			model.addAttribute("officeNameList",  StringUtil.join(officeNameList, ","));
		}

		model.addAttribute("oaNotify", oaNotify);
//		if (oaNotify!=null) {
//			if (oaNotify.getStatus()!=null&&oaNotify.getStatus().equals("1")) {
//				return "modules/oa/oaNotifyFormBroadcastDetail";
//			}
//		}

		return "modules/oa/oaNotifyFormBroadcast";
	}


	@RequiresPermissions("oa:oaNotify:edit")
	@RequestMapping(value = "saveBroadcast")
	public String saveBroadcast(OaNotify oaNotify, Model model, RedirectAttributes redirectAttributes,HttpServletRequest request) {

		String sId = oaNotify.getsId();
		String protype =  oaNotify.getProtype();

		if("1".equals(oaNotify.getSendType())){//通知公告
			oaNotify.setType("3");
		}
		//如果是定向发送，则跳转到定向发送的保存方法
		if ("2".equals(oaNotify.getSendType())) {
			String returnStr = saveAssignBySendType(oaNotify,model,redirectAttributes);
			return returnStr;
		}


		oaNotifyService.saveCollegeBroadcast(oaNotify);
		addMessage(redirectAttributes, "保存通知'" + oaNotify.getTitle() + "'成功");

		if (StringUtil.isNoneBlank(sId)) {
			if ("'1'".equals(protype)) {
				ProjectAnnounce projectAnnounce = new ProjectAnnounce();
				projectAnnounce = projectAnnounceService.get(sId);
				projectAnnounce.setProjectState("1");
				projectAnnounceService.save(projectAnnounce);
				return "redirect:" + adminPath + "/project/projectAnnounce?repage";
			}else if("3".equals(protype)){  //actYw
				ActYw actYw = actYwService.get(sId);
				actYw.setStatus("1");
				actYwService.save(actYw);
				return "redirect:" + adminPath + "/actyw/actYw?repage";
			} else{
				return "redirect:" + adminPath + "/gcontest/gContestAnnounce?repage";
			}
		}
		return "redirect:" + adminPath + "/oa/oaNotify/broadcastList?repage";
	}


	public String saveAssignBySendType(OaNotify oaNotify, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, oaNotify)) {
			return form(oaNotify, model);
		}
		if (StringUtil.isNoneBlank(oaNotify.getsId())) {
			oaNotifyService.saveCollegeBroadcast(oaNotify);
			addMessage(redirectAttributes, "保存通知'" + oaNotify.getTitle() + "'成功");
			if ("'1'".equals(oaNotify.getProtype())) {
				return "redirect:" + adminPath + "/project/projectAnnounce?repage";
			}else{
				return "redirect:" + adminPath + "/gcontest/gContestAnnounce?repage";
			}
		}
		// 如果是修改，则状态为已发布，则不能再进行操作
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			OaNotify e = oaNotifyService.get(oaNotify.getId());
			if ("1".equals(e.getStatus())) {
				addMessage(redirectAttributes, "已发布，不能操作！");
				return "redirect:" + adminPath + "/oa/oaNotify/formBroadcast?id="+oaNotify.getId();
			}
		}
		oaNotify.setSendType("2");//定向的发送类型定为2
		oaNotifyService.saveCollege(oaNotify);
		addMessage(redirectAttributes, "保存通知'" + oaNotify.getTitle() + "'成功");
		return "redirect:" + adminPath + "/oa/oaNotify/broadcastList?repage";
	}



	@RequiresPermissions("oa:oaNotify:edit")
	@RequestMapping(value = "saveAssign")
	public String saveAssign(OaNotify oaNotify, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, oaNotify)) {
			return form(oaNotify, model);
		}
		// 如果是修改，则状态为已发布，则不能再进行操作
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			OaNotify e = oaNotifyService.get(oaNotify.getId());
			if ("1".equals(e.getStatus())) {
				addMessage(redirectAttributes, "已发布，不能操作！");
				return "redirect:" + adminPath + "/oa/oaNotify/formAssign?id="+oaNotify.getId();
			}
		}
		oaNotify.setSendType("2");//定向的发送类型定为2
		oaNotifyService.save(oaNotify);
		addMessage(redirectAttributes, "保存通知'" + oaNotify.getTitle() + "'成功");
		return "redirect:" + adminPath + "/oa/oaNotify/assignList?repage";
	}


	@RequiresPermissions("oa:oaNotify:edit")
	@RequestMapping(value = "save")
	public String save(OaNotify oaNotify, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, oaNotify)) {
			return form(oaNotify, model);
		}
		// 如果是修改，则状态为已发布，则不能再进行操作
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			OaNotify e = oaNotifyService.get(oaNotify.getId());
			if ("1".equals(e.getStatus())) {
				addMessage(redirectAttributes, "已发布，不能操作！");
				return "redirect:" + adminPath + "/oa/oaNotify/form?id="+oaNotify.getId();
			}
		}
		oaNotifyService.save(oaNotify);
		addMessage(redirectAttributes, "保存通知'" + oaNotify.getTitle() + "'成功");
		return "redirect:" + adminPath + "/oa/oaNotify/?repage";
	}


	@RequiresPermissions("oa:oaNotify:edit")
	@RequestMapping(value = "delete")
	public String delete(OaNotify oaNotify, RedirectAttributes redirectAttributes) {
		oaNotifyService.delete(oaNotify);
		addMessage(redirectAttributes, "删除通告成功");
		return "redirect:" + adminPath + "/oa/oaNotify/broadcastList?repage";
	}

	/**
	 * 我的通知列表
	 */
	@RequestMapping(value = "self")
	public String selfList(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
		oaNotify.setIsSelf(true);
		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify);
		model.addAttribute("page", page);
		return "modules/oa/oaNotifyList";
	}



	/**
	 * 我的通知列表-数据
	 */
	@RequiresPermissions("oa:oaNotify:view")
	@RequestMapping(value = "selfData")
	@ResponseBody
	public Page<OaNotify> listData(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
		oaNotify.setIsSelf(true);
		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify);
		return page;
	}

	/**
	 * 查看我的通知
	 */
	@RequestMapping(value = "view")
	public String view(OaNotify oaNotify, Model model) {
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			oaNotifyService.updateReadFlag(oaNotify);
			oaNotify = oaNotifyService.getRecordList(oaNotify);
			if (oaNotify!=null&&StringUtil.isNotEmpty(oaNotify.getContent())) {
				oaNotify.setContent(oaNotify.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
			}
			model.addAttribute("oaNotify", oaNotify);
			/*if ("1".equals(oaNotify.getType())) {  //团建通知
				//根据team 查询团建信息
				String teamId=oaNotify.getContent();
				Team team=teamService.get(teamId);
				model.addAttribute("team", team);

//				OaNotifyRecord oaNotifyRecord=



//				//根据 teamId、userId 查询team_user_relation的状态
//				TeamUserRelation tu=new TeamUserRelation();
//				tu.setTeamId(teamId);
//				User user= UserUtils.getUser();
//				tu.setUser(user);
//				TeamUserRelation  teamUserRelation=teamUserRelationService.getByTeamAndUser(tu);
//				String state=teamUserRelation.getState();
//				model.addAttribute("state", state);  //state为0可以同意、不同意、忽略
				return "modules/oa/oaNotifyTeam";
			}*/
			if (oaNotify!=null) {
				if ("1".equals(oaNotify.getSendType())) {
					return "modules/oa/oaNotifyFormBroadcast";
				}else if ("2".equals(oaNotify.getSendType())) {
					return "modules/oa/oaNotifyFormAssign";
				}
			}else{
			  return "redirect:" + adminPath + "/oa/oaNotify/self?repage";
			}

			//return "modules/oa/oaNotifyForm";
		}
		return "redirect:" + adminPath + "/oa/oaNotify/self?repage";
	}

	/**
	 * 查看我的通知-数据
	 */
	@RequestMapping(value = "viewData")
	@ResponseBody
	public OaNotify viewData(OaNotify oaNotify, Model model) {
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			oaNotifyService.updateReadFlag(oaNotify);
			return oaNotify;
		}
		return null;
	}

	/**
	 * 查看我的通知-发送记录
	 */
	@RequestMapping(value = "viewRecordData")
	@ResponseBody
	public OaNotify viewRecordData(OaNotify oaNotify, Model model) {
		if (StringUtil.isNotBlank(oaNotify.getId())) {
			oaNotify = oaNotifyService.getRecordList(oaNotify);
			return oaNotify;
		}
		return null;
	}

	/**
	 * 获取我的通知数目
	 */
	@RequestMapping(value = "self/count")
	@ResponseBody
	public String selfCount(OaNotify oaNotify, Model model) {
		oaNotify.setIsSelf(true);
		oaNotify.setReadFlag("0");
		return String.valueOf(oaNotifyService.findCount(oaNotify));
	}
	/**
	 * 获取我的通知数目
	 */
	@RequestMapping(value = "validateName")
	@ResponseBody
	public String validateName(String title,String oldTitle) {
		if(StringUtil.equals(title,oldTitle)){
			return "0";
		}
		OaNotify oaNotify = new OaNotify();
		oaNotify.setTitle(title);
		return String.valueOf(oaNotifyService.findCount(oaNotify));
	}

	@RequestMapping(value = "checkTitle")
	@ResponseBody
	public boolean checkTitle(String title,String oldTitle) {
		if (StringUtil.equals(title,oldTitle)) {
			return true;
		}

		OaNotify oaNotify = new OaNotify();
		oaNotify.setTitle(title);
		if ( oaNotifyService.findCount(oaNotify)>0) {
			return false;
		}
		return true;
	}
	//通告添加
	@RequestMapping(value = "allNoticeForm")
	public String allNoticeForm(OaNotify oaNotify, Model model, RedirectAttributes redirectAttributes) {

	    if(oaNotify != null){
	  	  //处理关键字
	  		if(StringUtil.isNotEmpty(oaNotify.getId())){
	  			if("4".equals(oaNotify.getType())||"8".equals(oaNotify.getType())||"9".equals(oaNotify.getType())){
	  					oaNotify.setKeywords(oaNotifyKeywordService.findListByEsid(oaNotify.getId()));
	  			}
	  		}
	  		if (oaNotify!=null&&StringUtil.isNotEmpty(oaNotify.getContent())) {
	  			oaNotify.setContent(oaNotify.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
	  		}
	    }
		return  "modules/oa/notice/allNoticeForm";
	}
	@RequestMapping(value = "saveAllNotice")
	public String saveAllNotice(OaNotify oaNotify, Model model, RedirectAttributes redirectAttributes) {
		oaNotifyService.saveCollegeBroadcast(oaNotify);
		addMessage(redirectAttributes, "保存通告'" + oaNotify.getTitle() + "'成功");
		return "redirect:" + adminPath + "/oa/oaNotify/broadcastList";
	}

}