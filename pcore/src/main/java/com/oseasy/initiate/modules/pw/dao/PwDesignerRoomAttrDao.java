package com.hch.platform.pcore.modules.pw.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwDesignerRoomAttr;
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