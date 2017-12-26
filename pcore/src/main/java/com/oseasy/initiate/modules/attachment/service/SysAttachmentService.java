package com.oseasy.initiate.modules.attachment.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.ftp.VsftpUtils;
import com.oseasy.initiate.common.ftp.exceptions.FtpException;
import com.oseasy.initiate.common.ftp.vo.FileVo;
import com.oseasy.initiate.common.ftp.vo.VsFile;
import com.oseasy.initiate.common.persistence.AttachMentEntity;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.FileUtil;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.attachment.dao.SysAttachmentDao;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.exception.FileDealException;
import com.oseasy.initiate.modules.ftp.service.UeditorUploadService;
import com.oseasy.initiate.modules.sys.entity.User;

/**
 * 附件信息表Service
 * 
 * @author zy
 * @version 2017-03-23
 */
@Service
@Transactional(readOnly = true)
public class SysAttachmentService extends CrudService<SysAttachmentDao, SysAttachment> {
	public static Logger logger = Logger.getLogger(SysAttachmentService.class);
	/**
	 * 删除所有根据查询条件得到的
	 * 
	 * @param s
	 */
	@Transactional(readOnly = false)
	public void deleteByCdnNotInSet(SysAttachment s, Set<String> set) {
		List<SysAttachment> list = dao.getFilesNotInSet(s, set);
		dao.deleteByCdnNotInSet(s, set);
		if (list != null && list.size() > 0) {
			for (SysAttachment sa : list) {
				if (StringUtil.isNotEmpty(sa.getUrl())) {
					VsftpUtils.removeFile(sa.getRemotePath(), sa.getFileName());
				}
			}
		}
	}

	/**
	 * 删除所有根据查询条件得到的
	 * 
	 * @param s
	 */
	@Transactional(readOnly = false)
	public void deleteByCdn(SysAttachment s) {
		List<SysAttachment> list = dao.getFiles(s);
		dao.deleteByCdn(s);
		if (list != null && list.size() > 0) {
			
			for (SysAttachment sa : list) {
				if (StringUtil.isNotEmpty(sa.getUrl())) {
					VsftpUtils.removeFile(sa.getRemotePath(), sa.getFileName());
				}
			}
		}
	}

	public List<SysAttachment> getFiles(SysAttachment s) {
		List<SysAttachment> list = dao.getFiles(s);
		return list;
	}

	public List<Map<String, String>> getFileInfo(Map<String, String> map) {
		List<Map<String, String>> list = dao.getFileInfo(map);
		for (Map<String, String> m : list) {
			String extname = m.get("suffix").toLowerCase();
			switch (extname) {
			case "xls":
			case "xlsx":
				extname = "excel";
				break;
			case "doc":
			case "docx":
				extname = "word";
				break;
			case "ppt":
			case "pptx":
				extname = "ppt";
				break;
			case "jpg":
			case "jpeg":
			case "gif":
			case "png":
			case "bmp":
				extname = "image";
				break;
			case "rar":
			case "zip":
			case "txt":
			case "project":
				break;
			default:
				extname = "unknow";
			}
			m.put("imgType", extname);
		}
		return list;
	}

	public SysAttachment get(String id) {
		return super.get(id);
	}

	public List<SysAttachment> findList(SysAttachment sysAttachment) {
		return super.findList(sysAttachment);
	}

	public Page<SysAttachment> findPage(Page<SysAttachment> page, SysAttachment sysAttachment) {
		return super.findPage(page, sysAttachment);
	}

	@Transactional(readOnly = false)
	public void save(SysAttachment sysAttachment) {
		super.save(sysAttachment);
	}

