package com.oseasy.initiate.modules.actyw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.dao.ActYwGroupDao;

/**
 * 项目流程组Service.
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwGroupService extends CrudService<ActYwGroupDao, ActYwGroup> {

  public ActYwGroup get(String id) {
    return super.get(id);
  }

  public List<ActYwGroup> findList(ActYwGroup actYwGroup) {
    return super.findList(actYwGroup);
  }

  public Page<ActYwGroup> findPage(Page<ActYwGroup> page, ActYwGroup actYwGroup) {
    return super.findPage(page, actYwGroup);
  }

  @Transactional(readOnly = false)
  public void save(ActYwGroup actYwGroup) {
    super.save(actYwGroup);
  }

  @Transactional(readOnly = false)
  public void delete(ActYwGroup actYwGroup) {
    super.delete(actYwGroup);
  }

}