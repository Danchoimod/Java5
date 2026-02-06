package com.fpoly.java5demo.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fpoly.java5demo.entities.CartDetail;
import com.fpoly.java5demo.entities.Product;
import com.fpoly.java5demo.entities.User;
import com.fpoly.java5demo.jpas.UserJPA;
import com.fpoly.java5demo.services.CartDetailServices;
import com.fpoly.java5demo.services.ProductServices;
import com.fpoly.java5demo.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class CartSessionListener implements HttpSessionListener {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CartDetailServices cartDetailServices;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserJPA userJPA;// Services

	@Autowired
	private ProductServices productServices;

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		String userId = Utils.getCookieValue(Utils.COOKIE_KEY_USER_ID, request);
		Map<Integer, Integer> cartMap = new HashMap<>();

		if (userId != null) {
			List<CartDetail> cartDetails = cartDetailServices.getCartsByUserId(Integer.parseInt(userId));
//			Chuyển danh sách giỏ hàng ở DB vào Map 
			for (CartDetail cartDetail : cartDetails) {
				cartMap.put(cartDetail.getProduct().getId(), cartDetail.getQuantity());
			}
		}

//		Chuyển dữ liệu của Map về chuỗi json 
		String newCartJson = objectMapper.writeValueAsString(cartMap);
		session.setAttribute("cart", newCartJson);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();

		String userId = Utils.getCookieValue(Utils.COOKIE_KEY_USER_ID, request);
		if (userId == null) {
			return;
		}
		User user = userJPA.findById(Integer.parseInt(userId)).orElse(null);
		if (user == null) {
			return;
		}
		String cartJson = (String) session.getAttribute("cart");

		if (cartJson != null && !cartJson.isEmpty()) {
			try {
//				Chuyển dữ liệu của chuỗi json về map 
				Map<Integer, Integer> cartMap = objectMapper.readValue(cartJson,
						new TypeReference<Map<Integer, Integer>>() {
						});

//				Convert Map to Cart Detail 
				List<CartDetail> cartDetails = new ArrayList<CartDetail>();
				for (Map.Entry<Integer, Integer> entry : cartMap.entrySet()) {
					int prodId = entry.getKey();
					int quantity = entry.getValue();
//					
					Product product = productServices.findById(prodId);

					if (product == null) {
						continue;
					}

					CartDetail cartDetail = new CartDetail();
					cartDetail.setProduct(product);
					cartDetail.setUser(user);
					cartDetail.setQuantity(quantity);
					cartDetails.add(cartDetail);
				}

				cartDetailServices.syncSessionCartToDatabase(cartDetails);
			} catch (Exception e) {
				System.err.println("Lỗi parse JSON trong Listener: " + e.getMessage());
			}
		}
	}
}
