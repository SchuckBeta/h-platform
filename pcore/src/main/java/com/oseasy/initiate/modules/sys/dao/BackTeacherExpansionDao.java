package com.hch.platform.pcore.modules.sys.dao;

import java.util.List;

import com.hch.platform.pcore.modules.project.vo.ProjectExpVo;
import com.hch.platform.pcore.modules.sys.entity.GContestUndergo;
import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.team.entity.Team;

/**
 * 导师扩展信息表DAO接口
 * @author l
 * @version 2017-03-31
 */
@MyBatisDao
public interface BackTeacherExpansionDao extends CrudDao<BackTeacherExpansion> {
	
	BackTeacherExpansion findTeacherByUserIdAndType(@Param(value="userId")String userId,@Param(value="type")String type);
	public BackTeacherExpansion getByUserId(String uid);
	List<Team> findTeamById(@Param(value="userId") String userId);

	public List<BackTeacherExpansion> findTeacherAward(String userId);

	String findTeacherIdByUserId(String userId);
	BackTeacherExpansion findTeacherByUserId(String userId);

	List<BackTeacherExpansion> findTeacherList(BackTeacherExpansion backTeacherExpansion);

	BackTeacherExpansion findTeacherByTopShow(@Param(value="teachertype") String teacherType);

	List<BackTeacherExpansion> getQYTeacher(String id);
	List<BackTeacherExpansion> getXYTeacher(String id);

	List<ProjectExpVo> findProjectByTeacherId(String id);

	List<GContestUndergo> findGContestByTeacherId(String id);
}