	/**
	 * @param content
	 *            需要处理的内容,注意需要未转义的内容,注意处理ftp的http占位字符串(存进数据库的ftp链接头要用FtpUtil.
	 *            FTP_MARKER替换)
	 * @param attachMentUid
	 * @param attachMentType
	 * @param attachMentFileStep
	 * @return map key:ret,content ret=0代表无url需要处理，1代表有url被处理，content为处理后的内容
	 */
	@Transactional(readOnly = false)
	public Map<String, String> moveAndSaveTempFile(String content, String attachMentUid, FileTypeEnum attachMentType,
			FileStepEnum attachMentFileStep) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			int tag = 0;// 判断是否存在需要处理的新增ftp链接 （含有/temp/的）
			Set<String> set = getFtpFileUrl(content);// 获取所有ftp链接
			SysAttachment sa = new SysAttachment();
			sa.setUid(attachMentUid);
			sa.setType(attachMentType);
			sa.setFileStep(attachMentFileStep);
			deleteByCdnNotInSet(sa, getOldFileUrl(set));// 删除所有不在content内容里的文件（ftp和数据库）
			if (set != null && set.size() > 0) {
				
				for (String tempstr : set) {
					if (tempstr.contains("/temp/")) {// 只需要处理新增的temp链接
						tag++;
						String tempurl = tempstr.split("\\?")[0];
						String tempparam = tempstr.split("\\?")[1];
						String url = VsftpUtils.moveFile("/tool" + tempurl.replace(FtpUtil.FTP_HTTPURL, ""));
						SysAttachment temsa = new SysAttachment();
						temsa.setUid(attachMentUid);
						temsa.setType(attachMentType);
						temsa.setFileStep(attachMentFileStep);
						temsa.setSize(tempparam.split("&")[0].split("=")[1]);
						temsa.setName(tempparam.split("&")[1].split("=")[1]);
						temsa.setSuffix(tempparam.split("&")[2].split("=")[1]);
						temsa.setUrl(url);
						save(temsa);
						content = content.replaceAll(escapeExprSpecialWord(tempstr),
								"/ftp/ueditorUpload/downFile?url=" + url);
					}
				}
			}
			if (tag == 0) {
				map.put("ret", "0");
			} else {
				map.put("ret", "1");
			}
			map.put("content", content);
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Set<String> getOldFileUrl(Set<String> set) {
		Set<String> oldset = new HashSet<String>();
		for (String tempstr : set) {
			if (!tempstr.contains("/temp/")) {
				oldset.add("/tool" + tempstr.split("\\?")[0].replace(FtpUtil.FTP_HTTPURL, ""));
			}
		}
		return oldset;
	}

	private Set<String> getFtpFileUrl(String content) {
		Set<String> set = new HashSet<String>();
		String regxpForTag = "(['\"])(" + FtpUtil.FTP_HTTPURL + "(.(?!\\1))*.)\\1";
		Pattern patternForTag = Pattern.compile(regxpForTag, Pattern.CASE_INSENSITIVE);
		Matcher matcherForTag = patternForTag.matcher(content);
		while (matcherForTag.find()) {
			set.add(matcherForTag.group(2));
		}
		return set;
	}

