package com.oseasy.initiate.modules.pw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.mapper.JsonMapper;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FileUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.json.JsonAliUtils;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwApply;
import com.oseasy.initiate.modules.actyw.service.ActYwApplyService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowYwId;
import com.oseasy.initiate.modules.actyw.vo.ActYwApplyVo;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.ftp.service.UeditorUploadService;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.oa.vo.OaNotifySendType;
import com.oseasy.initiate.modules.oa.vo.OaNotifyTypeStatus;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.pw.dao.PwEnterDao;
import com.oseasy.initiate.modules.pw.entity.PwEnter;
import com.oseasy.initiate.modules.pw.entity.PwEnterDetail;
import com.oseasy.initiate.modules.pw.entity.PwEnterRel;
import com.oseasy.initiate.modules.pw.entity.PwEnterRoom;
import com.oseasy.initiate.modules.pw.exception.AuditFailException;
import com.oseasy.initiate.modules.pw.exception.EnterException;
import com.oseasy.initiate.modules.pw.exception.NoTeamException;
import com.oseasy.initiate.modules.pw.vo.DtypeTerm;
import com.oseasy.initiate.modules.pw.vo.PwEnterExpireVo;
import com.oseasy.initiate.modules.pw.vo.PwEnterShStatus;
import com.oseasy.initiate.modules.pw.vo.PwEnterStatus;
import com.oseasy.initiate.modules.pw.vo.PwEnterType;
import com.oseasy.initiate.modules.pw.vo.PwEnterVo;
import com.oseasy.initiate.modules.pw.vo.SvalPw;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.tool.SysNoType;
import com.oseasy.initiate.modules.sys.tool.SysNodeTool;
import com.oseasy.initiate.modules.team.entity.Team;

import net.sf.json.JSONObject;

