/**
 * 
 */
package com.hch.platform.pcore.test.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.service.TreeService;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.test.dao.TestTreeDao;
import com.hch.platform.pcore.test.entity.TestTree;

/**
 * 树结构生成Service

 * @version 2015-04-06
 */
@Service
@Transactional(readOnly = true)
public class TestTreeService extends TreeService<TestTreeDao, TestTree> {

	public TestTree get(String id) {
		return super.get(id);
	}
	
	public List<TestTree> findList(TestTree testTree) {
		if (StringUtil.isNotBlank(testTree.getParentIds())) {
			testTree.setParentIds(","+testTree.getParentIds()+",");
		}
		return super.findList(testTree);
	}
	
	@Transactional(readOnly = false)
	public void save(TestTree testTree) {
		super.save(testTree);
	}
	
	@Transactional(readOnly = false)
	public void delete(TestTree testTree) {
		super.delete(testTree);
	}
	
}