package com.oseasy.initiate.modules.pw.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.entity.PwCategory;
import com.oseasy.initiate.modules.pw.entity.PwCosMaterial;
import com.oseasy.initiate.modules.pw.service.PwCosMaterialService;

/**
 * 耗材Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwCosMaterial")
public class PwCosMaterialController extends BaseController {

	@Autowired
	private PwCosMaterialService pwCosMaterialService;

	@ModelAttribute
	public PwCosMaterial get(@RequestParam(required=false) String id) {
		PwCosMaterial entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwCosMaterialService.get(id);
		}
		if (entity == null){
			entity = new PwCosMaterial();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwCosMaterial:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwCosMaterial pwCosMaterial, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwCosMaterial> page = pwCosMaterialService.findPage(new Page<PwCosMaterial>(request, response), pwCosMaterial);
		model.addAttribute("page", page);
		return "modules/pw/pwCosMaterialList";
	}

	@RequiresPermissions("pw:pwCosMaterial:view")
	@RequestMapping(value = "form")
	public String form(PwCosMaterial pwCosMaterial, Model model) {
		model.addAttribute("pwCosMaterial", pwCosMaterial);
		return "modules/pw/pwCosMaterialForm";
	}

	@RequiresPermissions("pw:pwCosMaterial:edit")
	@RequestMapping(value = "save")
	public String save(PwCosMaterial pwCosMaterial, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwCosMaterial)){
			return form(pwCosMaterial, model);
		}
		pwCosMaterialService.save(pwCosMaterial);
		addMessage(redirectAttributes, "保存耗材成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCosMaterial/?repage";
	}

	@RequiresPermissions("pw:pwCosMaterial:edit")
	@RequestMapping(value = "delete")
	public String delete(PwCosMaterial pwCosMaterial, RedirectAttributes redirectAttributes) {
		pwCosMaterialService.delete(pwCosMaterial);
		addMessage(redirectAttributes, "删除耗材成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCosMaterial/?repage";
	}

  @RequiresPermissions("user")
  @ResponseBody
  @RequestMapping(value = "treeData/{cid}")
  public ActYwRstatus<List<PwCosMaterial>> treeData(@PathVariable String cid, HttpServletResponse response) {
    List<PwCosMaterial> list = pwCosMaterialService.findList(new PwCosMaterial(new PwCategory(cid)));
    if ((list == null) || (list.size() <= 0)){
      return new ActYwRstatus<List<PwCosMaterial>>(false, "请求失败或数据为空！");
    }
    return new ActYwRstatus<List<PwCosMaterial>>(true, "请求成功", list);
  }
}