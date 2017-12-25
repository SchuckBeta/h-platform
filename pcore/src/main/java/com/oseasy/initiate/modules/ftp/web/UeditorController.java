package com.oseasy.initiate.modules.ftp.web;

import com.baidu.ueditor.ActionEnter;
import com.oseasy.initiate.common.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zhangzheng on 2017/6/26.  百度富文本编辑器初始化controller
 */
@Controller
@RequestMapping(value = "/ueditor")
public class UeditorController  {

    @RequestMapping(value = "upload")
    public void upload(HttpServletRequest request, HttpServletResponse response)throws Exception{
        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "text/html");
        String rootPath =request.getSession().getServletContext().getRealPath("/");
        response.getWriter().write(new MyActionEnter(request, rootPath).exec());
    }







}
