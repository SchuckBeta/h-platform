package com.oseasy.initiate.modules.actyw.tool.query.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.actyw.tool.query.entity.ActYwRuTask;
import com.oseasy.initiate.modules.actyw.tool.query.dao.ActYwRuTaskDao;

/**
 * 流程运行任务Service.
 *
 * @author chenhao
 * @version 2017-06-08
 */
@Service
@Transactional(readOnly = true)
public class ActYwRuTaskService extends CrudService<ActYwRuTaskDao, ActYwRuTask> {

  public ActYwRuTask get(String id) {
    return super.get(id);
  }

  public List<ActYwRuTask> findList(ActYwRuTask actYwRuTask) {
    return super.findList(actYwRuTask);
  }

  public Page<ActYwRuTask> findPage(Page<ActYwRuTask> page, ActYwRuTask actYwRuTask) {
    return super.findPage(page, actYwRuTask);
  }

  @Transactional(readOnly = false)
  public void save(ActYwRuTask actYwRuTask) {
    super.save(actYwRuTask);
  }

  @Transactional(readOnly = false)
  public void delete(ActYwRuTask actYwRuTask) {
    super.delete(actYwRuTask);
  }

}