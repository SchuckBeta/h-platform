package com.oseasy.initiate.common.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.ftp.exceptions.FtpException;
import com.oseasy.initiate.common.ftp.vo.FileVo;
import com.oseasy.initiate.common.ftp.vo.VsFile;
import com.oseasy.initiate.common.utils.FileUtil;
import com.oseasy.initiate.common.utils.StringUtil;

/**
 * Created by zhangchuansheng on 2017/7/20. 提供连接 ，上传，下载的方法
 */
public class Vsftp {
	protected FTPClient ftpclient = null;
	protected String name;

	public Vsftp(String host, int port, String username, String passwd) {
		this.connect(host, port, username, passwd);
		this.setName(new java.util.Random().nextInt(900) + 100 + "");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected FTPClient getFtpclient() {
		return ftpclient;
	}

	protected void connect(String host, int port, String username, String passwd) {
		this.ftpclient = new FTPClient();
		try {
			int reply;
			ftpclient.connect(host, port);// 连接FTP服务器
			ftpclient.login(username, passwd);// 登录
			ftpclient.setFileType(FTPClient.BINARY_FILE_TYPE);
			reply = ftpclient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpclient.disconnect();
			}
		} catch (IOException e) {
			throw new FtpException(e);
		}
	}

	protected boolean uploadFile(String remotePath, String filename, InputStream input) {
		boolean success = false;
		try {
			boolean isExist = existDirectory(remotePath);
			if (!isExist) {
				mkDirectory(remotePath);
			}
			ftpclient.changeWorkingDirectory(remotePath);
			// 设置文件名上传的编码格式为 utf-8
			ftpclient.setBufferSize(1024);
			ftpclient.setControlEncoding("utf-8");
			success = ftpclient.storeFile(new String(filename.getBytes("utf-8"), "iso-8859-1"), input);
			input.close();
		} catch (IOException e) {
			success = false;
			throw new FtpException(e);
		}
		return success;

	}

	protected boolean downFile(String remotePath, String fileName, String localPath) {
		boolean success = true;
		try {
			ftpclient.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
			FTPFile[] fs = ftpclient.listFiles();
			for (FTPFile ff : fs) {
				String remotFileName = new String(ff.getName().getBytes("iso-8859-1"), "utf-8");
				if (remotFileName.equals(fileName)) {
					FileUtil.createDirectory(localPath);
					File localFile = new File(localPath + "/" + remotFileName);
					OutputStream is = new FileOutputStream(localFile);
					success = ftpclient.retrieveFile(ff.getName(), is);
					is.close();
				}
			}
		} catch (IOException e) {
			success = false;
			throw new FtpException(e);
		}
		return success;
	}

	/**
	 * FTP批量下载文件.
	 * 
	 * @param vsFiles
	 *            文件路径列表.
	 */
	protected FileVo downFiles(List<VsFile> vsFiles) {
		FileVo fileVo = null;
		OutputStream is = null;
		try {
			List<File> suFiles = Lists.newArrayList();
			List<File> falFiles = Lists.newArrayList();
			for (VsFile vsFile : vsFiles) {
				ftpclient.changeWorkingDirectory(vsFile.getRemotePath());// 转移到FTP服务器目录
				FTPFile[] fs = ftpclient.listFiles();
				for (FTPFile ff : fs) {
					String remotFileName = new String(ff.getName().getBytes("iso-8859-1"), "utf-8");
					if (remotFileName.equals(vsFile.getRfileName())) {
						FileUtil.createDirectory(vsFile.getLocalPath());
						File localFile = new File(vsFile.getLocalPath() + "/" + vsFile.getLfileName());
						is = new FileOutputStream(localFile);
						if (ftpclient.retrieveFile(ff.getName(), is)) {
							suFiles.add(localFile);
						} else {
							falFiles.add(localFile);
						}
						if (is != null) {
							is.close();
						}
					}
				}
			}
			if ((falFiles == null) || (falFiles.size() <= 0)) {
				fileVo = new FileVo(FileVo.SUCCESS, suFiles);
			} else if ((suFiles == null) || (suFiles.size() <= 0)) {
				fileVo = new FileVo(FileVo.FAIL, falFiles);
			} else {
				fileVo = new FileVo(suFiles, falFiles);
			}
		} catch (IOException e) {
			fileVo = new FileVo(FileVo.FAIL);
			throw new FtpException(e);
		}
		return fileVo;
	}

