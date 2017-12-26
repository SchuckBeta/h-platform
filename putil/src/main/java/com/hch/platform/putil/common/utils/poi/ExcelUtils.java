package com.oseasy.initiate.common.utils.poi;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;


/**
 * @author u1
 *
 */
public class ExcelUtils {

  //创建xlsx
  public static void writeDataExcelx(String filePath, String[] dataExcel,
      String title,String[] unitTitle, String[] itemTitle) {
    // TODO Auto-generated method stub
    OutputStream ostream=null;
    try {
      ostream = new FileOutputStream(filePath);
      XSSFWorkbook excel=new XSSFWorkbook();
      XSSFSheet sheet0=excel.createSheet(title);
      //excel中第一行  poi中行、列开始都是以0开始计数
      //合并第一行 显示总标题  占据第一行的第一列到第15列
      sheet0.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
      XSSFRow row=sheet0.createRow(0);
      XSSFCell cell=row.createCell(0);
      cell.setCellValue(title);
      //设置样式
      XSSFCellStyle cellStyle=excel.createCellStyle();
      //居中对齐
      cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
      //字体
      XSSFFont font=excel.createFont();
      font.setFontHeightInPoints((short) 16);
      font.setColor(new XSSFColor(Color.RED));
      cellStyle.setFont(font);
      //设置第一行的样式
      cell.setCellStyle(cellStyle);

      //显示第二行 表头 各项标题
      row=sheet0.createRow(1);
      for (int i = 0; i < itemTitle.length; i++) {
        cell=row.createCell(i);
        cell.setCellValue(itemTitle[i]);
        cell.setCellStyle(cellStyle);
      }

      //从第三行开始显示 各大公司的数据
      int start=2;
      for (String unit : unitTitle) {
        row=sheet0.createRow(start);
        //第一列显示单位名称
        cell=row.createCell(0);
        cell.setCellValue(unit);
        //添加合计行
        if("合计".equals(unit)){
          for (int i = 0; i < dataExcel.length; i++) {
            cell=row.createCell(i+1);
            cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
            char charaColumn=(char)('b'+i);
            String formula="sum("+charaColumn+2+":"+charaColumn+start+")";
            cell.setCellFormula(formula);
          }
        }else { //添加数据行
          for (int i = 0; i < dataExcel.length; i++) {
            cell=row.createCell(i+1);
            cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Double.valueOf(dataExcel[i]));
          }
        }
        start++;
      }




      excel.write(ostream);
      System.out.println("创建excel成功");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally{
      if(ostream!=null)
        try {
          ostream.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
    }

  }

  //读取xlsx的数据
  public static String readDataExcelx(String filePath) {
    // TODO Auto-generated method stub
    String content="";
    try {
      OPCPackage pkg=OPCPackage.open(filePath);
      XSSFWorkbook excel=new XSSFWorkbook(pkg);
      //获取第一个sheet
      XSSFSheet sheet0=excel.getSheetAt(0);
      for (Iterator rowIterator=sheet0.iterator();rowIterator.hasNext();) {
        XSSFRow row=(XSSFRow) rowIterator.next();
        for (Iterator iterator=row.cellIterator();iterator.hasNext();) {
          XSSFCell cell=(XSSFCell) iterator.next();
          //根据单元的的类型 读取相应的结果
          if(cell.getCellType()==XSSFCell.CELL_TYPE_STRING) content+=cell.getStringCellValue()+"\t";
          else if(cell.getCellType()==XSSFCell.CELL_TYPE_NUMERIC) content+=cell.getNumericCellValue()+"\t";
          else if(cell.getCellType()==XSSFCell.CELL_TYPE_FORMULA) content+=cell.getCellFormula()+"\t";
        }
        content+="\n";
      }



    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return content;
  }




}
