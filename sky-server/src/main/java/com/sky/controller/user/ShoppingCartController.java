package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping ("/user/shoppingCart")
@Slf4j
@Api (tags = "C-端購物車相關接口")
public class ShoppingCartController {
    
    @Resource
    private ShoppingCartService shoppingCartService;
    
    /**
     * 添加購物車
     *
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping ("/add")
    @ApiOperation ("添加購物車")
    public Result add (@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info ("要添加的購物車信息：{}", shoppingCartDTO);
        shoppingCartService.add (shoppingCartDTO);
        
        return Result.success ();
    }
    
    @GetMapping ("/list")
    @ApiOperation ("查看購物車")
    public Result<List<ShoppingCart>> list () {
        List<ShoppingCart> shoppingCartList = shoppingCartService.list ();
        return Result.success (shoppingCartList);
    }
    
    @DeleteMapping ("/clean")
    @ApiOperation ("清空購物車")
    public Result cleanShoppingCart () {
        shoppingCartService.cleanShoppingCart ();
        
        return Result.success ();
    }
    
}
