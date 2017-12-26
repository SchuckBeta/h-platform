package com.oseasy.initiate.modules.pw.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYwApply;
import com.oseasy.initiate.modules.actyw.service.ActYwApplyService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.entity.PwCompany;
import com.oseasy.initiate.modules.pw.entity.PwEnter;
import com.oseasy.initiate.modules.pw.exception.NoTeamException;
import com.oseasy.initiate.modules.pw.service.PwEnterService;
import com.oseasy.initiate.modules.pw.vo.DtypeTerm;
import com.oseasy.initiate.modules.pw.vo.PwEnterStatus;
import com.oseasy.initiate.modules.pw.vo.PwEnterVo;
import com.oseasy.initiate.modules.pw.vo.SvalPw;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

/**
 * 入驻申报Controller.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${frontPath}/pw/pwEnter")
public class FrontPwEnterController extends BaseController {

  @Autowired
  private PwEnterService pwEnterService;
  @Autowired
  private ActYwApplyService actYwApplyService;

  @ModelAttribute
  public PwEnter get(@RequestParam(required = false) String id) {
    PwEnter entity = null;
    if (StringUtil.isNotBlank(id)) {
      entity = pwEnterService.getByGroup(id);
    }
    if (entity == null) {
      entity = new PwEnter();
    }
    return entity;
  }

  @RequestMapping(value = "form")
  public String form(PwEnter pwEnter, Model model) {
    if (pwEnter == null) {
      pwEnter = new PwEnter();
    }

    if (pwEnter.getApplicant() == null) {
      pwEnter.setApplicant(UserUtils.getUser());
    }

    if(StringUtil.isEmpty(pwEnter.getApplicant().getId())){
      return UserUtils.toLogin();
    }

    if(StringUtil.isNotEmpty(pwEnter.getApplicant().getId()) && StringUtil.isEmpty(pwEnter.getId())){
      pwEnter.setIsTemp(Global.YES);
      List<PwEnter> pwEnters = pwEnterService.findList(pwEnter);
      if((pwEnters != null) && (pwEnters.size() >= SvalPw.getEnterApplyMaxNum())){
        model.addAttribute(SvalPw.IS_MAX, true);
        model.addAttribute(SvalPw.MAXNUM, SvalPw.getEnterApplyMaxNum());
      }else{
        model.addAttribute(SvalPw.IS_MAX, false);
      }
      model.addAttribute("pwEnters", pwEnters);
    }
    model.addAttribute("pwEnter", pwEnter);
    return "modules/pw/frontPwEnterForm";
  }

  @RequestMapping(value = "formStep2")
  public String formStep2(PwEnter pwEnter, Model model) {
    model.addAttribute("pwEnter", pwEnter);
    PwEnterVo pwEnterVo = PwEnterVo.convert(pwEnter);

    /**
     * 判断页面是否可以点保存.
     */
    if(StringUtil.isNotEmpty(pwEnter.getId())){
      ActYwApply actYwApply = new ActYwApply();
      actYwApply.setApplyUser(UserUtils.getUser());
      actYwApply.setRelId(pwEnter.getId());

      if(UserUtils.getUser() == null){
        return UserUtils.toLogin();
      }

      List<ActYwApply> actYwApplys = actYwApplyService.findList(actYwApply);
      pwEnterVo.setIsSave((actYwApplys != null) && (actYwApplys.size() > 0));
    }else{
      pwEnterVo.setIsSave(false);
    }
    model.addAttribute("pwEnterVo", pwEnterVo);
    if((pwEnter == null) ||  StringUtil.isEmpty(pwEnter.getId())){
      return "redirect:" + Global.getFrontPath() + "/pw/pwEnter/form?repage";
    }
    return "modules/pw/frontPwEnterFormStep2";
  }

  @RequiresPermissions("pw:pwEnter:edit")
  @RequestMapping(value = "save")
  public String save(PwEnter pwEnter, Model model, RedirectAttributes redirectAttributes) {
    if (!beanValidator(model, pwEnter)) {
      return form(pwEnter, model);
    }
    pwEnterService.save(pwEnter);
    addMessage(redirectAttributes, "保存入驻申报成功");
    return "redirect:" + Global.getFrontPath() + "/pw/pwEnter/?repage";
  }

  @RequestMapping(value = "ajaxDelete")
  public String ajaxDelete(PwEnter pwEnter, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    pwEnterService.deleteWLEnter(pwEnter.getId(), request, response);
    addMessage(redirectAttributes, "删除入驻申报成功");
    return "redirect:" + Global.getFrontPath() + "/pw/pwEnter/form";
  }

  /**
   * 保存入驻基本信息.
   * @param uid 用户ID
   * @param term 周期
   * @return ActYwRstatus 结果状态
   */
  @ResponseBody
  @RequestMapping(value = "ajaxSave/{uid}")
  public ActYwRstatus<PwEnter> ajaxSave(@PathVariable(value = "uid") String uid, @RequestParam(required = false) String id, @RequestParam(required = false) Integer term) {
    if (StringUtil.isEmpty(uid)) {
      return new ActYwRstatus<PwEnter>(false, "入驻失败, 申请人不能为空！");
    }
    if ((term == null)) {
      return new ActYwRstatus<PwEnter>(false, "入驻失败, 申请周期不能为空！");
    }
    PwEnter pwEnter = null;
    if (StringUtil.isNotEmpty(id)) {
      pwEnter = get(id);
    }

    if(pwEnter == null){
      pwEnter = new PwEnter();
      pwEnter.setStatus(PwEnterStatus.PES_DSH.getKey());
    }
    pwEnter.setApplicant(new User(uid));
    Integer addTerm = DtypeTerm.addDayByType(term, new Date());
    if(addTerm == null){
      return new ActYwRstatus<PwEnter>(false, "入驻失败");
    }
    pwEnter.setTerm(addTerm);

    pwEnterService.save(pwEnter);
    return new ActYwRstatus<PwEnter>(true, "请求成功", pwEnter);
  }

  /**
   * 保存入驻企业基本信息.
   * @param uid 用户ID
   * @param term 周期
   * @return ActYwRstatus 结果状态
   */
  @SuppressWarnings("rawtypes")
  @ResponseBody
  @RequestMapping(value = "/ajaxSaveAll", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<PwEnter> ajaxSaveAll(@RequestBody JSONObject ppeVo) {
    ActYwRstatus<PwEnter> rstatus = new ActYwRstatus<PwEnter>(false, "保存失败(申报信息保存失败)！");
    PwEnterVo peVo = null;
    try {
      Map<String, Class> classMap = new HashMap<String, Class>();
      classMap.put("pwCompany", PwCompany.class);
      peVo = (PwEnterVo) JSONObject.toBean(ppeVo, PwEnterVo.class, classMap);
    } catch (Exception e) {
      rstatus = new ActYwRstatus<PwEnter>(false, "入驻申请参数格式异常");
    }finally {
      if(peVo == null){
        rstatus = new ActYwRstatus<PwEnter>(false, "入驻申请参数格式异常");
      }
    }

    try {
      rstatus = pwEnterService.saveEnterApply(peVo);
    } catch (NoTeamException e) {
      rstatus = new ActYwRstatus<PwEnter>(false, "保存失败(没有团队)！", new PwEnter(peVo.getEid()));
    }

    PwEnter curPwEnter = rstatus.getDatas();
    if(curPwEnter != null){
      if(curPwEnter.getEcompany() != null){
        curPwEnter.getEcompany().setPwEnter(null);
      }
      if(curPwEnter.getEproject() != null){
        curPwEnter.getEproject().setPwEnter(null);
      }
      if(curPwEnter.getEteam() != null){
        curPwEnter.getEteam().setPwEnter(null);
      }
      rstatus.setDatas(curPwEnter);
    }
    return rstatus;
  }
}