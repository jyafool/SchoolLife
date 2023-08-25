package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
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
}
