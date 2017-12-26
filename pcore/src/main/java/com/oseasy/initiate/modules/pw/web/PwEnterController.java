package com.oseasy.initiate.modules.pw.web;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.config.SysJkey;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.pw.entity.PwEnter;
import com.oseasy.initiate.modules.pw.service.PwEnterService;
import com.oseasy.initiate.modules.pw.vo.PwEnterStatus;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 入驻申报Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwEnter")
public class PwEnterController extends BaseController {
  @Autowired
	private PwEnterService pwEnterService;

	@ModelAttribute
	public PwEnter get(@RequestParam(required=false) String id) {
		PwEnter entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwEnterService.getByGroup(id);
		}
		if (entity == null){
			entity = new PwEnter();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwEnter pwEnter, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
	  Page<PwEnter> pag = new Page<PwEnter>(request, response);
	  pag.setOrderBy(PwEnter.STATUS);
	  pag.setOrderByType(Page.ORDER_ASC);
	  Page<PwEnter> page = pwEnterService.findPageByGroup(pag, pwEnter);
	    model.addAttribute("page", page);
      return "modules/pw/pwEnterList";
	}

	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = {"listQuery"})
	public String listQuery(PwEnter pwEnter, HttpServletRequest request, HttpServletResponse response, Model model) {
	  Page<PwEnter> page = pwEnterService.findPageByGroup(new Page<PwEnter>(request, response), pwEnter);
    model.addAttribute("page", page);
	  return "modules/pw/pwEnterListQuery";
	}

	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = {"listQXRZ"})
	public String listQXRZ(PwEnter pwEnter, HttpServletRequest request, HttpServletResponse response, Model model) {
    pwEnter.setPstatus(PwEnterStatus.getKeyByQXRZ());
	  Page<PwEnter> page = pwEnterService.findPageByGroup(new Page<PwEnter>(request, response), pwEnter);
	  model.addAttribute("page", page);
	  return "modules/pw/pwEnterListQXRZ";
	}

	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = {"listXQRZ"})
	public String listXQRZ(PwEnter pwEnter, HttpServletRequest request, HttpServletResponse response, Model model) {
	  pwEnter.setPstatus(PwEnterStatus.getKeyByXQRZ());
	  Page<PwEnter> page = pwEnterService.findPageByGroup(new Page<PwEnter>(request, response), pwEnter);
	  model.addAttribute("page", page);
	  return "modules/pw/pwEnterListXQRZ";
	}

	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = {"listFPCD"})
	public String listFPCD(PwEnter pwEnter, HttpServletRequest request, HttpServletResponse response, Model model) {
	  pwEnter.setPstatus(PwEnterStatus.getKeyByDFP() + StringUtil.DOTH + PwEnterStatus.getKeyByYFP());
	  Page<PwEnter> page = pwEnterService.findPageByGroup(new Page<PwEnter>(request, response), pwEnter);
	  model.addAttribute("page", page);
	  return "modules/pw/pwEnterListFPCD";
	}

	@RequiresPermissions("pw:pwEnter:view")
	@RequestMapping(value = "form")
	public String form(PwEnter pwEnter, Model model) {
		model.addAttribute("pwEnter", pwEnter);
    model.addAttribute("root", SysIds.SYS_TREE_ROOT.getId());
		return "modules/pw/pwEnterForm";
	}

	@RequiresPermissions("pw:pwEnter:edit")
	@RequestMapping(value = "save")
	public String save(PwEnter pwEnter, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwEnter)){
			return form(pwEnter, model);
		}
		pwEnterService.save(pwEnter);
		addMessage(redirectAttributes, "保存入驻申报成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnter/?repage";
	}

	@RequiresPermissions("pw:pwEnter:edit")
	@RequestMapping(value = "delete")
	public String delete(PwEnter pwEnter, RedirectAttributes redirectAttributes) {
		pwEnterService.delete(pwEnter);
		addMessage(redirectAttributes, "删除入驻申报成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnter/?repage";
	}

  /**
   * 入驻审核-提醒完善资料.
   * @param id 审核ID
   * @return
   */
  @RequestMapping(value = "ajaxSendNotify")
  public String ajaxSendNotify(PwEnter pwEnter, String type, RedirectAttributes redirectAttributes) {
    ActYwRstatus<PwEnter> rstatus = pwEnterService.sendMsg(pwEnter.getId(), UserUtils.getUser(), type);
    addMessage(redirectAttributes, rstatus.getMsg());
    return "redirect:"+Global.getAdminPath()+"/pw/pwEnter/list?repage";
  }

  /**
   * 入驻审核-删除.
   * @param id 审核ID
   * @return
   */
  @RequestMapping(value = "ajaxDelete")
  public String ajaxDelete(PwEnter pwEnter, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    pwEnterService.deleteWLEnter(pwEnter.getId(), request, response);
    addMessage(redirectAttributes, "删除入驻申报成功");
    return "redirect:"+Global.getAdminPath()+"/pw/pwEnter/list?repage";
  }

  /**
   * 入驻审核.
   * @param id 审核ID
   * @param edid 审核类型
   * @param atype 审核结果类型
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "ajaxAudit")
  public ActYwRstatus<PwEnter> ajaxAudit(String id, String edid, String atype, String remarks) {
    return pwEnterService.auditEnter(id, edid, atype, remarks);
  }

  /**
   * 续期.
   * @param id 审核ID
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "ajaxXq")
  public ActYwRstatus<PwEnter> ajaxXq(PwEnter pwEnter, Model model, RedirectAttributes redirectAttributes) {
    return pwEnterService.xqEnter(pwEnter.getId(), pwEnter.getTerm());
  }

  /**
   * 续期.
   * @param id 审核ID
   * @return ActYwRstatus
   */
  @RequestMapping(value = "ajaxFormXq")
  public String ajaxFormXq(PwEnter pwEnter, Model model, RedirectAttributes redirectAttributes) {
    ActYwRstatus<PwEnter> rstatus = pwEnterService.xqEnter(pwEnter.getId(), pwEnter.getTerm());
    addMessage(redirectAttributes, rstatus.getMsg());
    return "redirect:"+Global.getAdminPath()+"/pw/pwEnter/listXQRZ?repage";
  }

  /**
   * 退孵.
   * @param id 审核ID
   * @return ActYwRstatus
   */
  @RequestMapping(value = "ajaxExit")
  public String ajaxExit(PwEnter pwEnter, Model model, RedirectAttributes redirectAttributes) {
    ActYwRstatus<PwEnter> rstatus = pwEnterService.exitEnter(pwEnter.getId());
    addMessage(redirectAttributes, rstatus.getMsg());
    return "redirect:"+Global.getAdminPath()+"/pw/pwEnter/listQXRZ?repage";
  }

  /**
   * 查询房间分配情况.
   * isAll == true，表示查询所有
   * isAll = false,表示非所有,此时rids必填
   * @param isAll 查询所有
   * @param status 状态
   * @param rids 房间ID
   * @param response
   * @return List
   */
  @RequiresPermissions("user")
  @ResponseBody
  @RequestMapping(value = "treeData")
  public List<Map<String, Object>> treeData(@RequestParam(required = false) Boolean isAll, @RequestParam(required = false) String status, @RequestParam(required = false) String rids, HttpServletResponse response) {
      List<Map<String, Object>> mapList = Lists.newArrayList();
      if(isAll == null){
        isAll = false;
      }

      PwEnter pwEnter = new PwEnter();
      if(!isAll){
        if(StringUtil.isEmpty(rids)){
          return mapList;
        }
        List<String> ids = Arrays.asList((rids).split(StringUtil.DOTH));
        if((ids == null) || (ids.size() <= 0)){
          return mapList;
        }
        pwEnter.setIds(ids);
      }

      if(StringUtil.isNotEmpty(status)){
        if((PwEnterStatus.getKeyByDFP()).equals(status)){
          pwEnter.setPstatus(PwEnterStatus.getKeyByDFP());
        }else if((PwEnterStatus.getKeyByYFP()).equals(status)){
          pwEnter.setPstatus(PwEnterStatus.getKeyByYFP());
        }else if((PwEnterStatus.getKeyByQXRZ()).equals(status)){
          pwEnter.setPstatus(PwEnterStatus.getKeyByQXRZ());
        }else if((PwEnterStatus.getKeyByXQRZ()).equals(status)){
          pwEnter.setPstatus(PwEnterStatus.getKeyByXQRZ());
        }
      }

      List<PwEnter> list = Lists.newArrayList();
      if(StringUtil.isNotEmpty(pwEnter.getPstatus())){
        list = pwEnterService.findListByGroup(pwEnter);
      }

      StringBuffer name;
      for (int i = 0; i < list.size(); i++) {
          PwEnter e = list.get(i);
          if (e != null) {
            name = new StringBuffer();
              Map<String, Object> map = Maps.newHashMap();
              map.put("id", e.getId());
              map.put("pId", SysIds.SYS_TREE_ROOT.getId());

              if((e.getEcompany() != null) && (e.getEcompany().getPwCompany() != null) && StringUtil.isNotEmpty(e.getEcompany().getPwCompany().getName())){
                name.append(e.getEcompany().getPwCompany().getName());
              }
              if((e.getEteam() != null) && (e.getEteam().getTeam() != null) && StringUtil.isNotEmpty(e.getEteam().getTeam().getName())){
                name.append(e.getEteam().getTeam().getName());
              }
              if((e.getEproject() != null) && (e.getEproject().getProject() != null) && StringUtil.isNotEmpty(e.getEproject().getProject().getName())){
                name.append(e.getEproject().getProject().getName());
              }

//              map.put("name", name);
              map.put("name", e.getNo());
              map.put(SysJkey.JK_DATA, e);
              mapList.add(map);
          }
      }
      return mapList;
  }
}