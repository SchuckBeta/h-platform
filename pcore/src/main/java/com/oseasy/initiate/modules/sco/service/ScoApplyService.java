package com.oseasy.initiate.modules.sco.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.ftp.VsftpUtils;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.dao.ActDao;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwTool;
import com.oseasy.initiate.modules.attachment.dao.SysAttachmentDao;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.sco.dao.ScoApplyDao;
import com.oseasy.initiate.modules.sco.entity.ScoAffirmConf;
import com.oseasy.initiate.modules.sco.entity.ScoApply;
import com.oseasy.initiate.modules.sco.entity.ScoAuditing;
import com.oseasy.initiate.modules.sco.entity.ScoScore;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 学分课程申请Service.
 * @author zhangzheng
 * @version 2017-07-13
 */
@Service
@Transactional(readOnly = true)
public class ScoApplyService extends CrudService<ScoApplyDao, ScoApply> {

	@Autowired
	SysAttachmentDao sysAttachmentDao;
	@Autowired
	private ScoAuditingService scoAuditingService;
	@Autowired
	private ScoScoreService scoScoreService;
	@Autowired
	private ScoAffirmConfService scoAffirmConfService;
	@Autowired
	ActTaskService actTaskService;
	@Autowired
	ActYwService actYwService;
	@Autowired
	UserService userService;
	@Autowired
	SystemService systemService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	IdentityService identityService;
	@Autowired
	TaskService taskService;
	@Autowired
	ActDao actDao;

	public ScoApply get(String id) {
		return super.get(id);
	}

	public List<ScoApply> findList(ScoApply scoApply) {
		return super.findList(scoApply);
	}

	public Page<ScoApply> findPage(Page<ScoApply> page, ScoApply scoApply) {
		return super.findPage(page, scoApply);
	}
	//添加学分认定
	@Transactional(readOnly = false)
	public void add(ScoApply scoApply){
		super.save(scoApply);
	}

	@Transactional(readOnly = false)
	public void save(ScoApply scoApply) {
		if(StringUtil.isNotBlank(scoApply.getId())){//如果是新增 则需要删除原来的附件表的数据
			sysAttachmentDao.deleteByUid(scoApply.getId());
		}
		super.save(scoApply);
		//附件处理  1、把tmp目录的文件移到正式目录  2、改变url，保存到数据库
		List<SysAttachment> attachmentList = scoApply.getAttachmentList();
		for (SysAttachment attachment:attachmentList){
			String tmpPath = attachment.getUrl();
			String realPath=tmpPath.replace("/temp", "");
			try{
				VsftpUtils.moveFile(tmpPath);
			}catch (IOException e){
				logger.info("移动临时文件异常");
			}
			//保存数据库
			attachment.setUrl(realPath);
			attachment.setUid(scoApply.getId());
			attachment.setType(FileTypeEnum.S5);
			attachment.preInsert();
			sysAttachmentDao.insert(attachment);
		}
		//走工作流  学分类型的0000000127

		//User user = UserUtils.getUser();
		//ActYw actYw=actYwService.get(scoApply.getActYwId());
		delRun(scoApply);
		startRun(scoApply);
	}

	@Transactional(readOnly = false)
	public void delRun(ScoApply scoApply) {

	}

	//开启新审核流程
	@Transactional(readOnly = false)
	public void startRun(ScoApply scoApply) {
		ScoAffirmConf scoAffirmConf =scoAffirmConfService.getByItem("0000000127");
		// 根据学分配置得到审核节点
		ActYw actYw=actYwService.get(scoAffirmConf.getProcId());
		//启动工作流
		if(actYw!=null){
			//得到流程下一步审核角色id
			String roleId=actTaskService.getProcessStartRoleName(ActYw.getPkey(actYw.getGroup(),actYw.getProProject()));  //从工作流中查询 下一步的角色集合
			//actTaskService.getNextRoleName()
			Role role= systemService.getNamebyId(roleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length()));
			//启动节点
			String roleName=role.getName();
			List<String> roles=new ArrayList<String>();
			if(roleName.contains(SysIds.ISCOLLEGE.getRemark())||roleName.contains(SysIds.ISMS.getRemark())){
				roles=userService.getRolesByName(role.getEnname(),scoApply.getUserId());
			}else{
				roles=userService.getRolesByName(role.getEnname());
			}
			//根据角色id获得审核人
			//List<String> roles=userService.findListByRoleId(roleId);
			if (roles.size()>0) {
				//super.save(proModel);
				Map<String,Object> vars=new HashMap<String,Object>();
				vars=scoApply.getVars();
				vars.put(roleId+"s",roles);
				//vars.put(scoAffirmConf.getProcId(),roles);
				String key=ActYw.getPkey(actYw.getGroup(),actYw.getProProject());
				String userId = UserUtils.getUser().getLoginName();
				identityService.setAuthenticatedUserId(userId);
				//启动流程节点
				ProcessInstance procIns=runtimeService.startProcessInstanceByKey(key, "score"+":"+scoApply.getId(),vars);
				//流程id返写业务表
				Act act = new Act();
				act.setBusinessTable("sco_apply");// 业务表名
				act.setBusinessId(scoApply.getId());	// 业务表ID
				act.setProcInsId(procIns.getId());
				actDao.updateProcInsIdByBusinessId(act);
				scoApply.setProcInsId(act.getProcInsId());
				super.save(scoApply);
			}
		}
	}


	@Transactional(readOnly = false)
	public void delete(ScoApply scoApply) {
		super.delete(scoApply);
	}

	@Transactional(readOnly = false)
	public void saveAudit(ScoApply scoApply,String suggest) {
		ScoAuditing scoAuditing=new ScoAuditing();
		scoAuditing.setApplyId(scoApply.getId());
		scoAuditing.setType("1");
		scoAuditing.setScoreVal(String.valueOf(scoApply.getScore()));
		scoAuditing.setUser(UserUtils.getUser());
		scoAuditing.setProcInsId(scoApply.getProcInsId());
		if(StringUtil.isNotEmpty(suggest)){
			scoAuditing.setSuggest(suggest);
		}
		scoAuditingService.save(scoAuditing);
		super.save(scoApply);
		Map<String,Object> vars=new HashMap<String,Object>();
		vars=scoApply.getVars();
		String taskId=actTaskService.getTaskidByProcInsId(scoApply.getProcInsId());
		taskService.complete(taskId, vars);
		if(scoApply.getAuditStatus().equals("4")){
			saveScore(scoApply);
		}else{
			scoApply.setScore(0f);
			super.save(scoApply);
		}
	}

	@Transactional(readOnly = false)
	private void saveScore(ScoApply scoApply){
		ScoScore scoScore =new ScoScore ();
		scoScore.setCourseId(scoApply.getCourseId());
		String userId=scoApply.getUserId();
		scoScore.setUser(userService.findUserById(userId));
		float score=scoApply.getScore();
		if(score>0&score<0.5){
			score = 0.5f;
		}
		scoScore.setCourseScore((double)score);
		scoScoreService.save(scoScore);
	}
}