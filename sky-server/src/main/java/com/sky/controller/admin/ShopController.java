package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController ("adminShopController")
@RequestMapping ("/admin/shop")
@Api (tags = "店鋪相關接口")
@Slf4j
public class ShopController {
    
    public static final String KEY = "SHOP_STATUS";
    @Resource
    private RedisTemplate redisTemplate;
    
    @PutMapping ("/{status}")
    @ApiOperation ("設置店鋪的營業狀態")
    public Result setStatus (@PathVariable Integer status) {
        log.info ("設置店鋪的營業狀態為：{}", status == 1 ? "營業中" : "打烊中");
        redisTemplate.opsForValue ().set (KEY, status);
        return Result.success ();
    }
    
    @GetMapping ("/status")
    @ApiOperation ("獲取店鋪營業狀態")
    public Result<Integer> getStatus () {
        Integer status = (Integer) redisTemplate.opsForValue ().get (KEY);
        log.info ("獲取到的店鋪營業狀態為：{}", status == 1 ? "營業中" : "打烊中");
        return Result.success ();
    }
    
    
}
