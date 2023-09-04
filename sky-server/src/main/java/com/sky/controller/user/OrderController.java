package com.sky.controller.user;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping ("/payment")
    @ApiOperation ("订单支付")
    public Result<OrderPaymentVO> payment (@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info ("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment (ordersPaymentDTO);
        log.info ("生成预支付交易单：{}", orderPaymentVO);
        return Result.success (orderPaymentVO);
    }
    
    @GetMapping ("/orderDetail/{id}")
    @ApiOperation ("查看訂單詳情")
    public Result<OrderVO> getOrdersById (@PathVariable Long id) {
        log.info ("要查找的訂單id：{}", id);
        OrderVO orderVO = orderService.getOrdersById (id);
        return Result.success (orderVO);
    }
    
    @GetMapping ("/historyOrders")
    @ApiOperation ("歷史訂單查詢")
    public Result<PageResult> pageQueryOrders (OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.pageQueryOrders (ordersPageQueryDTO);
        return Result.success (pageResult);
    }
    
    /**
     * 用户取消订单
     *
     * @return
     */
    
    @PutMapping ("/cancel/{id}")
    @ApiOperation ("取消订单")
    public Result cancel (@PathVariable ("id") Long id) throws Exception {
        orderService.userCancelById (id);
        return Result.success ();
    }
    
    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping ("/repetition/{id}")
    @ApiOperation ("再来一单")
    public Result repetition (@PathVariable Long id) {
        orderService.repetition (id);
        return Result.success ();
    }
    
    
}
