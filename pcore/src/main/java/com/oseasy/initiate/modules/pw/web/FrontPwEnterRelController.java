package com.oseasy.initiate.modules.pw.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.pw.entity.PwEnter;
import com.oseasy.initiate.modules.pw.entity.PwEnterDetail;
import com.oseasy.initiate.modules.pw.entity.PwEnterRel;
import com.oseasy.initiate.modules.pw.service.PwEnterRelService;
import com.oseasy.initiate.modules.pw.service.PwEnterRoomService;
import com.oseasy.initiate.modules.pw.service.PwEnterService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 入驻申报关联Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${frontPath}/pw/pwEnterRel")
public class FrontPwEnterRelController extends BaseController {

  @Autowired
  private PwEnterService pwEnterService;
  @Autowired
  private PwEnterRelService pwEnterRelService;
  @Autowired
  private PwEnterRoomService pwEnterRoomService;

	@ModelAttribute
	public PwEnterRel get(@RequestParam(required=false) String id) {
		PwEnterRel entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwEnterRelService.get(id);
		}
		if (entity == null){
			entity = new PwEnterRel();
		}
		return entity;
	}

	@RequestMapping(value = {"list"})
	public String list(PwEnterRel pwEnterRel, HttpServletRequest request, HttpServletResponse response, Model model) {
	  if (pwEnterRel.getPwEnterDetail() == null) {
	    pwEnterRel.setPwEnterDetail(new PwEnterDetail());
    }

	  if (pwEnterRel.getPwEnterDetail().getPwEnter() == null) {
	    pwEnterRel.getPwEnterDetail().setPwEnter(new PwEnter());
	  }

	  if (pwEnterRel.getPwEnterDetail().getPwEnter().getApplicant() == null) {
	    pwEnterRel.getPwEnterDetail().getPwEnter().setApplicant(UserUtils.getUser());
	  }

	  if (StringUtil.isEmpty(pwEnterRel.getPwEnterDetail().getPwEnter().getApplicant().getId())) {
	    return UserUtils.toLogin();
	  }else{
  		Page<PwEnterRel> page = pwEnterRelService.findPage(new Page<PwEnterRel>(request, response), pwEnterRel);
//  		List<PwEnterRel> pwEnterRels = page.getList();
//      List<PwEnterRoom> pwEnterRooms = pwEnterRoomService.findListByinIds(PwEnterRel.getIds(pwEnterRels, PwEnterRel.PER_EID));
//      page.setList(PwEnterRel.addPwRoom(pwEnterRels, pwEnterRooms));
      model.addAttribute("page", page);
	  }
		return "modules/pw/frontPwEnterRelList";
	}

  @RequestMapping(value = "form")
  public String form(PwEnterRel pwEnterRel, Model model) {
    if((pwEnterRel == null) || (pwEnterRel.getActYwApply() == null) || StringUtil.isEmpty(pwEnterRel.getActYwApply().getRelId())){
      return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRel/?repage";
    }

    PwEnter pwEnter = pwEnterService.getByGroup(pwEnterRel.getActYwApply().getRelId());
    if(pwEnter == null){
      return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRel/?repage";
    }
    model.addAttribute("pwEnter", pwEnter);
    model.addAttribute("pwEnterRel", pwEnterRel);
    model.addAttribute("root", SysIds.SYS_TREE_ROOT.getId());
    return "modules/pw/frontPwEnterRelForm";
  }

  @RequestMapping(value = "view")
  public String view(PwEnterRel pwEnterRel, Model model) {
    if((pwEnterRel == null) || (pwEnterRel.getActYwApply() == null) || StringUtil.isEmpty(pwEnterRel.getActYwApply().getRelId())){
      return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRel/?repage";
    }

    PwEnter pwEnter = pwEnterService.getByGroup(pwEnterRel.getActYwApply().getRelId());
    if(pwEnter == null){
      return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRel/?repage";
    }
    model.addAttribute("pwEnter", pwEnter);
    model.addAttribute("pwEnterRel", pwEnterRel);
    return "modules/pw/frontPwEnterRelView";
  }
}