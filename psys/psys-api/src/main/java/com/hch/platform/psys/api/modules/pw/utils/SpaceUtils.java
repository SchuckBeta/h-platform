package com.hch.platform.pcore.modules.pw.utils;

import com.hch.platform.pcore.common.config.SysIds;
import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.pcore.modules.pw.entity.PwCategory;
import com.hch.platform.pcore.modules.pw.service.PwCategoryService;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.enums.EuserType;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SpaceUtils {

    private static PwCategoryService pwCategoryService = SpringContextHolder.getBean(PwCategoryService.class);

    private static UserService userService = SpringContextHolder.getBean(UserService.class);

    public static List<PwCategory> findChildrenCategorys(String parentId) {
        PwCategory pwCategory = new PwCategory();
        PwCategory p = new PwCategory();
        p.setId(StringUtils.isNotBlank(parentId) ? parentId : SysIds.SYS_TREE_ROOT.getId());
        pwCategory.setParent(p);
        return pwCategoryService.findList(pwCategory);
    }

    public static boolean isUserInfoComplete(){
        AbsUser user = UserUtils.getUser();
        if (user != null) {
            if(StringUtils.isBlank(user.getIdNumber())){
                return false;
            }
            if(user.getOffice() == null || StringUtils.isBlank(user.getOffice().getId())){
                return false;
            }
            if(StringUtils.isBlank(user.getMobile())){
                return false;
            }
            if(StringUtils.isBlank(user.getName())){
                return false;
            }
            if(StringUtils.isBlank(user.getLoginName())){
                return false;
            }
            if(StringUtils.isBlank(user.getIdType())){
                return false;
            }
        }
        return true;
    }



}
