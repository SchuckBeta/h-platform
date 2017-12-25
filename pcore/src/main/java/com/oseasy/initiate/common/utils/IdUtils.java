/**
 * Copyright (c) 2005-2012 springside.org.cn
 */
package com.oseasy.initiate.common.utils;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.web.context.ContextLoader;

import com.oseasy.initiate.modules.sys.dao.SysNumRuleDao;
import com.oseasy.initiate.modules.sys.entity.SysNumRule;
import com.oseasy.initiate.modules.sys.enums.NumRuleEnum;
import org.apache.log4j.Logger;
/**
 * 封装根据指定规则生成编号
 * eg：ZZ  20170309 111111  00012  AA
 */
public class IdUtils {
	protected static DataFieldMaxValueIncrementer dictNumGenarater=(DataFieldMaxValueIncrementer)ContextLoader.getCurrentWebApplicationContext().getBean("dictNumGenarater");
	protected static DataFieldMaxValueIncrementer projectDeclareIdGenarater=(DataFieldMaxValueIncrementer)ContextLoader.getCurrentWebApplicationContext().getBean("projectDeclareIdGenarater");
	protected static DataFieldMaxValueIncrementer gContestIdGenarater=(DataFieldMaxValueIncrementer)ContextLoader.getCurrentWebApplicationContext().getBean("gContestIdGenarater");
	protected static SysNumRuleDao sysNumRuleDao=(SysNumRuleDao)ContextLoader.getCurrentWebApplicationContext().getBean("sysNumRuleDao");
	public static Logger logger = Logger.getLogger(IdUtils.class);
	public static String genNumber(Rule rule) {
        StringBuffer res = new StringBuffer("");
        if (!"".equals(rule.getPrefix())) {
            res.append(rule.getPrefix());
        }
        if (!"".equals(rule.getD_format())) {
            res.append(DateUtil.getDate(rule.getD_format()));
        }

        //
        System.out.println("key:" + res.toString());
        Object obj = EhCacheUtils.get(res.toString());
        int sequence=rule.getStart();
        if (obj != null) {
            System.out.println("obj:" + obj);
             sequence = obj != null ? (Integer.valueOf(obj.toString()) + 1) : rule.getStart();
            System.out.println("sequence:" + sequence);
        }

        System.out.println("put key:" + res.toString());
        EhCacheUtils.remove(res.toString());
        EhCacheUtils.put(res.toString(), sequence); //缓存+1  //注意要清空前一天的缓存

        if (!"".equals(rule.getT_format())) {
            res.append(DateUtil.getDate(rule.getT_format()));
        }
        res = res.append(String.format("%0" + rule.getDigit() + "d", sequence));
        if (StringUtil.isNotBlank(rule.getSuffix())) {
            res.append(DateUtil.getDate(rule.getPrefix()));
        }
        res = res.append(rule.getSuffix());

        return res.toString();
    }
    public static String getProjectNumberByDb() {
    	StringBuffer res = new StringBuffer("");
    	SysNumRule sr=sysNumRuleDao.getByType(NumRuleEnum.PROJECT.getValue());
    	try {
			if (sr.getPrefix()!=null)res.append(sr.getPrefix());
			if (sr.getDateFormat()!=null)res.append(DateUtil.getDate(sr.getDateFormat()));
			if (sr.getTimieFormat()!=null)res.append(DateUtil.getDate(sr.getTimieFormat()));
			int numLength=Integer.parseInt(sr.getNumLength());//检验格式
			int next=projectDeclareIdGenarater.nextIntValue();
			if (String.valueOf(next).length()>numLength) {
				res.append(projectDeclareIdGenarater.nextIntValue());
			}else{
				res.append(String.format("%0"+sr.getNumLength()+"d", projectDeclareIdGenarater.nextIntValue()));
			}
			if (sr.getSuffix()!=null)res.append(sr.getSuffix());
		} catch (Exception e) {
			res = new StringBuffer("");
			res.append(DateUtil.getDate("yyyyMMddHHmmss")).append(String.format("%06d", projectDeclareIdGenarater.nextIntValue()));
			logger.error("无效的项目编号规则", e);
		}
    	return res.toString();
    }
    public static String getGContestNumberByDb() {
    	StringBuffer res = new StringBuffer("");
    	SysNumRule sr=sysNumRuleDao.getByType(NumRuleEnum.GCONTEST.getValue());
    	try {
			if (sr.getPrefix()!=null)res.append(sr.getPrefix());
			if (sr.getDateFormat()!=null)res.append(DateUtil.getDate(sr.getDateFormat()));
			if (sr.getTimieFormat()!=null)res.append(DateUtil.getDate(sr.getTimieFormat()));
			int numLength=Integer.parseInt(sr.getNumLength());//检验格式
			int next=gContestIdGenarater.nextIntValue();
			if (String.valueOf(next).length()>numLength) {
				res.append(gContestIdGenarater.nextIntValue());
			}else{
				res.append(String.format("%0"+sr.getNumLength()+"d", gContestIdGenarater.nextIntValue()));
			}
			if (sr.getSuffix()!=null)res.append(sr.getSuffix());
		} catch (Exception e) {
			res = new StringBuffer("");
			res.append(DateUtil.getDate("yyyyMMddHHmmss")).append(String.format("%06d", gContestIdGenarater.nextIntValue()));
			logger.error("无效的大赛编号规则", e);
		}
    	return res.toString();
    }
    public static String getDictNumberByDb() {
    	return String.format("%010d", dictNumGenarater.nextIntValue());
    }
}
