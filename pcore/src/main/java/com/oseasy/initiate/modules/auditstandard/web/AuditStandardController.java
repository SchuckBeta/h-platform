package com.oseasy.initiate.modules.auditstandard.web;

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

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.auditstandard.entity.AuditStandard;
import com.oseasy.initiate.modules.auditstandard.entity.AuditStandardFlow;
import com.oseasy.initiate.modules.auditstandard.service.AuditStandardDetailService;
import com.oseasy.initiate.modules.auditstandard.service.AuditStandardFlowService;
import com.oseasy.initiate.modules.auditstandard.service.AuditStandardService;
import com.oseasy.initiate.modules.auditstandard.vo.AuditStandardVo;

import net.sf.json.JSONObject;

/**
 * 评审标准Controller.
 * @author 9527
 * @version 2017-07-28
 */
@Controller
@RequestMapping(value = "${adminPath}/auditstandard/auditStandard")
public class AuditStandardController extends BaseController {

	@Autowired
	private AuditStandardService auditStandardService;
	@Autowired
	private AuditStandardDetailService auditStandardDetailService;
	@Autowired
	private AuditStandardFlowService auditStandardFlowService;
	@ModelAttribute
	public AuditStandard get(@RequestParam(required=false) String id) {
		AuditStandard entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = auditStandardService.get(id);
		}
		if (entity == null){
			entity = new AuditStandard();
		}
		return entity;
	}

	@RequestMapping(value = {"list", ""})
	public String list(AuditStandard auditStandard,HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AuditStandard> page = auditStandardService.findPage(new Page<AuditStandard>(request, response), auditStandard);
		model.addAttribute("page", page);
		return "modules/auditstandard/auditStandardList";
	}
	@RequestMapping(value = {"listFlow"})
	public String listFlow(AuditStandardVo auditStandard,HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AuditStandardVo> page = auditStandardService.findPageVo(new Page<AuditStandardVo>(request, response), auditStandard);
		model.addAttribute("page", page);
		if(!model.containsAttribute("vo")){
			model.addAttribute("vo", auditStandard);
		}
		return "modules/auditstandard/auditStandardFlowList";
	}
	@RequestMapping(value = "view")
	public String view(AuditStandard auditStandard, Model model) {
		model.addAttribute("auditStandard", auditStandard);
		if(StringUtil.isNotEmpty(auditStandard.getId())){
			model.addAttribute("details", auditStandardDetailService.findByFid(auditStandard.getId()));
		}else{
			auditStandard.setIsEscore("0");
		}
		return "modules/auditstandard/auditStandardView";
	}
	@RequestMapping(value = "form")
	public String form(AuditStandard auditStandard, Model model) {
		model.addAttribute("auditStandard", auditStandard);
		if(StringUtil.isNotEmpty(auditStandard.getId())){
			model.addAttribute("details", auditStandardDetailService.findByFid(auditStandard.getId()));
		}else{
			auditStandard.setIsEscore("0");
		}
		return "modules/auditstandard/auditStandardForm";
	}

	@RequiresPermissions("auditstandard:auditStandard:edit")
	@RequestMapping(value = "save")
	public String save(AuditStandard auditStandard, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, auditStandard)){
			return form(auditStandard, model);
		}
		auditStandardService.save(auditStandard);
		addMessage(redirectAttributes, "保存评审标准成功");
		return "redirect:"+Global.getAdminPath()+"/auditstandard/auditStandard/?repage";
	}
	@RequiresPermissions("auditstandard:auditStandard:edit")
	@RequestMapping(value = "deleteDetail")
	public String deleteDetail(@ModelAttribute AuditStandardVo vo, RedirectAttributes redirectAttributes) {
		auditStandardService.delete(vo);
		addMessage(redirectAttributes, "删除关联成功");
		return "redirect:"+Global.getAdminPath()+"/auditstandard/auditStandard/listFlow?repage";
	}
	@RequiresPermissions("auditstandard:auditStandard:edit")
	@RequestMapping(value = "delete")
	public String delete(@ModelAttribute AuditStandardVo vo, RedirectAttributes redirectAttributes) {
		auditStandardService.delete(vo);
		addMessage(redirectAttributes, "删除评审标准成功");
		return "redirect:"+Global.getAdminPath()+"/auditstandard/auditStandard/?repage";
	}
	@RequestMapping(value = "checkName")
	@ResponseBody
	public boolean checkName(String id,String name) {
		if (auditStandardService.checkName(id, name)>0) {
			return false;
		}
		return true;
	}
	@RequiresPermissions("auditstandard:auditStandard:edit")
	@RequestMapping(value = "saveDetail")
	public String saveDetail(@ModelAttribute AuditStandardVo vo, Model model, RedirectAttributes redirectAttributes) {
		if(StringUtil.isEmpty(vo.getFlow())||StringUtil.isEmpty(vo.getNode())||StringUtil.isEmpty(vo.getName())){
			addMessage(redirectAttributes, "添加失败");
		}else if(auditStandardFlowService.findByCdn(vo.getFlow(), vo.getNode())>0){
			addMessage(redirectAttributes, "添加失败，该节点已关联标准");
		}else{
			AuditStandardFlow a=new AuditStandardFlow();
			a.setAuditStandardId(vo.getName());
			a.setFlow(vo.getFlow());
			a.setNode(vo.getNode());
			auditStandardFlowService.save(a);
			addMessage(redirectAttributes, "添加成功");
		}
		redirectAttributes.addFlashAttribute("vo", vo);
		return "redirect:"+Global.getAdminPath()+"/auditstandard/auditStandard/listFlow/?repage";
	}
	@RequiresPermissions("auditstandard:auditStandard:edit")
	@RequestMapping(value = "saveChild")
	@ResponseBody
	public JSONObject saveChild(@ModelAttribute AuditStandardVo vo, Model model, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		if(StringUtil.isEmpty(vo.getRelationId())){
			js.put("ret", "0");
			js.put("msg", "设置失败");
		}else{
			auditStandardFlowService.saveChild(vo);
			js.put("ret", "1");
			js.put("msg", "设置成功");
		}
		return js;
	}
}