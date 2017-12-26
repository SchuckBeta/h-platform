package com.hch.platform.pcore.modules.sys.utils;

import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.actyw.tool.process.vo.FormType;
import com.hch.platform.pcore.modules.project.dao.ProjectDeclareDao;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.proproject.dao.ProProjectDao;
import com.hch.platform.pcore.modules.proproject.entity.ProProject;

/**
 * Created by zhangzheng on 2017/3/18.
 */
public class EnumUtils {
    public static String getFormLabel(String value) {
        return FormType.getNameByValue(value);
    }


}
