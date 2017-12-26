/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.hch.platform.pcore.modules.dasai.web;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.act.entity.Act;
import com.hch.platform.pcore.modules.act.service.ActTaskService;
import com.hch.platform.pcore.modules.dasai.entity.OaDasai;
import com.hch.platform.pcore.modules.dasai.service.OaDasaiService;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 大赛测试Controller
 * @author zhangzheng
 * @version 2017-02-24
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/dasai")
public class OaDasaiController extends BaseController {

	@Autowired
	private OaDasaiService oaDasaiService;

	@Autowired
	ActTaskService actTaskService;
	
	@ModelAttribute
	public OaDasai get(@RequestParam(required=false) String id) {
		OaDasai entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = oaDasaiService.get(id);
		}
		if (entity == null) {
			entity = new OaDasai();
		}
		return entity;
	}
	
    //我的报名 审核中列表
	@RequestMapping(value = "auditingList")
	public String audingList(OaDasai oaDasai, HttpServletRequest request, HttpServletResponse response, Model model) {
		oaDasai.setState("1");
		AbsUser user= UserUtils.getUser();
		oaDasai.setCreateBy(user);
		Page<OaDasai> page = oaDasaiService.findPage(new Page<OaDasai>(request, response), oaDasai);
		List<OaDasai> list=page.getList();
		for(OaDasai dasai:list) {
			dasai.getAct().setProcDefId(actTaskService.getProcessDefinitionIdByProInstId(dasai.getProcInsId()));
		}
		model.addAttribute("page", page);
		return "modules/dasai/auditingList";
	}


	//我的报名 审核完毕
	@RequestMapping(value = "auditedList")
	public String auditedList(OaDasai oaDasai, HttpServletRequest request, HttpServletResponse response, Model model) {
		oaDasai.setState("2");
		AbsUser user= UserUtils.getUser();
		oaDasai.setCreateBy(user);
		Page<OaDasai> page = oaDasaiService.findPage(new Page<OaDasai>(request, response), oaDasai);
		model.addAttribute("page", page);
		return "modules/dasai/auditedList";
	}

	//大赛报名
	@RequestMapping(value = "form")
	public String form(OaDasai oaDasai, Model model) {
		model.addAttribute("oaDasai", oaDasai);
		return "modules/dasai/oaDasaiForm";
	}


	@RequestMapping(value = "save")
	public String save(OaDasai oaDasai, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, oaDasai)) {
			return form(oaDasai, model);
		}
		oaDasaiService.save(oaDasai);
		addMessage(redirectAttributes, "保存大赛成功");
		return "redirect:"+Global.getAdminPath()+"/oa/dasai/form?repage";
	}

	@RequestMapping(value = "audit1")
	public String  audit1(OaDasai oaDasai,HttpServletRequest request, Model model) {
		//查找大赛提交的表单  供评分老师查看
//		Act act=oaDasai.getAct();
//		String businessId=act.getBusinessId();
//		oaDasai= oaDasaiService.get(businessId);
		model.addAttribute("oaDasai", oaDasai);
		//工作流相关信息
//		model.addAttribute("act", act);
		return "modules/dasai/markForm";
	}

	@RequestMapping(value = "audit2")
	public String  audit2(OaDasai oaDasai,HttpServletRequest request, Model model) {
		//查找大赛提交的表单  供评分老师查看
//		Act act=oaDasai.getAct();
//		String businessId=act.getBusinessId();
//		oaDasai= oaDasaiService.get(businessId);
		model.addAttribute("oaDasai", oaDasai);
		//工作流相关信息
//		model.addAttribute("act", act);
		return "modules/dasai/markForm2";
	}

	//学院评分提交处理
	@RequestMapping(value = "saveAudit1")
	public String saveAudit1(OaDasai oaDasai,HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Integer score=oaDasai.getScore();
		//执行工作流
		oaDasaiService.saveAudit1(oaDasai);

		addMessage(redirectAttributes, "评分成功");
		return "redirect:"+Global.getAdminPath()+"/oa/dasai/markList?repage";
	}

	//班级评分提交处理
	@RequestMapping(value = "saveAudit2")
	public String saveAudit2(OaDasai oaDasai,HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		Integer score=oaDasai.getScore();
		//执行工作流
		oaDasaiService.saveAudit2(oaDasai);

		addMessage(redirectAttributes, "评分成功");
		return "redirect:"+Global.getAdminPath()+"/oa/dasai/markList2?repage";
	}




	@RequestMapping(value = "markList")
	public String markList(Act act, HttpServletRequest request,HttpServletResponse response, Model model) {
		//查询代办任务
		act.setProcDefKey("multi_task");
		Page<Act> pageForSearch =new Page<Act>(request, response);

		Page<Act> page=actTaskService.todoListForPage(pageForSearch,act);
		model.addAttribute("page",page);

		return "modules/dasai/markList";
	}


	@RequestMapping(value = "markList2")
	public String markList2(Act act, HttpServletRequest request,HttpServletResponse response, Model model) {
		//查询代办任务
		act.setProcDefKey("multi_task");
		Page<Act> pageForSearch =new Page<Act>(request, response);

		Page<Act> page=actTaskService.todoListForPage(pageForSearch,act);
		model.addAttribute("page",page);

		return "modules/dasai/markList2";
	}


	@RequestMapping(value = "delete")
	public String delete(OaDasai oaDasai, RedirectAttributes redirectAttributes) {
		oaDasaiService.delete(oaDasai);
		addMessage(redirectAttributes, "删除大赛成功");
		return "redirect:"+Global.getAdminPath()+"/dasai/oaDasai/?repage";
	}

}