/**
 *
 */
package com.hch.platform.pcore.modules.sys.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hch.platform.pcore.common.mapper.JsonMapper;
import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.pcore.modules.sco.service.ScoAffirmConfService;
import com.hch.platform.pcore.modules.sys.dao.DictDao;
import com.hch.platform.pcore.modules.sys.entity.Dict;

/**
 * 字典工具类


 */
public class DictUtils {
	private static ScoAffirmConfService scoAffirmConfService = SpringContextHolder.getBean(ScoAffirmConfService.class);
	private static DictDao dictDao = SpringContextHolder.getBean(DictDao.class);
	
	public static final String CACHE_DICT_MAP = "dictMap";
	public static List<Dict> getPublishDictList(){
		List<Dict> l=new ArrayList<Dict>();
		Set<String> pset=scoAffirmConfService.getTypeSetData("1,");
		Set<String> gset=scoAffirmConfService.getTypeSetData("7,");
		if(pset!=null&&pset.size()>0){
			List<Dict> l2=scoAffirmConfService.getDictForScoAffirm(pset,"project_style");
			for(Dict d:l2){
				d.setValue("1-"+d.getValue());
				l.add(d);
			}
		}
		if(gset!=null&&gset.size()>0){
			List<Dict> l2=scoAffirmConfService.getDictForScoAffirm(gset,"competition_type");
			for(Dict d:l2){
				d.setValue("7-"+d.getValue());
				l.add(d);
			}
		}
		return l;
	}
	public static String getDictLabelByBoolean(Boolean value, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && (value == Boolean.parseBoolean(dict.getValue()))) {
					return dict.getLabel();
				}
			}
		}
		return defaultValue;
	}

	public static String getDictLabel(String value, String type, String defaultValue) {
	  if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
	    for (Dict dict : getDictList(type)) {
	      if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
	        return dict.getLabel();
	      }
	    }
	  }
	  return defaultValue;
	}

	public static String getDictLabels(String values, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)) {
			List<String> valueList = Lists.newArrayList();
			for (String value : StringUtils.split(values, ",")) {
				valueList.add(getDictLabel(value, type, defaultValue));
			}
			return StringUtils.join(valueList, ",");
		}
		return defaultValue;
	}

	public static String getDictValue(String label, String type, String defaultLabel) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && label.equals(dict.getLabel())) {
					return dict.getValue();
				}
			}
		}
		return defaultLabel;
	}
	public static Dict getDictByLabel(String type,String label) {
		List<Dict> list=getDictList(type);
		if (list!=null&&!list.isEmpty()) {
			for(Dict d:list) {
				if (d.getLabel().equals(label)) {
					return d;
				}
			}
		}else{
			return null;
		}
		return null;
	}
	public static List<Dict> getDictList(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>)CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap==null||dictMap.size()==0) {
			dictMap = Maps.newHashMap();
			for (Dict dict : dictDao.findAllList(new Dict())) {
				List<Dict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				}else{
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		List<Dict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	/**
	 * 返回字典列表（JSON）
	 * @param type
	 * @return
	 */
	public static String getDictListJson(String type) {
		return JsonMapper.toJsonString(getDictList(type));
	}

}
