package com.oseasy.initiate.modules.proprojectmd.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.ftp.vo.FileVo;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.impdata.dao.ImpInfoDao;
@Service
@Transactional(readOnly = true)
public class ImpExpService {
	public static Logger log = Logger.getLogger(ImpExpService.class);
	public final static int close_sheet0_head=2;
	public final static String[] close_sheet0_foot=new String[]{"经手人：                       联系电话：                   学院负责人（签名）：                 "};
	public final static int mid_sheet0_head=2;
	public final static String[] mid_sheet0_foot=new String[]{"经手人：                       联系电话：                   "};
	public final static int approval_sheet0_head=2;
	public final static int approval_sheet1_head=2;
	public final static String[] approval_sheet0_foot=new String[]{"联系人签字：                       联系电话：                   负责人签字：                   ","注： 1.“A”为学生自主选题，来源于自己对课题长期积累与兴趣；“B”为学生来源于教师科研项目选题；“C”为学生承担社会、企业委托项目选题。","2.“来源项目名称”和“来源项目类别”栏限“B”和“C”的项目填写，“来源项目类别”栏填写“863项目”、“973项目”、“国家自然科学基金项目”、“省级自然科学基金项目”、“教师横向科研项目”、“企业委托项目”、“社会委托项目”以及其他项目标识。","3. 项目其他成员信息格式：姓名1（学号1）、姓名2（学号2）、…","4. 申报级别按“国家级（含省级备选项目）”、“校级”填写。"};
	public final static String[] approval_sheet1_foot=new String[]{"联系人签字：                       联系电话：                   负责人签字：                   ","注： 1.“A”为学生自主选题，来源于自己对课题长期积累与兴趣；“B”为学生来源于教师科研项目选题；“C”为学生承担社会、企业委托项目选题。","2.“来源项目名称”和“来源项目类别”栏限“B”和“C”的项目填写，“来源项目类别”栏填写“863项目”、“973项目”、“国家自然科学基金项目”、“省级自然科学基金项目”、“教师横向科研项目”、“企业委托项目”、“社会委托项目”以及其他项目标识。","3. 项目其他成员信息格式：姓名1（学号1）、姓名2（学号2）、…","4. 申报级别按“国家级（含省级备选项目）”、“校级”填写。","5.申报类型：创新训练项目、创业训练项目、创业实践项目。","6.“是否已申请入孵”为创业项目填写，团队已提交“中南民族大学大学生创业孵化示范基地入驻申请”请填是，否则填否。"};
	@Autowired
	private ImpInfoDao impInfoDao;
	@Autowired
	private SysAttachmentService sysAttachmentService;
	@Autowired
	private ProModelMdService proModelMdService;
	@Autowired
	private ActTaskService actTaskService;
	public void expAll(HttpServletRequest request, HttpServletResponse response) {
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		FileInputStream fs = null;
		OutputStream out = null;
		try {
//			String actywId=request.getParameter("actywId");
			// excel模板路径
			String fileName = "项目信息.xlsx";
			String headStr = "attachment; filename=\"" + new String(fileName .getBytes(), "ISO-8859-1") + "\"";
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", headStr);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "exp_projectmd_template.xlsx");
			fs = new FileInputStream(fi);
			// 读取了模板内所有sheet内容
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			List<String> pids=proModelMdService.getAllPromodelMd();
			List<Map<String, String>> prodata=impInfoDao.getProjectMdData(pids);
			
			XSSFCellStyle rowStyle = wb.createCellStyle();
			rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			XSSFDataFormat format = wb.createDataFormat();
			rowStyle.setDataFormat(format.getFormat("@"));
			
			XSSFSheet sheet0 = wb.getSheetAt(0);
			
			XSSFCell c0=sheet0.getRow(0).getCell(0);
			c0.setCellValue(c0.getStringCellValue());
			
			int row0=3;
			for(Map<String, String> map:prodata){
				XSSFRow row=sheet0.createRow(row0);
				row0++;
				row.createCell(0).setCellValue(row0-3+"");
				row.createCell(1).setCellValue(map.get("oname"));
				row.createCell(2).setCellValue(map.get("p_number"));
				row.createCell(3).setCellValue(map.get("p_name"));
				row.createCell(4).setCellValue(map.get("pro_category"));
				row.createCell(5).setCellValue(map.get("level"));
				row.createCell(6).setCellValue(map.get("leader_name"));
				row.createCell(7).setCellValue(map.get("no"));
				int nums=1;
				if(StringUtil.isNotEmpty(map.get("members"))){
					nums=nums+map.get("members").split("、").length;
				}
				row.createCell(8).setCellValue(nums+"");
				row.createCell(9).setCellValue(map.get("members"));
				String[] teas=null;
				if(StringUtil.isNotEmpty(map.get("teachers"))){
					teas=map.get("teachers").split(",");
				}
				row.createCell(10).setCellValue(teas!=null?teas[0]:"");
				row.createCell(11).setCellValue(teas!=null?teas[1]:"");
				row.createCell(12).setCellValue(teas!=null?teas[2]:"");
				row.createCell(13).setCellValue(map.get("s3l"));
				row.createCell(14).setCellValue(map.get("introduction"));
				
				//设置样式
				for(int m=0;m<=14;m++){
					row.getCell(m).setCellStyle(rowStyle);
				}
			}
			out = response.getOutputStream();
			wb.write(out);
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				if (out!=null)out.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	public void expClose(HttpServletRequest request, HttpServletResponse response) {
		System.setProperty("sun.jnu.encoding","utf-8");//设置文件的编码
		log.info("系统编码是：：："+System.getProperty("file.encoding"));

		String gnodeId=request.getParameter("gnodeId");
		String actywId=request.getParameter("actywId");
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		String tempPath=File.separator+"tempFileDir"+File.separator+IdGen.uuid();//生成的文件所在目录
		File tempPathDir = new File(tempPath+File.separator);
		if(!tempPathDir.exists()){
			tempPathDir.mkdirs();
		}
		try {
			List<String> pids=actTaskService.getAllTodoId(actywId,gnodeId);//获取结项需要审核的pro_model id
			if(pids!=null&&pids.size()>0){
				List<String> fileSteps=new ArrayList<String>();
				fileSteps.add(FileStepEnum.S2300.getValue());
				FileVo fileVo = sysAttachmentService.downloads(FileTypeEnum.S10, pids, fileSteps, tempPath);//下载学生结项上传的文件,并创建目录结构
				if((fileVo.getStatus()).equals(FileVo.SUCCESS)){
					List<Map<String, String>> data=impInfoDao.getCloseData(pids);//获取需要审核的项目信息
					if(data!=null&&data.size()>0){
						Map<String, List<Map<String, String>>> map=new HashMap<String, List<Map<String, String>>>();//将需要审核的项目信息按学院名称分类，学院名称-数据
						for(Map<String, String> datam:data){
							String offname=datam.get("oname");
							List<Map<String, String>> olist=map.get(offname);
							if(olist==null){
								olist=new ArrayList<Map<String, String>>();
								map.put(offname, olist);
							}
							olist.add(datam);
						}
						for(String key:map.keySet()){
							//按学院名称生成项目审核信息
							expCloseFileByOffice("",rootpath,tempPath+File.separator+FileStepEnum.S2300.getName()+File.separator+key,key, map.get(key));
						}
					}
				}else{
					log.error("下载项目结项报告失败");
				}
			}
			//对生成的文件压缩，下载
			createZip("",FileStepEnum.S2300.getName(),tempPath+File.separator+FileStepEnum.S2300.getName(), response);
		} catch (Exception e) {
			log.error(e);
		}finally {
			//删除临时文件目录
			File f=new File(tempPath);
			if(f.exists()){
				deleteDir(f);
			}
		}
	}
	//导出学院结项汇总表
	private void expCloseFileByOffice(String year,String rootpath,String filepath,String oname,List<Map<String, String>> list){
		File dirFile=new File(filepath);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		File file = new File(filepath+File.separator+"大学生创新训练计划项目结题验收汇总表_"+oname+".xlsx");
		FileOutputStream fos=null;
		FileInputStream fs = null;
		try {
			file.createNewFile();
			fos = new FileOutputStream(file);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "exp_close_template.xlsx");
			fs = new FileInputStream(fi);
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			
			XSSFCellStyle rowStyle = wb.createCellStyle();
			rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			XSSFDataFormat format = wb.createDataFormat();
			rowStyle.setDataFormat(format.getFormat("@"));
			
			XSSFSheet sheet0 = wb.getSheetAt(0);
			XSSFCell c0=sheet0.getRow(0).getCell(0);
			c0.setCellValue(year+c0.getStringCellValue());
			sheet0.getRow(1).getCell(0).setCellValue("学院名称(盖章)："+oname+"           年     月     日");
			int row0=close_sheet0_head+2;
			for(Map<String, String> map:list){
				XSSFRow row=null;
				int rowinx=0;
				row=sheet0.createRow(row0);
				rowinx=row0-close_sheet0_head-1;
				row0++;
				row.createCell(0).setCellValue(rowinx+"");
				row.createCell(1).setCellValue(map.get("p_number"));
				row.createCell(2).setCellValue(map.get("p_name"));
				row.createCell(3).setCellValue(map.get("leader_name"));
				row.createCell(4).setCellValue(map.get("no"));
				row.createCell(5).setCellValue(map.get("mobile"));
				row.createCell(6).setCellValue(map.get("members"));
				String[] teas=null;
				if(StringUtil.isNotEmpty(map.get("teachers"))){
					teas=map.get("teachers").split(",");
				}
				row.createCell(7).setCellValue(teas!=null?teas[0]:"");
				row.createCell(8).setCellValue(teas!=null?teas[1]:"");
				row.createCell(9).setCellValue(map.get("level"));
				row.createCell(10).setCellValue(map.get("pro_category"));
				row.createCell(11).setCellValue(map.get("result"));
				row.createCell(12).setCellValue("");
				row.createCell(13).setCellValue("");
				row.createCell(14).setCellValue(map.get("reimbursement_amount"));
				row.createCell(15).setCellValue(map.get("id"));
				String gnodeid="";
				ActYwGnode actYwGnode=actTaskService.getNodeByProInsId(map.get("proc_ins_id"));
				if(actYwGnode!=null){
					gnodeid=actYwGnode.getId();
				}
				row.createCell(16).setCellValue(gnodeid);
				//设置样式
				for(int m=0;m<=16;m++){
					row.getCell(m).setCellStyle(rowStyle);
				}
			}
			//尾部
			XSSFCellStyle cellStyle = wb.createCellStyle();  
			XSSFFont font = wb.createFont();    
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);   
			font.setFontName("仿宋_GB2312");
			font.setFontHeightInPoints((short) 11);//设置字体大小 
			cellStyle.setFont(font);
			
			for(int k=0;k<close_sheet0_foot.length;k++){
				sheet0.createRow(row0+k).createCell(0).setCellValue(close_sheet0_foot[k]);
			}
			sheet0.getRow(row0).getCell(0).setCellStyle(cellStyle);
			wb.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos!=null)fos.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void expMid(HttpServletRequest request, HttpServletResponse response) {
		System.setProperty("sun.jnu.encoding","utf-8");//设置文件的编码
		log.info("系统编码是：：："+System.getProperty("file.encoding"));

		String gnodeId=request.getParameter("gnodeId");
		String actywId=request.getParameter("actywId");
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		String tempPath=File.separator+"tempFileDir"+File.separator+IdGen.uuid();//生成的文件所在目录
		File tempPathDir = new File(tempPath+File.separator);
		if(!tempPathDir.exists()){
			tempPathDir.mkdirs();
		}
		try {
			List<String> pids=actTaskService.getAllTodoId(actywId,gnodeId);//获取中期检查需要审核的pro_model id
			if(pids!=null&&pids.size()>0){
				List<String> fileSteps=new ArrayList<String>();
				fileSteps.add(FileStepEnum.S2200.getValue());
				FileVo fileVo = sysAttachmentService.downloads(FileTypeEnum.S10, pids, fileSteps, tempPath);//下载学生中期检查上传的文件,并创建目录结构
				if((fileVo.getStatus()).equals(FileVo.SUCCESS)){
					List<Map<String, String>> data=impInfoDao.getMidData(pids);//获取需要审核的项目信息
					if(data!=null&&data.size()>0){
						Map<String, List<Map<String, String>>> map=new HashMap<String, List<Map<String, String>>>();//将需要审核的项目信息按学院名称分类，学院名称-数据
						for(Map<String, String> datam:data){
							String offname=datam.get("oname");
							List<Map<String, String>> olist=map.get(offname);
							if(olist==null){
								olist=new ArrayList<Map<String, String>>();
								map.put(offname, olist);
							}
							olist.add(datam);
						}
						for(String key:map.keySet()){
							//按学院名称生成项目审核信息
							expMidFileByOffice("",rootpath,tempPath+File.separator+FileStepEnum.S2200.getName()+File.separator+key,key, map.get(key));
						}
					}
				}else{
					log.error("下载项目中期报告失败");
				}
			}
			//对生成的文件压缩，下载
			createZip("",FileStepEnum.S2200.getName(),tempPath+File.separator+FileStepEnum.S2200.getName(), response);
		} catch (Exception e) {
			log.error(e);
		}finally {
			//删除临时文件目录
			File f=new File(tempPath);
			if(f.exists()){
				deleteDir(f);
			}
		}
	}
	//导出学院中期汇总表
	private void expMidFileByOffice(String year,String rootpath,String filepath,String oname,List<Map<String, String>> list){
			File dirFile=new File(filepath);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
			File file = new File(filepath+File.separator+"大学生创新创业训练计划检查汇总表_"+oname+".xlsx");
			FileOutputStream fos=null;
			FileInputStream fs = null;
			try {
				file.createNewFile();
				fos = new FileOutputStream(file);
				File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
						+ File.separator + "exp_mid_template.xlsx");
				fs = new FileInputStream(fi);
				XSSFWorkbook wb = new XSSFWorkbook(fs);
				
				XSSFCellStyle rowStyle = wb.createCellStyle();
				rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
				rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
				rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
				rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
				XSSFDataFormat format = wb.createDataFormat();
				rowStyle.setDataFormat(format.getFormat("@"));
				
				XSSFSheet sheet0 = wb.getSheetAt(0);
				XSSFCell c0=sheet0.getRow(0).getCell(0);
				c0.setCellValue(year+c0.getStringCellValue());
				sheet0.getRow(1).getCell(0).setCellValue("学院名称(盖章)："+oname+"           年     月     日");
				int row0=mid_sheet0_head+2;
				for(Map<String, String> map:list){
					XSSFRow row=null;
					int rowinx=0;
					row=sheet0.createRow(row0);
					rowinx=row0-mid_sheet0_head-1;
					row0++;
					row.createCell(0).setCellValue(rowinx+"");
					row.createCell(1).setCellValue(map.get("p_number"));
					row.createCell(2).setCellValue(map.get("p_name"));
					row.createCell(3).setCellValue(map.get("leader_name"));
					row.createCell(4).setCellValue(map.get("no"));
					row.createCell(5).setCellValue(map.get("mobile"));
					row.createCell(6).setCellValue(map.get("teachers"));
					row.createCell(7).setCellValue(map.get("pro_category"));
					row.createCell(8).setCellValue(map.get("level"));
					row.createCell(9).setCellValue("");
					row.createCell(10).setCellValue(map.get("stage_result"));
					row.createCell(11).setCellValue(map.get("reimbursement_amount"));
					row.createCell(12).setCellValue(map.get("id"));
					String gnodeid="";
					ActYwGnode actYwGnode=actTaskService.getNodeByProInsId(map.get("proc_ins_id"));
					if(actYwGnode!=null){
						gnodeid=actYwGnode.getId();
					}
					row.createCell(13).setCellValue(gnodeid);
					//设置样式
					for(int m=0;m<=13;m++){
						row.getCell(m).setCellStyle(rowStyle);
					}
				}
				//尾部
				XSSFCellStyle cellStyle = wb.createCellStyle();  
				XSSFFont font = wb.createFont();    
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);   
				font.setFontName("仿宋_GB2312");
				font.setFontHeightInPoints((short) 11);//设置字体大小 
				cellStyle.setFont(font);
				
				for(int k=0;k<mid_sheet0_foot.length;k++){
					sheet0.createRow(row0+k).createCell(0).setCellValue(mid_sheet0_foot[k]);
				}
				sheet0.getRow(row0).getCell(0).setCellStyle(cellStyle);
				wb.write(fos);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos!=null)fos.close();
					if (fs!=null)fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	public void expApproval(HttpServletRequest request, HttpServletResponse response) {
		System.setProperty("sun.jnu.encoding","utf-8");//设置文件的编码
		log.info("系统编码是：：："+System.getProperty("file.encoding"));

		String year="";
		String gnodeId=request.getParameter("gnodeId");
		String actywId=request.getParameter("actywId");
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		String tempPath=File.separator+"tempFileDir"+File.separator+IdGen.uuid();//生成的文件所在目录
		File tempPathDir = new File(tempPath+File.separator);
		if(!tempPathDir.exists()){
			tempPathDir.mkdirs();
		}
		try {
			List<String> pids=actTaskService.getAllTodoId(actywId,gnodeId);//获取立项需要审核的pro_model id
			if(pids!=null&&pids.size()>0){
				List<String> fileSteps=new ArrayList<String>();
				fileSteps.add(FileStepEnum.S2000.getValue());
				FileVo fileVo = sysAttachmentService.downloads(FileTypeEnum.S10, pids, fileSteps, tempPath);//下载学生申报时上传的文件,并创建目录结构
				if((fileVo.getStatus()).equals(FileVo.SUCCESS)){
					List<Map<String, String>> data=impInfoDao.getApprovalData(pids);//获取需要审核的项目信息
					if(data!=null&&data.size()>0){
						Map<String, List<Map<String, String>>> map=new HashMap<String, List<Map<String, String>>>();//将需要审核的项目信息按学院名称分类，学院名称-数据
						for(Map<String, String> datam:data){
							String offname=datam.get("oname");
							List<Map<String, String>> olist=map.get(offname);
							if(olist==null){
								olist=new ArrayList<Map<String, String>>();
								map.put(offname, olist);
							}
							olist.add(datam);
						}
						for(String key:map.keySet()){
							//按学院名称生成项目审核信息
							expApprovalFileByOffice(year,rootpath,tempPath+File.separator+FileStepEnum.S2000.getName()+File.separator+key,key, map.get(key));
						}
					}
					List<Map<String, String>> prodata=impInfoDao.getProjectMdData(pids);
					//导出全部项目信息
					expProject(year,rootpath, tempPath+File.separator+FileStepEnum.S2000.getName(), prodata);
				}else{
					log.error("下载项目申请报告失败");
				}
			}
			//对生成的文件压缩，下载
			createZip(year,FileStepEnum.S2000.getName(),tempPath+File.separator+FileStepEnum.S2000.getName(), response);
		} catch (Exception e) {
			log.error(e);
		}finally {
			//删除临时文件目录
			File f=new File(tempPath);
			if(f.exists()){
				deleteDir(f);
			}
		}
	}
	//导出项目表
	private void expProject(String year,String rootpath,String filepath,List<Map<String, String>> list){
		File file = new File(filepath+File.separator+"项目信息.xlsx");
		FileOutputStream fos=null;
		FileInputStream fs = null;
		try {
			file.createNewFile();
			fos = new FileOutputStream(file);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "exp_projectmd_template.xlsx");
			fs = new FileInputStream(fi);
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			
			XSSFCellStyle rowStyle = wb.createCellStyle();
			rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			XSSFDataFormat format = wb.createDataFormat();
			rowStyle.setDataFormat(format.getFormat("@"));
			
			XSSFSheet sheet0 = wb.getSheetAt(0);
			
			XSSFCell c0=sheet0.getRow(0).getCell(0);
			c0.setCellValue(year+c0.getStringCellValue());
			
			int row0=3;
			for(Map<String, String> map:list){
				XSSFRow row=sheet0.createRow(row0);
				row0++;
				row.createCell(0).setCellValue(row0-3+"");
				row.createCell(1).setCellValue(map.get("oname"));
				row.createCell(2).setCellValue(map.get("p_number"));
				row.createCell(3).setCellValue(map.get("p_name"));
				row.createCell(4).setCellValue(map.get("pro_category"));
				row.createCell(5).setCellValue(map.get("level"));
				row.createCell(6).setCellValue(map.get("leader_name"));
				row.createCell(7).setCellValue(map.get("no"));
				int nums=1;
				if(StringUtil.isNotEmpty(map.get("members"))){
					nums=nums+map.get("members").split("、").length;
				}
				row.createCell(8).setCellValue(nums+"");
				row.createCell(9).setCellValue(map.get("members"));
				String[] teas=null;
				if(StringUtil.isNotEmpty(map.get("teachers"))){
					teas=map.get("teachers").split(",");
				}
				row.createCell(10).setCellValue(teas!=null?teas[0]:"");
				row.createCell(11).setCellValue(teas!=null?teas[1]:"");
				row.createCell(12).setCellValue(teas!=null?teas[2]:"");
				row.createCell(13).setCellValue(map.get("s3l"));
				row.createCell(14).setCellValue(map.get("introduction"));
				
				//设置样式
				for(int m=0;m<=14;m++){
					row.getCell(m).setCellStyle(rowStyle);
				}
			}
			wb.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos!=null)fos.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	//导出学院申报汇总表
	private void expApprovalFileByOffice(String year,String rootpath,String filepath,String oname,List<Map<String, String>> list){
		File dirFile=new File(filepath);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		File file = new File(filepath+File.separator+"大学生创新创业训练计划申报汇总表_"+oname+".xlsx");
		FileOutputStream fos=null;
		FileInputStream fs = null;
		try {
			file.createNewFile();
			fos = new FileOutputStream(file);
			File fi = new File(rootpath + File.separator + "static" + File.separator + "excel-template"
					+ File.separator + "exp_approval_template.xlsx");
			fs = new FileInputStream(fi);
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			
			XSSFCellStyle rowStyle = wb.createCellStyle();
			rowStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
			rowStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
			rowStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框
			rowStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
			XSSFDataFormat format = wb.createDataFormat();
			rowStyle.setDataFormat(format.getFormat("@"));
			
			XSSFSheet sheet0 = wb.getSheetAt(0);
			XSSFCell c0=sheet0.getRow(0).getCell(0);
			c0.setCellValue(year+c0.getStringCellValue());
			sheet0.getRow(1).getCell(0).setCellValue("学院名称(盖章)："+oname);
			XSSFSheet sheet1 = wb.getSheetAt(1);
			XSSFCell c1=sheet1.getRow(0).getCell(0);
			c1.setCellValue(year+c1.getStringCellValue());
			sheet1.getRow(1).getCell(0).setCellValue("学院名称(盖章)："+oname);
			int row0=approval_sheet0_head+2;
			int row1=approval_sheet1_head+2;
			for(Map<String, String> map:list){
				XSSFRow row=null;
				int rowinx=0;
				if("0000000197".equals(map.get("app_level"))||"0000000198".equals(map.get("app_level"))){//国家级、省级
					row=sheet0.createRow(row0);
					rowinx=row0-approval_sheet0_head-1;
					row0++;
				}else if("0000000199".equals(map.get("app_level"))){//校级
					row=sheet1.createRow(row1);
					rowinx=row1-approval_sheet1_head-1;
					row1++;
				}else{
					row=sheet1.createRow(row1);
					rowinx=row1-approval_sheet1_head-1;
					row1++;
				}
				row.createCell(0).setCellValue(rowinx+"");
				row.createCell(1).setCellValue(map.get("pro_category"));
				row.createCell(2).setCellValue(map.get("level"));
				row.createCell(3).setCellValue(map.get("p_number"));
				row.createCell(4).setCellValue(map.get("p_name"));
				row.createCell(5).setCellValue(map.get("leader_name"));
				row.createCell(6).setCellValue(map.get("no"));
				row.createCell(7).setCellValue(map.get("mobile"));
				row.createCell(8).setCellValue("A".equals(map.get("pro_source"))?"√":"");
				row.createCell(9).setCellValue("B".equals(map.get("pro_source"))?"√":"");
				row.createCell(10).setCellValue("C".equals(map.get("pro_source"))?"√":"");
				row.createCell(11).setCellValue(map.get("source_project_name"));
				row.createCell(12).setCellValue(map.get("source_project_type"));
				String[] teas=null;
				if(StringUtil.isNotEmpty(map.get("teachers"))){
					teas=map.get("teachers").split(",");
				}
				row.createCell(13).setCellValue(teas!=null?teas[0]:"");
				row.createCell(14).setCellValue(teas!=null?teas[1]:"");
				row.createCell(15).setCellValue(teas!=null?teas[2]:"");
				row.createCell(16).setCellValue(teas!=null?teas[3]:"");
				row.createCell(17).setCellValue(map.get(""));
				String[] stus=null;
				if(StringUtil.isNotEmpty(map.get("members"))){
					stus=map.get("members").split(",");
				}
				row.createCell(18).setCellValue(stus!=null?stus[0]:"");
				row.createCell(19).setCellValue(stus!=null?stus[1]:"");
				row.createCell(20).setCellValue(stus!=null?stus[2]:"");
				row.createCell(21).setCellValue(map.get(""));
				row.createCell(22).setCellValue(map.get("id"));
				String gnodeid="";
				ActYwGnode actYwGnode=actTaskService.getNodeByProInsId(map.get("proc_ins_id"));
				if(actYwGnode!=null){
					gnodeid=actYwGnode.getId();
				}
				row.createCell(23).setCellValue(gnodeid);
				//设置样式
				for(int m=0;m<=23;m++){
					row.getCell(m).setCellStyle(rowStyle);
				}
			}
			//尾部
			XSSFCellStyle cellStyle = wb.createCellStyle();  
			XSSFFont font = wb.createFont();    
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);   
			font.setFontName("仿宋_GB2312");
			font.setFontHeightInPoints((short) 14);//设置字体大小 
			cellStyle.setFont(font);
			
			for(int k=0;k<approval_sheet0_foot.length;k++){
				sheet0.createRow(row0+k).createCell(0).setCellValue(approval_sheet0_foot[k]);
			}
			sheet0.getRow(row0).getCell(0).setCellStyle(cellStyle);
			
			for(int k=0;k<approval_sheet1_foot.length;k++){
				sheet1.createRow(row1+k).createCell(0).setCellValue(approval_sheet1_foot[k]);
			}
			sheet1.getRow(row1).getCell(0).setCellStyle(cellStyle);
			
			wb.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos!=null)fos.close();
				if (fs!=null)fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	private  void createZip(String year,String filename,String sourcePath,HttpServletResponse response) {
        OutputStream fos = null;
        ZipOutputStream zos = null;
        try {
        	String headStr = "attachment; filename=\"" + new String((year+filename+".zip") .getBytes(), "ISO-8859-1") + "\"";
        	response.setContentType("APPLICATION/OCTET-STREAM");
        	response.setHeader("Content-Disposition", headStr);
            fos = response.getOutputStream();
            zos = new ZipOutputStream(fos);
            zos.setEncoding("utf-8"); //解决linxu乱码
            writeZip(new File(sourcePath), "", zos);
        } catch (Exception e) {
            log.error("失败",e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                log.error("创建ZIP文件失败",e);
            }
        }
    }

