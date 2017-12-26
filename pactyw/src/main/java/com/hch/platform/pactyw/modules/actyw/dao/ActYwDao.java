package com.oseasy.initiate.modules.actyw.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYw;

/**
 * 项目流程关联DAO接口.
 * @author chenhao
 * @version 2017-05-23
 */
@MyBatisDao
public interface ActYwDao extends CrudDao<ActYw> {
  /**
   * 根据流程keyss 获取对象.
   * @param keyss 流程标识
   * @return List
   */
  public List<ActYw> getByKeyss(@Param("keyss")String keyss);

  /**
   * 根据条件查询已部署的流程.
   * @param actYw 项目流程
   * @return List
   */
  public List<ActYw> findListByDeploy(ActYw actYw);

  /**
   * 批量更新显示到时间轴状态.
   * @param entitys 修改记录（id不能为空）
   * @param isShowAxis 修改值
   */
  public void updateIsShowAxisPL(@Param("entitys") List<ActYw> entitys, Boolean isShowAxis);
}