package com.hch.platform.pcore.modules.sco.web;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmCriterionCouse;
import com.hch.platform.pcore.modules.sco.entity.ScoApply;
import com.hch.platform.pcore.modules.sco.entity.ScoAuditing;
import com.hch.platform.pcore.modules.sco.entity.ScoCourse;
import com.hch.platform.pcore.modules.sco.service.ScoAffirmCriterionCouseService;
import com.hch.platform.pcore.modules.sco.service.ScoApplyService;
import com.hch.platform.pcore.modules.sco.service.ScoAuditingService;
import com.hch.platform.pcore.modules.sco.service.ScoCourseService;
import com.hch.platform.pcore.modules.sys.entity.StudentExpansion;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.StudentExpansionService;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 学分课程申请Controller.
 * @author zhangzheng
 * @version 2017-07-13
 */
@Controller
@RequestMapping(value = "${adminPath}/scoapply")
public class ScoApplybackController extends BaseController {

	@Autowired
	private ScoApplyService scoApplyService;
	@Autowired
	ScoCourseService scoCourseService;
	@Autowired
	StudentExpansionService studentExpansionService;
	@Autowired
	SysAttachmentService sysAttachmentService;
	@Autowired
	ScoAuditingService scoAuditingService;
	@Autowired
	private SystemService systemService;
	@Autowired
	ScoAffirmCriterionCouseService scoAffirmCriterionCouseService;


