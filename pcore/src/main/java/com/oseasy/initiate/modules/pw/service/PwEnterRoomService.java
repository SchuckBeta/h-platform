package com.oseasy.initiate.modules.pw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.dao.PwEnterDao;
import com.oseasy.initiate.modules.pw.dao.PwEnterDetailDao;
import com.oseasy.initiate.modules.pw.dao.PwEnterRoomDao;
import com.oseasy.initiate.modules.pw.entity.PwEnter;
import com.oseasy.initiate.modules.pw.entity.PwEnterDetail;
import com.oseasy.initiate.modules.pw.entity.PwEnterRoom;
import com.oseasy.initiate.modules.pw.vo.PwEnterShStatus;
import com.oseasy.initiate.modules.pw.vo.PwEnterStatus;

/**
 * 入驻场地分配Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwEnterRoomService extends CrudService<PwEnterRoomDao, PwEnterRoom> {
  @Autowired
  PwEnterDao pwEnterDao;
  @Autowired
  PwEnterDetailDao pwEnterDetailDao;

	public PwEnterRoom get(String id) {
		return super.get(id);
	}

	public List<PwEnterRoom> findList(PwEnterRoom pwEnterRoom) {
		return super.findList(pwEnterRoom);
	}

	public Page<PwEnterRoom> findPage(Page<PwEnterRoom> page, PwEnterRoom pwEnterRoom) {
		return super.findPage(page, pwEnterRoom);
	}

	@Transactional(readOnly = false)
	public void save(PwEnterRoom pwEnterRoom) {
		super.save(pwEnterRoom);
	}

	/**
	 * 入驻.
	 * @param pwEnterRoom
	 */
	@Transactional(readOnly = false)
	public Boolean saveEnter(PwEnterRoom pwEnterRoom) {
	  PwEnter pwEnter = pwEnterDao.get(pwEnterRoom.getPwEnter());
	  if(pwEnter != null){
      PwEnterDetail ppwEnterDetail = new PwEnterDetail();
      ppwEnterDetail.setPwEnter(new PwEnter(pwEnter.getId()));
      List<PwEnterDetail> perDetails = pwEnterDetailDao.findList(ppwEnterDetail);
      /**
       * 处理所有待审核的数据.
       */
      for (PwEnterDetail perDetail : perDetails) {
        if(((PwEnterShStatus.PESS_DFP.getKey()).equals(perDetail.getStatus()))){
          perDetail.setStatus(PwEnterShStatus.PESS_YFP.getKey());
          pwEnterDetailDao.update(perDetail);
        }
      }

	    pwEnter.setStatus(PwEnterStatus.PES_RZCGYFP.getKey());
	    pwEnterDao.update(pwEnter);
	    super.save(pwEnterRoom);
	    return true;
	  }
    return false;
	}

	@Transactional(readOnly = false)
	public Boolean deletePLWLByErid(PwEnterRoom pwEnterRoom) {
	  if(pwEnterRoom == null){
	    return false;
	  }

	  if((pwEnterRoom.getPwEnter() == null) || StringUtil.isEmpty(pwEnterRoom.getPwEnter().getId())){
	    return false;
	  }

    if((pwEnterRoom.getPwRoom() == null) || StringUtil.isEmpty(pwEnterRoom.getPwRoom().getId())){
	    return false;
	  }

    PwEnter pwEnter = pwEnterDao.get(pwEnterRoom.getPwEnter());
    if(pwEnter != null){
      dao.deletePLWLByErid(pwEnterRoom);

      PwEnterDetail ppwEnterDetail = new PwEnterDetail();
      ppwEnterDetail.setPwEnter(new PwEnter(pwEnter.getId()));
      List<PwEnterDetail> perDetails = pwEnterDetailDao.findList(ppwEnterDetail);
      /**
       * 处理所有待审核的数据.
       */
      for (PwEnterDetail perDetail : perDetails) {
        if(((PwEnterShStatus.PESS_YFP.getKey()).equals(perDetail.getStatus()))){
          perDetail.setStatus(PwEnterShStatus.PESS_DFP.getKey());
          pwEnterDetailDao.update(perDetail);
        }
      }

      pwEnter.setStatus(PwEnterStatus.PES_RZCG.getKey());
      pwEnterDao.update(pwEnter);
      return true;
    }
    return false;
	}

	@Transactional(readOnly = false)
	public ActYwRstatus<List<PwEnterRoom>> checkDeleteByRid(PwEnterRoom pwEnterRoom) {
    if((pwEnterRoom == null) || (pwEnterRoom.getPwRoom() == null) || StringUtil.isEmpty(pwEnterRoom.getPwRoom().getId())){
      return new ActYwRstatus<List<PwEnterRoom>>(false, "房间取消分配失败，参数不对！");
    }
    List<PwEnterRoom> pwEnterRooms = dao.findList(pwEnterRoom);
    if((pwEnterRooms == null) || (pwEnterRooms.size() <= 0)){
      return new ActYwRstatus<List<PwEnterRoom>>(true, "房间可以取消分配！", pwEnterRooms);
    }else{
      return new ActYwRstatus<List<PwEnterRoom>>(false, "房间被使用无法取消分配");
    }
	}

	@Transactional(readOnly = false)
	public Boolean deletePLWLByRid(PwEnterRoom pwEnterRoom) {
	    dao.deletePLWLByRid(pwEnterRoom);
	    return true;
	}

  @Transactional(readOnly = false)
  public void delete(PwEnterRoom pwEnterRoom) {
    super.delete(pwEnterRoom);
  }

  @Transactional(readOnly = false)
  public void deleteWL(PwEnterRoom pwEnterRoom) {
    dao.deleteWL(pwEnterRoom);
  }

  @Transactional(readOnly = false)
  public void deletePLWL(List<String> ids) {
    dao.deletePLWL(ids);
  }

  public List<PwEnterRoom> findListByinIds(List<String> ids) {
    return dao.findListByinIds(ids);
  }
}