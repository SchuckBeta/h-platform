package com.hch.platform.pcore.modules.interactive.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.pcore.modules.course.dao.CourseDao;
import com.hch.platform.pcore.modules.excellent.dao.ExcellentShowDao;
import com.hch.platform.pcore.modules.interactive.dao.SysViewsDao;
import com.hch.platform.pcore.modules.interactive.entity.SysViews;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

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
	@Autowired
	private UserDao userDao;
	public List<Map<String,String>> getBrowse(String uid){
		return dao.getBrowse(uid);
	}
	public List<Map<String,String>> getVisitors(String uid){
		return dao.getVisitors(uid);
	}
	/**
	 * 评导师、学生队列的处理
	 * @return 处理的数据条数
	 */
	@Transactional(readOnly = false)
	public int handleUserInfoViews(){
		List<SysViews> list=new ArrayList<SysViews>();//需要保存的list
		Map<String,Integer> map=new HashMap<String,Integer>();//需要更新浏览数量的map
		int tatol=10000;
		int count=0;
		Integer up=null;
		SysViews sc=(SysViews)CacheUtils.rpop(CacheUtils.USER_VIEWS_QUEUE);
		while(count<tatol&&sc!=null){
			count++;//增加了一条数据
			up=map.get(sc.getForeignId());
			if(up==null){
				map.put(sc.getForeignId(), 1);	
			}else{
				map.put(sc.getForeignId(), up+1);
			}
			list.add(sc);
			if(count<tatol){
				sc=(SysViews)CacheUtils.rpop(CacheUtils.USER_VIEWS_QUEUE);
			}
		}
		if(count>0){//有数据需要处理
			dao.insertBatch(list);
			userDao.updateViews(map);
			for(String userid:map.keySet()){
				CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_ID_ +userid);
			}
		}
		return count;
	}
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