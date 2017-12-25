package com.oseasy.initiate.modules.sys.utils;

import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FormType;
import com.oseasy.initiate.modules.project.dao.ProjectDeclareDao;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.proproject.dao.ProProjectDao;
import com.oseasy.initiate.modules.proproject.entity.ProProject;

/**
 * Created by zhangzheng on 2017/3/18.
 */
public class EnumUtils {
    public static String getFormLabel(String value) {
        return FormType.getNameByValue(value);
    }


}
