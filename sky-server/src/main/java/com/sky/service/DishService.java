package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {
    
    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    int addDish (DishDTO dishDTO);
    
    /**
     * 菜品分頁查詢
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult dishPageQuery (DishPageQueryDTO dishPageQueryDTO);
    
    void deleteDish (List<Long> ids);
}
