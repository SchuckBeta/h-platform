package com.oseasy.initiate.modules.auditstandard.util;

import java.util.List;

import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.modules.auditstandard.entity.AuditStandard;
import com.oseasy.initiate.modules.auditstandard.service.AuditStandardService;

public class AuditStandardUtils {
	private static AuditStandardService auditStandardService = SpringContextHolder.getBean(AuditStandardService.class);
	public static List<AuditStandard> getAuditStandardList() {
	    return auditStandardService.findList(new AuditStandard());
	}
}
