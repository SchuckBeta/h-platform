package com.oseasy.initiate.modules.sys.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;

/**
 * 学生扩展信息表DAO接口
 * @author zy
 * @version 2017-03-27
 */
@MyBatisDao
public interface StudentExpansionDao extends CrudDao<StudentExpansion> {

public StudentExpansion getByUserId(String userId);

public int getStuExListCount(Map<String, Object> param);

public List<Map<String, String>> getStuExList(Map<String, Object> param);
	
}