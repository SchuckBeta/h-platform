package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwDesignerRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房间设计表DAO接口.
 * @author zy
 * @version 2017-12-18
 */
@MyBatisDao
public interface PwDesignerRoomDao extends CrudDao<PwDesignerRoom> {

	List<PwDesignerRoom> findListByCid(@Param("cid") String  cid);

	void deleteAllByCid(@Param("cid") String cid);
}