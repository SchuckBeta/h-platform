package com.hch.platform.pcore.modules.auditstandard.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.auditstandard.entity.AuditStandardFlow;
import com.hch.platform.pcore.modules.auditstandard.vo.AsdYwGnode;
import com.hch.platform.pcore.modules.auditstandard.vo.AuditStandardVo;

/**
 * 评审标准、流程关系表DAO接口.
 * @author 9527
 * @version 2017-07-28
 */
@MyBatisDao
public interface AuditStandardFlowDao extends CrudDao<AuditStandardFlow> {
	public void delByCdn(@Param(value="pid") String pid,@Param(value="flow") String flow,@Param(value="node") String node);
	public void delByPid(@Param(value="pid") String pid);
	public AuditStandardFlow getByDef(@Param(value="defName")String defName);
	public int findByCdn(@Param(value="flow") String flow,@Param(value="node") String node);
	public List<AsdYwGnode> getGnodeListByYwid(String id);
	public void saveChild(AuditStandardVo vo);
}