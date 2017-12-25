package com.oseasy.initiate.jobs.excellent;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oseasy.initiate.jobs.AbstractJobDetail;
import com.oseasy.initiate.modules.interactive.service.SysViewsService;
@Service("excellentViewsJob") 
public class ExcellentViewsJob extends AbstractJobDetail{
	public static Logger logger = Logger.getLogger(ExcellentViewsJob.class);
	
	
	@Autowired
	private SysViewsService sysViewsService;
	
	@Override
	public void doWork() {
//		logger.info("处理优秀展示浏览队列任务开始");
		int count=0;
		try {
			 count=sysViewsService.handleExcellentViews();
		} catch (Exception e) {
			logger.error("处理优秀展示浏览队列任务出错",e);
		}
//		logger.info("处理优秀展示浏览队列任务结束,处理了"+count+"条");
	}
}
