package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.pw.dao.PwEnterDetailDao;
import com.hch.platform.pcore.modules.pw.entity.PwCompany;
import com.hch.platform.pcore.modules.pw.entity.PwEnterDetail;

/**
 * 入驻申报详情Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwEnterDetailService extends CrudService<PwEnterDetailDao, PwEnterDetail> {
  @Autowired
  PwCompanyService pwCompanyService;

	public PwEnterDetail get(String id) {
		return super.get(id);
	}

	public List<PwEnterDetail> findList(PwEnterDetail pwEnterDetail) {
		return super.findList(pwEnterDetail);
	}

	public Page<PwEnterDetail> findPage(Page<PwEnterDetail> page, PwEnterDetail pwEnterDetail) {
		return super.findPage(page, pwEnterDetail);
	}

  public List<PwEnterDetail> findListByTeam(PwEnterDetail pwEnterDetail) {
    return dao.findListByTeam(pwEnterDetail);
  }

  public Page<PwEnterDetail> findPageByTeam(Page<PwEnterDetail> page, PwEnterDetail entity) {
    entity.setPage(page);
    page.setList(findListByTeam(entity));
    return page;
  }

  public List<PwEnterDetail> findListByQy(PwEnterDetail pwEnterDetail) {
    return dao.findListByQy(pwEnterDetail);
  }

  public Page<PwEnterDetail> findPageByQy(Page<PwEnterDetail> page, PwEnterDetail entity) {
    entity.setPage(page);
    page.setList(findListByQy(entity));
    return page;
  }

  public List<PwEnterDetail> findListByXm(PwEnterDetail pwEnterDetail) {
    return dao.findListByXm(pwEnterDetail);
  }

  public Page<PwEnterDetail> findPageByXm(Page<PwEnterDetail> page, PwEnterDetail entity) {
    entity.setPage(page);
    page.setList(findListByXm(entity));
    return page;
  }

  public List<PwEnterDetail> findListByAll(PwEnterDetail pwEnterDetail) {
    return dao.findListByAll(pwEnterDetail);
  }

  public Page<PwEnterDetail> findPageByAll(Page<PwEnterDetail> page, PwEnterDetail entity) {
    entity.setPage(page);
    page.setList(findListByAll(entity));
    return page;
  }


	@Transactional(readOnly = false)
	public void save(PwEnterDetail pwEnterDetail) {
	  if(pwEnterDetail.getIsNewRecord()){
	    if(StringUtil.isEmpty(pwEnterDetail.getPteam())){
	      pwEnterDetail.setPteam(Global.NO);
	    }
	  }
		super.save(pwEnterDetail);
	}

	@Transactional(readOnly = false)
	public PwEnterDetail saveRT(PwEnterDetail pwEnterDetail) {
	  save(pwEnterDetail);
	  return pwEnterDetail;
	}

	/**
	 * 企业入驻信心保存.
	 * @param pwEnterDetail
	 */
	@Transactional(readOnly = false)
	public PwEnterDetail saveCompany(PwEnterDetail pwEnterDetail) {
	  if(pwEnterDetail.getIsNewRecord()){
	    if((pwEnterDetail.getPwEnter() != null) && (pwEnterDetail.getPwCompany() != null)){
	      PwCompany newPwCompany = pwEnterDetail.getPwCompany();
  	    pwCompanyService.save(newPwCompany);
  	    pwEnterDetail.setPwCompany(newPwCompany);
        pwEnterDetail.setRid(newPwCompany.getId());
  	    save(pwEnterDetail);
  	    return pwEnterDetail;
  	  }
	  }else{
      save(pwEnterDetail);
      pwEnterDetail.getPwCompany().setIsNewRecord(false);
      pwCompanyService.save(pwEnterDetail.getPwCompany());
      return pwEnterDetail;
	  }
	  return null;
	}

	@Transactional(readOnly = false)
	public void delete(PwEnterDetail pwEnterDetail) {
		super.delete(pwEnterDetail);
	}

	public void deleteByEid(String id) {
		dao.deleteByEid(id);
	}

	/**
	 * 物理删除数据.
	 * @param perDetail
	 */
  public void deleteWL(PwEnterDetail perDetail) {
    dao.deleteWL(perDetail);
  }

  @Transactional(readOnly = false)
  public void updateStatus(PwEnterDetail perDetail) {
    dao.updateStatus(perDetail);
  }
}