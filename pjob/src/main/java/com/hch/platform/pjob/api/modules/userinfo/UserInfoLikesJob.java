package com.oseasy.initiate.jobs.userinfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oseasy.initiate.jobs.AbstractJobDetail;
import com.oseasy.initiate.modules.interactive.service.SysLikesService;
@Service("userInfoLikesJob") 
public class UserInfoLikesJob extends AbstractJobDetail{
	public static Logger logger = Logger.getLogger(UserInfoLikesJob.class);
	
	
	@Autowired
	private SysLikesService sysLikesService;
	
	@Override
	public void doWork() {
		try {
			sysLikesService.handleUserInfoLikes();
		} catch (Exception e) {
			logger.error("处理导师、学生详情点赞队列任务出错",e);
		}
	}
}
