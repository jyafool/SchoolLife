package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    
    /**
     * 根據菜品id查詢
     *
     * @param dishIds
     * @return 返回套餐id
     */
// select setmeal_id from setmeal_dish where dish_id in(1,2,3,4)
    List<Long> selectByDishId (List<Long> dishIds);
}
