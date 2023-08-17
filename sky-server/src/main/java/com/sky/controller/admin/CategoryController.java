package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api ("商品分類相關接口")
@RequestMapping ("/admin/category")
@Slf4j
public class CategoryController {
    
    @Resource (name = "categoryServiceImpl")
    private CategoryService categoryService;
    
    
    /**
     * 添加菜品分類：葷菜、素菜、涼菜等等
     * 添加的時候默認是禁用狀態，因爲剛添加該分類時並沒有將菜品加入
     *
     * @return
     */
    @PostMapping
    @ApiOperation ("新增分類")
    public Result<String> addNewCategory (@RequestBody CategoryDTO categoryDTO) {
        log.info ("新增分類");
        
        int resultRow = categoryService.addNewCategory (categoryDTO);
        if (resultRow == 1) {
            log.info ("添加成功。。。。。");
        }
        return Result.success ();
    }
    
    /**
     * 按類型查詢
     *
     * @param type
     * @return
     */
    @GetMapping ("/list")
    @ApiOperation ("按類型查詢")
    public Result<List<Category>> getCategoryByType (Integer type) {
        log.info ("按類型查詢");
        
        List<Category> categoryList = categoryService.getCategoryByType (type);
        
        return Result.success (categoryList);
    }
    
    /**
     * 分頁查詢
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping ("/page")
    @ApiOperation ("分頁查詢")
    public Result<PageResult> categoryPageQuery (CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info ("菜品類別分頁查詢");
        
        PageResult categoryPageQueryResult = categoryService.categoryPageQuery (categoryPageQueryDTO);
        
        
        return Result.success (categoryPageQueryResult);
    }
    
    /**
     * 啓用、禁用分類
     *
     * @param status,id
     * @return
     */
    @PostMapping ("/status/{status}")
    @ApiOperation ("啓用、禁用分類")
    public Result<String> changeStatus (@PathVariable ("status") Integer status, @RequestParam ("id") Long id) {
        log.info ("啓用、禁用分類.........");
        int resultRow = categoryService.changeStatus (status, id);
        if (resultRow == 1) {
            log.info ("修改成功。。。。。。。。。");
        }
        return Result.success ();
    }
    
    /**
     * 根據id刪除分類
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation ("根據id刪除分類")
    public Result<String> deleteCategoryById (@RequestParam ("id") Long id) {
        log.info ("根據id刪除分類.....");
        categoryService.deleteCategoryById (id);
        
        return Result.success ();
    }
    
    /**
     * 修改分類
     *
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation ("修改分類")
    public Result<String> updateCategory (@RequestBody CategoryDTO categoryDTO) {
        log.info ("修改分類...........");
        int resultRow = categoryService.updateCategory (categoryDTO);
        if (resultRow == 1) {
            log.info ("修改成功。。。。。");
        }
        return Result.success ();
    }
}
