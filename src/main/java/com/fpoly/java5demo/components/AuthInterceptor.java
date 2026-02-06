package com.fpoly.java5demo.components;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
//		String userID = Utils.getCookieValue(Utils.COOKIE_KEY_USER_ID, req);
//		String role = Utils.getCookieValue(Utils.COOKIE_KEY_ROLE, req);
//
//		if (userID == null || role == null) {
//			resp.sendRedirect("/login");
//			return false;
//		}
////		Có userid và role => ?làm sao lấy được role mới nhất từ db?
//
//		String path = req.getRequestURI();
//
//		User user = UserServices.getUserInfoById(Integer.parseInt(userID));
//
//		if (user.getStatus() == 0) {
////			Bị khoá 
//			resp.sendRedirect(req.getContextPath() + "/login");
//			return;
//		}
//
//		if (!role.equals(String.valueOf(user.getRole()))) {
//			Utils.clearCookie(req, resp);
//
//			Utils.setCookie(Utils.COOKIE_KEY_USER_ID, String.valueOf(user.getId()), resp);
//			Utils.setCookie(Utils.COOKIE_KEY_ROLE, String.valueOf(user.getRole()), resp);
////			Duy trì đăng nhập khi có tương tác vào hệ thống
//
//			role = String.valueOf(user.getRole());
//		}
//
//		if (path.contains("/user/") && !role.equals("1") && !role.equals("2")) {
//			resp.sendRedirect(req.getContextPath() + "/login");
//			return;
//		}
//
//		if (path.contains("/editer/") && !role.equals("2")) {
//			resp.sendRedirect(req.getContextPath() + "/login");
//			return;
//		}
//
//		if (path.contains("/admin/") && !role.equals("3")) {
//			resp.sendRedirect(req.getContextPath() + "/login");
//			return;
//		}

		return true;
	}
}
