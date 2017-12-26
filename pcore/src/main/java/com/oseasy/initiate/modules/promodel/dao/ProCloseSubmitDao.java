package com.hch.platform.pcore.modules.promodel.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.promodel.entity.ProCloseSubmit;
import org.apache.ibatis.annotations.Param;

/**
 * 结项提交信息表DAO接口.
 * @author zy
 * @version 2017-12-01
 */
@MyBatisDao
public interface ProCloseSubmitDao extends CrudDao<ProCloseSubmit> {

	ProCloseSubmit getByGnodeId(@Param("promodelId")String promodelId, @Param("gnodeId")String gnodeId);
}