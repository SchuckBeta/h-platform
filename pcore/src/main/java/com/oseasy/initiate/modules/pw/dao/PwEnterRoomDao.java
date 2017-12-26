package com.hch.platform.pcore.modules.pw.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwEnterRoom;

/**
 * 入驻场地分配DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwEnterRoomDao extends CrudDao<PwEnterRoom> {

  /**
   * 物理删除.
   * @author chenhao
   * @param pwEnterRoom 参数
   */
  public void deleteWL(PwEnterRoom pwEnterRoom);

  /**
   * 批量物理删除.
   * @author chenhao
   * @param ids ID列表
   */
  public void deletePLWL(@Param("ids")List<String> ids);

  /**
   * 根据Eid和Rid批量物理删除.
   * @author chenhao
   * @param pwEnterRoom 参数
   */
  public void deletePLWLByErid(PwEnterRoom pwEnterRoom);

  /**
   * 根据Eid批量物理删除.
   * @author chenhao
   * @param pwEnterRoom 参数
   */
  public void deletePLWLByEid(PwEnterRoom pwEnterRoom);

  /**
   * 根据Rid批量物理删除.
   * @author chenhao
   * @param pwEnterRoom 参数
   */
  public void deletePLWLByRid(PwEnterRoom pwEnterRoom);

  /**
   * @author chenhao
   * @param ids ID列表
   * @return List
   */
  public List<PwEnterRoom> findListByinIds(@Param("ids") List<String> ids);
}