/**
 * 入驻申报Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwEnterService extends CrudService<PwEnterDao, PwEnter> {
  private static final String Apply_A = "A";
  @Autowired
  private UeditorUploadService ueditorUploadService;
  @Autowired
  private SysAttachmentService sysAttachmentService;
  @Autowired
  private ActYwApplyService actYwApplyService;
	@Autowired
	PwEnterRelService pwEnterRelService;
	@Autowired
	PwEnterDetailService pwEnterDetailService;
	@Autowired
	PwEnterRoomService pwEnterRoomService;
	@Autowired
	PwCompanyService pwCompanyService;
	@Autowired
	OaNotifyService oaNotifyService;

	public PwEnter get(String id) {
		return super.get(id);
	}

	public PwEnter getByGroup(String id) {
	  return dao.getByGroup(id);
	}

	public List<PwEnter> findList(PwEnter pwEnter) {
		return super.findList(pwEnter);
	}

	public Page<PwEnter> findPage(Page<PwEnter> page, PwEnter pwEnter) {
		return super.findPage(page, pwEnter);
	}

	public List<PwEnter> findListByGroup(PwEnter pwEnter) {
	  return dao.findListByGroup(pwEnter);
	}

	public Page<PwEnter> findPageByGroup(Page<PwEnter> page, PwEnter pwEnter) {
	  pwEnter.setPage(page);
    page.setList(findListByGroup(pwEnter));
    return page;
	}

	@Transactional(readOnly = false)
	public void save(PwEnter pwEnter) {
	  if(pwEnter.getIsNewRecord()){
	    if(StringUtil.isEmpty(pwEnter.getNo())){
	      pwEnter.setNo(SysNodeTool.genByKeyss(SysNoType.NO_PW_ER));
	    }
	    if(StringUtil.isEmpty(pwEnter.getIsTemp())){
	      pwEnter.setIsTemp(Global.YES);
	    }
	  }
		super.save(pwEnter);
	}

  @Transactional(readOnly = false)
  public void delete(PwEnter pwEnter) {
    super.delete(pwEnter);
  }

  @Transactional(readOnly = false)
  public void deleteApply(PwEnter pwEnter) {
    dao.delete(pwEnter);//删除入驻

//    actYwApplyService.getByEid(pwEnter.getId());
    actYwApplyService.delete(new ActYwApply());//删除申报

//  pwEnterDetailService.getByEid(pwEnter.getId());
    pwEnterDetailService.delete(new PwEnterDetail());//删除项目

//  pwEnterDetailService.getByEid(pwEnter.getId());
    pwEnterDetailService.delete(new PwEnterDetail());//删除团队

//  pwEnterDetailService.getByEid(pwEnter.getId());
    pwEnterDetailService.delete(new PwEnterDetail());//删除企业

//  pwEnterRelService.getByEid(pwEnter.getId());
    pwEnterRelService.delete(new PwEnterRel());
  }

  @Transactional(readOnly = false)
  public ActYwApply saveApply(PwEnter pwEnter) {
    ActYwApply actYwApply = new ActYwApply();
    actYwApply.setActYw(new ActYw(FlowYwId.FY_ENTER.getId()));
    actYwApply.setType(FlowType.FWT_ENTER.getKey());
    actYwApply.setRelId(pwEnter.getId());
    actYwApplyService.saveApplyAndSubmit(actYwApply);
    return actYwApply;
  }

  /**
   * 入驻申报.
   * @param pwEnter
   * @param peVo
   * @return
   * @throws EnterException
   */
	@Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> saveEnterApply(PwEnterVo peVo) throws NoTeamException, EnterException{
    if (StringUtil.isEmpty(peVo.getEid())) {
      return new ActYwRstatus<PwEnter>(false, "入驻申请不能为空");
    }

    if ((!peVo.getHasCompany()) && StringUtil.isEmpty(peVo.getTeamId())) {
      return new ActYwRstatus<PwEnter>(false, "入驻团队不能为空");
    }

    PwEnter pwEnter = getByGroup(peVo.getEid());
    if (pwEnter == null) {
      return new ActYwRstatus<PwEnter>(false, "入驻记录不存在！");
    }

	  if (peVo.getHasCompany()) {
      if ((peVo.getPwCompany() != null)) {
        if((pwEnter.getEcompany() == null) || StringUtil.isEmpty(peVo.getPwCompany().getId()) || !(peVo.getPwCompany().getId()).equals(pwEnter.getEcompany().getPwCompany().getId())){
          pwEnter.setEcompany(new PwEnterDetail(peVo.getPwCompany()));
          pwEnter.getEcompany().setRemarks(peVo.getPwCompanyRemarks());
        }else{
          pwEnter.getEcompany().setPwCompany(peVo.getPwCompany());
          pwEnter.getEcompany().getPwCompany().setIsNewRecord(false);
          pwEnter.getEcompany().setRemarks(peVo.getPwCompanyRemarks());
          pwEnter.getEcompany().setIsNewRecord(false);
        }
      }else{
        return new ActYwRstatus<PwEnter>(false, "入驻企业参数不能为空");
      }
    }

    if (peVo.getHasProject()) {
      if((pwEnter.getEproject() == null) || !(peVo.getProjectId()).equals(pwEnter.getEproject().getProject().getId())){
        pwEnter.setEproject(new PwEnterDetail(new ProjectDeclare(peVo.getProjectId())));
        pwEnter.getEproject().setRemarks(peVo.getProjectRemarks());
        pwEnter.getEproject().setPteam(peVo.getProjectPteam());
      }else{
        pwEnter.getEproject().setProject(new ProjectDeclare(peVo.getProjectId()));
        pwEnter.getEproject().setRemarks(peVo.getProjectRemarks());
        pwEnter.getEproject().setPteam(peVo.getProjectPteam());
        pwEnter.getEproject().setIsNewRecord(false);
      }

      if((Global.YES).equals(pwEnter.getEproject().getPteam())){
        if(pwEnter.getEteam() == null){
          pwEnter.setEteam(new PwEnterDetail(new Team(peVo.getTeamId())));
        }else{
          pwEnter.getEteam().setTeam(new Team(peVo.getTeamId()));
        }
      }else{
        if(pwEnter.getEteam() != null){
          if(!(pwEnter.getEteam().getTeam().getId()).equals(peVo.getTeamId())){
            pwEnter.getEteam().setTeam(new Team(peVo.getTeamId()));
          }
        }
      }
    }

    if (peVo.getHasTeam()) {
      if((pwEnter.getEteam() == null)){
        pwEnter.setEteam(new PwEnterDetail(new Team(peVo.getTeamId())));
        pwEnter.getEteam().setRemarks(peVo.getTeamRemarks());
      }else{
        pwEnter.getEteam().setTeam(new Team(peVo.getTeamId()));
        pwEnter.getEteam().setRemarks(peVo.getTeamRemarks());
        pwEnter.getEteam().setIsNewRecord(false);
      }
    }
	  return saveEnterApply(pwEnter, peVo.getIsSave());
	}

	/**
	 * 入驻申报.
	 * @param pwEnter
	 * @param isSaveRel
	 * @return
	 * @throws NoTeamException
	 * @throws EnterException
	 */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> saveEnterApply(PwEnter pwEnter, Boolean isSaveRel) throws NoTeamException, EnterException{
	  if(isSaveRel == null){
	    isSaveRel = false;
	  }
	  Boolean isTrue = false;
    ActYwApply actYwApply = null;

    if(isSaveRel){
      pwEnter.setIsTemp(Global.NO);
      save(pwEnter);
  	  actYwApply = saveApply(pwEnter);
      if ((actYwApply == null)){
        throw new EnterException("保存失败(申报信息保存失败)！");
      }
    }

    if ((pwEnter.getEteam() != null) && (StringUtil.isNotEmpty((pwEnter.getEteam().getTeam().getId())))){
      isTrue = true;
      PwEnterDetail pedTeam = null;
      if(pwEnter.getEteam().getIsNewRecord()){
        pedTeam = new PwEnterDetail();
        pedTeam.setStatus(PwEnterStatus.PES_DSH.getKey());
        pedTeam.setType(PwEnterType.PET_TEAM.getKey());
        pedTeam.setRid(pwEnter.getEteam().getTeam().getId());
        pedTeam.setRemarks(pwEnter.getEteam().getRemarks());
        pedTeam.setPwEnter(pwEnter);
        pwEnterDetailService.save(pedTeam);
        pwEnter.setEteam(pedTeam);
      }else{
        pwEnter.getEteam().setPwEnter(new PwEnter(pwEnter.getId()));
        pwEnter.getEteam().setRid(pwEnter.getEteam().getTeam().getId());
        pwEnter.getEteam().setStatus(pwEnter.getEteam().getStatus());
        pedTeam = pwEnterDetailService.saveRT(pwEnter.getEteam());
        if(pedTeam == null){
          throw new EnterException("保存失败(申报信息保存失败)！");
        }
      }

      if(isSaveRel){
        PwEnterRel pwEnterRel = new PwEnterRel();
        pwEnterRel.setActYwApply(actYwApply);
        pwEnterRel.setPwEnterDetail(pedTeam);
        pwEnterRelService.save(pwEnterRel);
      }
    }else{
      if(pwEnter.getEcompany() == null){
        throw new NoTeamException();
      }
    }

    if ((pwEnter.getEcompany() != null)){
      isTrue = true;
      PwEnterDetail pedCompany = null;
      if(pwEnter.getEcompany().getIsNewRecord()){
        pedCompany = new PwEnterDetail();
        pedCompany.setStatus(PwEnterStatus.PES_DSH.getKey());
        pedCompany.setType(PwEnterType.PET_QY.getKey());
        pedCompany.setPwCompany(pwEnter.getEcompany().getPwCompany());
        pedCompany.setRemarks(pwEnter.getEcompany().getRemarks());
        pedCompany.setPwEnter(pwEnter);
        pwEnterDetailService.saveCompany(pedCompany);
      }else{
        pwEnter.getEcompany().setPwEnter(new PwEnter(pwEnter.getId()));
        pwEnter.getEcompany().setRid(pwEnter.getEcompany().getPwCompany().getId());
        pwEnter.getEcompany().setStatus(pwEnter.getEcompany().getStatus());
        pedCompany = pwEnterDetailService.saveCompany(pwEnter.getEcompany());
        if(pedCompany == null){
          throw new EnterException("保存失败(申报信息保存失败)！");
          //return new ActYwRstatus<PwEnter>(false, "保存失败(申报信息保存失败)！", pwEnter);
        }
      }

      if(isSaveRel){
        PwEnterRel pwEnterRel = new PwEnterRel();
        pwEnterRel.setActYwApply(actYwApply);
        pwEnterRel.setPwEnterDetail(pedCompany);
        pwEnterRelService.save(pwEnterRel);
      }
    }

    if ((pwEnter.getEproject() != null) && (StringUtil.isNotEmpty((pwEnter.getEproject().getProject().getId())))){
      isTrue = true;
      PwEnterDetail pedProject = null;
      if(pwEnter.getEproject().getIsNewRecord()){
        pedProject = new PwEnterDetail();
        pedProject.setStatus(PwEnterStatus.PES_DSH.getKey());
        pedProject.setType(PwEnterType.PET_XM.getKey());
        pedProject.setRid(pwEnter.getEproject().getProject().getId());
        pedProject.setRemarks(pwEnter.getEproject().getRemarks());
        pedProject.setPteam(pwEnter.getEproject().getPteam());
        pedProject.setPwEnter(pwEnter);
        pwEnterDetailService.save(pedProject);
      }else{
        pwEnter.getEproject().setPwEnter(new PwEnter(pwEnter.getId()));
        pwEnter.getEproject().setRid(pwEnter.getEproject().getProject().getId());
        pwEnter.getEproject().setStatus(pwEnter.getEproject().getStatus());
        pedProject = pwEnterDetailService.saveRT(pwEnter.getEproject());
        if(pedProject == null){
          throw new EnterException("保存失败(申报信息保存失败)！");
        }
      }
      pwEnter.setEproject(pedProject);

      if(isSaveRel){
        PwEnterRel pwEnterRel = new PwEnterRel();
        pwEnterRel.setActYwApply(actYwApply);
        pwEnterRel.setPwEnterDetail(pedProject);
        pwEnterRelService.save(pwEnterRel);
      }
    }

    if(isTrue){
      return new ActYwRstatus<PwEnter>(true, "保存成功！", pwEnter);
//      return new ActYwRstatus<PwEnter>(true, "保存成功！");
    }else{
      throw new EnterException("保存失败！");
      //return new ActYwRstatus<PwEnter>(false, "保存失败！", pwEnter);
    }
  }

	/**
   * 入驻审核.
	 * @param id 审核ID
	 * @param edid 审核类型
	 * @param atype 审核结果类型
	 * @param status 审核结果状态
	 * @return ActYwRstatus
	 */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> auditEnter(String id, String edid, String atype, String remarks) {
    if (StringUtil.isEmpty(id)) {
      return new ActYwRstatus<PwEnter>(false, "入驻申请ID不能为空");
    }

    if (StringUtil.isEmpty(atype)) {
      return new ActYwRstatus<PwEnter>(false, "入驻申请审核类型不能为空");
    }

    if ((ActYwApplyVo.GRADE_END).equals(atype) && StringUtil.isEmpty(remarks)) {
      return new ActYwRstatus<PwEnter>(false, "入驻申请审核拒绝通过需要说明原因");
    }

    PwEnter per = getByGroup(id);
    if(per == null){
      return new ActYwRstatus<PwEnter>(false, "入驻申请不存在,ID = " + id);
    }

    if(per.getApply() == null){
      return new ActYwRstatus<PwEnter>(false, "入驻申请信息未完善，请督促用户完善信息提交后再审核(" + id +")!");
    }

    ActYwRstatus<?> rstatus = actYwApplyService.audit(new ActYwApplyVo(per.getApply().getId(), per.getApply().getActYw(), per.getApply().getApplyUser(), per.getApply().getProcInsId(), atype));
    if(rstatus.getStatus()){
      return saveEnterByDFP(per, edid, atype, remarks);
//        SuAstatus status = new SuAstatus();
//        SuAudit audit = new SuAudit();
//        new SuoEnter(Apply_A, audit, status);
//        SuStatusParam param = status.dealAstatus(Apply_A);
//        System.out.println("【入驻】获取到的状态为：" + SuStatusGrade.getGradeByGnode(param.getStatus(), Apply_A).getGrades().toString());
//        SuRstatus suRstatus = audit.dealAudit(new SuStatusAparam(Apply_A, atype), per.getApply().getId(), FlowType.FWT_ENTER);
//        System.out.println(suRstatus.getRtVal());
    }else{
      throw new AuditFailException("入驻申请审核失败," + rstatus.getMsg());
    }
  }

  /**
   * 入驻审核-待分配.
   * @param per 入驻
   * @param edid 审核类型
   * @param atype 审核结果类型
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> saveEnterByDFP(PwEnter per, String edid, String atype, String remarks) {
    List<PwEnterDetail> perDetails;
    if (StringUtil.isNotEmpty(edid)) {
      PwEnterDetail perDetail = pwEnterDetailService.get(edid);
      if(perDetail == null){
        return new ActYwRstatus<PwEnter>(false, "入驻申请申请类型不存在,ID = " + edid);
      }
      perDetails = Lists.newArrayList();
      perDetails.add(perDetail);
    }else{
      PwEnterDetail ppwEnterDetail = new PwEnterDetail();
      ppwEnterDetail.setPwEnter(new PwEnter(per.getId()));
      perDetails = pwEnterDetailService.findList(ppwEnterDetail);
      if((perDetails == null) || (perDetails.size() <= 0)){
        throw new AuditFailException("入驻申请信息不完善,请完善申请信息或重新申请.");
      }
    }

    /**
     * 处理所有待审核的数据.
     */
    for (PwEnterDetail perDetail : perDetails) {
      if(((PwEnterShStatus.PESS_DSH.getKey()).equals(perDetail.getStatus()))){
        if((ActYwApplyVo.GRADE_PASS).equals(atype)){
          perDetail.setStatus(PwEnterShStatus.PESS_DFP.getKey());
        }else{
          perDetail.setStatus(PwEnterShStatus.PESS_FAIL.getKey());
        }
        pwEnterDetailService.save(perDetail);
      }
    }

    if((ActYwApplyVo.GRADE_PASS).equals(atype)){
      per.setStatus(PwEnterStatus.PES_RZCG.getKey());
      per.setStartDate(DateUtil.newDate());
      per.setEndDate(DateUtil.addDay(per.getStartDate(), per.getTerm()));
    }else{
      per.setStatus(PwEnterStatus.PES_RZSB.getKey());
    }
    if(StringUtil.isNotEmpty(remarks)){
      per.setRemarks(remarks);
    }
    save(per);
    return new ActYwRstatus<PwEnter>(true, "入驻团队申请审核完成");
  }

  /**
   * 入驻-即将到期状态变更（定时任务专用）.
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnterExpireVo> expireEnterAll() {
    PwEnterExpireVo pwEnterExpireVo = new PwEnterExpireVo();
    PwEnter pwEnter = new PwEnter();
    pwEnter.setPstatus(PwEnterStatus.getKeyByXQRZ());
    List<PwEnter> pwEnters = findListByGroup(pwEnter);
    pwEnterExpireVo.setTotalNum(pwEnters.size());
    for (PwEnter pwer : pwEnters) {
      pwEnterExpireVo = expireEnter(pwer, pwEnterExpireVo);
    }
    pwEnterExpireVo.setLogFile(SvalPw.ENTER_EXPIRE_LOGFILE + StringUtil.LINE + DateUtil.formatDate(new Date(), DateUtil.FMT_YYYY_MM_DD_HHmmss) + StringUtil.LOG);
    JsonAliUtils.writeFile(pwEnterExpireVo.getLogFile(), JsonMapper.toJsonString(pwEnterExpireVo));
    return new ActYwRstatus<PwEnterExpireVo>(true, "处理完成,总数量:["+pwEnterExpireVo.getTotalNum()+"],成功数量:["+pwEnterExpireVo.getSuccNum()+"],失败数量:["+pwEnterExpireVo.getFailNum()+"]", pwEnterExpireVo);
  }

  /**
   * 入驻-即将到期状态变更.
   * @param id 入驻标识
   * @param status 审核类型
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public PwEnterExpireVo expireEnter(PwEnter pwEnter, PwEnterExpireVo pwEnterExpireVo) {
    if(pwEnterExpireVo == null){
      pwEnterExpireVo = new PwEnterExpireVo();
    }

    if((pwEnter != null) && (pwEnter.getEndDate() != null)){
      if(DateUtil.pastDays(pwEnter.getEndDate()) <= SvalPw.getEnterExpireMaxDay()){
        /**
         * 处理所有待审核的数据.
         */
        if (pwEnter.getEcompany() != null) {
          if((PwEnterStatus.getKeyByXQRZ()).contains(pwEnter.getEcompany().getStatus())){
            pwEnter.getEcompany().setStatus(PwEnterShStatus.PESS_DXQ.getKey());
            pwEnterDetailService.updateStatus(pwEnter.getEcompany());
          }
        }

        if ((pwEnter.getEcompany() != null) && StringUtil.isNotEmpty(pwEnter.getEcompany().getId())) {
          if((PwEnterStatus.getKeyByXQRZ()).contains(pwEnter.getEcompany().getStatus())){
            pwEnter.getEcompany().setStatus(PwEnterShStatus.PESS_DXQ.getKey());
            pwEnterDetailService.updateStatus(pwEnter.getEcompany());
          }
        }

        if ((pwEnter.getEproject() != null) && StringUtil.isNotEmpty(pwEnter.getEproject().getId())) {
          if((PwEnterStatus.getKeyByXQRZ()).contains(pwEnter.getEproject().getStatus())){
            pwEnter.getEproject().setStatus(PwEnterShStatus.PESS_DXQ.getKey());
            pwEnterDetailService.updateStatus(pwEnter.getEproject());
          }
        }

        if ((pwEnter.getEteam() != null) && StringUtil.isNotEmpty(pwEnter.getEteam().getId())) {
          if((PwEnterStatus.getKeyByXQRZ()).contains(pwEnter.getEteam().getStatus())){
            pwEnter.getEteam().setStatus(PwEnterShStatus.PESS_DXQ.getKey());
            pwEnterDetailService.updateStatus(pwEnter.getEteam());
          }
        }

        pwEnter.setStatus(PwEnterStatus.PES_DXQ.getKey());
        updateStatus(pwEnter);
        pwEnterExpireVo.getSuccEnters().add(new ActYwRstatus<PwEnter>(true, "处理待续期成功", pwEnter));
      }else{
        pwEnterExpireVo.getIgnorEnters().add(new ActYwRstatus<PwEnter>(true, "未处理", pwEnter));
      }
    }else{
      pwEnterExpireVo.getFailEnters().add(new ActYwRstatus<PwEnter>(false, "入驻申请不存在", pwEnter));
    }
    return pwEnterExpireVo;
  }

  /**
   * 入驻-续期.
   * @param per 入驻
   * @param edid 审核类型
   * @param atype 审核结果类型
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> xqEnter(String id, Integer term) {
    if((term == null) || (term <= 0)){
      return new ActYwRstatus<PwEnter>(false, "入驻续期期限不能为空，且应大于 0");
    }
    if(StringUtil.isEmpty(id)){
      return new ActYwRstatus<PwEnter>(false, "入驻申请不能为空");
    }
    PwEnter pwEnter = get(id);
    if(pwEnter != null){
      Integer addTerm = DtypeTerm.addDayByType(term, pwEnter.getEndDate());
      if(addTerm == null){
        return new ActYwRstatus<PwEnter>(false, "续期失败");
      }

      PwEnterDetail ppwEnterDetail = new PwEnterDetail();
      ppwEnterDetail.setPwEnter(new PwEnter(pwEnter.getId()));
      List<PwEnterDetail> perDetails = pwEnterDetailService.findList(ppwEnterDetail);
      /**
       * 处理所有待审核的数据.
       */
      for (PwEnterDetail perDetail : perDetails) {
        if((PwEnterStatus.getKeyByXQRZ()).contains(perDetail.getStatus())){
          perDetail.setStatus(PwEnterShStatus.PESS_YFP.getKey());
          pwEnterDetailService.save(perDetail);
        }
      }

      pwEnter.setStatus(PwEnterStatus.PES_YXQ.getKey());
      pwEnter.setTerm(pwEnter.getTerm() + addTerm);
      pwEnter.setEndDate(DateUtil.addDay(pwEnter.getStartDate(), pwEnter.getTerm()));
      save(pwEnter);
      return new ActYwRstatus<PwEnter>(true, "续期成功");
    }
    return new ActYwRstatus<PwEnter>(false, "入驻申请不存在");
  }

  /**
   * 入驻-退孵.
   * @param per 入驻
   * @param edid 审核类型
   * @param atype 审核结果类型
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> exitEnter(String id) {
    if(StringUtil.isEmpty(id)){
      return new ActYwRstatus<PwEnter>(false, "入驻申请不能为空");
    }
    PwEnter pwEnter = get(id);
    if(pwEnter != null){
      PwEnterDetail ppwEnterDetail = new PwEnterDetail();
      ppwEnterDetail.setPwEnter(new PwEnter(pwEnter.getId()));
      List<PwEnterDetail> perDetails = pwEnterDetailService.findList(ppwEnterDetail);
      /**
       * 处理所有待审核的数据.
       */
      for (PwEnterDetail perDetail : perDetails) {
        if((PwEnterStatus.getKeyByQXRZ()).contains(perDetail.getStatus())){
          perDetail.setStatus(PwEnterShStatus.PESS_YTC.getKey());
          pwEnterDetailService.save(perDetail);
        }
      }

      List<String> ids = PwEnterRoom.getIds(pwEnterRoomService.findList(new PwEnterRoom(new PwEnter(pwEnter.getId()))));
      if((ids != null) && (ids.size() > 0)){
          pwEnterRoomService.deletePLWL(ids);
      }

      pwEnter.setStatus(PwEnterStatus.PES_YTF.getKey());
      pwEnter.setEndDate(new Date());
      save(pwEnter);
      return new ActYwRstatus<PwEnter>(true, "退孵成功");
    }
    return new ActYwRstatus<PwEnter>(false, "入驻申请不存在");
  }

  /**
   * 入驻-删除（会删除附件）.
   * @param per 入驻
   * @param edid 审核类型
   * @param atype 审核结果类型
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> deleteWLEnter(String id, HttpServletRequest request, HttpServletResponse response) {
    ActYwRstatus<PwEnter> rstatus = deleteWLEnter(id);
    if(rstatus.getStatus()){
      sysAttachmentService.ajaxDelete(null, id, ueditorUploadService, request, response);
      rstatus = new ActYwRstatus<PwEnter>(true, "删除成功");
    }
    return rstatus;
  }

  /**
   * 入驻-删除.
   * @param per 入驻
   * @param edid 审核类型
   * @param atype 审核结果类型
   * @return ActYwRstatus
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> deleteWLEnter(String id) {
    if(StringUtil.isEmpty(id)){
      return new ActYwRstatus<PwEnter>(false, "入驻申请ID不能为空");
    }
    PwEnter pwEnter = get(id);
    if(pwEnter != null){
      PwEnterDetail ppwEnterDetail = new PwEnterDetail();
      ppwEnterDetail.setPwEnter(new PwEnter(pwEnter.getId()));
      List<PwEnterDetail> perDetails = pwEnterDetailService.findList(ppwEnterDetail);
      /**
       * 处理所有待审核的数据.
       */
      for (PwEnterDetail perDetail : perDetails) {
          pwEnterDetailService.deleteWL(perDetail);
      }
      deleteWL(pwEnter);
      return new ActYwRstatus<PwEnter>(true, "删除成功");
    }
    return new ActYwRstatus<PwEnter>(false, "入驻申请不存在");
  }

  /**
   * 物理删除.
   * @param pwEnter 审核结果类型
   */
  @Transactional(readOnly = false)
  public void deleteWL(PwEnter pwEnter) {
    dao.deleteWL(pwEnter);
  }

  /**
   * 发送入驻站内信.
   * @param entity 发送参数
   * @param user 发送用户
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> sendMsg(String id, User user, String type) {
    if(StringUtil.isEmpty(id)){
      return new ActYwRstatus<PwEnter>(false, "发送参数不能为空！");
    }

    if(StringUtil.isEmpty(type)){
      return new ActYwRstatus<PwEnter>(false, "发送参数错误,消息类型");
    }

    PwEnter pwEnter = getByGroup(id);
    OaNotify.Type_Enum oanType = OaNotify.Type_Enum.getByValue(type);
    if((OaNotify.Type_Enum.TYPE61).equals(oanType)){
      return sendOaNotify(pwEnter, user, OaNotify.Type_Enum.TYPE61, "入驻完善资料通知", String.format("您入驻申请的资料不完整，编号[%s]，请及时更新，否则将会被清理。", pwEnter.getNo(), pwEnter.getId()));
    }else if((OaNotify.Type_Enum.TYPE62).equals(oanType)){
      return sendOaNotify(pwEnter, user, OaNotify.Type_Enum.TYPE62, "入驻申请警告通知", "您入驻申请过于频繁，请珍惜申请权限，否则将承担相应惩罚。");
    }else{
      return new ActYwRstatus<PwEnter>(false, "发送参数消息类型未定义");
    }
  }

  /**
   * 发送入驻站内信.
   * @param entity 发送参数
   * @param user 发送用户
   */
  @Transactional(readOnly = false)
  public ActYwRstatus<PwEnter> sendOaNotify(PwEnter pwEnter, User user, OaNotify.Type_Enum type, String title, String content) {
    if((pwEnter == null)){
      return new ActYwRstatus<PwEnter>(false, "发送入驻参数不正确！");
    }

    if((user == null)){
      return new ActYwRstatus<PwEnter>(false, "发送人资料不正确！");
    }

    if(StringUtil.isEmpty(title) || StringUtil.isEmpty(content) || (type == null)){
      return new ActYwRstatus<PwEnter>(false, "发送参数不完整[主题|内容|消息类型]");
    }

    OaNotifyRecord oaNotifyRecord = new OaNotifyRecord();
    OaNotify oaNotify = new OaNotify();
    oaNotify.setTitle(title);
    oaNotify.setContent(content);
    oaNotify.setType(type.getValue());
    oaNotify.setsId(pwEnter.getId());
    oaNotify.setCreateBy(user);
    oaNotify.setCreateDate(new Date());
    oaNotify.setUpdateBy(user);
    oaNotify.setUpdateDate(oaNotify.getCreateDate());
    oaNotify.setEffectiveDate(oaNotify.getCreateDate());
    oaNotify.setStatus(OaNotifyTypeStatus.DEPLOY.getKey());
    oaNotify.setSendType(OaNotifySendType.DIRECRIONAL.getVal());

    List<OaNotifyRecord> recList = new ArrayList<>();
    oaNotifyRecord.setId(IdGen.uuid());
    oaNotifyRecord.setOaNotify(oaNotify);
    oaNotifyRecord.setUser(pwEnter.getApplicant());
    oaNotifyRecord.setReadFlag(Global.NO);
    oaNotifyRecord.setOperateFlag(Global.NO);
    recList.add(oaNotifyRecord);

    oaNotify.setOaNotifyRecordList(recList);
    oaNotifyService.save(oaNotify);
    return new ActYwRstatus<PwEnter>(true, "通知发送成功！");
  }

  @Transactional(readOnly = false)
  public void updateStatus(PwEnter pwEnter) {
    dao.updateStatus(pwEnter);
  }
}