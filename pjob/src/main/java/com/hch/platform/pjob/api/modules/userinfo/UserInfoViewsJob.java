package com.oseasy.initiate.jobs.userinfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oseasy.initiate.jobs.AbstractJobDetail;
import com.oseasy.initiate.modules.interactive.service.SysViewsService;
@Service("userInfoViewsJob") 
public class UserInfoViewsJob extends AbstractJobDetail{
	public static Logger logger = Logger.getLogger(UserInfoViewsJob.class);
	
	
	@Autowired
	private SysViewsService sysViewsService;
	
	@Override
	public void doWork() {
		try {
			 sysViewsService.handleUserInfoViews();
		} catch (Exception e) {
			logger.error("处理导师、学生详情浏览队列任务出错",e);
		}
	}
}
