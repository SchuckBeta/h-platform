package com.hch.platform.pcore.modules.sys.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.SysCertificateIssued;

/**
 * 系统证书执行记录DAO接口.
 * @author chenh
 * @version 2017-11-07
 */
@MyBatisDao
public interface SysCertificateIssuedDao extends CrudDao<SysCertificateIssued> {
  /**
   * 根据证书查询授予记录列表;
   * @param entity
   * @return
   */
  public List<SysCertificateIssued> findListByCert(SysCertificateIssued entity);

  /**
   * 根据执行人查询授予记录列表;
   * @param entity
   * @return
   */
  public List<SysCertificateIssued> findListByIssued(SysCertificateIssued entity);

  /**
   * 根据被执行人查询授予记录列表;
   * @param entity
   * @return
   */
  public List<SysCertificateIssued> findListByAccept(SysCertificateIssued entity);

  /**
   * 根据业务查询授予记录列表;
   * @param entity
   * @return
   */
  public List<SysCertificateIssued> findListByYw(SysCertificateIssued entity);

}