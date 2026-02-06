package com.fpoly.java5demo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.java5demo.controllers.RegisterController;
import com.fpoly.java5demo.entities.CartDetail;
import com.fpoly.java5demo.entities.Product;
import com.fpoly.java5demo.jpas.CartDetailJPA;

import jakarta.servlet.http.HttpSession;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class CartDetailServices {

	private final RegisterController registerController;

	@Autowired
	private CartDetailJPA cartDetailJPA;

	@Autowired
	private HttpSession session;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductServices productServices;

	CartDetailServices(RegisterController registerController) {
		this.registerController = registerController;
	}

//	Cách 1: Lấy thông tin user bằng serJPTA => user.getCartDetails()
//	Cách 2: Viết thêm script sql ở cartDetailJPA
// 	Dùng để lấy danh sách sản phẩm ở DB lưu vào Session khi khởi tạo
	public List<CartDetail> getCartsByUserId(int id) {
		List<CartDetail> cartDetails = new ArrayList<CartDetail>();
		try {
			cartDetails = cartDetailJPA.findByUserId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cartDetails;
	}

//	Hàm đồng bộ dữ liệu từ Session qua CartDetail ở DB
	public boolean syncSessionCartToDatabase(List<CartDetail> cartDetails) {
		try {
//			Xoá tất cả các cartDetail của user
			cartDetailJPA.deleteByUserId(cartDetails.get(0).getUser().getId());
//			Thêm lại danh sách cartDetail được gửi qua ở Session
			cartDetailJPA.saveAll(cartDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addToCart(int prodId) {
//		Lấy danh sách sản phẩm trong giỏ hàng ở session
//		Kiểm tra sản phẩm có trong giỏ hàng không?
//		- Ktra số lượng sp ở db <==> Nếu có + 1 số lượng
//		- Ktra số lượng sp ở db <==> Nếu không có thêm vào với số lượng là 1
		Map<Integer, Integer> cartMap = this.getCartMapSession();
		Product product = productServices.findById(prodId);
		if (product == null || !product.isStatus()) {
			return false;
		}
		if (cartMap.get(prodId) == null) {
//			Nếu không có thêm vào với số lượng là 1
			if (product.getQuantity() < 1) {
				return false;
			}
			cartMap.put(prodId, 1);
		} else {
			int quantity = cartMap.get(prodId);
//			Nếu có + 1 số lượng
			if (product.getQuantity() < quantity + 1) {
				return false;
			}
			cartMap.put(prodId, quantity + 1);
		}

		String newCartJson = objectMapper.writeValueAsString(cartMap);
		session.setAttribute("cart", newCartJson);
		return true;
	}

	public boolean updateQuantity(int prodId, int quantity) {
		return false;
	}

	public boolean removeCart(int prodId) {
		return false;
	}

	public Map<Integer, Integer> getCartMapSession() {
		Map<Integer, Integer> cartMap = new HashMap<Integer, Integer>();
		String cartJson = (String) session.getAttribute("cart");
		if (cartJson != null && !cartJson.isEmpty()) {
			try {
				cartMap = objectMapper.readValue(cartJson, new TypeReference<Map<Integer, Integer>>() {
				});
			} catch (Exception e) {
				System.err.println("Lỗi parse JSON trong Listener: " + e.getMessage());
			}
		}

		return cartMap;
	}
}
