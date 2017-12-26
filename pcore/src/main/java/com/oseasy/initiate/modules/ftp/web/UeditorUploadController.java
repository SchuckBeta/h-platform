package com.oseasy.initiate.modules.ftp.web;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.oseasy.initiate.common.ftp.VsftpUtils;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FileUtil;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.ftp.service.UeditorUploadService;

import net.sf.json.JSONObject;

/**
 * Created by zhangzheng on 2017/6/26.
 */
@Controller
public class UeditorUploadController extends BaseController {
	@Autowired
	private UeditorUploadService ueditorUploadService;
	@Autowired
	private SysAttachmentService sysAttachmentService;
	
	// 富文本文件上传
	@RequestMapping(value = "/ftp/ueditorUpload/upload")
	@ResponseBody
	public JSONObject upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject obj = new JSONObject();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile imgFile1 = multipartRequest.getFile("upfile"); // 文件
		String urlFileName = imgFile1.getOriginalFilename();
		// 得到文件名后缀，用id到名称保存。.
		String filename = urlFileName.substring(0, urlFileName.lastIndexOf(".")); // 文件名
		String suffix = urlFileName.substring(urlFileName.lastIndexOf(".") + 1);
		String ftpId = IdGen.uuid();
		String saveFileName = ftpId + "." + suffix;
		String folder = request.getParameter("folder");
		if (StringUtil.isBlank(folder)) {
			folder = "ueditor";
		}
		String ftpPath = folder + "/" + DateUtil.getDate("yyyy-MM-dd");
		long size = imgFile1.getSize();
		String remotePath = "/tool/oseasy/" + ftpPath;
		System.out.println(("上传路径......：" + remotePath));
		boolean res = VsftpUtils.uploadFile(remotePath, saveFileName, imgFile1.getInputStream());
		System.out.println(("上传结果......：" + res));
		if (res) {
			obj.put("state", "SUCCESS");// 上传成功
			obj.put("original", filename);
			obj.put("size", size);
			obj.put("title", urlFileName);
			obj.put("type", suffix);
			obj.put("url", FtpUtil.ftpImgUrl("/tool/oseasy/" + ftpPath + "/" + saveFileName));
			obj.put("ftpUrl", "/tool/oseasy/" + ftpPath + "/" + saveFileName);
		}
		return obj;
	}

	// 文本上传到临时目录
	@RequestMapping(value = "/ftp/ueditorUpload/uploadTemp")
	@ResponseBody
	public JSONObject uploadTemp(HttpServletRequest request, HttpServletResponse response){
		return ueditorUploadService.uploadTempBiz(request, response);
	}

	// 上传图片到临时目录
	@RequestMapping(value = "/ftp/ueditorUpload/uploadImg")
	@ResponseBody
	public JSONObject uploadImg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject obj = new JSONObject();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile imgFile1 = multipartRequest.getFile("upfile"); // 文件
		String urlFileName = imgFile1.getOriginalFilename();

		// 判断图片大小
		String imgWidth = (String) request.getParameter("imgWidth"); // 宽度标准
		String imgHeight = (String) request.getParameter("imgHeight"); // 高度标准

		BufferedImage bi = ImageIO.read(imgFile1.getInputStream());
		String realWidth = bi.getWidth() + "";
		String realHeight = bi.getHeight() + "";
		if (!StringUtil.equals(imgWidth, realWidth) && !StringUtil.equals(imgHeight, realHeight)) {
			obj.put("state", "false");
			obj.put("msg", "图片长 X 宽应为(" + imgHeight + "px X " + imgWidth + "px)");
		} else {
			// 得到文件名后缀，用id到名称保存。.
			String filename = urlFileName.substring(0, urlFileName.lastIndexOf(".")); // 文件名
			String suffix = urlFileName.substring(urlFileName.lastIndexOf(".") + 1);
			String ftpId = IdGen.uuid();
			String saveFileName = ftpId + "." + suffix;
			String folder = request.getParameter("folder");
			if (StringUtil.isBlank(folder)) {
				folder = "ueditor";
			}
			String ftpPath = folder + "/" + DateUtil.getDate("yyyy-MM-dd");
			long size = imgFile1.getSize();
			String remotePath = "/tool/oseasy/temp/" + ftpPath;
			boolean res = VsftpUtils.uploadFile(remotePath, saveFileName, imgFile1.getInputStream());

			if (res) {
				obj.put("state", "SUCCESS");// 上传成功
				obj.put("original", filename);
				obj.put("size", size);
				obj.put("title", urlFileName);
				obj.put("type", suffix);
				obj.put("url", FtpUtil.ftpImgUrl("/tool/oseasy/temp/" + ftpPath + "/" + saveFileName));
				obj.put("ftpUrl", "/tool/oseasy/temp/" + ftpPath + "/" + saveFileName);
				obj.put("width", bi.getWidth());
				obj.put("height", bi.getHeight());
			}
		}
		return obj;
	}

	// 文件删除
	@RequestMapping(value = "/ftp/ueditorUpload/delFile")
	@ResponseBody
	public boolean delFile(HttpServletRequest request, HttpServletResponse response) {
		return ueditorUploadService.delFile(request, response);
	}

	// 文件下载
	@RequestMapping(value = "/ftp/ueditorUpload/downFile")
	public void downFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String url =request.getParameter("url");
		if(StringUtil.isNotEmpty(url)){
				String fileName=request.getParameter(FileUtil.FILE_NAME);
				if(StringUtil.isEmpty(fileName)){
					SysAttachment sa=sysAttachmentService.getByUrl(url);
						if(sa!=null){
						fileName = URLDecoder.decode(URLDecoder.decode(sa.getName(), FileUtil.UTF_8),FileUtil.UTF_8);
					}
				}else{
					fileName = URLDecoder.decode(URLDecoder.decode(fileName, FileUtil.UTF_8),FileUtil.UTF_8);
				}
				if(StringUtil.isEmpty(fileName)){
					fileName="未知的文件";
				}
				String realName = url.substring(url.lastIndexOf("/") + 1);
				String path = url.substring(0, url.lastIndexOf("/") + 1);
				VsftpUtils.downFileWithName(request, response, fileName, realName, path);
		}
	}

	// 图片裁剪
	@RequestMapping(value = "/ftp/ueditorUpload/cutImg")
	@ResponseBody
	public JSONObject cutImg(HttpServletRequest request, int x, int y, int width, int height) throws Exception {
		JSONObject obj = new JSONObject();
		if ((x < 0) || (y < 0) || (width < 0) || (height < 0)) {
			obj.put("state", false);// 上传失败
			obj.put("msg", "参数值不能小于0");//
			return obj;
		}
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile imgFile1 = multipartRequest.getFile("upfile"); // 文件
		String urlFileName = imgFile1.getOriginalFilename();
		// 得到文件名后缀，用id到名称保存。.
		String filename = urlFileName.substring(0, urlFileName.lastIndexOf(".")); // 文件名
		String suffix = urlFileName.substring(urlFileName.lastIndexOf(".") + 1);
		String ftpId = IdGen.uuid();
		String saveFileName = ftpId + "." + suffix;
		String folder = request.getParameter("folder");
		if (StringUtil.isBlank(folder)) {
			folder = "ueditor";
		}
		String ftpPath = folder + "/" + DateUtil.getDate("yyyy-MM-dd");
		long size = imgFile1.getSize();
		String remotePath = "/tool/oseasy/" + ftpPath;

		/** 读取图片 */
		Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(suffix);
		ImageReader reader = it.next();

		/** 获取图片流 */
		InputStream is = imgFile1.getInputStream();
		ImageInputStream iis = ImageIO.createImageInputStream(is);

		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, width, height);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(bi, suffix, os);
		InputStream is2 = new ByteArrayInputStream(os.toByteArray());

		boolean res = VsftpUtils.uploadFile(remotePath, saveFileName, is2);

		if (res) {
			obj.put("state", "SUCCESS");// 上传成功
			obj.put("original", filename);
			obj.put("size", size);
			obj.put("title", urlFileName);
			obj.put("type", suffix);
			obj.put("url", FtpUtil.ftpImgUrl("/tool/oseasy/" + ftpPath + "/" + saveFileName));
			obj.put("ftpUrl", "/tool/oseasy/" + ftpPath + "/" + saveFileName);
		}
		return obj;
	}

	@RequestMapping(value = "/ftp/pagePdfView")
	public String pagePdfView(Model model, String url, String fileName, String ftpUrl) {
		model.addAttribute("url", url);
		model.addAttribute("fileName", fileName);
		model.addAttribute("ftpUrl", ftpUrl);
		return "modules/website/util/pagePdfView";
	}


	/**
	 * 直接上传文件，不处理文件
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ftp/ueditorUpload/normal")
	@ResponseBody
	public JSONObject normal(HttpServletRequest request) throws Exception {
		JSONObject obj = new JSONObject();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("upfile"); // 文件
		String originalFilename = multipartFile.getOriginalFilename();
		String fileName = FilenameUtils.getName(originalFilename);
		String suffix = FilenameUtils.getExtension(fileName);
		String ftpId = IdGen.uuid();
		String saveFileName = ftpId + "." + suffix;
		String folder = request.getParameter("folder");
		if (StringUtil.isBlank(folder)) {
			folder = "ueditor";
		}
		String ftpPath = folder + "/" + DateUtil.getDate("yyyy-MM-dd");
		String remotePath = "/tool/oseasy/" + ftpPath;
		boolean res = VsftpUtils.uploadFile(remotePath, saveFileName, multipartFile.getInputStream());
		if (res) {
			obj.put("state", "SUCCESS");// 上传成功
			obj.put("original", fileName);
			obj.put("size", multipartFile.getSize());
			obj.put("title", originalFilename);
			obj.put("type", suffix);
			obj.put("url", FtpUtil.ftpImgUrl(remotePath + "/" + saveFileName));
			obj.put("ftpUrl", remotePath + "/" + saveFileName);
		}
		return obj;
	}

}
