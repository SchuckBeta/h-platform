package com.oseasy.initiate.modules.pw.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.pw.entity.PwRoom;
import com.oseasy.initiate.modules.pw.entity.PwSpaceRoom;
import com.oseasy.initiate.modules.pw.vo.PwAppointmentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房间DAO接口.
 *
 * @author chenh
 * @version 2017-11-26
 */
@MyBatisDao
public interface PwRoomDao extends CrudDao<PwRoom> {

    /**
     * 级联查询所有房间.
     *
     * @param pwRoom
     * @return List
     */
    public List<PwRoom> findListByJL(PwRoom pwRoom);

    /**
     * 级联查询所有已被分配房间.
     *
     * @param pwRoom
     * @return List
     */
    public List<PwRoom> findListByJLKfpCD(PwRoom pwRoom);

    /**
     * 删除
     *
     * @param roomIds
     * @return
     */
    public int deleteByRoomIds(@Param("roomIds") List<String> roomIds);

    /**
     * 查询房间和其上述楼层和楼栋
     *
     * @return
     */
    List<PwSpaceRoom> findSpaceAndRoom();

    List<PwRoom> findListByPwAppointmentVo(PwAppointmentVo pwAppointmentVo);

    /**
     * 验证房间名是否存在.
     * @param name 房间名
     * @param sid 楼层
     * @return List
     */
    public List<PwRoom> verifyNameBySpace(@Param("name") String name, @Param("sid") String sid);
}