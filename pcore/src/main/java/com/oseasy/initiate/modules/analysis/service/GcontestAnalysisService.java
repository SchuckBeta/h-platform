package com.oseasy.initiate.modules.analysis.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.modules.analysis.dao.GcontestAnalysisDao;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(readOnly = true)
public class GcontestAnalysisService {
	@Autowired
	private GcontestAnalysisDao gcontestAnalysisDao;
	public JSONObject getData() {
		JSONObject data=new JSONObject();
		data.put("data1", getData1());
		data.put("data2", getData2());
		data.put("data3", getData3());
		return data;
	}
	private JSONObject getData1() {
		JSONObject data=new JSONObject();
		List<Map<String, Object>> list=gcontestAnalysisDao.getData1();
		if (list!=null&&list.size()>0) {
			Map<String, Integer> map1=new HashMap<String,Integer>();//大赛类型:索引值
			Map<String, Integer> map2=new HashMap<String,Integer>();//学院:索引值
			Map<String, Long> map3=new HashMap<String,Long>();//大赛类型-学院:数据值
			List<String> list1=new ArrayList<String>();//大赛类型集合，需有序
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
				JSONObject series=getData1Series();
				series.put("name", label);
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
	private JSONObject getData1Series() {
		JSONObject js=new JSONObject();
		js.put("type", "bar");
		js.put("barMaxWidth", 30);
		js.put("stack", "搜索引擎");
		JSONObject itemStyle=new JSONObject();
		JSONObject normal=new JSONObject();
		normal.put("color", "#5c6d78");
		itemStyle.put("normal",normal);
		js.put("itemStyle", itemStyle);
		JSONObject label=new JSONObject();
		JSONObject normal1=new JSONObject();
		normal1.put("show", true);
		normal1.put("position", "inside");
		label.put("normal",normal1);
		js.put("label", label);
		return js;
	}
	private JSONObject getData2() {
		JSONObject data=new JSONObject();
		List<Map<String, Object>> list=gcontestAnalysisDao.getData2();
		if (list!=null&&list.size()>0) {
			Map<String, Integer> map1=new HashMap<String,Integer>();//在校学生参赛:索引值
			Map<String, Integer> map2=new HashMap<String,Integer>();//组别:索引值
			Map<String, Long> map3=new HashMap<String,Long>();//在校学生参赛-组别:数据值
			List<String> list1=new ArrayList<String>();//在校学生参赛集合，需有序
			List<String> list2=new ArrayList<String>();//组别集合，需有序
			
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
	private JSONObject getData3() {
		JSONObject data=new JSONObject();
		List<Map<String, Object>> list=gcontestAnalysisDao.getData3();
		if (list!=null&&list.size()>0) {
			Map<String, Integer> map1=new HashMap<String,Integer>();//'参赛项目数':索引值
			Map<String, Integer> map2=new HashMap<String,Integer>();//互联网+参赛类别:索引值
			Map<String, Long> map3=new HashMap<String,Long>();//'参赛项目数'-互联网+参赛类别:数据值
			List<String> list1=new ArrayList<String>();//'参赛项目数'集合，需有序
			List<String> list2=new ArrayList<String>();//互联网+参赛类别集合，需有序
			
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
				JSONObject series=getData3Series();
				series.put("name", label);
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
	private JSONObject getData3Series() {
		JSONObject js=new JSONObject();
		js.put("type", "bar");
		js.put("barMaxWidth",30);
		JSONObject label=new JSONObject();
		JSONObject normal1=new JSONObject();
		normal1.put("show", true);
		normal1.put("position", "inside");
		JSONObject textStyle=new JSONObject();
		textStyle.put("color", "#000");
		normal1.put("textStyle", textStyle);
		label.put("normal",normal1);
		js.put("label", label);
		return js;
	}
}
