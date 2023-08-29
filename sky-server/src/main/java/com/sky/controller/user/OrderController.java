package com.sky.controller.user;


import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping ("/user/order")
@Slf4j
@Api (tags = "C-端訂單相關接口")
public class OrderController {
    
    @Resource (name = "orderServiceImpl")
    private OrderService orderService;
    
    @PostMapping ("/submit")
    @ApiOperation ("用戶下單")
    public Result<OrderSubmitVO> submitOrder (@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info ("用戶下單信息：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder (ordersSubmitDTO);
        log.info ("返回的vo對象：{}", orderSubmitVO);
        return Result.success (orderSubmitVO);
    }
    
    
}
