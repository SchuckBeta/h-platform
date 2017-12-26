package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.pw.entity.PwDesignerRoomAttr;
import com.hch.platform.pcore.modules.pw.dao.PwDesignerRoomAttrDao;

/**
 * 房间属性表Service.
 * @author zy
 * @version 2017-12-18
 */
@Service
@Transactional(readOnly = true)
public class PwDesignerRoomAttrService extends CrudService<PwDesignerRoomAttrDao, PwDesignerRoomAttr> {

	public PwDesignerRoomAttr get(String id) {
		return super.get(id);
	}

	public List<PwDesignerRoomAttr> findList(PwDesignerRoomAttr pwDesignerRoomAttr) {
		return super.findList(pwDesignerRoomAttr);
	}

	public Page<PwDesignerRoomAttr> findPage(Page<PwDesignerRoomAttr> page, PwDesignerRoomAttr pwDesignerRoomAttr) {
		return super.findPage(page, pwDesignerRoomAttr);
	}

	@Transactional(readOnly = false)
	public void save(PwDesignerRoomAttr pwDesignerRoomAttr) {
		super.save(pwDesignerRoomAttr);
	}

	@Transactional(readOnly = false)
	public void delete(PwDesignerRoomAttr pwDesignerRoomAttr) {
		super.delete(pwDesignerRoomAttr);
	}

	public void deleteAllByRoomId(String rid) {
		dao.deleteAllByRoomId(rid);
	}

	public List<PwDesignerRoomAttr> findListByRid(String rid) {
		return dao.findListByRid(rid);
	}
}