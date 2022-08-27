package org.example.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @Description:    获取HTTPSession对象的配置类
 * @author  唐唐的编程笔记
 * @email 2858629780@qq.com
 * @date  2022/8/27  10:56
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

	/**
	 * 获取到 HTTPSession ，并将其存储在 ServerEndpointConfig对象中
	 * @param config
	 * @param request
	 * @param response
	 */
	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession=(HttpSession)request.getHttpSession();
		config.getUserProperties().put(HttpSession.class.getName(),httpSession);
	}
}
