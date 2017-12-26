package com.hch.platform.pcore.modules.sco.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoCourse;

import com.hch.platform.pcore.modules.sco.vo.ScoCourseVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学分课程DAO接口.
 * @author 张正
 * @version 2017-07-13
 */
@MyBatisDao
public interface ScoCourseDao extends CrudDao<ScoCourse> {
    //根据课程名或者课程代码查询学分课程
    public List<ScoCourse> findListByNameOrCode(@Param("keyword") String keyword);

    public List<ScoCourse> findCourseList(ScoCourse scoCourse);
    
    public int checkName(@Param("id") String id,@Param("name")String name);
	public int checkCode(@Param("id") String id,@Param("code") String code);

	List<ScoCourseVo> findscoCourseVoList(ScoCourseVo scoCourseVo);
}