package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    
    
    /**
     * 添加菜品：
     *
     * @param setmealDish
     * @return
     */
    @Insert ("""
            insert into setmeal_dish
            (setmeal_id, dish_id, name, price, copies)
            VALUES
            (#{setmealId},#{dishId},#{name},#{price},#{copies})
            """)
    int insertSetmealDish (SetmealDish setmealDish);
    
    @Select ("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId (Long setmealId);
    
    @Delete ("delete from setmeal_dish where setmeal_id = #{sermealId}")
    int deleteBySetmealId (Long setmealId);
    
    /**
     * 批量添加
     *
     * @param setmealDishes
     * @return
     */
    int insertSetmealDishBatch (List<SetmealDish> setmealDishes);
}
