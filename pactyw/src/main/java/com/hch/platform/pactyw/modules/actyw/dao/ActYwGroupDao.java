package com.oseasy.initiate.modules.actyw.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;

/**
 * 自定义流程DAO接口.
 * @author chenhao
 * @version 2017-05-23
 */
@MyBatisDao
public interface ActYwGroupDao extends CrudDao<ActYwGroup> {
    /**
     * 根据流程keyss 获取对象.
     * @param keyss 流程标识
     * @return ActYwGroup
     */
    public List<ActYwGroup> getByKeyss(@Param("keyss")String keyss);

}