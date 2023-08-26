package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    int createSetmeal (SetmealDTO setmealDTO);
    
    PageResult setmealPageQuery (SetmealPageQueryDTO setmealPageQueryDTO);
    
    SetmealVO retrieveBySetmealId (Long id);
    
    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    void updateSetmeal (SetmealDTO setmealDTO);
    
    int changeStatus (Integer status, Long id);
    
    int deleteSetmeal (List<Long> ids);
    
    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> list (Setmeal setmeal);
    
    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById (Long id);
}
