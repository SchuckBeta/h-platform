/**
 *
 */
package com.hch.platform.pcore.modules.sys.service;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.security.Digests;
import com.hch.platform.pcore.common.security.shiro.session.SessionDAO;
import com.hch.platform.pcore.common.service.AbsBaseService;
import com.hch.platform.pcore.modules.sys.dao.RoleDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.entity.AbsRole;
import com.hch.platform.putil.common.utils.Encodes;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.


 */
@Service
@Transactional(readOnly = true)
public abstract class AbsSystemService extends AbsBaseService implements InitializingBean {

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;

	@Autowired
	private RoleDao roleDao;
	@Autowired
	private SessionDAO sessionDao;

	public SessionDAO getSessionDao() {
		return sessionDao;
	}

	@Autowired
	private IdentityService identityService;

	//-- User Service --//
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}

	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}

	/**
	 * 获得活动会话
	 * @return
	 */
	public Collection<Session> getActiveSessions() {
		return sessionDao.getActiveSessions(false);
	}

	//-- Role Service --//

	public AbsRole getRole(String id) {
		return roleDao.get(id);
	}

	public AbsRole getNamebyId(String id) {
			return roleDao.getNamebyId(id);
		}

	public abstract AbsRole getRoleByName(String name);

	public abstract AbsRole getRoleByEnname(String enname);

	public List<AbsRole> findRole(AbsRole role) {
		return roleDao.findList(role);
	}

	/**
	 * 获取Key加载信息
	 */
	public static boolean printKeyLoadMessage() {
		/*StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用 "+Global.getConfig("productName")+"  - Powered By http://initiate.com\r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());*/
		return true;
	}

	///////////////// Synchronized to the Activiti //////////////////

	// 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java

	/**
	 * 是需要同步Activiti数据，如果从未同步过，则同步数据。
	 */
	private static boolean isSynActivitiIndetity = true;

	private void deleteActivitiUser(AbsUser user) {
		if (!Global.isSynActivitiIndetity()) {
			return;
		}
		if (user!=null) {
			String userId = user.getLoginName();//ObjectUtils.toString(user.getId());
			identityService.deleteUser(userId);
		}
	}

	///////////////// Synchronized to the Activiti end //////////////////


	public List<AbsRole> findListByUserId(String userId) {

		List<AbsRole> roleList= roleDao.findListByUserId(userId);

		return roleList;
	}
}
