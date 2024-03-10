package com.ecom.product;

import com.ecom.product.entity.PmsBrandEntity;
import com.ecom.product.service.PmsBrandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcomProductApplicationTests {

	@Autowired
	PmsBrandService brandService;

	@Test
	public void contextLoads() {
		PmsBrandEntity pmsBrandEntity = new PmsBrandEntity();
		pmsBrandEntity.setName("test");
		brandService.save(pmsBrandEntity);
		PmsBrandEntity one = brandService.getOne(null);
		Assertions.assertEquals(pmsBrandEntity.getName(), one.getName());
	}

}
