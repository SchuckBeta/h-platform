package com.hch.platform.pcore.modules.pw.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.pw.entity.PwEnterDetail;
import com.hch.platform.pcore.modules.pw.service.PwEnterDetailService;
import com.hch.platform.pcore.modules.pw.vo.PwEnterType;

/**
 * 入驻申报详情Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwEnterDetail")
public class PwEnterDetailController extends BaseController {

	@Autowired
	private PwEnterDetailService pwEnterDetailService;

	@ModelAttribute
	public PwEnterDetail get(@RequestParam(required=false) String id) {
		PwEnterDetail entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwEnterDetailService.get(id);
		}
		if (entity == null){
			entity = new PwEnterDetail();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwEnterDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwEnterDetail pwEnterDetail, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Page<PwEnterDetail> page = new Page<>();
		if(StringUtil.isEmpty(pwEnterDetail.getPetype())){
      page = pwEnterDetailService.findPageByAll(new Page<PwEnterDetail>(request, response), pwEnterDetail);
		}else{
  		PwEnterType pwEnterType = PwEnterType.getByKey(pwEnterDetail.getPetype());
  		if(pwEnterType != null){
    		if((PwEnterType.PET_TEAM).equals(pwEnterType)){
          pwEnterDetail.setType(PwEnterType.PET_TEAM.getKey());
    	    page = pwEnterDetailService.findPageByTeam(new Page<PwEnterDetail>(request, response), pwEnterDetail);
    		}else if((PwEnterType.PET_QY).equals(pwEnterType)){
          pwEnterDetail.setType(PwEnterType.PET_QY.getKey());
    	    page = pwEnterDetailService.findPageByQy(new Page<PwEnterDetail>(request, response), pwEnterDetail);
    		}else if((PwEnterType.PET_XM).equals(pwEnterType)){
          pwEnterDetail.setType(PwEnterType.PET_XM.getKey());
    	    page = pwEnterDetailService.findPageByXm(new Page<PwEnterDetail>(request, response), pwEnterDetail);
    		}else{
    	    addMessage(redirectAttributes, "入驻类型未定义");
    		}
        pwEnterDetail.setPename(pwEnterType.getName());
  		}
		}

		model.addAttribute("pwEnterDetail", pwEnterDetail);
    model.addAttribute("page", page);

		if(StringUtil.isEmpty(pwEnterDetail.getPetype())){
	    return "modules/pw/pwEnterDetailAllList";
    }else{
      return "modules/pw/pwEnterDetailList";
    }
	}

	@RequiresPermissions("pw:pwEnterDetail:view")
	@RequestMapping(value = "form")
	public String form(PwEnterDetail pwEnterDetail, Model model) {
		model.addAttribute("pwEnterDetail", pwEnterDetail);
		return "modules/pw/pwEnterDetailForm";
	}

	@RequiresPermissions("pw:pwEnterDetail:edit")
	@RequestMapping(value = "save")
	public String save(PwEnterDetail pwEnterDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwEnterDetail)){
			return form(pwEnterDetail, model);
		}
		pwEnterDetailService.save(pwEnterDetail);
		addMessage(redirectAttributes, "保存入驻申报详情成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnterDetail/?repage";
	}

	@RequiresPermissions("pw:pwEnterDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(PwEnterDetail pwEnterDetail, RedirectAttributes redirectAttributes) {
		pwEnterDetailService.delete(pwEnterDetail);
		addMessage(redirectAttributes, "删除入驻申报详情成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnterDetail/?repage";
	}

}