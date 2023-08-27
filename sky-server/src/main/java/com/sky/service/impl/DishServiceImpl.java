package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service ("dishServiceImpl")
@Slf4j
public class DishServiceImpl implements DishService {
    
    @Resource
    private DishMapper dishMapper;
    
    @Resource
    private SetmealDishMapper setmealDishMapper;
    
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
    
    /**
     * 菜品分頁查詢
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult dishPageQuery (DishPageQueryDTO dishPageQueryDTO) {
        // 設置分頁查詢的起始頁數和每頁條數
        PageHelper.startPage (dishPageQueryDTO.getPage (), dishPageQueryDTO.getPageSize ());
        
        Page<DishVO> page = dishMapper.dishPageQuery (dishPageQueryDTO);
        
        return new PageResult (page.getTotal (), page.getResult ());
    }
    
    /**
     * 菜品刪除業務邏輯：
     * 可以單個或者多個菜品刪除
     * 起售中的菜品不能刪除
     * 被套餐關連的菜品不能刪除
     * 刪除菜品后，關聯的口味數據也一并刪除
     *
     * @param ids
     */
    @Override
    @Transactional // 涉及到多表的增刪改應該使用事務注解，保證原子性
    public void deleteDish (List<Long> ids) {
        // 遍歷id挨個刪除
        for (Long id : ids) {
            Dish dish = dishMapper.selectDishById (id);
            // 1、先判斷菜品是否能夠被刪除
            // 判斷菜品是否正在起售中
            if (dish.getStatus () == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException (MessageConstant.DISH_ON_SALE);
            }
        }
        
        // TODO: 2023/8/19 這裏沒太懂
        // 判斷菜品是否套餐關聯,要先查詢套餐
        List<Long> setmealIds = setmealDishMapper.selectByDishId (ids);
        if (setmealIds != null && setmealIds.size () > 0) {
            // 套餐關聯了，不能刪除
            throw new DeletionNotAllowedException (MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        
        for (Long id : ids) {
            // 2、刪除菜品，連同口味一并刪除
            dishMapper.deleteById (id);
            dishFlavorMapper.deleteByDishId (id);
            log.info ("id為{}的菜品刪除完成", id);
        }
        
        
    }
    
    /**
     * 根據id查詢菜品：
     * 在查詢菜品的時候，同時要將菜品的口味查出來
     * 這涉及到兩張表的查詢
     *
     * @param id
     * @return
     */
    @Override
    public DishVO selectDishByIdWithFlavors (Long id) {
        DishVO dishVO = new DishVO ();
        // 1、先將dish查出來
        Dish dish = dishMapper.selectDishById (id);
        BeanUtils.copyProperties (dish, dishVO);
        // 2、再把flavor查出來
        dishVO.setFlavors (dishFlavorMapper.selectByDishId (id));
        // 封裝后返回
        return dishVO;
    }
    
    /**
     * 修改菜品信息:
     * 修改信息時會遇到口味的修改問題：先查詢該口味——再對比不同進行更改？
     * 這樣很麻煩，解決方法：先將口味表中的對應口味直接刪除，然後再進行新口味的添加
     */
    @Transactional  //
    @Override
    public void updateDish (DishDTO dishDTO) {
        //
        Dish dish = new Dish ();
        // 屬性拷貝
        BeanUtils.copyProperties (dishDTO, dish);
    
        Long dishId = dish.getId ();
    
        // 再對菜品表進行更新
        dishMapper.updateDish (dish);
    
        // 先將口味表刪除
        dishFlavorMapper.deleteByDishId (dishId);
        // 再添加新口味
        List<DishFlavor> flavors = dishDTO.getFlavors ();
        if (flavors != null && flavors.size () > 0) {
            flavors.forEach (dishFlavor -> dishFlavor.setDishId (dishId));
            dishFlavorMapper.insertBatch (flavors);
        }
    
    
    }
    
    /**
     * 啓售、停售的菜品
     *
     * @param dishDTO
     */
    @Override
    public void changeStatus (DishDTO dishDTO) {
        Dish dish = new Dish ();
        BeanUtils.copyProperties (dishDTO, dish);
        dishMapper.updateDish (dish);
    
    }
    
    /**
     * 根據分類id查詢菜品
     */
    @Override
    public List<Dish> selectByCategoryId (Long categoryId) {
        Dish dish = Dish.builder ()
                .categoryId (categoryId)
                .build ();
        return dishMapper.list (dish);
    }
    
    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor (Dish dish) {
        List<Dish> dishList = dishMapper.list (dish);
        
        List<DishVO> dishVOList = new ArrayList<> ();
        
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO ();
            BeanUtils.copyProperties (d, dishVO);
            
            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId (d.getId ());
            
            dishVO.setFlavors (flavors);
            dishVOList.add (dishVO);
        }
        
        return dishVOList;
    }
    
    
}
