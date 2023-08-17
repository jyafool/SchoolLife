package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    
    /**
     * 添加菜品分類：葷菜、素菜、涼菜等等
     *
     * @param categoryDTO
     * @return
     */
    int addNewCategory (CategoryDTO categoryDTO);
    
    /**
     * 按類型查詢
     *
     * @param type
     * @return
     */
    List<Category> getCategoryByType (Integer type);
    
    /**
     * 分頁查詢
     *
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult categoryPageQuery (CategoryPageQueryDTO categoryPageQueryDTO);
    
    /**
     * 啓用、禁用分類
     *
     * @param status
     * @param id
     * @return
     */
    int changeStatus (Integer status, Long id);
    
    /**
     * 修改分類
     *
     * @param categoryDTO
     * @return
     */
    int updateCategory (CategoryDTO categoryDTO);
    
    /**
     * 根據id刪除分類
     *
     * @return
     */
    int deleteCategoryById (Long id);
}