	/**
	 * @param vo
	 *            附件信息实体
	 * @param attachMentUid
	 *            附件关联业务表id
	 * @param attachMentType附件类型
	 * @param attachMentFileStep附件子类型
	 * @return
	 */
	@Transactional(readOnly = false)
	public SysAttachment saveByVoForSingle(AttachMentEntity vo, String attachMentUid, FileTypeEnum attachMentType,
			FileStepEnum attachMentFileStep) {
		try {
			if (vo != null && vo.getFielFtpUrl() != null && vo.getFielFtpUrl().size() > 0) {
				
				SysAttachment sa = new SysAttachment();
				sa.setUid(attachMentUid);
				sa.setName(
						URLDecoder.decode(URLDecoder.decode(vo.getFielTitle().get(0), FileUtil.UTF_8), FileUtil.UTF_8));
				sa.setGnodeId(vo.getGnodeId());
				sa.setSuffix(vo.getFielType().get(0));
				sa.setSize(vo.getFielSize().get(0));
				sa.setType(attachMentType);
				sa.setFileStep(attachMentFileStep);
				sa.setUrl(VsftpUtils.moveFile(vo.getFielFtpUrl().get(0)));
				deleteByCdn(sa);
				save(sa);
				return sa;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Transactional(readOnly = false)
	public Map<String, SysAttachment> saveByVo(AttachMentEntity vo, String attachMentUid) {
		Map<String, SysAttachment> map = new HashMap<String, SysAttachment>();
		try {
			if (vo != null && vo.getFielFtpUrl() != null && vo.getFielFtpUrl().size() > 0) {
				
				for (int i = 0; i < vo.getFielFtpUrl().size(); i++) {
					SysAttachment sa = new SysAttachment();
					sa.setUid(attachMentUid);
					sa.setName(URLDecoder.decode(URLDecoder.decode(vo.getFielTitle().get(i), FileUtil.UTF_8),
							FileUtil.UTF_8));
					sa.setGnodeId(vo.getGnodeId());
					sa.setSuffix(vo.getFielType().get(i));
					sa.setSize(vo.getFielSize().get(i));
					sa.setType(FileTypeEnum.getByValue(vo.getFileTypeEnum().get(i)));
					sa.setFileStep(FileStepEnum.getByValue(vo.getFileStepEnum().get(i)));
					sa.setUrl(VsftpUtils.moveFile(vo.getFielFtpUrl().get(i)));
					save(sa);
					map.put(vo.getFielFtpUrl().get(i), sa);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return map;
	}

	/**
	 * @return Map key-临时目录url，value-正式目录url
	 */
	@Transactional(readOnly = false)
	public Map<String, SysAttachment> saveByVo(AttachMentEntity vo, String attachMentUid, FileTypeEnum attachMentType,
			FileStepEnum attachMentFileStep) {
		Map<String, SysAttachment> map = new HashMap<String, SysAttachment>();
		try {
			if (vo != null && vo.getFielFtpUrl() != null && vo.getFielFtpUrl().size() > 0) {
				
				for (int i = 0; i < vo.getFielFtpUrl().size(); i++) {
					SysAttachment sa = new SysAttachment();
					sa.setUid(attachMentUid);
					sa.setName(URLDecoder.decode(URLDecoder.decode(vo.getFielTitle().get(i), FileUtil.UTF_8),
							FileUtil.UTF_8));
					sa.setGnodeId(vo.getGnodeId());
					sa.setSuffix(vo.getFielType().get(i));
					sa.setSize(vo.getFielSize().get(i));
					sa.setType(attachMentType);
					sa.setFileStep(attachMentFileStep);
					sa.setUrl(VsftpUtils.moveFile(vo.getFielFtpUrl().get(i)));
					save(sa);
					map.put(vo.getFielFtpUrl().get(i), sa);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return map;
	}

	@Transactional(readOnly = false)
	public void delete(SysAttachment sysAttachment) {
		super.delete(sysAttachment);
	}

	@Transactional(readOnly = false)
	public void saveList(List<Map<String, String>> maps, FileTypeEnum type, FileStepEnum fileStep, String uid) {
		try {
			if (maps != null) {
				
				for (Map<String, String> map : maps) {
					SysAttachment sa = new SysAttachment();
					sa.setUid(uid);
					sa.setName(map.get("arrName"));
					String[] ss = map.get("arrName").split("\\.");
					sa.setSuffix(ss[ss.length - 1]);
					sa.setType(type);
					sa.setFileStep(fileStep);
					sa.setUrl(VsftpUtils.moveFile(map.get("arrUrl")));
					super.save(sa);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 下载附件到指定目录.
	 * 
	 * @param uids
	 *            项目ID
	 * @param fileSteps
	 *            步骤
	 * @param distDir
	 *            存放目录
	 * @return FileVo
	 */
	public FileVo downloads(FileTypeEnum proType, List<String> uids, List<String> fileSteps, String distDir) {
		List<VsFile> vsFiles = Lists.newArrayList();
		SysAttachment psysAtt = new SysAttachment();
		if ((uids == null) || (proType == null)) {
			return new FileVo(FileVo.FAIL);
		}
		psysAtt.setType(proType);
		psysAtt.setUids(uids);
		if (fileSteps != null) {
			psysAtt.setFileSteps(fileSteps);
		}
		List<SysAttachment> sysAtts = dao.findListInIds(psysAtt);
		for (SysAttachment sysAtt : sysAtts) {
			User cuser = sysAtt.getCreateBy();
			if ((cuser != null) && (cuser.getOffice() != null)) {
				VsFile vsFile = new VsFile();
				vsFile.setRemotePath(sysAtt.getRemotePath());
				vsFile.setRfileName(sysAtt.getFileName());
				vsFile.setLocalPath(distDir + FileUtil.LINE + sysAtt.getFileStep().getName() + FileUtil.LINE
						+ cuser.getOffice().getName());
				vsFile.setLfileName(sysAtt.getName(cuser.getName() + cuser.getNo()));
				vsFiles.add(vsFile);
			}
		}
		return VsftpUtils.downFiles(vsFiles);
	}

	public SysAttachment getByUrl(String url) {
		return dao.getByUrl(url);
	}

	public void updateAtt(String gId, String oldGidId) {
		dao.updateAtt(gId, oldGidId);
	}

	public static String escapeExprSpecialWord(String keyword) {
		if (StringUtils.isNotEmpty(keyword)) {
			String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
			for (String key : fbsArr) {
				if (keyword.contains(key)) {
					keyword = keyword.replace(key, "\\" + key);
				}
			}
		}
		return keyword;
	}

	/**
	 * 查询流程节点附件附件.
	 * 
	 * @param sysAttachment
	 *            查询条件
	 * @param fstep
	 *            附件子类别
	 * @param request
	 * @param response
	 * @param model
	 * @return List
	 */
	@Transactional(readOnly = false)
	public List<SysAttachment> ajaxIcons(SysAttachment sysAttachment, String fstep, HttpServletRequest request,
			HttpServletResponse response) {
		/**
		 * 查询条件.
		 */
		sysAttachment.setType(FileTypeEnum.S_FLOW_ICON);
		List<String> fsteps = Lists.newArrayList();
		if (StringUtil.isNotEmpty(fstep)) {
			fsteps.add(fstep);
		} else {
			fsteps.add(FileStepEnum.S_FLOW_NODELV1.getValue());
			fsteps.add(FileStepEnum.S_FLOW_NODELV2.getValue());
		}

		return findList(sysAttachment);
	}

	/**
	 * 获取根据UID附件.
	 * 
	 * @param uid
	 *            业务ID
	 * @param type
	 *            附件类型
	 * @param fstep
	 *            附件子类别
	 * @param fsteps
	 *            子类别集合，逗号分隔
	 * @param gnodeId
	 *            节点ID
	 * @return ActYwRstatus
	 */
	@Transactional(readOnly = false)
	public ActYwRstatus<List<SysAttachment>> ajaxFiles(String uid, String type, String fstep, String fsteps,
			String gnodeId) {
		if (StringUtil.isEmpty(type)) {
			return new ActYwRstatus<List<SysAttachment>>(false, "获取失败, type不能为空！");
		}

		FileTypeEnum fileTypeEnum = FileTypeEnum.getByValue(type);
		if (fileTypeEnum == null) {
			return new ActYwRstatus<List<SysAttachment>>(false, "获取失败, type未定义！");
		}

		SysAttachment sysAttachment = new SysAttachment(uid, fileTypeEnum);
		if (StringUtil.isNotEmpty(fstep)) {
			FileStepEnum fileStepEnum = FileStepEnum.getByValue(fstep);
			if (fileStepEnum != null) {
				sysAttachment.setFileStep(fileStepEnum);
			}
		}

		if (StringUtil.isNotEmpty(fsteps)) {
			List<String> fstepsList = Arrays.asList(StringUtil.split(fsteps, StringUtil.DOTH));
			if (fstepsList.size() > 0) {
				sysAttachment.setFileSteps(fstepsList);
			}
		}

		if (StringUtil.isNotEmpty(gnodeId)) {
			sysAttachment.setGnodeId(gnodeId);
		}
		List<SysAttachment> sysAttachments = findList(sysAttachment);
		if ((sysAttachments == null) || (sysAttachments.size() <= 0)) {
			return new ActYwRstatus<List<SysAttachment>>(true, "查询结果为空！");
		}
		return new ActYwRstatus<List<SysAttachment>>(true, "查询成功！", sysAttachments);
	}

	/**
	 * 异步上传附件. 默认移动,传false表示不移动，需要手动移动
	 * 
	 * @param ftype
	 *            类型
	 * @param fileStep
	 *            类别
	 * @param uid
	 *            业务ID
	 * @param isMove
	 *            是否移动
	 * @param fileName
	 *            文件名
	 * @param request
	 *            请求
	 * @param model
	 *            模型
	 * @return Rstatus
	 * @throws IOException
	 */
	@Transactional(readOnly = false)
	public ActYwRstatus<SysAttachment> ajaxUpload(String ftype, String fileStep, String uid, Boolean isMove,
			String fileName, UeditorUploadService ueditorUploadService, HttpServletRequest request) throws IOException {
		if (StringUtil.isEmpty(ftype) || StringUtil.isEmpty(fileStep)) {
			return new ActYwRstatus<SysAttachment>(false, "上传失败, type和fileStep不能为空！");
		}

		FileTypeEnum fileTypeEnum = FileTypeEnum.getByValue(ftype);
		FileStepEnum fileStepEnum = FileStepEnum.getByValue(fileStep);
		if ((fileTypeEnum == null)) {
			return new ActYwRstatus<SysAttachment>(false, "上传失败, type类型未定义！");
		}
		if ((fileStepEnum == null)) {
			return new ActYwRstatus<SysAttachment>(false, "上传失败, fileStep类型未定义！");
		}

		SysAttachment newsysAttachment = ueditorUploadService.upload(request);
		if (newsysAttachment == null) {
			return new ActYwRstatus<SysAttachment>(false, "上传失败！");
		}

		if (StringUtil.isNotEmpty(uid)) {
			newsysAttachment.setUid(uid);
		} else {
			newsysAttachment.setUid(IdGen.uuid());
		}

		if (StringUtil.isNotEmpty(fileName)) {
			newsysAttachment.setName(fileName);
		}

		/**
		 * 移动附件到目录. 默认移动,传false表示不移动，需要手动移动
		 */
		if ((isMove == null) || isMove) {
			String url = VsftpUtils.moveFile(newsysAttachment.getUrl());
			if (StringUtil.isEmpty(url)) {
				throw new FileDealException();
			}
			newsysAttachment.setUrl(url);
		}

		newsysAttachment.setType(fileTypeEnum);
		newsysAttachment.setFileStep(fileStepEnum);
		save(newsysAttachment);

		return new ActYwRstatus<SysAttachment>(true, "上传成功！", newsysAttachment);
	}

	/**
	 * 异步删除附件.
	 * 
	 * @param fileUrl
	 *            附件地址
	 * @param id
	 *            类型
	 * @return ActYwRstatus
	 */
	@Transactional(readOnly = false)
	public ActYwRstatus<SysAttachment> ajaxDelete(String fileUrl, String id, UeditorUploadService ueditorUploadService,
			HttpServletRequest request, HttpServletResponse response) {
		if (StringUtil.isNotEmpty(id)) {
			SysAttachment sysAttachment = get(id);
			if ((sysAttachment == null)) {
				return new ActYwRstatus<SysAttachment>(false, "删除失败, 附件记录不存在！");
			} else {
				delete(sysAttachment);
			}

			if (StringUtil.isEmpty(fileUrl)) {
				fileUrl = sysAttachment.getUrl();
			}
		}

		boolean isDel = false;
		if (StringUtil.isNotEmpty(fileUrl)) {
			isDel = ueditorUploadService.delFile(fileUrl);
		}
		if (isDel) {
			return new ActYwRstatus<SysAttachment>(true, "删除成功！", new SysAttachment(id));
		} else {
			throw new FileDealException();
		}
	}

	/**
	 * 异步移动附件.
	 * 
	 * @param id
	 *            类型
	 * @return ActYwRstatus
	 * @throws IOException
	 * @throws FtpException
	 */
	@Transactional(readOnly = false)
	public ActYwRstatus<SysAttachment> ajaxMoveFtpFile(String id, HttpServletRequest request,
			HttpServletResponse response) throws FtpException, IOException {
		if (StringUtil.isEmpty(id)) {
			return new ActYwRstatus<SysAttachment>(false, "删除失败, 附件记录ID不能为空！");
		}

		SysAttachment sysAttachment = get(id);
		if ((sysAttachment == null)) {
			return new ActYwRstatus<SysAttachment>(false, "删除失败, 附件记录不存在！");
		}

		String url = VsftpUtils.moveFile(sysAttachment.getUrl());
		if (StringUtil.isEmpty(url)) {
			throw new FileDealException();
		}
		sysAttachment.setUrl(url);
		save(sysAttachment);
		return new ActYwRstatus<SysAttachment>(true, "移动成功！", sysAttachment);

	}
}