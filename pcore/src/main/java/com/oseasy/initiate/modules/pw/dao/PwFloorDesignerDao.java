package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwFloorDesigner;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 楼层设计DAO接口.
 * @author 章传胜
 * @version 2017-11-28
 */
@MyBatisDao
public interface PwFloorDesignerDao extends CrudDao<PwFloorDesigner> {
    public int insertAll(@Param("list") List<PwFloorDesigner> list);
    public void deleteAll(@Param("ids") List<String> ids);
}