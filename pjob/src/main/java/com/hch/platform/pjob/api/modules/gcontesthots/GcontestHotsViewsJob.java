package com.oseasy.initiate.jobs.gcontesthots;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oseasy.initiate.jobs.AbstractJobDetail;
import com.oseasy.initiate.modules.gcontesthots.service.GcontestHotsService;
@Service("gcontestHotsViewsJob") 
public class GcontestHotsViewsJob extends AbstractJobDetail{
	public static Logger logger = Logger.getLogger(GcontestHotsViewsJob.class);
	
	@Autowired
	private GcontestHotsService gcontestHotsService;
	
	@Override
	public void doWork() {
		try {
			gcontestHotsService.handleViews();
		} catch (Exception e) {
			logger.error("处理大赛热点浏览队列任务出错",e);
		}
	}
}
