package com.oseasy.initiate.jobs.excellent;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oseasy.initiate.jobs.AbstractJobDetail;
import com.oseasy.initiate.modules.interactive.service.SysLikesService;
@Service("courseLikesJob") 
public class CourseLikesJob extends AbstractJobDetail{
	public static Logger logger = Logger.getLogger(CourseLikesJob.class);
	
	@Autowired
	private SysLikesService sysLikesService;
	
	@Override
	public void doWork() {
//		logger.info("处理名师讲堂评论队列任务开始");
		int count=0;
		try {
			 count=sysLikesService.handleCourseLikes();
		} catch (Exception e) {
			logger.error("处理名师讲堂评论队列任务出错",e);
		}
//		logger.info("处理名师讲堂评论队列任务结束,处理了"+count+"条");
	}
}
