package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwDesignerRoomAttr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房间属性表DAO接口.
 * @author zy
 * @version 2017-12-18
 */
@MyBatisDao
public interface PwDesignerRoomAttrDao extends CrudDao<PwDesignerRoomAttr> {

	void deleteAllByRoomId(@Param("rid") String rid);

	List<PwDesignerRoomAttr> findListByRid(@Param("rid") String rid);
}