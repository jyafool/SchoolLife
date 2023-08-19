package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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
    
    /**
     * 根據id查詢菜品
     *
     * @param id
     * @return
     */
    DishVO selectDishByIdWithFlavors (Long id);
    
    /**
     * 修改菜品信息
     *
     * @param dishDTO
     */
    void updateDish (DishDTO dishDTO);
}
