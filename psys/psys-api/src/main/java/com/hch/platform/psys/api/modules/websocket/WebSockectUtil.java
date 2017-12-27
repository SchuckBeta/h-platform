package com.hch.platform.pcore.modules.websocket;

import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.putil.common.utils.StringUtil;

import net.sf.json.JSONObject;

public class WebSockectUtil {
	@SuppressWarnings("rawtypes")
	private static RedisTemplate redisTemplate= SpringContextHolder.getBean(RedisTemplate.class);
	/**推送消息
	 * @param session
	 * @param msg
	 */
	public static void sendMsg(Session session,String msg){
		session.getAsyncRemote().sendText(msg);
	}
	/**根据userid推送消息
	 * @param userid
	 * @param msg
	 */
	public static void sendMsg(String userid,String msg){
		if(StringUtil.isNotEmpty(userid)&&StringUtil.isNotEmpty(msg)){
			Map<String,Session> map=WebSocketServer.userid_session.get(userid);
			if(map!=null){
				for(String k:map.keySet()){
					sendMsg(map.get(k), msg);
				}
			}
		}
	}
	/**向渠道sendWsMsg发布消息
	 * @param userid
	 * @param msg
	 */
	public static void pushToRedis(String userid,WsMsg wbmsg){
		JSONObject msg=JSONObject.fromObject(wbmsg);
		JSONObject js=new JSONObject();
		js.put("userid", userid);
		js.put("msg", msg);
		try {
			redisTemplate.convertAndSend("sendWsMsg", js.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**向渠道sendWsMsg发布消息
	 * @param userid
	 * @param msg
	 */
	public static void pushToRedis(List<String> userids,WsMsg wbmsg){
		JSONObject msg=JSONObject.fromObject(wbmsg);
		if(userids!=null&&userids.size()>0){
			JSONObject js=new JSONObject();
			js.put("userid", StringUtils.join(userids.toArray(), ","));
			js.put("msg", msg);
			try {
				redisTemplate.convertAndSend("sendWsMsg", js.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
