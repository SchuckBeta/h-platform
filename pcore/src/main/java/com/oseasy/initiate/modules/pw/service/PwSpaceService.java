package com.hch.platform.pcore.modules.pw.service;

import com.hch.platform.pcore.common.service.TreeService;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.pw.dao.PwSpaceDao;
import com.hch.platform.pcore.modules.pw.entity.PwEnterRoom;
import com.hch.platform.pcore.modules.pw.entity.PwRoom;
import com.hch.platform.pcore.modules.pw.entity.PwSpace;
import com.hch.platform.pcore.modules.pw.vo.PwSpaceType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 设施Service.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwSpaceService extends TreeService<PwSpaceDao, PwSpace> {

    @Autowired
    private PwSpaceDao pwSpaceDao;

    @Autowired
    private PwRoomService pwRoomService;

    @Autowired
    private PwFassetsService pwFassetsService;

    @Autowired
    private PwAppointmentService pwAppointmentService;

    @Autowired
    private PwEnterRoomService pwEnterRoomService;

    public PwSpace get(String id) {
        return super.get(id);
    }

    public List<PwSpace> findList(PwSpace pwSpace) {
        if (StringUtil.isNotBlank(pwSpace.getParentIds())) {
            pwSpace.setParentIds("," + pwSpace.getParentIds() + ",");
        }
        return super.findList(pwSpace);
    }

    @Transactional(readOnly = false)
    public void save(PwSpace pwSpace) {
        validatePwSpace(pwSpace);
        if (StringUtils.isBlank(pwSpace.getId())) {
            pwSpace.setIsNewRecord(true);
            pwSpace.setId(IdGen.uuid());
        }
        /**
         * 添加楼层时不能超过总楼层数
         */
        if (PwSpaceType.FLOOR.getValue().equals(pwSpace.getType()) && pwSpace.getIsNewRecord()) {
            PwSpace s = new PwSpace();
            PwSpace p = new PwSpace(pwSpace.getParentId());
            s.setParent(p);
            List<PwSpace> children = findList(s);
            String f = StringUtils.isBlank(pwSpace.getParent().getFloorNum()) ? get(pwSpace.getParentId()).getFloorNum() : pwSpace.getParent().getFloorNum();
            int floorNum = Integer.valueOf(f);
            if (floorNum == children.size()) {
                throw new RuntimeException("已达到最大楼层数，不能再添加");
            }
        }
        /**
         * 处理背景图片
         */
        if (StringUtils.isNotBlank(pwSpace.getImageUrl())) {
            try {
                String imageUrl = FtpUtil.moveFile(FtpUtil.getftpClient(), pwSpace.getImageUrl());
                pwSpace.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("保存附件失败，请检查");
            }
        }
        super.save(pwSpace);
        /**
         * 创建楼栋时根据楼层数添加楼层
         */
        if (PwSpaceType.BUILDING.getValue().equals(pwSpace.getType()) && pwSpace.getIsNewRecord()) {
            if (StringUtils.isNotBlank(pwSpace.getFloorNum())) {
                int floor = Integer.valueOf(pwSpace.getFloorNum());
                for (int i = 0; i < floor; i++) {
                    PwSpace space = new PwSpace();
                    space.setParent(pwSpace);
                    String s = i + 1 + "";
                    if (s.length() < 2) {
                        s = "0" + s;
                    }
                    space.setName(s + "层");
                    space.setType(PwSpaceType.FLOOR.getValue());
                    super.save(space);
                }
            }
        }

    }

    @Transactional(readOnly = false)
    public void delete(PwSpace pwSpace) {
        PwSpace newPwSpace = this.get(pwSpace.getId());
        if (newPwSpace == null) {
            throw new RuntimeException("指定的设施不存在");
        }

        /**
         * 删除规则：下级的楼层中的房间如果有关联资产，需要先解除资产关联关系，再删除房间，最后删除设施;
         *          房间相关的预约全部取消; 入驻有使用到，不能删除
         *
         */
        List<String> roomIds = new ArrayList<>();//存放所有子设施的房间ID
        if (newPwSpace.getType().equals(PwSpaceType.FLOOR.getValue())) {
            findRooms(newPwSpace, roomIds);
        } else {
            String parentIds = newPwSpace.getParentIds();//当前设施的parentIds
            parentIds += newPwSpace.getId();
            PwSpace p = new PwSpace();
            p.setParentIds(parentIds);
            List<PwSpace> children = pwSpaceDao.findByParentIdsLike(p);//当前设施所有的子
            if (!children.isEmpty()) {
                for (PwSpace child : children) {
                    if (child.getType().equals(PwSpaceType.FLOOR.getValue())) {
                        findRooms(child, roomIds);
                    }
                }
            }
        }
        if (!roomIds.isEmpty()) {
            pwAppointmentService.deleteByRoomIds(roomIds);//删除预约
            pwFassetsService.clearByRoomIds(roomIds);//回收所有已关联房间的固定资产
            pwRoomService.deleteByRoomIds(roomIds);//删除房间
        }
        super.delete(pwSpace);
    }

    private void findRooms(PwSpace pwSpace, List<String> roomIds) {
        PwRoom pwRoom = new PwRoom();
        pwRoom.setPwSpace(pwSpace);
        List<PwRoom> roomList = pwRoomService.findList(pwRoom);
        if (!roomList.isEmpty()) {
            for (PwRoom room : roomList) {
                PwEnterRoom pwEnterRoom = new PwEnterRoom(new PwRoom(pwRoom.getId()));
                if (!pwEnterRoomService.findList(pwEnterRoom).isEmpty()) {
                    throw new RuntimeException(String.format("%s/%s/%s有入驻信息，无法删除",
                            room.getPwSpace().getParent().getName(), room.getPwSpace().getName(), room.getName()));
                }
                roomIds.add(room.getId());
            }
        }
    }


    private void validatePwSpace(PwSpace pwSpace) {
        List<PwSpace> list = this.findList(pwSpace);
        if (!list.isEmpty()) {
            for (PwSpace space : list) {
                if (space.getName().equals(pwSpace.getName())) {
                    throw new RuntimeException("同级下名称不能重复");
                }
            }
        }

    }

    /**
     * 查询指定pwSpace的所有子
     *
     * @param id
     * @return
     */
    public List<PwSpace> findChildren(String id) {
        if (StringUtils.isBlank(id)) {
            return new ArrayList<>(0);
        }
        PwSpace newPwSpace = get(id);
        if (newPwSpace == null) {
            return new ArrayList<>(0);
        }
        PwSpace p = new PwSpace();
        p.setParentIds(newPwSpace.getParentIds() + newPwSpace.getId());
        return pwSpaceDao.findByParentIdsLike(p);
    }


}