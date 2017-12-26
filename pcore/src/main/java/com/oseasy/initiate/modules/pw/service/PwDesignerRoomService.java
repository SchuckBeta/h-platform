package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.pw.entity.PwDesignerRoom;
import com.hch.platform.pcore.modules.pw.dao.PwDesignerRoomDao;

/**
 * 房间设计表Service.
 * @author zy
 * @version 2017-12-18
 */
@Service
@Transactional(readOnly = true)
public class PwDesignerRoomService extends CrudService<PwDesignerRoomDao, PwDesignerRoom> {

	public PwDesignerRoom get(String id) {
		return super.get(id);
	}

	public List<PwDesignerRoom> findList(PwDesignerRoom pwDesignerRoom) {
		return super.findList(pwDesignerRoom);
	}


	public List<PwDesignerRoom> findListByCid(String cid) {
		return dao.findListByCid(cid);
	}

	public Page<PwDesignerRoom> findPage(Page<PwDesignerRoom> page, PwDesignerRoom pwDesignerRoom) {
		return super.findPage(page, pwDesignerRoom);
	}

	@Transactional(readOnly = false)
	public void save(PwDesignerRoom pwDesignerRoom) {
		super.save(pwDesignerRoom);
	}

	@Transactional(readOnly = false)
	public void delete(PwDesignerRoom pwDesignerRoom) {
		super.delete(pwDesignerRoom);
	}

	public void deleteAllByCid(String CId) {
		dao.deleteAllByCid(CId);
	}
}