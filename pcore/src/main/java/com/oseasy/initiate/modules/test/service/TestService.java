/**
 * 
 */
package com.oseasy.initiate.modules.test.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.test.dao.TestDao;
import com.oseasy.initiate.modules.test.entity.Test;

/**
 * 测试Service


 */
@Service
@Transactional(readOnly = true)
public class TestService extends CrudService<TestDao, Test> {

}
