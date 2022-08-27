package org.example.websocket;


import com.alibaba.fastjson.JSONObject;
import org.example.util.MessageUtil;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 唐唐的编程笔记
 * @Description: java类作用描述
 * @email 2858629780@qq.com
 * @date 2022/8/27  13:24
 */
@ServerEndpoint(value = "/websocket", configurator = GetHttpSessionConfigurator.class)
public class ChatServlet {

	// 该map集合用来存储所有在线用户的实例信息
	private static final Map<HttpSession, ChatServlet> onlineUsers = new HashMap<HttpSession, ChatServlet>();

	// 记录在线用户数
	private static int onlineCount = 0;

	// 用户的 HttpSession
	private HttpSession httpSession;

	

	// 用户的 WS 的会话信息 session
	private Session session;

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {

		this.session = session;
		this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		if (httpSession.getAttribute("username") != null) {
			onlineUsers.put(httpSession, this);
		}

		String usernames = getUserNames();

		String content = MessageUtil.getContent(MessageUtil.TYPE_USER, "", "", usernames);
		System.out.println("服务端给客户端广播消息：" + content);

		broadcastAll(content);
		addOnlineCount();    // 在线数加1
		System.out.println("有新连接加入！当前在线人数为：" + onlineUsers.size());
	}


	@OnMessage
	public void OnMessage(String message, Session session) {

		System.out.println("onClose-OnMessage-username:" + httpSession.getAttribute("username"));

		Map<String, String> map = JSONObject.parseObject(message, Map.class);
		String fromName = map.get(MessageUtil.FROM_NAME);
		String toName = map.get(MessageUtil.TO_NAME);
		String content = map.get("content");
		String type = map.get(MessageUtil.TYPE);

//判断是否有接收人，如果没有接收人则不处理
		if (toName == null || toName.isEmpty()) {
			return;
		}

		// 如果接收人是 all，则说明是广播消息
		String sendMsg = "";
		if ("all".equals(toName)) {
			sendMsg = MessageUtil.getContent(MessageUtil.TYPE_MESSAGE, fromName, "all", content);
			broadcastAll(sendMsg);
		} else {
			// 如果不是 all，则给指定用户推送消息
			sendMsg = MessageUtil.getContent(MessageUtil.TYPE_MESSAGE, fromName, toName, content);
			singleChat(toName, sendMsg);
		}
		System.out.println("来自客户端的消息:" + message);
	}

	private void singleChat(String toName, String sendMsg) {

		HttpSession toHttpSession = null;

		for (HttpSession key : onlineUsers.keySet()) {
			String username = (String) key.getAttribute("username");
			if (username.equals(toName)) {
				toHttpSession = key;
				break;
			}
		}

		if (toHttpSession != null) {
			try {
				onlineUsers.get(toHttpSession).session.getBasicRemote().sendText(sendMsg);
				onlineUsers.get(httpSession).session.getBasicRemote().sendText(sendMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("onClose-HttpSession-username:" + httpSession.getAttribute("username"));
		onlineUsers.remove(httpSession);
		subOnlineCount();

		String content = httpSession.getAttribute("username") + "下线了";
		String sendMsg = MessageUtil.getContent(MessageUtil.TYPE_MESSAGE, MessageUtil.FROM_NAME, "all", content);
		broadcastAll(sendMsg);


		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}


	@OnError
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
		System.out.println("发生错误");
	}


	private static synchronized void addOnlineCount() {
		ChatServlet.onlineCount++;
	}

	private static synchronized void subOnlineCount() {
		ChatServlet.onlineCount--;
	}

	private static synchronized int getOnlineCount() {
		return onlineCount;
	}


	// 发送广播
	private void broadcastAll(String msg) {

		for (HttpSession key : onlineUsers.keySet()) {
			try {
				onlineUsers.get(key).session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//获得当前在用用户名
	private String getUserNames() {

		StringBuffer names = new StringBuffer();


		for (HttpSession key : onlineUsers.keySet()) {

			String username = (String) key.getAttribute("username");

			names.append(username + ",");
		}


		if (names.length() > 0) {
			return names.deleteCharAt(names.length() - 1).toString();

		}

		return "";
	}


}
