package com.oseasy.initiate.modules.pw.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwAppointment;
import com.oseasy.initiate.modules.pw.vo.PwAppCalendarParam;
import com.oseasy.initiate.modules.pw.vo.PwAppMouthVo;
import com.oseasy.initiate.modules.pw.vo.PwAppointmentVo;
import org.apache.ibatis.annotations.Param;

/**
 * 预约DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwAppointmentDao extends CrudDao<PwAppointment> {

  /**
   * 根据日历条件查询预约申请.
   * @param pwAppcParam
   * @return
   */
  public List<PwAppointment> findListByCalendarParam(PwAppCalendarParam pwAppcParam);

  List<PwAppointment>  findMyPwAppointmentList(PwAppointment pwAppointment);

  List<PwAppointment> findViewMonth(PwAppointment pwAppointment);

  List<PwAppointment> findListByPwAppointmentVo(PwAppointmentVo pwAppointmentVo);

  List<PwAppMouthVo> findViewMonthList(PwAppointmentVo pwAppointmentVo);

  int deleteByRoomIds(@Param("roomIds") List<String> roomIds);
}