package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定時任務類
 */
@Component
@Slf4j
public class OrderTask {
    
    @Resource
    private OrderMapper orderMapper;
    
    /**
     * 處理超時訂單
     */
    @Scheduled (cron = "0 * * * * ? ")   // 每分鐘觸發一次
    public void processTimeOutOrder () {
        log.info ("定時處理超時訂單：{}", LocalDateTime.now ());
        
        LocalDateTime time = LocalDateTime.now ().plusMinutes (-15);

//        select * from orders where status = #{status} and order_time < #{time}
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT (Orders.PENDING_PAYMENT, time);
        
        if (ordersList.size () > 0 && ordersList != null) {
            for (Orders orders : ordersList) {
                orders.setStatus (Orders.CANCELLED);
                orders.setCancelReason ("訂單超時，自動取消");
                orders.setCancelTime (LocalDateTime.now ());
                orderMapper.update (orders);
            }
        }
    }
    
    /**
     * 定時處理派送中訂單
     */
    @Scheduled (cron = "0 0 1 * * ? ")   // 每分鐘觸發一次
    public void processDeliveryOrder () {
        log.info ("定時處理派送中訂單：{}", LocalDateTime.now ());
        
        LocalDateTime time = LocalDateTime.now ().plusMinutes (-60);

//        select * from orders where status = #{status} and order_time < #{time}
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT (Orders.PENDING_PAYMENT, time);
        
        if (ordersList.size () > 0 && ordersList != null) {
            for (Orders orders : ordersList) {
                orders.setStatus (Orders.CANCELLED);
                orderMapper.update (orders);
            }
        }
    }
    
}
