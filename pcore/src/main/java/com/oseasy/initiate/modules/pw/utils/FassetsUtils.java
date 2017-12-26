package com.hch.platform.pcore.modules.pw.utils;

import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.pw.dao.PwFassetsnoRuleDao;
import com.hch.platform.pcore.modules.pw.entity.PwCategory;
import com.hch.platform.pcore.modules.pw.entity.PwFassetsnoRule;
import com.hch.platform.pcore.modules.pw.service.PwCategoryService;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FassetsUtils {

    private static PwCategoryService pwCategoryService = SpringContextHolder.getBean(PwCategoryService.class);

    private static PwFassetsnoRuleDao pwFassetsnoRuleDao = SpringContextHolder.getBean(PwFassetsnoRuleDao.class);

    /**
     * 生成一个资产编号
     * 查询表中当前最大的序号，以此为基地生成。
     * 在此过程中，其他线程如果修改了记录，则返回“”，调用方再次调用该方法即可，直到正确返回一个不为“”的编号
     * @param pwCategory
     * @return
     */
    public static String genFasNo(PwCategory pwCategory) {
        PwCategory newCategory = pwCategoryService.get(pwCategory.getId());
        PwFassetsnoRule rule = newCategory.getPwFassetsnoRule();
        if (rule == null || StringUtils.isBlank(rule.getId()) || StringUtils.isBlank(rule.getPrefix())) {
            throw new RuntimeException("未设置该类型固定资产编号规则");
        }
        PwFassetsnoRule pRule = newCategory.getParent().getPwFassetsnoRule();
        if (pRule == null || StringUtils.isBlank(pRule.getId()) || StringUtils.isBlank(pRule.getPrefix())) {
            throw new RuntimeException("未设置父类型固定资产编号规则");
        }
        StringBuffer sb = new StringBuffer(pRule.getPrefix() + rule.getPrefix());
        if (StringUtils.isNotBlank(rule.getFormat())) {
            SimpleDateFormat sdf = new SimpleDateFormat(rule.getFormat());
            sb.append(sdf.format(new Date()));
        }
        int startNumber = Integer.valueOf(rule.getStartNumber());
        int maxValue = rule.getMaxValue() + 1;
        if(maxValue < startNumber){
            maxValue = startNumber;
        }
        sb.append(StringUtil.autoGenZero(Integer.valueOf(rule.getNumberLen()), new Long(maxValue)));

        rule.setMaxValue(maxValue);

        if(pwFassetsnoRuleDao.update(rule) > 0){
            return sb.toString();
        }
        return "";
    }
}