	@ModelAttribute
	public ScoApply get(@RequestParam(required=false) String id) {
		ScoApply entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = scoApplyService.get(id);
		}
		if (entity == null){
			entity = new ScoApply();
		}
		return entity;
	}

	//课程学分认定列表
	@RequestMapping(value = "scoApplyList")
	public String scoApplyList(ScoApply scoApply, HttpServletRequest request, HttpServletResponse response, Model model) {
		scoApply.setUserId(UserUtils.getUser().getId());
		Page<ScoApply> page = scoApplyService.findPage(new Page<ScoApply>(request, response), scoApply);
		model.addAttribute("page", page);
		return "modules/sco/scoApply/scoApplyList";
	}

	//申请认定课程
	@RequestMapping(value = "scoApplyForm")
	public String scoApplyForm(ScoApply scoApply, Model model) {
		AbsUser user= UserUtils.getUser();
        Date date =new Date();
		StudentExpansion studentExpansion=studentExpansionService.getByUserId(user.getId());
		model.addAttribute("user", user);
		model.addAttribute("studentExpansion", studentExpansion);
		model.addAttribute("date", date);
		ScoCourse scoCourse = scoCourseService.get(scoApply.getCourseId());

		model.addAttribute("scoApply", scoApply);
		model.addAttribute("scoCourse", scoCourse);
		return "modules/sco/scoApply/scoApplyForm";
	}

	//添加认定课程
	@RequestMapping(value = "scoApplyAdd")
	public String scoApplyAdd(ScoApply scoApply, Model model){
		ScoCourse scoCourse =new ScoCourse();
		model.addAttribute("scoCourse", scoCourse);
		return "modules/sco/scoApply/scoApplyAdd";
	}

	//根据课程名或者课程代码查询学分课程
	@RequestMapping(value = "findListByNameOrCode")
	@ResponseBody
	public List<ScoCourse> findListByNameOrCode(HttpServletRequest request){
		String keyword = request.getParameter("keyword");
		List<ScoCourse> scoCourseList = scoCourseService.findListByNameOrCode(keyword);
		return scoCourseList;
	}
	//根据课程性质、课程类型、专业科别、课程名或者课程代码查询学分课程
	@RequestMapping(value = "findCourseList")
	@ResponseBody
	public List<ScoCourse> findCourseList(ScoCourse scoCourse){
		List<ScoCourse> scoCourseList = scoCourseService.findCourseList(scoCourse);
		return scoCourseList;
	}

	//保存添加申请
	@RequestMapping(value = "saveAdd")
	@ResponseBody
	public boolean saveAdd(ScoApply scoApply) {
		scoApply.setUserId(UserUtils.getUser().getId());
		scoApply.setAuditStatus("1"); //待提交认定
		scoApplyService.save(scoApply);
		return true;
	}
	//课程审核
	@RequestMapping(value = "auditForm")
	public String auditForm(ScoApply scoApply,HttpServletRequest request){
		String suggest=request.getParameter("suggest");
		scoApplyService.saveAudit(scoApply,suggest);
		return "redirect:/a/sco/scoreGrade/courseList/?repage";
	}

	//查看详情
	@RequestMapping(value = "view")
	public String view(ScoApply scoApply, Model model) {
		String userId= scoApply.getUserId();
		if(userId!=null){
			AbsUser user = systemService.getUser(userId);
			StudentExpansion studentExpansion=studentExpansionService.getByUserId(scoApply.getUserId());
			model.addAttribute("user", user);
			model.addAttribute("studentExpansion", studentExpansion);
		}

		ScoCourse scoCourse = scoCourseService.get(scoApply.getCourseId());

		model.addAttribute("scoApply", scoApply);
		model.addAttribute("scoCourse", scoCourse);
		//查找附件
		SysAttachment sysAttachment = new SysAttachment();
		sysAttachment.setUid(scoApply.getId());
		List<SysAttachment> attachmentList = sysAttachmentService.findList(sysAttachment);
		scoApply.setAttachmentList(attachmentList);
		//查找审核记录
		ScoAuditing scoAuditing = new ScoAuditing();
		scoAuditing.setApplyId(scoApply.getId());
		List<ScoAuditing> scoAuditingList = scoAuditingService.findList(scoAuditing);
		model.addAttribute("scoAuditingList", scoAuditingList);
		return "modules/sco/grade/scoCourseGradeView";
	}

	//审核详情
	@RequestMapping(value = "auditView")
	public String auditView(ScoApply scoApply, Model model) {
		String userId= scoApply.getUserId();
		if(userId!=null){
			AbsUser user = systemService.getUser(userId);
			StudentExpansion studentExpansion=studentExpansionService.getByUserId(scoApply.getUserId());
			model.addAttribute("user", user);
			model.addAttribute("studentExpansion", studentExpansion);
		}

		ScoCourse scoCourse = scoCourseService.get(scoApply.getCourseId());

		model.addAttribute("scoApply", scoApply);
		model.addAttribute("scoCourse", scoCourse);
		//查找附件
		SysAttachment sysAttachment = new SysAttachment();
		sysAttachment.setUid(scoApply.getId());
		List<SysAttachment> attachmentList = sysAttachmentService.findList(sysAttachment);
		scoApply.setAttachmentList(attachmentList);
		//查找审核记录
		ScoAuditing scoAuditing = new ScoAuditing();
		scoAuditing.setApplyId(scoApply.getId());
		List<ScoAuditing> scoAuditingList = scoAuditingService.findList(scoAuditing);
		model.addAttribute("scoAuditingList", scoAuditingList);
		//查询相应课程课时列表
		List<ScoAffirmCriterionCouse> couseNumList = scoAffirmCriterionCouseService.findListByFidCouseNum(scoApply.getCourseId());
		if(couseNumList!=null){
			String autoScore=null;
			for(ScoAffirmCriterionCouse index:couseNumList){
				int begin=Integer.valueOf(index.getStart());
				int end=Integer.valueOf(index.getEnd());
				if((scoApply.getRealTime()<end || scoApply.getRealTime()== end) && (scoApply.getRealTime()>begin||scoApply.getRealTime()==begin)){
					List<ScoAffirmCriterionCouse> couseScoreList = scoAffirmCriterionCouseService.findListByParentId(index.getId());
					if(couseScoreList!=null){
						for(ScoAffirmCriterionCouse indexScore:couseScoreList){
							int beginScore=Integer.valueOf(indexScore.getStart());
							int endScore=Integer.valueOf(indexScore.getEnd());
							if((scoApply.getRealScore()<endScore || scoApply.getRealScore()== endScore) &&
									(scoApply.getRealScore()>beginScore|| scoApply.getRealScore()== beginScore)){
								autoScore=indexScore.getScore();
								model.addAttribute("autoScore", autoScore);
							}
							if(StringUtil.isNotEmpty(autoScore)){
								break;
							}

						}
					}
				}
				if(StringUtil.isNotEmpty(autoScore)){
					break;
				}
			}
		}
		//查询课程课时分段列表


		return "modules/sco/grade/scoCourseGradeAuditView";
	}



	@RequestMapping(value = "delete")
	public String delete(ScoApply scoApply, RedirectAttributes redirectAttributes) {
		scoApplyService.delete(scoApply);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getFrontPath()+"/scoapply/scoApplyList/?repage";
	}

}