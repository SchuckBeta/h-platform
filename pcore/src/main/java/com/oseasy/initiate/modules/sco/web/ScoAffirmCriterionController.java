package com.hch.platform.pcore.modules.sco.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
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
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmCriterion;
import com.hch.platform.pcore.modules.sco.service.ScoAffirmConfService;
import com.hch.platform.pcore.modules.sco.service.ScoAffirmCriterionService;
import com.hch.platform.pcore.modules.sys.entity.Dict;
import com.hch.platform.pcore.modules.sys.service.DictService;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;

/**
 * 学分认定标准Controller.
 * @author 9527
 * @version 2017-07-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sco/scoAffirmCriterion")
public class ScoAffirmCriterionController extends BaseController {
	@Autowired
	private DictService dictService;
	@Autowired
	private ScoAffirmCriterionService scoAffirmCriterionService;
	@Autowired
	private ScoAffirmConfService scoAffirmConfService;
	
	@ModelAttribute
	public ScoAffirmCriterion get(@RequestParam(required=false) String id) {
		ScoAffirmCriterion entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = scoAffirmCriterionService.get(id);
		}
		if (entity == null){
			entity = new ScoAffirmCriterion();
		}
		return entity;
	}

	@RequiresPermissions("sco:scoAffirmCriterion:view")
	@RequestMapping(value = {"list", ""})
	public String list(ScoAffirmCriterion scoAffirmCriterion, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ScoAffirmCriterion> page = scoAffirmCriterionService.findPage(new Page<ScoAffirmCriterion>(request, response), scoAffirmCriterion);
		model.addAttribute("page", page);
		return "modules/sco/scoAffirmCriterionList";
	}
	@RequiresPermissions("sco:scoAffirmCriterion:view")
	@RequestMapping(value = {"getScores"})
	@ResponseBody
	public List<ScoAffirmCriterion>  getScores(HttpServletRequest request) {
		String confId=request.getParameter("confId");
		if(StringUtil.isNotEmpty(confId)){
			return scoAffirmCriterionService.findListByConfid(confId);
		}else{
			return null;
		}
	}
	@RequiresPermissions("sco:scoAffirmCriterion:view")
	@RequestMapping(value = "form")
	public String form(ScoAffirmCriterion scoAffirmCriterion, Model model,HttpServletRequest request) {
		String confId=request.getParameter("confId");
		String type=request.getParameter("type");
		String item=request.getParameter("item");
		String category=request.getParameter("category");
		if(StringUtil.isNotEmpty(confId)){
			model.addAttribute("confId",confId);
			
		}
		if(type!=null){
			model.addAttribute("titleName",DictUtils.getDictLabel(type, "0000000118", null));
		}
		if(("0000000123".equals(type)||"0000000124".equals(type))&&"0000000128".equals(item)){//双创项目
			if("1".equals(category)){//大创项目
				model.addAttribute("categoryName",dictService.get("fcc2aa837d1f4931ae4c9c1a9b915d0a").getLabel());
				List<Dict> l1=DictUtils.getDictList("project_degree");
				if(l1!=null){
					for(int i=0;i<l1.size();i++){
						Dict d=l1.get(i);
						if("5".equals(d.getValue())||"6".equals(d.getValue())){
							l1.remove(i);
							i--;
						}
					}
				}
				List<Dict> l2=DictUtils.getDictList("project_result");
				if(l2!=null){
					for(int i=0;i<l2.size();i++){
						Dict d=l2.get(i);
						if("3".equals(d.getValue())){
							l2.remove(i);
							i--;
						}
					}
				}
				model.addAttribute("categoryList",l1);
				model.addAttribute("resultList",l2);
			}else{
				model.addAttribute("categoryName",dictService.get("fcc2aa837d1f4931ae4c9c1a9b915d0a").getLabel());
				Set<String> lset=scoAffirmConfService.getLevelSetData("1,", category);
				Set<String> rset=scoAffirmConfService.getFinalResSetData("1,", category);
				if(lset!=null&&lset.size()>0&&rset!=null&&rset.size()>0){
					List<Dict> l1=scoAffirmConfService.getDictForScoAffirm(lset,"project_degree");
					List<Dict> l2=scoAffirmConfService.getDictForScoAffirm(rset,"project_result");
					model.addAttribute("categoryList",l1);
					model.addAttribute("resultList",l2);
				}
			}
		}else if("0000000125".equals(type)&&"0000000129".equals(item)){//双创大赛
			if("1".equals(category)){//互联网+大赛
				model.addAttribute("categoryName",dictService.get("cd39dd2473064f608c05c23f589da221").getLabel());
				List<Dict> l1=DictUtils.getDictList("competition_format");
				List<Dict> l2=DictUtils.getDictList("competition_college_prise");
				model.addAttribute("categoryList",l1);
				model.addAttribute("resultList",l2);
			}else{
				model.addAttribute("categoryName",dictService.get("cd39dd2473064f608c05c23f589da221").getLabel());
				Set<String> lset=scoAffirmConfService.getLevelSetData("7,", category);
				Set<String> rset=scoAffirmConfService.getFinalResSetData("7,", category);
				if(lset!=null&&lset.size()>0&&rset!=null&&rset.size()>0){
					List<Dict> l1=scoAffirmConfService.getDictForScoAffirm(lset,"competition_format");
					List<Dict> l2=scoAffirmConfService.getDictForScoAffirm(rset,"competition_college_prise");
					model.addAttribute("categoryList",l1);
					model.addAttribute("resultList",l2);
				}
			}
		}else if("0000000126".equals(type)&&"0000000130".equals(item)){//学术论文
			model.addAttribute("categoryName",dictService.get("c8c9dd66e2694c90af1229da7d5ab102").getLabel());
			List<Dict> l1=DictUtils.getDictList("0000000154");
			List<Dict> l2=DictUtils.getDictList("0000000151");
			model.addAttribute("categoryList",l1);
			model.addAttribute("resultList",l2);
		}else if("0000000126".equals(type)&&"0000000131".equals(item)){//知识产权
			model.addAttribute("categoryName",dictService.get("97dd9eea33a3431e96081088dd44802a").getLabel());
			List<Dict> l1=DictUtils.getDictList("0000000161");
			List<Dict> l2=DictUtils.getDictList("0000000151");
			model.addAttribute("categoryList",l1);
			model.addAttribute("resultList",l2);
		}
		return "modules/sco/scoAffirmCriterionForm";
	}

	@RequiresPermissions("sco:scoAffirmCriterion:edit")
	@RequestMapping(value = "save")
	public String save(ScoAffirmCriterion scoAffirmCriterion, Model model, RedirectAttributes redirectAttributes,HttpServletRequest request) {
		if (!beanValidator(model, scoAffirmCriterion)){
			return form(scoAffirmCriterion, model,request);
		}
		scoAffirmCriterion.setDataJson(StringEscapeUtils.unescapeHtml4(scoAffirmCriterion.getDataJson()));
		scoAffirmCriterionService.save(scoAffirmCriterion);
		addMessage(redirectAttributes, "保存学分认定标准成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoAffirmConf/?repage";
	}

	@RequiresPermissions("sco:scoAffirmCriterion:edit")
	@RequestMapping(value = "delete")
	public String delete(ScoAffirmCriterion scoAffirmCriterion, RedirectAttributes redirectAttributes) {
		scoAffirmCriterionService.delete(scoAffirmCriterion);
		addMessage(redirectAttributes, "删除学分认定标准成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoAffirmCriterion/?repage";
	}

}