package com.hch.platform.pcore.modules.pw.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwFassetsnoRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 固定资产编号规则DAO接口.
 *
 * @author pw
 * @version 2017-12-05
 */
@MyBatisDao
public interface PwFassetsnoRuleDao extends CrudDao<PwFassetsnoRule> {

    int deleteByFcids(@Param("fcids")List<String> fcids);

}