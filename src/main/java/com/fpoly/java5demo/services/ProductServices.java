package com.fpoly.java5demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.java5demo.entities.Image;
import com.fpoly.java5demo.entities.Product;
import com.fpoly.java5demo.jpas.ImageJPA;
import com.fpoly.java5demo.jpas.ProductJPA;

@Service
public class ProductServices {

	@Autowired
	ProductJPA productJPA;

	@Autowired
	ImageJPA imageJPA;

	@Autowired
	ImageServices imageService;

	public List<Product> getList() {
		List<Product> products = new ArrayList<Product>();
		try {
			products = productJPA.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return products;
	}

	public String addProduct(Product product, List<MultipartFile> images) {
		try {
			Product prodSave = productJPA.save(product);

			for (MultipartFile image : images) {
				String name = imageService.save(image);

				if (name != null) {
					Image imageSave = new Image();
					imageSave.setName(name);
					imageSave.setProduct(prodSave);
					imageJPA.save(imageSave);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Add product error";
		}
		return null;
	}

	public String updateProduct(Product product, List<Image> images) {
		try {
			Product prodSave = productJPA.save(product);

			if (images == null || images.size() == 0) {
				return null;
			} else {
				for (Image image : prodSave.getImages()) {
					imageJPA.delete(image);
				}

				for (Image image : images) {
					Image imageSave = image;
					imageSave.setProduct(prodSave);
					imageJPA.save(imageSave);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Add product error";
		}
		return null;
	}

	public String remove(int id) {
		try {
			Optional<Product> prodOptional = productJPA.findById(id);
			if (prodOptional.isPresent()) {
				Product product = prodOptional.get();
				if (product.getCartDetails().size() > 0) {
					product.setStatus(false);
					productJPA.save(product);
				} else {
					for (Image image : product.getImages()) {
						imageJPA.delete(image);
					}

					productJPA.delete(product);
				}

				return null;
			}

			return "Error";
		} catch (Exception e) {
			e.printStackTrace();
			return "Add product error";
		}
	}

	public Product findById(int id) {
		Optional<Product> prodOptional = productJPA.findById(id);
		return prodOptional.isPresent() ? prodOptional.get() : null;
	}
}
