package com.hch.platform.pcore.modules.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.modules.cms.entity.Comment;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

/**
 * Created by victor on 2017/2/23.
 */
@Service
public class DemoService {

    @Autowired
    UserDao dao;

    public void find(AbsUser user) {
        Long lo =dao.findAllCount(user);
        System.out.println("l::::::::"+lo);
    }

}
