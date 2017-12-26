package com.hch.platform.pcore.modules.pw.service;

import com.google.common.collect.Lists;
import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.hch.platform.pcore.modules.pw.dao.PwRoomDao;
import com.hch.platform.pcore.modules.pw.dao.PwSpaceDao;
import com.hch.platform.pcore.modules.pw.entity.PwEnterRoom;
import com.hch.platform.pcore.modules.pw.entity.PwRoom;
import com.hch.platform.pcore.modules.pw.entity.PwSpace;
import com.hch.platform.pcore.modules.pw.entity.PwSpaceRoom;
import com.hch.platform.pcore.modules.pw.vo.PwAppointmentVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 房间Service.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwRoomService extends CrudService<PwRoomDao, PwRoom> {

    @Autowired
    PwSpaceDao pwSpaceDao;
    @Autowired
    private PwRoomDao pwRoomDao;
    @Autowired
    private PwFassetsService pwFassetsService;
    @Autowired
    private PwAppointmentService pwAppointmentService;
    @Autowired
    private PwEnterRoomService pwEnterRoomService;

    public PwRoom get(String id) {
        return super.get(id);
    }

    public List<PwRoom> findList(PwRoom pwRoom) {
        return super.findList(pwRoom);
    }

    public Page<PwRoom> findPage(Page<PwRoom> page, PwRoom pwRoom) {
        return super.findPage(page, pwRoom);
    }

    public List<PwRoom> findListByJL(PwRoom pwRoom) {
        if ((pwRoom.getPwSpace() != null) && StringUtil.isNotEmpty(pwRoom.getPwSpace().getId())) {
            PwSpace pwSpace = pwSpaceDao.get(pwRoom.getPwSpace());
            PwSpace ppwSpace = new PwSpace();
            ppwSpace.setParentIds(pwSpace.getParentIds() + pwSpace.getId() + StringUtil.DOTH);
            List<PwSpace> pwSpaces = pwSpaceDao.findList(ppwSpace);
            List<String> pids = Lists.newArrayList();
            pids.add(pwSpace.getId());
            for (PwSpace pspace : pwSpaces) {
                pids.add(pspace.getId());
            }
            pwRoom.setIds(pids);
        }
        return dao.findListByJL(pwRoom);
    }

    public Page<PwRoom> findPageByJL(Page<PwRoom> page, PwRoom pwRoom) {
        pwRoom.setPage(page);
        page.setList(findListByJL(pwRoom));
        return page;
    }

    /**
     * 查询可分配场地.
     *
     * @param pwRoom
     * @return
     */
    public List<PwRoom> findListByJLKfpCD(PwRoom pwRoom) {
        if ((pwRoom.getPwSpace() != null) && StringUtil.isNotEmpty(pwRoom.getPwSpace().getId())) {
            PwSpace pwSpace = pwSpaceDao.get(pwRoom.getPwSpace());
            if(pwSpace == null){
              return Lists.newArrayList();
            }
            PwSpace ppwSpace = new PwSpace();
            ppwSpace.setParentIds(pwSpace.getParentIds() + pwSpace.getId() + StringUtil.DOTH);
            List<PwSpace> pwSpaces = pwSpaceDao.findList(ppwSpace);
            List<String> pids = Lists.newArrayList();
            pids.add(pwSpace.getId());
            for (PwSpace pspace : pwSpaces) {
                pids.add(pspace.getId());
            }
            pwRoom.setIds(pids);
        }
        return dao.findListByJLKfpCD(pwRoom);
    }

    public Page<PwRoom> findPageByJLKfpCD(Page<PwRoom> page, PwRoom pwRoom) {
        pwRoom.setPage(page);
        page.setList(findListByJLKfpCD(pwRoom));
        return page;
    }

    @Transactional(readOnly = false)
    public ActYwRstatus<PwRoom> savePR(PwRoom pwRoom) {
        if (StringUtil.isEmpty(pwRoom.getIsUsable())) {
            pwRoom.setIsUsable(Global.NO);
        }

        if (StringUtil.isEmpty(pwRoom.getIsAssign())) {
            pwRoom.setIsAssign(Global.NO);
        }

        if (!pwRoom.getIsNewRecord()) {
            if ((Global.NO).equals(pwRoom.getIsAssign())) {//不可分配删除预约记录
                ActYwRstatus<List<PwEnterRoom>> rstatus = pwEnterRoomService.checkDeleteByRid(new PwEnterRoom(pwRoom));
                if (!rstatus.getStatus()) {
                    return new ActYwRstatus<PwRoom>(false, rstatus.getMsg(), pwRoom);
                }
            }

            if ((Global.NO).equals(pwRoom.getIsUsable())) {//不可预约删除预约记录
                pwAppointmentService.deleteByRoomIds(Arrays.asList(new String[]{pwRoom.getId()}));
            }
        }

        if ((Global.YES).equals(pwRoom.getIsAssign()) && (Global.YES).equals(pwRoom.getIsUsable())) {
            return new ActYwRstatus<PwRoom>(false, "可分配和可预约不能同时开启", pwRoom);
        }
        save(pwRoom);
        return new ActYwRstatus<PwRoom>(true, "保存成功", pwRoom);
    }

    @Transactional(readOnly = false)
    public void save(PwRoom pwRoom) {
        super.save(pwRoom);
    }

    @Transactional(readOnly = false)
    public void deleteAndClear(PwRoom pwRoom) {
        if (StringUtils.isBlank(pwRoom.getId())) {
            throw new RuntimeException("指定的房间未找到");
        }
        PwEnterRoom pwEnterRoom = new PwEnterRoom(new PwRoom(pwRoom.getId()));
        if(!pwEnterRoomService.findList(pwEnterRoom).isEmpty()){
            throw new RuntimeException("该房间有入驻信息，无法删除");
        }
        pwFassetsService.clearByRoomIds(Arrays.asList(new String[]{pwRoom.getId()}));
        pwAppointmentService.deleteByRoomIds(Arrays.asList(new String(pwRoom.getId())));
        delete(pwRoom);
    }

    @Transactional(readOnly = false)
    public void delete(PwRoom pwRoom) {
        super.delete(pwRoom);
    }

    public List<PwSpaceRoom> findSpaceAndRoom() {
        return pwRoomDao.findSpaceAndRoom();
    }

    public List<PwRoom> findListByPwAppointmentVo(PwAppointmentVo pwAppointmentVo) {
        return pwRoomDao.findListByPwAppointmentVo(pwAppointmentVo);
    }

    @Transactional(readOnly = false)
    public int deleteByRoomIds(List<String> roomIds) {
        return pwRoomDao.deleteByRoomIds(roomIds);
    }

    /**
     * 验证房间名重复.
     * @param name 房间名称
     * @param sid 楼层
     * @return ActYwRstatus
     */
    public ActYwRstatus<String> verifyName(String name, String sid, String id) {
      if(StringUtil.isEmpty(name) || StringUtil.isEmpty(sid)){
        return new ActYwRstatus<String>(true, "房间名和楼层为空");
      }

      List<PwRoom> pwRooms = dao.verifyNameBySpace(StringUtil.trim(name), sid);
      if((pwRooms == null) || (pwRooms.size() <= 0)){
        return new ActYwRstatus<String>(true, "房间名不存在", name);
      }

      /* if(StringUtil.isNotEmpty(id)){
        if((pwRooms.size() == 1)){
          PwRoom pwRoom = dao.get(id);
          if((id).equals(pwRooms.get(0).getId()) && (name).equals(pwRoom.getName())){
            return new ActYwRstatus<String>(true, "房间名不存在", name);
          }
        }
      }*/

      return new ActYwRstatus<String>(false, "房间名已存在", name);
    }

}