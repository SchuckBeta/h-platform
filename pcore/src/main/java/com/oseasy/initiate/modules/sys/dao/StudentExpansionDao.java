package com.hch.platform.pcore.modules.sys.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.project.vo.ProjectExpVo;
import com.hch.platform.pcore.modules.sys.entity.GContestUndergo;
import com.hch.platform.pcore.modules.sys.entity.StudentExpansion;

/**
 * 学生扩展信息表DAO接口
 * @author zy
 * @version 2017-03-27
 */
@MyBatisDao
public interface StudentExpansionDao extends CrudDao<StudentExpansion> {
	public List<ProjectExpVo> findProjectByStudentId(String id);

	public List<GContestUndergo> findGContestByStudentId(String id);
	public StudentExpansion getByUserId(String userId);

	public int getStuExListCount(Map<String, Object> param);

	public List<Map<String, String>> getStuExList(Map<String, Object> param);

	List<StudentExpansion> getStudentByTeamId(String teamId);
}