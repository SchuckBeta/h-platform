/**
 * 
 */
package com.hch.platform.pcore.modules.test.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.test.dao.TestDao;
import com.hch.platform.pcore.modules.test.entity.Test;

/**
 * 测试Service


 */
@Service
@Transactional(readOnly = true)
public class TestService extends CrudService<TestDao, Test> {

}
