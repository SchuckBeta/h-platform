package com.oseasy.initiate.modules.authorize.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.license.License;
import com.oseasy.initiate.common.utils.license.LicenseCacheUtils;
import com.oseasy.initiate.common.utils.license.MachineCacheUtils;
import com.oseasy.initiate.common.utils.machine.HardWareUtils;
import com.oseasy.initiate.common.utils.machine.MacUtil;
import com.oseasy.initiate.common.utils.rsa.Base64;
import com.oseasy.initiate.common.utils.rsa.RSAEncrypt;
import com.oseasy.initiate.modules.authorize.entity.SysLicense;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(readOnly = true)
public class AuthorizeService {
	public static final String product_key="kaichuangla";
	public static Logger logger = Logger.getLogger(AuthorizeService.class);
	@Autowired
	private SysLicenseService sysLicenseService;
	@Transactional(readOnly = false)
	public void initInfo() {
		initSysLicenseInfo();
		putMachineInfo();
	}
	@Transactional(readOnly = false)
	public void initSysLicenseInfo() {
		try {
			SysLicense s=sysLicenseService.getLicense();
			if (s==null) {
				s=new SysLicense();
				s.setId(SysLicenseService.KEY);
				sysLicenseService.insertWithId(s);
			}
		} catch (Exception e1) {
			logger.error("初始化SysLicense出错",e1);
		}
	}
	public void putMachineInfo() {
		JSONObject js = getMachineInfo();
		try {
			if (StringUtil.isEmpty(js.getString("cpu"))) {
				logger.error("无法获取CPU信息！系统将终止启动或无法正常使用");
				SysLicenseService.unValid=true;
				System.exit(0);
			}
			if (StringUtil.isEmpty(js.getString("mac"))) {
				logger.error("无法获取MAC信息！系统将终止启动或无法正常使用");
				SysLicenseService.unValid=true;
				System.exit(0);
			}
			if (!SysLicenseService.unValid)MachineCacheUtils.put(js.getString("cpu") + js.getString("mac"), js.toString());
		} catch (Exception e) {
			logger.error("获取机器信息出错！系统将终止启动或无法正常使用",e);
			SysLicenseService.unValid=true;
			System.exit(0);
		}
	}
	public License getLicenseInfo() {
		String linfo = null;
		SysLicense o =(SysLicense) LicenseCacheUtils.get(SysLicenseService.KEY);
		if (o == null) {
			SysLicense sl = sysLicenseService.getLicense();
			if (sl == null) {
				return null;
			} else {
				LicenseCacheUtils.put(SysLicenseService.KEY, sl);
				linfo = sl.getLicense();
			}
		} else {
			linfo = o.getLicense();
		}
		if (!StringUtil.isEmpty(linfo)) {
			linfo=decrypt(linfo);
			if (linfo!=null) {
				JSONObject jp=JSONObject.fromObject(linfo);
				if (!validateLicense(linfo)) {
					jp.put("valid","0");
				}else{
					jp.put("valid","1");
				}
				jp.put("machineCode", JSONArray.fromObject(jp.get("machineCode")));
				License license=(License)JSONObject.toBean(jp, License.class);
				return license;
			}
		}
		return null;
	}
	public static JSONObject getMachineInfo() {
		String cpu = HardWareUtils.getCPUSerial();
		logger.info("cpu:"+cpu);
		String mac = MacUtil.getMACAddress();
		logger.info("mac:"+mac);
		JSONObject js = new JSONObject();
		js.put("cpu", cpu);
		js.put("mac", mac);
		return js;
	}
	@Transactional(readOnly = false)
	public JSONObject uploadFile(HttpServletRequest request) {
		JSONObject obj = new JSONObject();

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		//读取上传的文件内容
		MultipartFile imgFile1 = multipartRequest.getFile("fileName");
		InputStream is = null;
		BufferedReader reader = null;
		try {
			is = imgFile1.getInputStream();

			reader = new BufferedReader(new InputStreamReader(is));
			String tempString = null;
			StringBuffer sb = new StringBuffer();
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			String decrypt_lic=decrypt(sb.toString());//解码文件内容
			if (!validateLicense(decrypt_lic)) {
				obj.put("ret", "0");
				obj.put("msg", "无效的授权文件");
				return obj;
			} else {
				SysLicense sl=new SysLicense();
				sl.setLicense(sb.toString());
				sysLicenseService.saveLicense(sl);//保存未解码的文件内容
				LicenseCacheUtils.put(SysLicenseService.KEY, sl);//放入缓存
				obj.put("ret", "1");
				obj.put("msg", "授权成功");

				License license=getLicenseInfo();
				if (license!=null) {
					try {
						Date expiredDate=DateUtil.parseDate(license.getExpiredDate(),"yyyy-MM-dd HH:mm:ss");
						Date exp=DateUtil.addMonth(expiredDate, Integer.parseInt(license.getMonth()));
						obj.put("exp", DateUtil.formatDate(exp , "yyyy-MM-dd HH:mm:ss"));
					} catch (Exception e) {
						logger.error("错误:",e);
					}
				}

				return obj;
			}
		} catch (Exception e) {
			logger.error("无效的授权文件", e);
			obj.put("ret", "0");
			obj.put("msg", "无效的授权文件");
			return obj;
		} finally {
			try {
				is.close();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*对解码后的信息验证*/
	public static boolean validateLicense(String decrypt_lic) {
		try {
			if (decrypt_lic!=null) {
				JSONObject jp=JSONObject.fromObject(decrypt_lic);
				jp.put("machineCode", JSONArray.fromObject(jp.get("machineCode")));
				License license=(License)JSONObject.toBean(jp, License.class);
				if (!license.getProduct_key().equals(product_key)) {//验证产品key是否一致
					return false;
				}
				if ("0".equals(license.getProductType())) {//试用授权，不验证机器码
					
				}else{
					//验证机器信息是否一致
					JSONObject m=getMachineInfo();
					String cpu=m.getString("cpu");
					String mac=m.getString("mac");
					int tag=0;
					JSONArray ja=license.getMachineCode();
					for(int i=0;i<ja.size();i++) {
						JSONObject mi=ja.getJSONObject(i);
						if (cpu.equals(mi.getString("cpu"))&&mac.equals(mi.getString("mac"))) {
							tag=1;
							break;
						}
					}
					if (tag==0) {
						return false;
					}
				}
				//验证日期是否有效
				if ("0".equals(license.getMonth())) {//0无期限
				}else if (DateUtil.addMonth(DateUtil.parseDate(license.getExpiredDate(),"yyyy-MM-dd HH:mm:ss"), Integer.parseInt(license.getMonth())).before(new Date())) {
					return false;
				}
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	/*解码*/
		public String decrypt(String lic) {
			try {
				byte[] res;
				res = RSAEncrypt.decryptPlus(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(this.getClass().getResource("/publicKey.keystore").getFile())), Base64.decode(lic));
				String restr = new String(res,"UTF-8");
				return restr;
			} catch (Exception e) {
				return null;
			}
		}
}
