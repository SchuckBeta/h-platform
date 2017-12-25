package com.oseasy.initiate.modules.actyw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl.RtLevelVal;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.actyw.dao.ActYwNodeDao;

/**
 * 项目流程节点Service.
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwNodeService extends CrudService<ActYwNodeDao, ActYwNode> {

	public ActYwNode get(String id) {
		return super.get(id);
	}

	public List<ActYwNode> findList(ActYwNode actYwNode) {
		return super.findList(actYwNode);
	}

	public Page<ActYwNode> findPage(Page<ActYwNode> page, ActYwNode actYwNode) {
		return super.findPage(page, actYwNode);
	}

	@Transactional(readOnly = false)
	public void save(ActYwNode actYwNode) {
	  actYwNode.setOffice(UserUtils.getAdminOffice());
    actYwNode.setIsSys((UserUtils.getUser().getAdmin()) ? "1": "0");
    actYwNode.setIsForm(((actYwNode.getIsForm() != null) && actYwNode.getIsForm()) ? true:false);
    if((actYwNode.getLevel()).equals(RtLevelVal.RT_LV1)){
      actYwNode.setNodeType(StenType.ST_JG_SUB_PROCESS.getType().getKey());
      actYwNode.setNodeKey(StenType.ST_JG_SUB_PROCESS.getKey());
    }else if((actYwNode.getLevel()).equals(RtLevelVal.RT_LV2)){
      actYwNode.setNodeType(StenType.ST_TASK_USER.getType().getKey());
      actYwNode.setNodeKey(StenType.ST_TASK_USER.getKey());
    }
		super.save(actYwNode);
	}

	@Transactional(readOnly = false)
	public void delete(ActYwNode actYwNode) {
		super.delete(actYwNode);
	}

}