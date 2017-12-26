package com.oseasy.initiate.modules.sco.web;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.sco.entity.ScoAffirmCriterionCouse;
import com.oseasy.initiate.modules.sco.service.ScoAffirmCriterionCouseService;

/**
 * 课程学分认定标准Controller.
 * @author 9527
 * @version 2017-07-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sco/scoAffirmCriterionCouse")
public class ScoAffirmCriterionCouseController extends BaseController {

	@Autowired
	private ScoAffirmCriterionCouseService scoAffirmCriterionCouseService;

	@ModelAttribute
	public ScoAffirmCriterionCouse get(@RequestParam(required=false) String id) {
		ScoAffirmCriterionCouse entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = scoAffirmCriterionCouseService.get(id);
		}
		if (entity == null){
			entity = new ScoAffirmCriterionCouse();
		}
		return entity;
	}

	@RequiresPermissions("sco:scoAffirmCriterionCouse:view")
	@RequestMapping(value = {"list", ""})
	public String list(ScoAffirmCriterionCouse scoAffirmCriterionCouse, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ScoAffirmCriterionCouse> page = scoAffirmCriterionCouseService.findPage(new Page<ScoAffirmCriterionCouse>(request, response), scoAffirmCriterionCouse);
		model.addAttribute("page", page);
		return "modules/sco/scoAffirmCriterionCouseList";
	}

	@SuppressWarnings("unchecked")
	@RequiresPermissions("sco:scoAffirmCriterionCouse:view")
	@RequestMapping(value = "form")
	public String form(ScoAffirmCriterionCouse scoAffirmCriterionCouse, Model model,HttpServletRequest request) {
		String fromPage=request.getParameter("fromPage");
		String cname=request.getParameter("cname");
		try {
			cname=URLDecoder.decode(cname, "utf-8");
		} catch (Exception e) {
		}
		model.addAttribute("cname", cname);
		scoAffirmCriterionCouse.setFromPage(fromPage);
		String foreign_id=request.getParameter("foreign_id");
		model.addAttribute("foreignId", foreign_id);
		List<ScoAffirmCriterionCouse> list=scoAffirmCriterionCouseService.findListByFid(foreign_id);
		model.addAttribute("maxCols", 3);
		if(list!=null&&list.size()>0){
			int maxCols=0;
			Map<String,Map<String,Object>> map=new LinkedHashMap<String,Map<String,Object>>();
			for(ScoAffirmCriterionCouse sacc:list){
				if("0".equals(sacc.getParentId())){//课时
					if(map.get(sacc.getId())==null){
						Map<String,Object> tmap=new HashMap<String,Object>();
						tmap.put("couse", sacc);
						map.put(sacc.getId(), tmap);
					}else{
						map.get(sacc.getId()).put("couse", sacc);
					}
				}else{//分值
					if(map.get(sacc.getParentId())==null){
						Map<String,Object> tmap=new HashMap<String,Object>();
						List<ScoAffirmCriterionCouse> tlist=new ArrayList<ScoAffirmCriterionCouse>();
						tlist.add(sacc);
						tmap.put("scores", tlist);
						map.put(sacc.getParentId(), tmap);
						if(tlist.size()>maxCols){
							maxCols=tlist.size();
						}
					}else{
						Map<String,Object> tmap=map.get(sacc.getParentId());
						List<ScoAffirmCriterionCouse> tlist=null;
						if(tmap.get("scores")==null){
							tlist=new ArrayList<ScoAffirmCriterionCouse>();
							tlist.add(sacc);
							tmap.put("scores", tlist);
						}else{
							tlist=(List<ScoAffirmCriterionCouse>)tmap.get("scores");
							tlist.add(sacc);
						}
						if(tlist.size()>maxCols){
							maxCols=tlist.size();
						}
					}
				}
			}
			model.addAttribute("map", map);
			model.addAttribute("maxCols", maxCols);
		}
		model.addAttribute("scoAffirmCriterionCouse", scoAffirmCriterionCouse);
		return "modules/sco/scoAffirmCriterionCouseForm";
	}

	@RequiresPermissions("sco:scoAffirmCriterionCouse:edit")
	@RequestMapping(value = "save")
	public String save(ScoAffirmCriterionCouse scoAffirmCriterionCouse, Model model, RedirectAttributes redirectAttributes,HttpServletRequest request) {
		if(StringUtil.isEmpty(scoAffirmCriterionCouse.getForeignId())){
			addMessage(redirectAttributes, "保存失败");
		}else{
			scoAffirmCriterionCouse.setDataJson(StringEscapeUtils.unescapeHtml4(scoAffirmCriterionCouse.getDataJson()));
			scoAffirmCriterionCouseService.save(scoAffirmCriterionCouse);
			addMessage(redirectAttributes, "保存课程学分认定标准成功");
		}
		return "redirect:"+Global.getAdminPath()+"/sco/"+scoAffirmCriterionCouse.getFromPage()+"/?repage";
	}

	@RequiresPermissions("sco:scoAffirmCriterionCouse:edit")
	@RequestMapping(value = "delete")
	public String delete(ScoAffirmCriterionCouse scoAffirmCriterionCouse, RedirectAttributes redirectAttributes) {
		scoAffirmCriterionCouseService.delete(scoAffirmCriterionCouse);
		addMessage(redirectAttributes, "删除课程学分认定标准成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoAffirmCriterionCouse/?repage";
	}

}