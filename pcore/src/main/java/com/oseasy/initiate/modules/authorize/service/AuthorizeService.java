package com.oseasy.initiate.modules.authorize.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Pattern;

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
import com.oseasy.initiate.modules.authorize.enums.MenuEnum;
import com.oseasy.initiate.modules.authorize.enums.MenuPlusEnum;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(readOnly = true)
public class AuthorizeService {
	public static String cpu = null;
	public static String mac = null;
	public static final String product_key = "kaichuangla";
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
			SysLicense s = sysLicenseService.getLicense();
			if (s == null) {
				s = new SysLicense();
				s.setId(SysLicenseService.KEY);
				sysLicenseService.insertWithId(s);
			}
		} catch (Exception e1) {
			logger.error("初始化SysLicense出错", e1);
		}
	}

	public void putMachineInfo() {
		JSONObject js = getMachineInfo();
		try {
			if (StringUtil.isEmpty(js.getString("cpu"))) {
				logger.error("无法获取CPU信息！系统将终止启动或无法正常使用");
				SysLicenseService.unValid = true;
				System.exit(0);
			}
			if (StringUtil.isEmpty(js.getString("mac"))) {
				logger.error("无法获取MAC信息！系统将终止启动或无法正常使用");
				SysLicenseService.unValid = true;
				System.exit(0);
			}
			if (!SysLicenseService.unValid)
				MachineCacheUtils.put(js.getString("cpu") + js.getString("mac"), js.toString());
		} catch (Exception e) {
			logger.error("获取机器信息出错！系统将终止启动或无法正常使用", e);
			SysLicenseService.unValid = true;
			System.exit(0);
		}
	}

	public License getLicenseInfo() {
		String linfo = null;
		SysLicense o = (SysLicense) LicenseCacheUtils.get(SysLicenseService.KEY);
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
			linfo = decrypt(linfo);
			if (linfo != null) {
				JSONObject jp = JSONObject.fromObject(linfo);
				jp.put("valid", validateLicense(linfo));
				jp.put("machineCode", JSONArray.fromObject(jp.get("machineCode")));
				License license = (License) JSONObject.toBean(jp, License.class);
				return license;
			}
		}
		return null;
	}

	public static JSONObject getMachineInfo() {
		if (cpu == null) {
			cpu = HardWareUtils.getCPUSerial();
			logger.info("cpu:" + cpu);
		}
		if (mac == null) {
			mac = MacUtil.getMACAddress();
			logger.info("mac:" + mac);
		}
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
			String decrypt_lic = decrypt(sb.toString());//解码文件内容
			String vaild = validateLicense(decrypt_lic);
			if ("0".equals(vaild)) {
				obj.put("ret", "0");
				obj.put("msg", "无效的授权文件");
				return obj;
			} else if ("2".equals(vaild)) {
				obj.put("ret", "0");
				obj.put("msg", "授权文件已过期");
				return obj;
			} else if ("1".equals(vaild)) {
				SysLicense sl = new SysLicense();
				sl.setLicense(sb.toString());
				sysLicenseService.saveLicense(sl);//保存未解码的文件内容
				LicenseCacheUtils.put(SysLicenseService.KEY, sl);//放入缓存
				obj.put("ret", "1");
				obj.put("msg", "授权成功");

				License license = getLicenseInfo();
				if (license != null) {
					try {
						Date expiredDate = DateUtil.parseDate(license.getExpiredDate(), "yyyy-MM-dd HH:mm:ss");
						Date exp = DateUtil.addMonth(expiredDate, Integer.parseInt(license.getMonth()));
						obj.put("exp", DateUtil.formatDate(exp, "yyyy-MM-dd HH:mm:ss"));
					} catch (Exception e) {
						logger.error("错误:", e);
					}
				}

			}
			return obj;
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

	/*对解码后的信息验证 0-无效，1-有效，2-已过期*/
	public static String validateLicense(String decrypt_lic) {
		try {
			if (decrypt_lic != null) {
				JSONObject jp = JSONObject.fromObject(decrypt_lic);
				jp.put("machineCode", JSONArray.fromObject(jp.get("machineCode")));
				License license = (License) JSONObject.toBean(jp, License.class);
				if (!license.getProduct_key().equals(product_key)) {//验证产品key是否一致
					return "0";
				}
				if ("0".equals(license.getProductType())) {//试用授权，不验证机器码

				} else {
					//验证机器信息是否一致
					JSONObject m = getMachineInfo();
					String cpu = m.getString("cpu");
					String mac = m.getString("mac");
					int tag = 0;
					JSONArray ja = license.getMachineCode();
					for (int i = 0; i < ja.size(); i++) {
						JSONObject mi = ja.getJSONObject(i);
						if (cpu.equals(mi.getString("cpu")) && mac.equals(mi.getString("mac"))) {
							tag = 1;
							break;
						}
					}
					if (tag == 0) {
						return "0";
					}
				}
				//验证modules值是否正确
				String regex = "[01]+";
				if (StringUtil.isEmpty(license.getModules()) || !Pattern.matches(regex, license.getModules())) {
					return "0";
				}
				//验证日期是否有效
				if ("0".equals(license.getMonth())) {//0无期限
				} else if (DateUtil.addMonth(DateUtil.parseDate(license.getExpiredDate(), "yyyy-MM-dd HH:mm:ss"), Integer.parseInt(license.getMonth())).before(new Date())) {
					return "2";
				}
				return "1";
			}
		} catch (Exception e) {
			return "0";
		}
		return "0";
	}

	/*解码*/
	public String decrypt(String lic) {
		try {
			byte[] res;
			res = RSAEncrypt.decryptPlus(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(this.getClass().getResource("/publicKey.keystore").getFile())), Base64.decode(lic));
			String restr = new String(res, "UTF-8");
			return restr;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean checkMenu(String id) {
		License license = getLicenseInfo();
		if (license == null || "0".equals(license.getValid())) {
			return false;
		}
		if (license.getModules().length() == 9) {
			int index = MenuEnum.getIndexById(id);
			if (index == -1 || license.getModules().length() <= index) {
				return false;
			}
			if ((index >= 0 && index <= 4)) {
				return true;
			}
			if ("1".equals(license.getValid()) && license.getModules().charAt(index) == '1') {
				return true;
			}
			return false;
		} else {
			int index = MenuPlusEnum.getIndexById(id);
			if (index == -1 || license.getModules().length() <= index) {
				return false;
			}
			if ((index >= 50 && index <= 54)) {
				return true;
			}
			if ("1".equals(license.getValid()) && license.getModules().charAt(index) == '1') {
				return true;
			}
			return false;
		}

	}

	public boolean checkChildMenu(String id) {
		License license = getLicenseInfo();
		if (license == null || "0".equals(license.getValid())) {
			return false;
		}
		if (license.getModules().length() == 9) {
			int index = MenuEnum.getIndexByChildMenuId(id);
			if (index == -1 || license.getModules().length() <= index) {
				return true;
			}
			if ((index >= 0 && index <= 4)) {
				return true;
			}
			if ("1".equals(license.getValid()) && license.getModules().charAt(index) == '1') {
				return true;
			}
			return false;
		} else {
			int index = MenuPlusEnum.getIndexByChildMenuId(id);
			if (index == -1 || license.getModules().length() <= index) {
				return true;
			}
			if ((index >= 50 && index <= 54)) {
				return true;
			}
			if ("1".equals(license.getValid()) && license.getModules().charAt(index) == '1') {
				return true;
			}
			return false;
		}

	}

	public boolean checkCategory(String id) {
		License license = getLicenseInfo();
		if (license == null || "0".equals(license.getValid())) {
			return false;
		}
		if (license.getModules().length() == 9) {
			int index = MenuEnum.getIndexByCategoryId(id);
			if (index == -1 || license.getModules().length() <= index) {
				return true;
			}
			if ((index >= 0 && index <= 4)) {
				return true;
			}
			if ("1".equals(license.getValid()) && license.getModules().charAt(index) == '1') {
				return true;
			}
			return false;
		} else {
			int index = MenuPlusEnum.getIndexByCategoryId(id);
			if (index == -1 || license.getModules().length() <= index) {
				return true;
			}
			if ((index >= 50 && index <= 54)) {
				return true;
			}
			if ("1".equals(license.getValid()) && license.getModules().charAt(index) == '1') {
				return true;
			}
			return false;
		}
	}
	/**根据编号判断授权信息
	 * @param num MenuPlusEnum 枚举值序号从0开始
	 * @return
	 */
	public boolean checkMenuByNum(Integer num){
		if(num!=null){
			String id=null;
			if(num==0){
				id=MenuPlusEnum.S0.getId();
			}else if(num==1){
				id=MenuPlusEnum.S1.getId();
			}else if(num==2){
				id=MenuPlusEnum.S2.getId();
			}else if(num==3){
				id=MenuPlusEnum.S3.getId();
			}else if(num==4){
				id=MenuPlusEnum.S4.getId();
			}else if(num==5){
				id=MenuPlusEnum.S5.getId();
			}else if(num==6){
				id=MenuPlusEnum.S6.getId();
			}else if(num==7){
				id=MenuPlusEnum.S7.getId();
			}else if(num==8){
				id=MenuPlusEnum.S8.getId();
			}
			return checkMenu(id);
		}else{
			return false;
		}
	}
}