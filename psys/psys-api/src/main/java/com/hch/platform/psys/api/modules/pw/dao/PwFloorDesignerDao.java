package com.hch.platform.pcore.modules.pw.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.pw.entity.PwFloorDesigner;
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