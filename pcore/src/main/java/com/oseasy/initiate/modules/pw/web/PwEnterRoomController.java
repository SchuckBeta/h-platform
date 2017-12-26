package com.oseasy.initiate.modules.pw.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.pw.entity.PwEnter;
import com.oseasy.initiate.modules.pw.service.PwEnterService;
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
import com.oseasy.initiate.modules.pw.entity.PwEnterRoom;
import com.oseasy.initiate.modules.pw.service.PwEnterRoomService;

/**
 * 入驻场地分配Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwEnterRoom")
public class PwEnterRoomController extends BaseController {

	@Autowired
	private PwEnterRoomService pwEnterRoomService;

	@Autowired
	private PwEnterService pwEnterService;

	@ModelAttribute
	public PwEnterRoom get(@RequestParam(required=false) String id) {
		PwEnterRoom entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwEnterRoomService.get(id);
		}
		if (entity == null){
			entity = new PwEnterRoom();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwEnterRoom:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwEnterRoom pwEnterRoom, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwEnterRoom> page = pwEnterRoomService.findPage(new Page<PwEnterRoom>(request, response), pwEnterRoom);
		model.addAttribute("page", page);
		return "modules/pw/pwEnterRoomList";
	}

	@RequiresPermissions("pw:pwEnterRoom:view")
	@RequestMapping(value = "form")
	public String form(PwEnterRoom pwEnterRoom, Model model) {
		model.addAttribute("pwEnterRoom", pwEnterRoom);
		return "modules/pw/pwEnterRoomForm";
	}

	@RequiresPermissions("pw:pwEnterRoom:edit")
	@RequestMapping(value = "save")
	public String save(PwEnterRoom pwEnterRoom, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwEnterRoom)){
			return form(pwEnterRoom, model);
		}
		pwEnterRoomService.save(pwEnterRoom);
		addMessage(redirectAttributes, "保存入驻场地分配成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRoom/?repage";
	}

	@RequiresPermissions("pw:pwEnterRoom:edit")
	@RequestMapping(value = "delete")
	public String delete(PwEnterRoom pwEnterRoom, RedirectAttributes redirectAttributes) {
		pwEnterRoomService.delete(pwEnterRoom);
		addMessage(redirectAttributes, "删除入驻场地分配成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRoom/?repage";
	}

  /**
   * 房间分配入驻团队.
   * @param pwRoom 实体
   * @param model 模型
   * @param request 请求
   * @param redirectAttributes 重定向
   * @return String
   */
  @ResponseBody
  @RequestMapping(value = "ajaxPwEnterRoom/{rid}")
  public ActYwRstatus<PwEnterRoom> ajaxPwEnterRoom(@PathVariable(value = "rid") String rid, @RequestParam(required = true) Boolean isEnter, @RequestParam(required = true) String eid, HttpServletRequest request, RedirectAttributes redirectAttributes) {
    if(isEnter == null){
      return new ActYwRstatus<PwEnterRoom>(false, "分配状态不能为空！");
    }

    if(StringUtil.isEmpty(eid)){
      return new ActYwRstatus<PwEnterRoom>(false, "申请记录不能为空！");
    }

    PwEnterRoom pwEnterRoom = new PwEnterRoom(rid, eid);
    if(isEnter){
      pwEnterRoomService.saveEnter(pwEnterRoom);
      return new ActYwRstatus<PwEnterRoom>(true, "分配成功！", pwEnterRoom);
    }else{
      Boolean isTrue = pwEnterRoomService.deletePLWLByErid(pwEnterRoom);
      if(isTrue){
        return new ActYwRstatus<PwEnterRoom>(true, "取消分配成功！", pwEnterRoom);
      }else{
        return new ActYwRstatus<PwEnterRoom>(false, "取消分配失败！", pwEnterRoom);
      }
    }
  }

	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = "assignRoomForm")
	public String assignRoomForm(PwEnterRoom pwEnterRoom, Model model) {
		PwEnter pwEnter = pwEnterService.getByGroup(pwEnterRoom.getPwEnter().getId());
		pwEnterRoom.setPwEnter(pwEnter);
		model.addAttribute("pwEnterRoom", pwEnterRoom);
		return "modules/pw/pwEnterRoomAssignForm";
	}

	/**
	 * 分配场地
	 * @param pwEnterRoom
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = "assignRoom")
	public String assignRoom(PwEnterRoom pwEnterRoom, Model model, RedirectAttributes redirectAttributes) {
		try {
			pwEnterRoomService.saveEnter(pwEnterRoom);
			addMessage(redirectAttributes, "分配场地成功");
		} catch (Exception e){
			addMessage(redirectAttributes, e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnter/?repage";
	}
}