package com.fpoly.java5demo.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.java5demo.entities.Order;
import com.fpoly.java5demo.entities.OrderDetail;
import com.fpoly.java5demo.entities.Product;
import com.fpoly.java5demo.entities.User;
import com.fpoly.java5demo.jpas.OrderDetailJPA;
import com.fpoly.java5demo.jpas.OrderJPA;
import com.fpoly.java5demo.jpas.ProductJPA;
import com.fpoly.java5demo.jpas.UserJPA;
import com.fpoly.java5demo.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.ObjectMapper;

@Service
public class OrderServices {
//	Tạo đơn hàng
//	Danh sách đơn hàng (lấy tất cả và lấy theo user_id hoặc trạng thái)
//	Cập nhật trạng thái của đơn hàng 
//	Huỷ đơn hàng 
//	Chi tiết đơn hàng 

	@Autowired
	OrderJPA orderJPA;
	@Autowired
	OrderDetailJPA orderDetailJPA;
	@Autowired
	UserJPA userJPA;
	@Autowired
	ProductJPA productJPA;
	@Autowired
	HttpSession session;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private CartDetailServices cartDetailServices;

	public String createOrder(Map<Integer, Integer> cartItems, String address) {
		try {
			String userId = Utils.getCookieValue(Utils.COOKIE_KEY_USER_ID, request);
			User user = userJPA.findById(Integer.parseInt(userId)).orElseThrow();
//			? Có thể insert đơn hàng chưa? 
//			Lấy danh sách tất cả sản phẩm theo id của giỏ hàng để kiểm tra số lượng 

//			for (Map.Entry<Integer, Integer> item : cartItems.entrySet()) {
//				int productId = item.getKey();
//				int quantity = item.getValue();
//				Product product = productJPA.findById(productId).orElseThrow();
//				if (product.getQuantity() < quantity) {
//					return "Error";
//				}
//			}

//			productJPA.findAllById(null)
//			Tạo 1 danh sách để chứa id => lấy id từ map để gán vào danh sách
//			=> dùng danh sách này để gọi func findAllById => lấy ra được product ở DB

			List<Integer> ids = new ArrayList<Integer>();
			for (Map.Entry<Integer, Integer> item : cartItems.entrySet()) {
				ids.add(item.getKey());
			}
			List<Product> products = productJPA.findAllById(ids);
			int totalPrice = 0;
			for (Product item : products) {
				if (item.getQuantity() < cartItems.get(item.getId())) {
					return "Error";
				}

				totalPrice += cartItems.get(item.getId()) * item.getPrice();
			}

			Order order = new Order();
			order.setAddress(address);
			order.setUser(user);
//			Convert từ util.Date => String  yyyy-mm-dd
			Calendar calendar = Calendar.getInstance();
			String date = String.format("%s-%s-%s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
					calendar.get(Calendar.DAY_OF_MONTH));
			order.setDate(Date.valueOf(date));
			order.setStatus(0);
			order.setTotalPrice(totalPrice);

			Order orderSave = orderJPA.save(order);
//			Lưu orderDetail
//			Trừ số lượng ở DB
//			Xoá sản phẩm ở giỏ hàng

//			Cách 1: dùng 3 vòng lập for riêng biệt
//			Cách 2: Dùng 1 vòng lập for 
			Map<Integer, Integer> cartItemSession = cartDetailServices.getCartMapSession();
			for (Product item : products) {
				OrderDetail orderDetail = new OrderDetail();
				orderDetail.setOrder(orderSave);
				orderDetail.setPrice(item.getPrice());
				orderDetail.setQuantity(cartItems.get(item.getId()));
				orderDetail.setProduct(item);
				orderDetailJPA.save(orderDetail);
//				Lưu được 1 item của đơn hàng 
				Product productNew = item;
				productNew.setQuantity(productNew.getQuantity() - cartItems.get(item.getId()));
				productJPA.save(productNew);
//				Cập nhật lại số lượng sản phẩm ở DB 
				cartItemSession.remove(item.getId());
			}

			String newCartJson = objectMapper.writeValueAsString(cartItemSession);
			session.setAttribute("cart", newCartJson);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	0: Chờ
//	1: Đã xác nhận
//	2: Đang giao hàng
//	3: Đã nhận hàng
//	4: Hoàn thành
//	5: Huỷ đơn 
	public String updateStatus(int orderId, int status) {
//		Chỉ dùng cho admin
		if (status == 0 || status == 4 || status == 5) {
			return "error";
		}
//		Chỉ được cập nhật các trạng thái 1 -> 3
//		Điều kiện để được cập nhật trạng thái
//		Trạng thái được cập nhật phải lớn hơn trạng thái cũ
//		Và chỉ cách trạng thái cũ 1 đơn vị
		Order order = orderJPA.findById(orderId).orElseThrow();
		if (order.getStatus() + 1 == status) {
//			Cập nhật 
		}
		return null;
	}

//	Huỷ đơn, hoàn thành đơn và chi tiết đơn hàng ở phía user
//	- Kiểm tra đơn hàng có thuộc của user hiện tại không?
//	- Phải kiểm tra trạng thái trước khi huỷ hoặc hoàn thành
//		- Huỷ trạng thái phải là 0
//		- Hoàn thành trạng thái là 3

//	Huỷ đơn ở admin: Trạng thái ở 0 || 1
}
