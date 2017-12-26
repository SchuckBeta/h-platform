package com.hch.platform.pcore.modules.pw.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
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
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.pw.entity.PwDesignerCanvas;
import com.hch.platform.pcore.modules.pw.service.PwDesignerCanvasService;

/**
 * 画布表Controller.
 * @author zy
 * @version 2017-12-18
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwDesignerCanvas")
public class PwDesignerCanvasController extends BaseController {

	@Autowired
	private PwDesignerCanvasService pwDesignerCanvasService;

	@ModelAttribute
	public PwDesignerCanvas get(@RequestParam(required=false) String id) {
		PwDesignerCanvas entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwDesignerCanvasService.get(id);
		}
		if (entity == null){
			entity = new PwDesignerCanvas();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwDesignerCanvas:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwDesignerCanvas pwDesignerCanvas, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwDesignerCanvas> page = pwDesignerCanvasService.findPage(new Page<PwDesignerCanvas>(request, response), pwDesignerCanvas);
		model.addAttribute("page", page);
		return "modules/pw/pwDesignerCanvasList";
	}

	@RequiresPermissions("pw:pwDesignerCanvas:view")
	@RequestMapping(value = "form")
	public String form(PwDesignerCanvas pwDesignerCanvas, Model model) {
		model.addAttribute("pwDesignerCanvas", pwDesignerCanvas);
		return "modules/pw/pwDesignerCanvasForm";
	}

	@RequiresPermissions("pw:pwDesignerCanvas:view")
	@RequestMapping(value = "index")
	public String index(PwDesignerCanvas pwDesignerCanvas, Model model) {
	  model.addAttribute("pwDesignerCanvas", pwDesignerCanvas);
	  return "modules/pw/pwDesignerCanvasIndex";
	}

	@RequiresPermissions("pw:pwDesignerCanvas:view")
	@RequestMapping(value = "view")
	public String view(PwDesignerCanvas pwDesignerCanvas, Model model) {
	  model.addAttribute("pwDesignerCanvas", pwDesignerCanvas);
	  return "modules/pw/pwDesignerCanvasView";
	}

	@RequiresPermissions("pw:pwDesignerCanvas:edit")
	@RequestMapping(value = "save")
	public String save(PwDesignerCanvas pwDesignerCanvas, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwDesignerCanvas)){
			return form(pwDesignerCanvas, model);
		}
		pwDesignerCanvasService.save(pwDesignerCanvas);
		addMessage(redirectAttributes, "保存画布表成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwDesignerCanvas/?repage";
	}

	@RequiresPermissions("pw:pwDesignerCanvas:edit")
	@RequestMapping(value = "delete")
	public String delete(PwDesignerCanvas pwDesignerCanvas, RedirectAttributes redirectAttributes) {
		pwDesignerCanvasService.delete(pwDesignerCanvas);
		addMessage(redirectAttributes, "删除画布表成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwDesignerCanvas/?repage";
	}

	@ResponseBody
	@RequestMapping(value = "saveAll")
	public JSONObject saveAll(PwDesignerCanvas pwDesignerCanvas, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		String jsonString=request.getParameter("json");
		JSONObject jsonData =new JSONObject();
		if(jsonString!=null){
			jsonData=JSONObject.fromObject(jsonString);
			boolean res=pwDesignerCanvasService.saveAll(jsonData);
			//保存成功
			if(res){
				jsonData.put("ret","1");
				return jsonData;
			}else{
				jsonData.put("ret","0");
				return jsonData;
			}
		}else{
			jsonData.put("ret","0");
			return jsonData;
		}
	}

	@ResponseBody
	@RequestMapping(value = "getAll")
	public JSONObject getAll(HttpServletRequest request) {
		String floorId=request.getParameter("floorId");
		JSONObject jsonData = pwDesignerCanvasService.getAll(floorId);
		return jsonData;
	}

}