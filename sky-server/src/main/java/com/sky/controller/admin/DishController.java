package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api (tags = "菜品相關接口")
@RequestMapping ("/admin/dish")
@Slf4j
public class DishController {
    
    @Resource (name = "dishServiceImpl")
    private DishService dishService;
    
    /**
     * 新增菜品 :新增的菜品中可以包含多中口味，因此對於口味的數據表操做要一同實現
     *
     * @param dishDTO
     * @return
     */
    @ApiOperation ("新增菜品")
    @PostMapping
    public Result addDish (@RequestBody DishDTO dishDTO) {
        log.info ("新增菜品.......");
        int resultRow = dishService.addDish (dishDTO);
        
        if (resultRow == 1) {
            log.info ("菜品添加成功");
        }
        
        return Result.success ();
    }
    
}
