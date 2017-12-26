package com.hch.platform.pcore.modules.cms.utils;

import java.util.List;

import com.hch.platform.pcore.common.utils.FtpUtil;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class FreemarkerGetFtpUrl implements TemplateMethodModelEx {

	@Override
	public Object exec(List args) throws TemplateModelException {
		String permissionsStr = args.get(0).toString();
		return FtpUtil.ftpImgUrl(permissionsStr);
	}
}
