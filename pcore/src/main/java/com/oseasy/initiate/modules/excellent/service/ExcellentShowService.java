package com.oseasy.initiate.modules.excellent.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.excellent.dao.ExcellentKeywordDao;
import com.oseasy.initiate.modules.excellent.dao.ExcellentShowDao;
import com.oseasy.initiate.modules.excellent.entity.ExcellentKeyword;
import com.oseasy.initiate.modules.excellent.entity.ExcellentShow;

import net.sf.json.JSONObject;

/**
 * 优秀展示Service.
 * @author 9527
 * @version 2017-06-23
 */
@Service
@Transactional(readOnly = true)
public class ExcellentShowService extends CrudService<ExcellentShowDao, ExcellentShow> {
	@Autowired
	private ExcellentShowDao excellentShowDao;
	@Autowired
	private ExcellentKeywordDao excellentKeywordDao;
	
	public List<Map<String,String>> getGcontestTeacherInfo(String gcontestId) {
		return excellentShowDao.getGcontestTeacherInfo(gcontestId);
	}
	public Map<String,String> getGcontestInfo(String gcontestId) {
		return excellentShowDao.getGcontestInfo(gcontestId);
	}
	
	public List<Map<String,String>> getProjectTeacherInfo(String projectId) {
		return excellentShowDao.getProjectTeacherInfo(projectId);
	}
	public Map<String,String> getProjectInfo(String projectId) {
		return excellentShowDao.getProjectInfo(projectId);
	}
	public Map<String,Object> findForIndex() {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("project", excellentShowDao.findProjectForIndex());
		map.put("gcontest", excellentShowDao.findGcontestForIndex());
		map.put("scientific", null);
		return map;
	}
	public ExcellentShow getByForid(String id) {
		return excellentShowDao.getByForid(id);
	}
	public ExcellentShow get(String id) {
		return super.get(id);
	}

	public List<ExcellentShow> findList(ExcellentShow excellentShow) {
		return super.findList(excellentShow);
	}

	public Page<ExcellentShow> findPage(Page<ExcellentShow> page, ExcellentShow excellentShow) {
		return super.findPage(page, excellentShow);
	}
	@Transactional(readOnly = false)
	public JSONObject frontSaveExcellentShow(ExcellentShow excellentShow) {
		JSONObject js=new JSONObject();
		js.put("ret", "1");
		js.put("msg", "保存成功");
		if(StringUtil.isEmpty(excellentShow.getContent())){
			js.put("ret", "0");
			js.put("msg", "请编辑页面内容");
			return js;
		}
		try {
			if (StringUtil.isNotEmpty(excellentShow.getId())) {
				ExcellentShow old=get(excellentShow.getId());
				if (old!=null) {
					if ("1".equals(old.getIsRelease())) {//已发布
						js.put("ret", "0");
						if ("0000000075".equals(excellentShow.getType())) {
							js.put("msg", "该项目展示已发布，不能再编辑");
						}
						if ("0000000076".equals(excellentShow.getType())) {
							js.put("msg", "该大赛展示已发布，不能再编辑");
						}
						if ("0000000077".equals(excellentShow.getType())) {
							js.put("msg", "该科研展示已发布，不能再编辑");
						}
						return js;
					}
					excellentShow.setIsComment(old.getIsComment());
					excellentShow.setIsTop(old.getIsTop());
					excellentShow.setIsRelease(old.getIsRelease());
					excellentShow.setReleaseDate(old.getReleaseDate());
				}
			}else{
				excellentShow.setIsComment("1");
				excellentShow.setIsTop("0");
				excellentShow.setIsRelease("0");
				if ("1".equals(excellentShow.getIsRelease())) {//发布
					excellentShow.setReleaseDate(new Date());
				}
			}
			excellentShow.setContent(excellentShow.getContent().replaceAll(FtpUtil.FTP_HTTPURL, FtpUtil.FTP_MARKER));//ftp_httpurl会变化，用固定的占位符代替，即使地址变化也不影响文件获取
			if (StringUtil.isNotEmpty(excellentShow.getId())) {
				excellentKeywordDao.delByEsid(excellentShow.getId());
			}
			super.save(excellentShow);
			js.put("id", excellentShow.getId());
			if (excellentShow.getKeywords()!=null) {
				for(String ek:excellentShow.getKeywords()) {
					ExcellentKeyword ekk=new ExcellentKeyword();
					ekk.setKeyword(ek);
					ekk.setExcellentId(excellentShow.getId());
					ekk.preInsert();
					excellentKeywordDao.insert(ekk);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			js.put("ret", "0");
			js.put("msg", "保存失败，系统错误");
		}
		return js;
	}
	@Transactional(readOnly = false)
	public JSONObject saveExcellentShow(ExcellentShow excellentShow) {
		JSONObject js=new JSONObject();
		js.put("ret", "1");
		js.put("msg", "保存成功");
		if(StringUtil.isEmpty(excellentShow.getContent())){
			js.put("ret", "0");
			js.put("msg", "请编辑页面内容");
			return js;
		}
		try {
			if (StringUtil.isNotEmpty(excellentShow.getId())) {
				ExcellentShow old=get(excellentShow.getId());
				if (old!=null) {
					if ("0".equals(old.getIsRelease())&&"1".equals(excellentShow.getIsRelease())) {//未发布变为已发布时
						excellentShow.setReleaseDate(new Date());
					}else{
						excellentShow.setReleaseDate(old.getReleaseDate());
					}
				}
			}else{
				if ("1".equals(excellentShow.getIsRelease())) {//发布
					excellentShow.setReleaseDate(new Date());
				}
			}
			excellentShow.setContent(excellentShow.getContent().replaceAll(FtpUtil.FTP_HTTPURL, FtpUtil.FTP_MARKER));//ftp_httpurl会变化，用固定的占位符代替，即使地址变化也不影响文件获取
			if (StringUtil.isNotEmpty(excellentShow.getId())) {
				excellentKeywordDao.delByEsid(excellentShow.getId());
			}
			super.save(excellentShow);
			js.put("id", excellentShow.getId());
			if (excellentShow.getKeywords()!=null) {
				for(String ek:excellentShow.getKeywords()) {
					ExcellentKeyword ekk=new ExcellentKeyword();
					ekk.setKeyword(ek);
					ekk.setExcellentId(excellentShow.getId());
					ekk.preInsert();
					excellentKeywordDao.insert(ekk);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			js.put("ret", "0");
			js.put("msg", "保存失败，系统错误");
		}
		return js;
	}
	@Transactional(readOnly = false)
	public void save(ExcellentShow excellentShow) {
		super.save(excellentShow);
	}

	@Transactional(readOnly = false)
	public void delete(ExcellentShow excellentShow) {
		super.delete(excellentShow);
	}

	public Page<Map<String,String>> findAllProjectShow(Page<Map<String, String>> page, Map<String, Object> param) {
		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		page.setPageSize(8);
		int count=excellentShowDao.findAllProjectShowCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> list=null;
		if (count>0) {
			list = excellentShowDao.findAllProjectShow(param);
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}

}