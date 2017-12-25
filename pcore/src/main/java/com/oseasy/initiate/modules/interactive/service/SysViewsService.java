package com.oseasy.initiate.modules.interactive.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.modules.interactive.entity.SysViews;
import com.oseasy.initiate.modules.course.dao.CourseDao;
import com.oseasy.initiate.modules.excellent.dao.ExcellentShowDao;
import com.oseasy.initiate.modules.interactive.dao.SysViewsDao;

/**
 * 浏览表Service.
 * @author 9527
 * @version 2017-06-30
 */
@Service
@Transactional(readOnly = true)
public class SysViewsService extends CrudService<SysViewsDao, SysViews> {
	@Autowired
	private ExcellentShowDao excellentShowDao;
	@Autowired
	private CourseDao courseDao;
	/**
	 * 名师讲堂浏览量队列的处理
	 * @return 处理的数据条数
	 */
	@Transactional(readOnly = false)
	public int handleCourseViews(){
		Map<String,Integer> map=new HashMap<String,Integer>();//需要更新浏览量数量的map
		int tatol=10000;
		int count=0;
		Integer up=null;
		SysViews sv=(SysViews)CacheUtils.rpop(CacheUtils.COURSE_VIEWS_QUEUE);
		while(count<tatol&&sv!=null){
			count++;//增加了一条数据
			up=map.get(sv.getForeignId());
			if(up==null){
				map.put(sv.getForeignId(), 1);	
			}else{
				map.put(sv.getForeignId(), up+1);
			}
			if(count<tatol){
				sv=(SysViews)CacheUtils.rpop(CacheUtils.COURSE_VIEWS_QUEUE);
			}
		}
		if(count>0){//有数据需要处理
			courseDao.updateViewsPlus(map);
		}
		return count;
	}
	/**
	 * 优秀展示浏览量队列的处理
	 * @return 处理的数据条数
	 */
	@Transactional(readOnly = false)
	public int handleExcellentViews(){
		Map<String,Integer> map=new HashMap<String,Integer>();//需要更新浏览量数量的map
		int tatol=10000;
		int count=0;
		Integer up=null;
		SysViews sv=(SysViews)CacheUtils.rpop(CacheUtils.EXCELLENT_VIEWS_QUEUE);
		while(count<tatol&&sv!=null){
			count++;//增加了一条数据
			up=map.get(sv.getForeignId());
			if(up==null){
				map.put(sv.getForeignId(), 1);	
			}else{
				map.put(sv.getForeignId(), up+1);
			}
			if(count<tatol){
				sv=(SysViews)CacheUtils.rpop(CacheUtils.EXCELLENT_VIEWS_QUEUE);
			}
		}
		if(count>0){//有数据需要处理
			excellentShowDao.updateViews(map);
		}
		return count;
	}
	public SysViews get(String id) {
		return super.get(id);
	}

	public List<SysViews> findList(SysViews sysViews) {
		return super.findList(sysViews);
	}

	public Page<SysViews> findPage(Page<SysViews> page, SysViews sysViews) {
		return super.findPage(page, sysViews);
	}

	@Transactional(readOnly = false)
	public void save(SysViews sysViews) {
		super.save(sysViews);
	}

	@Transactional(readOnly = false)
	public void delete(SysViews sysViews) {
		super.delete(sysViews);
	}

}