	protected boolean downFileWithName(HttpServletRequest request, HttpServletResponse response, String name,
			String realName, String path){
		OutputStream os = null;
		try {
			ftpclient.changeWorkingDirectory(path);
			name = FileUtil.dealBrowserChina(request, name);
			response = FileUtil.dealFileHeader(response, null, null, name);
			os = response.getOutputStream();
			ftpclient.retrieveFile(realName, os);
			os.flush();
		} catch (Exception e) {
			throw new FtpException(e);
		} finally {
			if (os != null){
				try {
					os.close();
				} catch (IOException e) {
					throw new FtpException(e);
				}
			}
		}
		return true;
	}

	/**
	 * @param remotePath
	 *            要删除的文件所在ftp的路径名不包含ftp地址
	 * @param fileName
	 *            要删除的文件名
	 * @return
	 * @throws IOException
	 */
	protected boolean removeFile(String remotePath, String fileName) {
		// TODO 中文名的文件 无法删除
		boolean result = false;
		if (!ftpclient.isConnected()) {
			return result;
		}
		try {
			// ftpclient.enterLocalPassiveMode();//设置ftp为被动模式
			ftpclient.changeWorkingDirectory(remotePath);
			result = ftpclient.deleteFile(fileName);// 删除远程文件
		} catch (IOException e) {
			result = false;
			throw new FtpException("连接ftp服务器失败！", e);
		}

		return result;
	}

	/*
	 * private boolean cd(String dir) throws IOException { if
	 * (ftpclient.changeWorkingDirectory(dir)) { return true; } else { return
	 * false; } }
	 */

	private boolean existDirectory(String path) {
		boolean flag = false;
		FTPFile[] listFiles;
		try {
			String localpath = ftpclient.printWorkingDirectory();
			listFiles = listFiles(localpath);
		} catch (Exception e) {
			return false;
		}
		if (listFiles == null)
			return false;
		for (FTPFile ffile : listFiles) {
			boolean isFile = ffile.isFile();
			if (!isFile) {
				if (ffile.getName().equalsIgnoreCase(path)) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	protected boolean rename(String from, String to) {
		boolean renameResult = false;
		try {
			renameResult = ftpclient.rename(from, to);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return renameResult;
	}

	private FTPFile[] listFiles(String fileName) throws IOException {
		FTPFile[] files = null;
		if (StringUtil.isNotEmpty(fileName)) {
			files = ftpclient.listFiles(fileName);
			if (files != null && files.length != 1) {
				// 如果没有不能下载文件，再用被动模式试一次（lidahu）
				ftpclient.enterLocalPassiveMode();
				files = ftpclient.listFiles(fileName);
			}
		} else {
			files = ftpclient.listFiles();
			if (files == null || files.length == 0) { // 以被动模式再试一次
				ftpclient.enterLocalPassiveMode();
				files = ftpclient.listFiles();
			}
		}
		return files;
	}

	private void mkDirectory(String path) {
		String[] ss = path.split("/");
		try {
			ftpclient.changeWorkingDirectory("/");
			for (String s : ss) {
				ftpclient.mkd(s);
				ftpclient.changeWorkingDirectory(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 移动文件从临时目录到正式目录
	protected String moveFile(String tmpPath) throws IOException {
		String realPath = tmpPath.replace("/temp", "");
		// 得到文件名后缀，用id到名称保存。
		String realDirectory = realPath.substring(0, realPath.lastIndexOf("/"));

		// 如果没有最终的文件夹目录则创建最终的文件夹目录
		boolean isExist = existDirectory(realDirectory);
		if (!isExist) {
			mkDirectory(realDirectory);
		}
		// 移出该文件
		ftpclient.rename(tmpPath, realPath);
		return realPath;
	}

}
