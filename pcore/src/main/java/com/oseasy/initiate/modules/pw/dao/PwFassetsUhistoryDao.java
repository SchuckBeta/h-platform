package com.hch.platform.pcore.modules.pw.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwFassetsUhistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 固定资产使用记录DAO接口.
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwFassetsUhistoryDao extends CrudDao<PwFassetsUhistory> {

    int deleteByFassetsIds(@Param("fassetsIds") List<String> fassetsIds);

    int deleteByRoomIds(@Param("roomIds") List<String> roomIds);

}