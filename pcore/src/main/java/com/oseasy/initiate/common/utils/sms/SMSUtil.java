package com.oseasy.initiate.common.utils.sms;

import java.util.Random;

import org.slf4j.LoggerFactory;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.httpclient.HttpClientUtil;

/**
 * Created by victor on 2017/3/10.
 */
public class SMSUtil {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SMSUtil.class);

    public static final String SMS_USERNAME = Global.getConfig("sms.username");
    public static final String SMS_PASSWD = Global.getConfig("sms.passwd");
    public static final String SMS_URL = Global.getConfig("sms.url");
    public static Random rand =new Random();
    public static String sendSms(String tel) {
        String content = String.format("%06d", rand.nextInt(1000000));
        String res = "";
        if (StringUtil.isNotBlank(SMS_USERNAME) && StringUtil.isNotBlank(SMS_PASSWD) && StringUtil.isNotBlank(SMS_URL)) {
            String url = new StringBuffer().append(SMS_URL).append("?uid=")
                    .append(SMS_USERNAME).append("&pwd=").append(SMS_PASSWD.toLowerCase()).append("&mobile=")
                    .append(tel).append("&content=").append(content).toString();
            System.out.println(url);
            res = HttpClientUtil.doGet(url);
            if ("100".equals(res)) {
            	return content;
            }
        }
        logger.info("请检查短信用户名，密码，地址是否有空值");
        return null;
    }

    public static String getResult() {
        String url = new StringBuffer().append(SMS_URL).append("?uid=")
                .append(SMS_USERNAME).append("&pwd=").append(SMS_PASSWD).toString();
        System.out.println(url);

        if (StringUtil.isNotBlank(SMS_USERNAME) && StringUtil.isNotBlank(SMS_PASSWD) && StringUtil.isNotBlank(SMS_URL)) {
            return    HttpClientUtil.doGet(url);
        }
        return null;
    }

    public static void main(String[] args) {
//        System.out.println( SMSUtil.sendSms("13501947586"));
//        System.out.println("e692fa032ea95b9f921e87087d9a8a4e".toLowerCase());
//        http://dxhttp.c123.cn/tx/?uid=500960080001&pwd=0f9288298ec4da604a9eeea0b8b4785e&mobile=13733515910&content=IlPFAA
//        http://dxhttp.c123.cn/tx/?uid=500960080001&pwd=0f9288298ec4da604a9eeea0b8b4785e
//        System.out.println(SMSUtil.getResult() );
    }



}
