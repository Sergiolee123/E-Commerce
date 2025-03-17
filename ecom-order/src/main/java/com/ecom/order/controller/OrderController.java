package com.ecom.order.controller;

import com.ecom.common.utils.PageUtils;
import com.ecom.common.utils.R;
import com.ecom.order.entity.OrderEntity;
import com.ecom.order.service.OrderService;
import com.ecom.order.vo.OrderConfirmVo;
import com.ecom.order.vo.OrderSubmitVo;
import com.ecom.order.vo.SubmitOrderResponseVo;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 订单
 *
 * @author Sergiolee123
 * @email lee.sergio.hk@gmail.com
 * @date 2024-03-11 22:49:05
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/toTrade")
    public R toTrade() {
        OrderConfirmVo orderConfirmVo = orderService.comfirmOrder();
        return R.ok().put("data", orderConfirmVo);
    }

    @PostMapping("/submitOrder")
    public R submitOrder(@RequestBody OrderSubmitVo orderSubmitVo) {
        SubmitOrderResponseVo vo = orderService.submitOrder(orderSubmitVo);
        return R.ok().put("data", vo);
    }



    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
