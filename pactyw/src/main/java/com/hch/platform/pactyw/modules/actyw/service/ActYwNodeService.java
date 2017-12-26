package com.oseasy.initiate.modules.actyw.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pconfig.common.Global;
import com.oseasy.initiate.common.ftp.VsftpUtils;
import com.hch.platform.pcore.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.dao.ActYwNodeDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl.RtLevelVal;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl.RtRequireVal;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl.RtTypeVal;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

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

	/**
	 * 查找流程节点，不包含type=0流程非业务节点
	 * @param actYwNode
	 * @return
	 */
	public List<ActYwNode> findListByTypeNoZero(ActYwNode actYwNode) {
	  return dao.findListByTypeNoZero(actYwNode);
	}

	public Page<ActYwNode> findPage(Page<ActYwNode> page, ActYwNode actYwNode) {
		return super.findPage(page, actYwNode);
	}

	@Transactional(readOnly = false)
	public void save(ActYwNode actYwNode) {
	  if(actYwNode != null){
	    if(actYwNode.getIsNewRecord()){
	      actYwNode.setOffice(UserUtils.getAdminOffice());
	    }

      if(StringUtil.isNotEmpty(actYwNode.getLevel())){
        if((actYwNode.getLevel()).equals(RtLevelVal.RT_LV1)){
          if(actYwNode.getType() == RtTypeVal.RT_T0){
            actYwNode.setIsRequire(((actYwNode.getIsRequire() != null)) ? actYwNode.getIsRequire(): RtRequireVal.RT_T1);
          }else{
            actYwNode.setNodeType(StenType.ST_JG_SUB_PROCESS.getType().getKey());
            actYwNode.setNodeKey(StenType.ST_JG_SUB_PROCESS.getKey());
            actYwNode.setIsRequire(((actYwNode.getIsRequire() != null)) ? actYwNode.getIsRequire(): RtRequireVal.RT_T4);
          }
          actYwNode.setIsFlow(true);
        }else if((actYwNode.getLevel()).equals(RtLevelVal.RT_LV2)){
          actYwNode.setNodeType(StenType.ST_TASK_USER.getType().getKey());
          actYwNode.setNodeKey(StenType.ST_TASK_USER.getKey());
          actYwNode.setIsFlow(true);
          actYwNode.setIsRequire(((actYwNode.getIsRequire() != null)) ? actYwNode.getIsRequire(): RtRequireVal.RT_T1);
        }else{
          actYwNode.setIsFlow(true);
          actYwNode.setIsRequire(((actYwNode.getIsRequire() != null)) ? actYwNode.getIsRequire(): RtRequireVal.RT_T3);
        }
      }

      actYwNode.setIsSys((UserUtils.getUser().getAdmin() || UserUtils.getUser().getSysAdmin()) ? Global.YES:  Global.NO);
      actYwNode.setIsShow(((actYwNode.getIsShow() != null) && actYwNode.getIsShow()) ? true : false);
      actYwNode.setIsForm(((actYwNode.getIsForm() != null) && actYwNode.getIsForm()) ? true : false);

	  String tmpPath = actYwNode.getIconUrl();
		  //图标处理先把 图片从临时目录移到正式目录，改变url
	  if(StringUtil.contains(tmpPath,"/temp")){
		  String realPath=tmpPath.replace("/temp", "");
		  try{
			  VsftpUtils.moveFile(tmpPath);
		  }catch (IOException e){
			  logger.info("移动临时文件异常");
		  }
		  actYwNode.setIconUrl(realPath);
	  }
	    super.save(actYwNode);
	  }
	}

	@Transactional(readOnly = false)
	public void delete(ActYwNode actYwNode) {
		super.delete(actYwNode);
	}

}