package com.ecom.product.web;

import com.ecom.product.service.SkuInfoService;
import com.ecom.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    private final SkuInfoService skuInfoService;

    public ItemController(SkuInfoService skuInfoService) {
        this.skuInfoService = skuInfoService;
    }

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model) {
        SkuItemVo itemVo = skuInfoService.item(skuId);
        model.addAttribute("item", itemVo);
        return "item";
    }

}
