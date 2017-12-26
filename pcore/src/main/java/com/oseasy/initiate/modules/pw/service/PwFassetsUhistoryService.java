package com.oseasy.initiate.modules.pw.service;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.pw.dao.PwFassetsUhistoryDao;
import com.oseasy.initiate.modules.pw.entity.PwFassetsUhistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 固定资产使用记录Service.
 *
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwFassetsUhistoryService extends CrudService<PwFassetsUhistoryDao, PwFassetsUhistory> {

    @Autowired
    private PwFassetsUhistoryDao pwFassetsUhistoryDao;

    public PwFassetsUhistory get(String id) {
        return super.get(id);
    }

    public List<PwFassetsUhistory> findList(PwFassetsUhistory pwFassetsUhistory) {
        return super.findList(pwFassetsUhistory);
    }

    public Page<PwFassetsUhistory> findPage(Page<PwFassetsUhistory> page, PwFassetsUhistory pwFassetsUhistory) {
        return super.findPage(page, pwFassetsUhistory);
    }

    @Transactional(readOnly = false)
    public void save(PwFassetsUhistory pwFassetsUhistory) {
        super.save(pwFassetsUhistory);
    }

    @Transactional(readOnly = false)
    public void delete(PwFassetsUhistory pwFassetsUhistory) {
        super.delete(pwFassetsUhistory);
    }

    @Transactional(readOnly = false)
    public void deleteByFassetsIds(List<String> fassetsIds) {
        pwFassetsUhistoryDao.deleteByFassetsIds(fassetsIds);
    }

    @Transactional(readOnly = false)
    public void deleteByRoomIds(List<String> roomIds) {
        pwFassetsUhistoryDao.deleteByRoomIds(roomIds);
    }


}