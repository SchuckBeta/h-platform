package com.hch.platform.pcore.modules.sys.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.TeacherKeyword;

import java.util.List;

/**
 * teacherKeywordDAO接口.
 * @author zy
 * @version 2017-07-03
 */
@MyBatisDao
public interface TeacherKeywordDao extends CrudDao<TeacherKeyword> {
    public void delByTeacherid(String teacherId);

    public List<TeacherKeyword> findByTeacherid(String teacherId);
    public List<String> findStringByTeacherid(String teacherId);
}