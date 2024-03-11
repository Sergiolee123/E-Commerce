package com.ecom.product;

import com.ecom.product.entity.BrandEntity;
import com.ecom.product.service.BrandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcomProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Test
	public void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		brandEntity.setName("test");
		brandService.save(brandEntity);
		BrandEntity one = brandService.getOne(null);
		Assertions.assertEquals(brandEntity.getName(), one.getName());
	}

}
