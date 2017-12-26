package com.hch.platform.pcore.modules.ftp.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hch.platform.pcore.common.ftp.VsFtpPool;
import com.hch.platform.pcore.common.ftp.VsftpUtils;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;

import net.sf.json.JSONObject;

@Service
@Transactional(readOnly = true)
public class UeditorUploadService {
	private Logger logger = LoggerFactory.getLogger(UeditorUploadService.class);
	@Autowired
	private VsFtpPool vsFtpPool;
	@Autowired
	private SysAttachmentService sysAttachmentService;

	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public JSONObject uploadTempBiz(HttpServletRequest request, HttpServletResponse response) {
		InputStream in = null;
		JSONObject obj = new JSONObject();
		try {
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
			String remotePath = "/tool/oseasy/temp/" + ftpPath;
			in = imgFile1.getInputStream();
			boolean res = VsftpUtils.uploadFile(remotePath, saveFileName, in);
			if (res) {
				obj.put("state", "SUCCESS");// 上传成功
				obj.put("original", filename);
				obj.put("size", size);
				obj.put("title", urlFileName);
				obj.put("type", suffix);
				String param = "?fileSize=" + size + "&fielTitle=" + urlFileName + "&fielType=" + suffix;
				obj.put("url", FtpUtil.ftpImgUrl("/tool/oseasy/temp/" + ftpPath + "/" + saveFileName) + param);
				obj.put("ftpUrl", "/tool/oseasy/temp/" + ftpPath + "/" + saveFileName);
			}
			return obj;
		} catch (Exception e) {
			logger.error(e.getMessage());
			obj.put("state", "FAIL");// 上传成功
			return obj;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * 上传到正式目录，成功返回SysAttachment 失败返回null
	 *
	 * @param request
	 *            传参upfile 文件input的name;folder 文件子目录
	 * @return
	 */
	public SysAttachment upload(HttpServletRequest request) {
		InputStream in = null;
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultipartFile imgFile1 = multipartRequest.getFile("upfile"); // 文件
			String urlFileName = imgFile1.getOriginalFilename();
			// 得到文件名后缀，用id到名称保存。.
			String suffix = urlFileName.substring(urlFileName.lastIndexOf(".") + 1);
			String ftpId = IdGen.uuid();
			String saveFileName = ftpId + "." + suffix;
			String folder = request.getParameter("folder");
			if (StringUtil.isBlank(folder)) {
				folder = "default";
			}
			String ftpPath = folder + "/" + DateUtil.getDate("yyyy-MM-dd");
			long size = imgFile1.getSize();
			String remotePath = "/tool/oseasy/" + ftpPath;
			in = imgFile1.getInputStream();
			boolean res = VsftpUtils.uploadFile(remotePath, saveFileName, in);
			if (res) {
				SysAttachment sa = new SysAttachment();
				sa.setSize(size + "");
				sa.setName(urlFileName);
				sa.setSuffix(suffix);
				sa.setUrl("/tool/oseasy/" + ftpPath + "/" + saveFileName);
				return sa;
			}
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}

	}

	@Transactional(readOnly = false)
	public boolean delFile(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		if (StringUtil.isNotEmpty(id)) {
			SysAttachment sysAttachment = new SysAttachment();
			sysAttachment.setId(id);
			sysAttachmentService.delete(sysAttachment);
		}
		String url = (String) request.getParameter("url");//
		return delFile(url);
	}

	@Transactional(readOnly = false)
	public boolean delFile(String url) {
		String remotePath = url.substring(0, url.lastIndexOf("/") + 1);
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		return VsftpUtils.removeFile(remotePath, fileName);
	}

	public VsFtpPool getVsfPool() {
		return vsFtpPool;
	}
}
