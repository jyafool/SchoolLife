package com.sky.controller.admin;

import com.sky.constant.OtherConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@RestController
@Api (tags = "菜品相關接口")
@RequestMapping ("/admin/dish")
@Slf4j
public class DishController {
    
    @Resource (name = "dishServiceImpl")
    private DishService dishService;
    
    @Resource
    private RedisTemplate redisTemplate;
    
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
        
        // 清理緩存數據
        cleanCache (OtherConstant.DISH_ + dishDTO.getCategoryId ());
        
        return Result.success ();
    }
    
    
    /**
     * 菜品分頁查詢
     *
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation ("菜品分頁查詢")
    @GetMapping ("/page")
    public Result<Object> dishPageQuery (DishPageQueryDTO dishPageQueryDTO) {
        log.info ("菜品分頁查詢:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.dishPageQuery (dishPageQueryDTO);
        return Result.success (pageResult);
    }
    
    /**
     * 菜品刪除業務邏輯：
     * 可以單個或者多個菜品刪除
     * 起售中的菜品不能刪除
     * 被套餐關連的菜品不能刪除
     * 刪除菜品后，關聯的口味數據也一并刪除\
     *
     * @return
     * @RequestParam : 表示動態解析前端傳過來的字符串類型的ids，
     * 並封裝到list集合裏
     */
    @ApiOperation ("菜品刪除")
    @DeleteMapping
    public Result deleteDish (@RequestParam List<Long> ids) {
        log.info ("要刪除菜品的id集合：{}", ids);
        dishService.deleteDish (ids);
    
        // 清理緩存數據
        cleanCache (OtherConstant.DISH_ + "*");
    
        return Result.success ();
    }
    
    /**
     * 根據id查詢菜品
     *
     * @param id
     * @return
     */
    @ApiOperation ("根據id查詢菜品")
    @GetMapping ("/{id}")
    public Result<DishVO> selectDishByIdWithFlavors (@PathVariable Long id) {
        log.info ("根據id查詢菜品：{}", id);
        DishVO dishVO = dishService.selectDishByIdWithFlavors (id);
        return Result.success (dishVO);
    }
    
    @ApiOperation ("修改菜品信息")
    @PutMapping
    public Result updateDish (@RequestBody DishDTO dishDTO) {
        log.info ("要修改的菜品信息：{}", dishDTO);
        dishService.updateDish (dishDTO);
    
        // 清理緩存數據
        cleanCache (OtherConstant.DISH_ + "*");
    
        return Result.success ();
    }
    
    /**
     * 起售停售菜品
     *
     * @param status
     * @param id
     * @return
     */
    @ApiOperation ("起售停售菜品")
    @PostMapping ("/status/{status}")
    public Result changeStatus (@PathVariable Integer status, Long id) {
        log.info ("要啓售、停售的菜品：{}", id);
        DishDTO dishDTO = DishDTO.builder ()
                .status (status)
                .id (id)
                .build ();
        dishService.changeStatus (dishDTO);
    
        // 清除該菜品緩存
        cleanCache (OtherConstant.DISH_ + "*");
        return Result.success ();
    }
    
    /**
     * 根據分類id查詢菜品
     *
     * @param categoryId
     * @return
     */
    @ApiOperation ("根據分類id查詢菜品")
    @GetMapping ("/list")
    public Result<List<Dish>> selectByCategoryId (@RequestParam Long categoryId) {
        log.info ("根據分類id查詢菜品:{}", categoryId);
        List<Dish> dishes = dishService.selectByCategoryId (categoryId);
    
        return Result.success (dishes);
    }
    
    /**
     * 清理緩存數據
     *
     * @param pattern
     */
    private void cleanCache (String pattern) {
        Set keys = redisTemplate.keys (pattern);
        redisTemplate.delete (keys);
    }
    
}
