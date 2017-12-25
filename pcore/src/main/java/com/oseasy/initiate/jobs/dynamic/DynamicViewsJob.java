package com.oseasy.initiate.jobs.dynamic;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oseasy.initiate.jobs.AbstractJobDetail;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
@Service("dynamicViewsJob") 
public class DynamicViewsJob extends AbstractJobDetail{
	public static Logger logger = Logger.getLogger(DynamicViewsJob.class);
	
	@Autowired
	private OaNotifyService oaNotifyService;
	
	@Override
	public void doWork() {
		try {
			oaNotifyService.handleViews();
		} catch (Exception e) {
			logger.error("处理双创动态浏览队列任务出错",e);
		}
	}
}
