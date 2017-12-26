package com.oseasy.initiate.common.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.common.ftp.vo.FileVo;
import com.oseasy.initiate.common.ftp.vo.VsFile;
import com.oseasy.initiate.common.utils.SpringContextHolder;

public class VsftpUtils {
	private static VsFtpPool vsFtpPool = SpringContextHolder.getBean(VsFtpPool.class);

	public static boolean uploadFile(String remotePath, String filename, InputStream input) {
		Vsftp vs = vsFtpPool.getResource();
		boolean ret;
		try {
			ret = vs.uploadFile(remotePath, filename, input);
		} finally {
			vsFtpPool.returnResource(vs);
		}
		return ret;
	}

	public static boolean downFile(String remotePath, String fileName, String localPath) {
		Vsftp vs = vsFtpPool.getResource();
		boolean ret;
		try {
			ret = vs.downFile(remotePath, fileName, localPath);
		} finally {
			vsFtpPool.returnResource(vs);
		}
		return ret;
	}

	/**
	 * FTP批量下载文件.
	 * 
	 * @param vsFiles
	 *            文件路径列表.
	 */
	public static FileVo downFiles(List<VsFile> vsFiles) {
		Vsftp vs = vsFtpPool.getResource();
		FileVo ret;
		try {
			ret = vs.downFiles(vsFiles);
		} finally {
			vsFtpPool.returnResource(vs);
		}
		return ret;
	}

	public static boolean downFileWithName(HttpServletRequest request, HttpServletResponse response, String name, String realName, String path) {
		Vsftp vs = vsFtpPool.getResource();
		boolean ret;
		try {
			ret = vs.downFileWithName(request, response, name, realName, path);
		} finally {
			vsFtpPool.returnResource(vs);
		}
		return ret;
	}

	/**
	 * @param remotePath
	 *            要删除的文件所在ftp的路径名不包含ftp地址
	 * @param fileName
	 *            要删除的文件名
	 * @return
	 * @throws IOException
	 */
	public static boolean removeFile(String remotePath, String fileName) {
		Vsftp vs = vsFtpPool.getResource();
		boolean ret;
		try {
			ret = vs.removeFile(remotePath, fileName);
		} finally {
			vsFtpPool.returnResource(vs);
		}
		return ret;
	}


	public static boolean rename(String from, String to) {
		Vsftp vs = vsFtpPool.getResource();
		boolean ret;
		try {
			ret = vs.rename(from, to);
		} finally {
			vsFtpPool.returnResource(vs);
		}
		return ret;
	}


	// 移动文件从临时目录到正式目录
	public static String moveFile(String tmpPath) throws IOException {
		Vsftp vs = vsFtpPool.getResource();
		String ret;
		try {
			ret = vs.moveFile(tmpPath);
		} finally {
			vsFtpPool.returnResource(vs);
		}
		return ret;
	}

}
