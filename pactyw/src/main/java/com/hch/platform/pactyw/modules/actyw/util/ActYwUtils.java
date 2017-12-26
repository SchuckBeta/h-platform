package com.oseasy.initiate.modules.actyw.util;

import java.util.List;

import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;

public class ActYwUtils {
	private static ActYwService actYwService = SpringContextHolder.getBean(ActYwService.class);
	public static List<ActYw> getActListData(String ftype) {
	    return actYwService.findListByDeploy(ftype);
	}

	public static String getFlowName(String key) {
	  FlowType flowType = FlowType.getByKey(key);
	  if(flowType == null){
	    return "";
	  }
	  return flowType.getName();
	}

	public static String getFlowSname(String key) {
	  FlowType flowType = FlowType.getByKey(key);
	  if(flowType == null){
	    return "";
	  }
	  return flowType.getSname();
	}
}
