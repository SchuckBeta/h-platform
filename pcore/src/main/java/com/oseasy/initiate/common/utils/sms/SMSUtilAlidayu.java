package com.oseasy.initiate.common.utils.sms;

import com.oseasy.initiate.common.config.Global;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * api:
 * https://api.alidayu.com/doc2/apiDetail?spm=a3142.8260425.1999205497.20.j8eFPJ&apiId=25450
 */
public class SMSUtilAlidayu {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SMSUtil.class);
    public static final String AILIDAYU_SMS_SECRET = Global.getConfig("ailidayu.sms.secret");
    public static final String AILIDAYU_SMS_APPKEY = Global.getConfig("ailidayu.sms.appkey");
    public static final String AILIDAYU_SMS_URL = Global.getConfig("ailidayu.sms.url");
    public static final String AILIDAYU_SMS_TemplateCode = Global.getConfig("ailidayu.sms.templatecode");
    public static final String AILIDAYU_SMS_SIGN = Global.getConfig("ailidayu.sms.sign");
    public static Random rand = new Random();

    public static String sendSms(String tel) {
        TaobaoClient client = new DefaultTaobaoClient(AILIDAYU_SMS_URL, AILIDAYU_SMS_APPKEY, AILIDAYU_SMS_SECRET);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
//        req.setExtend("");
        req.setSmsType("normal");
        req.setSmsFreeSignName(AILIDAYU_SMS_SIGN);
        String content = String.format("%06d", rand.nextInt(1000000));
        req.setSmsParamString("{\"code\":\""+content+"\"}");
        req.setRecNum(tel);
        req.setSmsTemplateCode(AILIDAYU_SMS_TemplateCode);
        AlibabaAliqinFcSmsNumSendResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if (rsp != null) {
          if ((rsp.getResult() != null) && rsp.getResult().getSuccess()) {
            return content;
          }
          logger.error("短信发送失败:原因【"+rsp.getBody() +"】");
        }
        return null;
    }

    public static String sendSmsBath(String tel) {


        return null;
    }


    public static void main(String[] args) {
     /*   //【开创啦】您在开创啦请求的验证码是：**/
        System.out.println(  SMSUtilAlidayu.sendSms("18672394980")  );


    }

}
