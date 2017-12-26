package com.hch.platform.pcore.modules.auditstandard.util;

import java.util.List;

import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.pcore.modules.auditstandard.entity.AuditStandard;
import com.hch.platform.pcore.modules.auditstandard.service.AuditStandardService;

public class AuditStandardUtils {
	private static AuditStandardService auditStandardService = SpringContextHolder.getBean(AuditStandardService.class);
	public static List<AuditStandard> getAuditStandardList() {
	    return auditStandardService.findList(new AuditStandard());
	}
}
