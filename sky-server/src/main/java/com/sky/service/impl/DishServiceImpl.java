package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service ("dishServiceImpl")
@Slf4j
public class DishServiceImpl implements DishService {
    
    @Resource
    private DishMapper dishMapper;
    
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    
    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @Override
    public int addDish (DishDTO dishDTO) {
        // 將dto封裝成數據庫對應的entity
        Dish dish = new Dish ();
        BeanUtils.copyProperties (dishDTO, dish);
        
        // 先添加菜品，再獲取菜品id，再循環添加口味
        int result = dishMapper.insertDish (dish);
        
        Long dishId = dish.getId ();
        List<DishFlavor> flavors = dishDTO.getFlavors ();
        if (flavors != null) {
            flavors.forEach (dishFlavor -> dishFlavor.setDishId (dishId));
            int row = dishFlavorMapper.insertBatch (flavors);
            if (row != 0) {
                log.info ("口味添加成功。。。。。");
            }
        }
        
        return result;
    }
    
    
}
