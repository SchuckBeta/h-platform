package com.oseasy.initiate.modules.analysis.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.modules.analysis.dao.ProjectAnalysisDao;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(readOnly = true)
public class ProjectAnalysisService {
	@Autowired
	private ProjectAnalysisDao projectAnalysisDao;
	public JSONObject getData() {
		JSONObject data=new JSONObject();
		data.put("data1", getData1());
		data.put("data2", getData2());
		data.put("data3", getData3());
		return data;
	}
	private JSONObject getData1() {
		JSONObject data=new JSONObject();
		List<Map<String, Object>> list1=projectAnalysisDao.getData1();
		if (list1!=null&&list1.size()>0) {
			JSONArray ja1=new JSONArray();
			JSONArray ja2=new JSONArray();
			for(Map<String, Object> map:list1) {
				String label=map.get("label").toString();
				long cc=((Long)map.get("cc")).longValue();
				
				ja1.add(label+"("+cc+")");
				
				JSONObject jo=new JSONObject();
				jo.put("value", cc);
				jo.put("name", label+"("+cc+")");
				ja2.add(jo);
			}
			data.put("ja1", ja1);
			data.put("ja2", ja2);
		}
		return data;
	}
	private JSONObject getData2() {
		JSONObject data=new JSONObject();
		List<Map<String, Object>> list1=projectAnalysisDao.getData2();
		if (list1!=null&&list1.size()>0) {
			JSONArray ja1=new JSONArray();
			JSONArray ja2=new JSONArray();
			for(Map<String, Object> map:list1) {
				String label=map.get("label").toString();
				long cc=((Long)map.get("cc")).longValue();
				
				ja1.add(label+"("+cc+")");
				
				JSONObject jo=new JSONObject();
				jo.put("value", cc);
				jo.put("name", label+"("+cc+")");
				ja2.add(jo);
			}
			data.put("ja1", ja1);
			data.put("ja2", ja2);
		}
		return data;
	}
	private JSONObject getData3() {
		JSONObject data=new JSONObject();
		List<Map<String, Object>> list=projectAnalysisDao.getData3();
		if (list!=null&&list.size()>0) {
			Map<String, Integer> map1=new HashMap<String,Integer>();//项目级别:索引值
			Map<String, Integer> map2=new HashMap<String,Integer>();//学院:索引值
			Map<String, Long> map3=new HashMap<String,Long>();//项目级别-学院:数据值
			List<String> list1=new ArrayList<String>();//项目级别集合，需有序
			List<String> list2=new ArrayList<String>();//学院集合，需有序
			
			for(Map<String, Object> map:list) {
				String label=map.get("label").toString();
				String name=map.get("name").toString();
				long cc=((Long)map.get("cc")).longValue();
				if (map1.get(label)==null) {
					list1.add(label);
					map1.put(label, list1.size()-1);
				}
				if (map2.get(name)==null) {
					list2.add(name);
					map2.put(name, list2.size()-1);
				}
				if (map3.get(label+"-"+name)==null) {
					map3.put(label+"-"+name, cc);
				}
			}
			
			JSONArray ja1=JSONArray.fromObject(list1);
			JSONArray ja2=JSONArray.fromObject(list2);
			JSONArray ja3=new JSONArray();
			for(String label:list1) {
				JSONObject series=new JSONObject();
				series.put("name", label);
				series.put("type", "bar");
				series.put("barMaxWidth", 30);
				long[] ls=new long[list2.size()];
				for(String name:list2) {
					if (map3.get(label+"-"+name)!=null) {
						ls[map2.get(name)]=map3.get(label+"-"+name);
					}
				}
				series.put("data", JSONArray.fromObject(ls));
				ja3.add(series);
			}
			data.put("ja1", ja1);
			data.put("ja2", ja2);
			data.put("ja3", ja3);
		}
		return data;
	}
}
