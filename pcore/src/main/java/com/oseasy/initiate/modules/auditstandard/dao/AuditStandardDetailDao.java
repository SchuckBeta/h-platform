package com.hch.platform.pcore.modules.auditstandard.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.auditstandard.entity.AuditStandardDetail;
import com.hch.platform.pcore.modules.project.vo.ProjectStandardDetailVo;
import org.apache.ibatis.annotations.Param;

/**
 * 评审标准详情DAO接口.
 * @author 9527
 * @version 2017-07-28
 */
@MyBatisDao
public interface AuditStandardDetailDao extends CrudDao<AuditStandardDetail> {
	public void delByFid(String fid);
	//根据节点找到标准细则
	public List<ProjectStandardDetailVo> findStandardDetailByNode(@Param("node")String node,@Param("flow")String flow);
	public List<AuditStandardDetail> findByFid(String fid);
}