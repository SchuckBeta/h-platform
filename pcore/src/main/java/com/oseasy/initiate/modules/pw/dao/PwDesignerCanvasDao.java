package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwDesignerCanvas;
import org.apache.ibatis.annotations.Param;

/**
 * 画布表DAO接口.
 * @author zy
 * @version 2017-12-18
 */
@MyBatisDao
public interface PwDesignerCanvasDao extends CrudDao<PwDesignerCanvas> {

	void deleteByFloorId(@Param("floorId")String floorId);

	PwDesignerCanvas getPwDesignerCanvasByFloorId(@Param("floorId")String floorId);
}