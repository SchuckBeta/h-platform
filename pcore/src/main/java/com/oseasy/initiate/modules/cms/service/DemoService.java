package com.oseasy.initiate.modules.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.modules.cms.entity.Comment;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.User;

/**
 * Created by victor on 2017/2/23.
 */
@Service
public class DemoService {

    @Autowired
    UserDao dao;

    public void find(User user) {
        Long lo =dao.findAllCount(user);
        System.out.println("l::::::::"+lo);
    }

}
