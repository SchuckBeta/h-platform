package com.oseasy.initiate.modules.gcontesthots.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.gcontesthots.dao.GcontestHotsDao;
import com.oseasy.initiate.modules.gcontesthots.dao.GcontestHotsKeywordDao;
import com.oseasy.initiate.modules.gcontesthots.entity.GcontestHots;
import com.oseasy.initiate.modules.gcontesthots.entity.GcontestHotsKeyword;
import com.oseasy.initiate.modules.interactive.entity.SysViews;

/**
 * 大赛热点Service.
 * @author 9527
 * @version 2017-07-12
 */
@Service
@Transactional(readOnly = true)
public class GcontestHotsService extends CrudService<GcontestHotsDao, GcontestHots> {
	@Autowired
	private GcontestHotsKeywordDao gcontestHotsKeywordDao;
	@Autowired
	private GcontestHotsDao gcontestHotsDao;
	
	
	/**
	 *大赛热点浏览量队列的处理
	 * @return 处理的数据条数
	 */
	@Transactional(readOnly = false)
	public int handleViews(){
		Map<String,Integer> map=new HashMap<String,Integer>();//需要更新浏览量数量的map
		int tatol=10000;
		int count=0;
		Integer up=null;
		SysViews sv=(SysViews)CacheUtils.rpop(CacheUtils.GCONTESTHOTS_VIEWS_QUEUE);
		while(count<tatol&&sv!=null){
			count++;//增加了一条数据
			up=map.get(sv.getForeignId());
			if(up==null){
				map.put(sv.getForeignId(), 1);	
			}else{
				map.put(sv.getForeignId(), up+1);
			}
			if(count<tatol){
				sv=(SysViews)CacheUtils.rpop(CacheUtils.GCONTESTHOTS_VIEWS_QUEUE);
			}
		}
		if(count>0){//有数据需要处理
			gcontestHotsDao.updateViews(map);
		}
		return count;
	}
	public GcontestHots get(String id) {
		return super.get(id);
	}
	public List<Map<String,Object>> getMore(String id,List<String> keys) {
		return gcontestHotsDao.getMore(id,keys);
	}
	public GcontestHots getTop() {
		return gcontestHotsDao.getTop();
	}
	public List<GcontestHots> findList(GcontestHots gcontestHots) {
		return super.findList(gcontestHots);
	}

	public Page<GcontestHots> findPage(Page<GcontestHots> page, GcontestHots gcontestHots) {
		return super.findPage(page, gcontestHots);
	}

	@Transactional(readOnly = false)
	public void save(GcontestHots gcontestHots) {
		if (StringUtil.isNotEmpty(gcontestHots.getId())) {
			gcontestHots.setContent(gcontestHots.getContent().replaceAll(FtpUtil.FTP_HTTPURL, FtpUtil.FTP_MARKER));
		}
		if (StringUtil.isNotEmpty(gcontestHots.getId())) {
			GcontestHots old=get(gcontestHots.getId());
			if (old!=null) {
				if ("0".equals(old.getIsRelease())&&"1".equals(gcontestHots.getIsRelease())) {//未发布变为已发布时
					gcontestHots.setReleaseDate(new Date());
				}else{
					gcontestHots.setReleaseDate(old.getReleaseDate());
				}
			}
		}else{
			if ("1".equals(gcontestHots.getIsRelease())) {//发布
				gcontestHots.setReleaseDate(new Date());
			}
		}
		if(gcontestHots.getViews()==null){
			gcontestHots.setViews("0");
		}
		super.save(gcontestHots);
		if (StringUtil.isNotEmpty(gcontestHots.getId())) {
			gcontestHotsKeywordDao.delByEsid(gcontestHots.getId());
		}
		if (gcontestHots.getKeywords()!=null) {
			for(String ek:gcontestHots.getKeywords()) {
				GcontestHotsKeyword ekk=new GcontestHotsKeyword();
				ekk.setKeyword(ek);
				ekk.setGcontestHotsId(gcontestHots.getId());
				ekk.preInsert();
				gcontestHotsKeywordDao.insert(ekk);
			}
		}
	}

	@Transactional(readOnly = false)
	public void delete(GcontestHots gcontestHots) {
		super.delete(gcontestHots);
	}

}