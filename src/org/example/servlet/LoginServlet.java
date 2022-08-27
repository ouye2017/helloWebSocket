package org.example.servlet;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:    登录
 * @author  唐唐的编程笔记
 * @email 2858629780@qq.com
 * @date  2022/8/26  17:45
 */
@WebServlet(name = "loginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
	private static final String PASSWORD = "123456";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 接收页面传递的参数，获取用户名与密码

		String username = req.getParameter("username");
		String password = req.getParameter("password");

		Map resultMap = new HashMap<>();
		// 2. 校验密码

		if (PASSWORD.equals(password)) {
			// 3. 如果用户名或密码不正确，响应登录失败的信息

			resultMap.put("success", true);
			resultMap.put("message", "登录成功");

			req.getSession().setAttribute("username", username);
		} else {
			// 4. 如果用户名与密码正确，响应登录成功的信息
			resultMap.put("success", false);
			resultMap.put("message", "用户名或密码错误");
		}
		//  5. 响应浏览器数据
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter writer = resp.getWriter();
		writer.write(JSON.toJSONString(resultMap));

	}
}
