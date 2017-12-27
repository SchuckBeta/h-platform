package com.hch.platform.pcore.modules.proprojectmd.web;

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

import com.google.common.collect.Lists;
import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.promodel.entity.ProModel;
import com.hch.platform.pcore.modules.promodel.service.ProModelService;
import com.hch.platform.pcore.modules.proprojectmd.entity.ProModelMd;
import com.hch.platform.pcore.modules.proprojectmd.service.ProModelMdService;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.StudentExpansion;
import com.hch.platform.pcore.modules.sys.service.BackTeacherExpansionService;
import com.hch.platform.pcore.modules.sys.service.StudentExpansionService;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.service.TeamService;
import com.hch.platform.pcore.modules.tpl.service.WordService;
import com.hch.platform.pcore.modules.tpl.vo.IWparam;
import com.hch.platform.pcore.modules.tpl.vo.Rstatus;
import com.hch.platform.pcore.modules.tpl.vo.WproType;

import net.sf.json.JSONObject;

/**
 * proProjectMdController.
 * @author zy
 * @version 2017-09-18
 */
@Controller
@RequestMapping(value = "${frontPath}/proprojectmd/proModelMd")
public class FrontProModelMdController extends BaseController {
	@Autowired
	private ProModelService proModelService;
	@Autowired
	private ProModelMdService proModelMdService;
	@Autowired
	private WordService wordService;
	@Autowired
	private TeamService teamService;

	@Autowired
	private BackTeacherExpansionService backTeacherExpansionService;
	@Autowired
	private StudentExpansionService studentExpansionService;


	@ModelAttribute
	public ProModelMd get(@RequestParam(required=false) String id) {
		ProModelMd entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = proModelMdService.get(id);
		}
		if (entity == null){
			entity = new ProModelMd();
		}
		return entity;
	}

	@RequiresPermissions("proprojectmd:proModelMd:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProModelMd proModelMd, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProModelMd> page = proModelMdService.findPage(new Page<ProModelMd>(request, response), proModelMd);
		model.addAttribute("page", page);
		return "modules/proprojectmd/proModelMdList";
	}


	@RequestMapping(value = "form")
	public String form(ProModelMd proModelMd, Model model) {
    model.addAttribute("proModelMd", proModelMd);
		//return "modules/proprojectmd/proModelMdForm";
		return "/template/form/project/md_applyForm";
	}

	@RequestMapping(value = "ajaxSave")
	@ResponseBody
	public JSONObject ajaxSave(ProModelMd proModelMd, Model model, RedirectAttributes redirectAttributes) {
		JSONObject js=proModelMdService.ajaxSaveProModelMd(proModelMd);
		js.put("id", proModelMd.getId());
		js.put("proModelId", proModelMd.getProModel().getId());
		js.put("proCategory", proModelMd.getProModel().getProCategory());
		return js;
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public JSONObject save(ProModelMd proModelMd, Model model, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		String msg=proModelMdService.saveProModelMd(proModelMd);
		js.put("msg", msg);
		js.put("id", proModelMd.getId());
		js.put("proModelId", proModelMd.getProModel().getId());
		js.put("proCategory", proModelMd.getProModel().getProCategory());
		js.put("fileUrl", proModelMd.getFileUrl());
		js.put("fileHttpUrl", FtpUtil.getFileUrl(proModelMd.getFileUrl()));
		js.put("fileId", proModelMd.getFileId());
		return js;
	}

	@RequestMapping(value = "ajaxWord")
	@ResponseBody
	public Rstatus ajaxWord(String proId, String type, String vsn, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		ProModelMd proModelMd = proModelMdService.get(proId);
		if((proModelMd == null) || StringUtil.isEmpty(proModelMd.getModelId())){
		  	return new Rstatus(false, "ProId 或 proModelMd 参数不能为空！");
		}

		ProModel proModel = proModelService.get(proModelMd.getModelId());
		if((proModel == null) || StringUtil.isEmpty(proModel.getTeamId())){
      		return new Rstatus(false, "ProModel Id 或 Team id 参数不能为空！");
		}

		String teamId = proModelMd.getProModel().getTeamId();
		Team team = teamService.get(teamId);
		List<BackTeacherExpansion> qytes = backTeacherExpansionService.getQYTeacher(teamId);
		List<BackTeacherExpansion> xytes = backTeacherExpansionService.getXYTeacher(teamId);
		List<StudentExpansion> tms = studentExpansionService.getStudentByTeamId(teamId);
		if((team == null) || ((qytes == null) && (xytes == null)) || (tms == null)){
      		return new Rstatus(false, "团队、导师参数不能为空！");
		}

		if(qytes == null){
		  qytes = Lists.newArrayList();
		}

		if(xytes == null){
		  xytes = Lists.newArrayList();
		}

		WproType wproType = WproType.getByKey(proModel.getProCategory());
		if(wproType == null){
		  	return new Rstatus(false, "proCategory 项目类型未定义["+proModel.getProCategory()+"]");
		}

		IWparam wordParam = proModelMdService.initIWparam(type, vsn, proModel, proModelMd, team, xytes, qytes, tms);
		if(wordParam != null){
			wordService.exeDownload(vsn, wordParam, request, response);
			return null;
		}
		return new Rstatus(false, "模板下载失败！");
	}

	@RequestMapping(value = "submit")
	@ResponseBody
	public JSONObject submit(ProModelMd proModelMd, Model model, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js=proModelMdService.submit(proModelMd,js);
		return js;
	}

	@RequestMapping(value = "midSubmit")
	@ResponseBody
	public JSONObject midSubmit(ProModelMd proModelMd, Model model,HttpServletRequest request, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		String gnodeId=request.getParameter("gnodeId");

		js=proModelMdService.midSubmit(proModelMd,js,gnodeId);
		return js;
	}

	@RequestMapping(value = "closeSubmit")
	@ResponseBody
	public JSONObject closeSubmit(ProModelMd proModelMd, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		String gnodeId=request.getParameter("gnodeId");

		js=proModelMdService.closeSubmit(proModelMd,js,gnodeId);
		return js;
	}

	@RequestMapping(value = "delete")
	public String delete(ProModelMd proModelMd, RedirectAttributes redirectAttributes) {
		proModelMdService.delete(proModelMd);
		addMessage(redirectAttributes, "删除proProjectMd成功");
		return "redirect:"+Global.getAdminPath()+"/proprojectmd/proModelMd/?repage";
	}

}