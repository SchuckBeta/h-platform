package com.hch.platform.pcore.modules.sysconfig.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.sysconfig.dao.SysConfigDao;
import com.hch.platform.pcore.modules.sysconfig.entity.SysConfig;
import com.hch.platform.pcore.modules.sysconfig.vo.GconSubTypeConf;
import com.hch.platform.pcore.modules.sysconfig.vo.LowTypeConf;
import com.hch.platform.pcore.modules.sysconfig.vo.PersonNumConf;
import com.hch.platform.pcore.modules.sysconfig.vo.ProSubTypeConf;
import com.hch.platform.pcore.modules.sysconfig.vo.SysConfigVo;

import net.sf.json.JSONObject;

public class SysConfigUtil {
	public static Logger logger = Logger.getLogger(SysConfigUtil.class);
	private static SysConfigDao sysConfigDao = SpringContextHolder.getBean(SysConfigDao.class);
	public static final String SYS_CONFIG_VO = "SysConfig";
	@SuppressWarnings("rawtypes")
	public static SysConfigVo getSysConfigVo(){
		String scv=(String)CacheUtils.get(SYS_CONFIG_VO);
		Map<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("proSubTypeConf", ProSubTypeConf.class);
		classMap.put("lowTypeConf", LowTypeConf.class);
		classMap.put("gconSubTypeConf", GconSubTypeConf.class);
		if(scv==null){
			SysConfig sc=sysConfigDao.getSysConfig();
			if(sc==null||StringUtil.isEmpty(sc.getContent())){
				return null;
			}else{
				try {
					CacheUtils.put(SYS_CONFIG_VO,sc.getContent());
					return (SysConfigVo)JSONObject.toBean(JSONObject.fromObject(sc.getContent()), SysConfigVo.class,classMap);
				} catch (Exception e) {
					logger.error(e.getMessage());
					return null;
				}
			}
		}else{
			return (SysConfigVo)JSONObject.toBean(JSONObject.fromObject(scv), SysConfigVo.class,classMap);
		}
	}
	public static void removeCache(){
		CacheUtils.remove(SYS_CONFIG_VO);
	}
	/**
	 * @param scv SysConfigVo 对象
	 * @param subType 项目类型
	 * @param lowType 项目类别
	 * @return
	 */
	public static PersonNumConf getProPersonNumConf(SysConfigVo scv,String subType,String lowType){
		try {
			for(ProSubTypeConf psc:scv.getApplyConf().getProConf().getProSubTypeConf()){
				if(subType.equals(psc.getSubType())){
					for(LowTypeConf ltc:psc.getLowTypeConf()){
						if(lowType.equals(ltc.getLowType())){
							return ltc.getPersonNumConf();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return null;
	}
	/**
	 * @param scv SysConfigVo 对象
	 * @param subType 大赛类型
	 * @return
	 */
	public static PersonNumConf getGconPersonNumConf(SysConfigVo scv,String subType){
		try {
			for(GconSubTypeConf gsc:scv.getApplyConf().getGconConf().getGconSubTypeConf()){
				if(subType.equals(gsc.getSubType())){
					return gsc.getPersonNumConf();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return null;
	}
}