    private void deleteDir(File dir) {
      if((dir != null) && (dir.list() != null)){
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children != null){
              for (int i=0; i<children.length; i++) {
                  deleteDir(new File(dir,children[i]));
              }
            }
        }
        dir.delete();
      }
    }

    private  void writeZip(File file, String parentPath, ZipOutputStream zos) throws IOException {
        if(file.exists()){
        	ZipEntry ze=null;
            if(file.isDirectory()){//处理文件夹
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                if(files != null){
                    for(File f: files){
                        writeZip(f, parentPath, zos);
                    }
                }else {       //空目录则创建当前目录
                        try {
                        	ze=new ZipEntry(parentPath);
                        	ze.setUnixMode(755);//解决linux乱码  文件设置644  目录设置755
                            zos.putNextEntry(ze);

                            zos.flush();
                        }finally{
                        	if (zos != null) {
                				zos.closeEntry();
                        	}
                        }
                }
            }else{
                FileInputStream fis=null;
                try {
                    fis=new FileInputStream(file);
                    ze = new ZipEntry(parentPath + file.getName());
                    ze.setUnixMode(644);//解决linux乱码  文件设置644  目录设置755
                    zos.putNextEntry(ze);
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }

                }finally{
                	if(fis!=null){
                		fis.close();
                	}
                	if (zos != null) {
        				zos.closeEntry();
                	}
                }
            }
        }
    }
}