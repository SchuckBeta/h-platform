package com.hch.platform.pcore.test.ftp;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hch.platform.pcore.common.ftp.VsftpUtils;

/**
 * TODO 测试vsftp
 *
 * @author zhangchuansheng
 * @date 2017年6月13日 下午2:50:47
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-context-ftp.xml")
public class FtpPoolTest {

      @Test
      public void uploadFile() {
          System.out.println("uploadFile..");
           Long start =System.currentTimeMillis();
            // // =======上传测试============
            String localFile = "D:\\7月学分计划.gif";
            String path = "/tool/oseasy/";
            String filename = "7月学分计划.gif";
            InputStream input = null;
            try {
              input = new FileInputStream(new File(localFile));
              System.out.println("开始上传..");
              boolean result =VsftpUtils.uploadFile(path, filename, input);
              assertEquals(result,true);
              System.out.println("end.." +result +" 上传耗时："+(System.currentTimeMillis()-start));
            } catch (FileNotFoundException e) {
              e.printStackTrace();
            }
      }


    @Test
    public void downFile() {
        System.out.println("downFile..");
        Long start =System.currentTimeMillis();
        // =======下载测试============
      	String localPath = "E:\\";
      	String remotePath ="/tool/oseasy/";
      	String fileName = "7月学分计划.gif";
        System.out.println("开始下载..");
        boolean result = VsftpUtils.downFile(remotePath, fileName, localPath);
        assertEquals(result,true);
        System.out.println("end.." +result +" 下载耗时："+(System.currentTimeMillis()-start));

    }

    @Test
    public void delFile() {
        Long start =System.currentTimeMillis();
        String remotePath ="/tool/oseasy/";
        String fileName = "7月学分计划.gif";
//        String fileName = "7.gif";
        boolean result = false;
        try {
            result = VsftpUtils.removeFile(remotePath, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end.." +result +" 删除耗时："+(System.currentTimeMillis()-start));
    }



}
