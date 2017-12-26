package com.hch.platform.pconfig.common.utils.sms.oseasy;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.putil.common.utils.sms.SMSState;
import com.hch.platform.putil.common.utils.sms.SmsAlidayuParam;
import com.hch.platform.putil.common.utils.sms.SmsAlidayuRstate;
import com.hch.platform.putil.common.utils.sms.SmsRstate;
import com.hch.platform.putil.common.utils.sms.impl.SendOeyParam;

/**
 * 高校展短信批量发送工具类.
 * 数据文件在OeySMSConfig.AILIDAYU_SMS_OEY_EXCELFILE定义路径
 * @author chenhao
 */
public class OeySMSUtils {
  private static final String REGEX_MOBILE = "1(3|5|7|8)[0-9]{9}";
  //private static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
//  private static final String REGEX_MOBILE = "1\\d{10}";

  public static void main(String[] args) throws Exception {

    //System.out.println(("‭").matches(REGEX_MOBILE));

    System.out.println("============================================================================================");
    SmsRstate smsRstate = new SmsRstate();
    List<SmsAlidayuParam<SendOeyParam>> params = Lists.newArrayList();
    List<SmsAlidayuParam<SendOeyParam>> allParams = readDataExcelx(OeySMSConfig.AILIDAYU_SMS_OEY_EXCELFILE);
    System.out.println(allParams.size());
    for (SmsAlidayuParam<SendOeyParam> smsAParam : allParams) {
      if(StringUtil.isEmpty(smsAParam.getTels()) || (smsAParam.getParam() == null)){
        smsRstate.getFailstates().add(new SmsAlidayuRstate(null, SMSState.FAIL201));
      }else{
        Boolean isMobile = true;
        for (String tel : (smsAParam.getTels()).split(StringUtil.DOTH)) {
          if(!(tel).matches(REGEX_MOBILE)){
            isMobile = false;
          }
        }

        if(isMobile){
          params.add(smsAParam);
        }else{
          smsRstate.getFailstates().add(new SmsAlidayuRstate(smsAParam.getTels(), SMSState.FAIL202));
        }
      }

      SendOeyParam oeyParam = smsAParam.getParam();
      System.out.println(oeyParam.getInviteNo() + ">>>" + smsAParam.getTels());
    }

//    SmsRstate srstate = SMSUtilAlidayu.sendOeySmsBath(params, OeySMSConfig.AILIDAYU_SMS_OEY_SIGN, OeySMSConfig.AILIDAYU_SMS_TemplateInvite);
//    if(smsRstate != null){
//      smsRstate.getFailstates().addAll(srstate.getFailstates());
//      smsRstate.getSucstates().addAll(srstate.getSucstates());
//    }

    System.out.println(params.size());
    System.out.println("============================================================================================");
    System.out.println(SmsRstate.validate(smsRstate).setMsgDetail());
    System.out.println("============================================================================================");
  }

  /**
   * OEY 短信发送Excel数据读取工具，读取xlsx的数据.
   * @param filePath
   * @return List
   */
  public static List<SmsAlidayuParam<SendOeyParam>> readDataExcelx(String filePath) {
    List<SmsAlidayuParam<SendOeyParam>> smss = Lists.newArrayList();
    try {
      OPCPackage pkg = OPCPackage.open(filePath);
      XSSFWorkbook excel = new XSSFWorkbook(pkg);
      // 获取第一个sheet
      XSSFSheet sheet0 = excel.getSheetAt(0);
      int idrow = 0, idcol;
      DecimalFormat format = new DecimalFormat("#");
      for (Iterator<?> rowIterator = sheet0.iterator(); rowIterator.hasNext();) {
        XSSFRow row = (XSSFRow) rowIterator.next();
        if (idrow >= OeySMSConfig.EXCEL_ROW) {
          idcol = 0;
          String mobile = null;
          SendOeyParam param = null;
          for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
            XSSFCell cell = (XSSFCell) iterator.next();
            if(cell == null){
              continue;
            }

            if ((idcol == OeySMSConfig.EXCEL_COL_INO)) {
              if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
                param = new SendOeyParam(StringUtil.trimToEmpty(cell.getRichStringCellValue().getString()));
              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
                param = new SendOeyParam(format.format(cell.getNumericCellValue()));
              }
//              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA){
//              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN){
//              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_BLANK){}
            }

            if ((idcol == OeySMSConfig.EXCEL_COL_MOBILE)) {
              if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
                mobile = StringUtil.trimToEmpty(cell.getRichStringCellValue().getString());
//              mobile = cell.getStringCellValue();
              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
                mobile = format.format(cell.getNumericCellValue());
              }
//              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN){
//              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA){
//              }else if(cell.getCellType() == XSSFCell.CELL_TYPE_BLANK){}
            }
            idcol++;
          }

          if ((param != null)) {
            smss.add(new SmsAlidayuParam<SendOeyParam>(mobile, param));
          }
        }
        idrow++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return smss;
  }
}
