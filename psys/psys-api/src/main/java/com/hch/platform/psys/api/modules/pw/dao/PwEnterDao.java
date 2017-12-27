package com.hch.platform.pcore.modules.pw.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwEnter;

/**
 * 入驻申报DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwEnterDao extends CrudDao<PwEnter> {

  /**
   * 获取单条入驻申报数据
   * @param id
   * @return
   */
  public PwEnter getByGroup(String id);

  /**
   * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
   * @param entity
   * @return
   */
  public List<PwEnter> findListByGroup(PwEnter entity);

  public void deleteWL(PwEnter pwEnter);

  public void updateStatus(PwEnter pwEnter);
}