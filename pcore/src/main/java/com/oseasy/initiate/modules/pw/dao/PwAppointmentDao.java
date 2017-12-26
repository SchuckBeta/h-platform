package com.hch.platform.pcore.modules.pw.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwAppointment;
import com.hch.platform.pcore.modules.pw.vo.PwAppCalendarParam;
import com.hch.platform.pcore.modules.pw.vo.PwAppMouthVo;
import com.hch.platform.pcore.modules.pw.vo.PwAppointmentVo;
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