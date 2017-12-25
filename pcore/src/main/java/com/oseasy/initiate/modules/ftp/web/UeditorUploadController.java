package com.oseasy.initiate.modules.ftp.web;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.IdGen;

import net.sf.json.JSONObject;

/**
 * Created by zhangzheng on 2017/6/26.
 */
@Controller
@RequestMapping(value = "/ftp/ueditorUpload")
public class UeditorUploadController {

    //文件上传
    @RequestMapping(value="upload")
    @ResponseBody
    public JSONObject upload(HttpServletRequest request, HttpServletResponse response) throws Exception{
        JSONObject obj = new JSONObject();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile imgFile1 = multipartRequest.getFile("upfile"); //文件

        String urlFileName=imgFile1.getOriginalFilename();
        //得到文件名后缀，用id到名称保存。.
        String filename=urlFileName.substring(0,urlFileName.lastIndexOf(".")); //文件名
        String suffix=urlFileName.substring(urlFileName.lastIndexOf(".")+1);
        String  ftpId= IdGen.uuid();
        String saveFileName=ftpId+"."+suffix;
        String ftpPath="ueditor/"+ DateUtil.getDate("yyyy-MM-dd");
        InputStream is=imgFile1.getInputStream();
        FTPClient ftpClient=FtpUtil.getftpClient();
         long size=imgFile1.getSize();
        boolean res=FtpUtil.uploadInputSteam(ftpClient,is,"/tool/oseasy/temp/"+ftpPath,saveFileName);
        if (res) {
            obj.put("state","SUCCESS");//上传成功
            obj.put("original", filename);
            obj.put("size", size);
            obj.put("title", urlFileName);
            obj.put("type", suffix);
            obj.put("url", FtpUtil.ftpImgUrl("/tool/oseasy/temp/"+ftpPath+"/"+saveFileName));
            obj.put("ftpUrl", "/tool/oseasy/temp/"+ftpPath+"/"+saveFileName);
        }

        return obj;
    }




}
