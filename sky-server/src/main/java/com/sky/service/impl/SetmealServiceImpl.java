package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 套餐相關業務
 */
@Service ("setmealServiceImpl")
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    
    @Resource
    private SetmealMapper setmealMapper;
    
    @Resource
    private SetmealDishMapper setmealDishMapper;
    
    /**
     * 套餐中包含套餐菜品信息，這應該分兩次添加：
     * 先添加套餐，再批量添加套餐相關的菜品
     * 并且跟分類的表關聯（不用管，知道就行）：
     * 即套餐的分類必須先存在與分類表中，才能添加
     *
     * @param setmealDTO
     * @return
     */
    @Transactional  // 涉及到多表的修改，應該開啓事務
    @Override
    public int createSetmeal (SetmealDTO setmealDTO) {
        
        // 套餐的添加可以默認為啓用
        Setmeal setmeal = new Setmeal ();
        BeanUtils.copyProperties (setmealDTO, setmeal);
        // 添加套餐
        setmealMapper.insertSetmeal (setmeal);
        // 循環添加套餐相關菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes ();
        setmealDishes.forEach (setmealDish -> {
            // 這裏用批量添加會更好，可以減少數據庫的訪問次數
            setmealDish.setSetmealId (setmeal.getId ());
            setmealDishMapper.insertSetmealDish (setmealDish);
        });
        return 0;
    }
    
    /**
     * 套餐分頁查詢:
     * 既要查套餐表，也要查套餐-菜品表
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult setmealPageQuery (SetmealPageQueryDTO setmealPageQueryDTO) {
        // 先對套餐表分頁查詢進行頁數和每頁條數的封裝
        PageHelper.startPage (setmealPageQueryDTO.getPage (), setmealPageQueryDTO.getPageSize ());
        log.info ("開始調用數據庫查詢。。。");
        //
        Page<SetmealVO> page = setmealMapper.setmealPageQuery (setmealPageQueryDTO);
        List<SetmealVO> setmealVOS = page.getResult ();
        // 再查套餐-菜品表
        setmealVOS.forEach (setmealVO -> {
            List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId (setmealVO.getId ());
            setmealVO.setSetmealDishes (setmealDishes);
        });


//        System.out.println ("----daole ----------------------------------");
        /*// 最後進行封裝返回
        setmealVOS.forEach (setmeal -> {
            // 獲取每個套餐id
            Long setmealId = setmeal.getId ();
            // 根據套餐id遍歷所有菜品集合
            setmealDishes.forEach (setmealDish -> {
                if (setmealId == setmealDish.getSetmealId ()){
                    // 把菜品賦值到套餐vo裏
                    setmeal.setSetmealDishes ();
                }
            });
            setmeal.setSetmealDishes ();
        });*/
        
        return new PageResult (page.getTotal (), setmealVOS);
    }
    
    @Override
    public SetmealVO retrieveBySetmealId (Long id) {
        SetmealVO setmealVO = setmealMapper.selectBySetmealId (id);
        // 還要查詢套餐相關菜品
        setmealVO.setSetmealDishes (setmealDishMapper.selectBySetmealId (id));
        return setmealVO;
    }
    
    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional // 雙表修改
    public void updateSetmeal (SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal ();
        BeanUtils.copyProperties (setmealDTO, setmeal);
        // 先把套餐表相關信息修改
        setmealMapper.updateSetmeal (setmeal);
        Long setmealId = setmealDTO.getId ();
        // 再修改套餐-菜品關聯表:先刪後加
        setmealDishMapper.deleteBySetmealId (setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes ();
        setmealDishes.forEach (setmealDish -> setmealDish.setSetmealId (setmealId));
        setmealDishMapper.insertSetmealDishBatch (setmealDishes);
    }
    
    /**
     * 套餐的啓售停售:
     * 套餐啓售條件：所有菜品為啓售狀態
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public int changeStatus (Integer status, Long id) {
        
        // 想將套餐啓售
        if (status == StatusConstant.ENABLE) {
            // 先判斷菜品是否有停售
            List<Dish> dishes = setmealMapper.selectSetmealDishesBySetmealId (id);
            //
            if (dishes.size () > 0 && dishes != null) {
                dishes.forEach (dish -> {
                    if (dish.getStatus () == StatusConstant.DISABLE) {
                        throw new SetmealEnableFailedException (MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        
        Setmeal setmeal = Setmeal.builder ()
                .status (status)
                .id (id)
                .build ();
        return setmealMapper.updateSetmeal (setmeal);
    }
    
    /**
     * 套餐的批量刪除：
     * 1、在啓售狀態的套餐不能刪除
     * 2、刪除套餐的同時也要將套餐-菜品關聯表中的相關數据刪除
     *
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public int deleteSetmeal (List<Long> ids) {
        // 先根據id遍歷查詢每份套餐判斷其售賣狀態
        ids.forEach (setmealId -> {
            SetmealVO setmealVO = setmealMapper.selectBySetmealId (setmealId);
            log.info ("當前套餐id：{}，狀態：{}", setmealVO.getId (), setmealVO.getStatus ());
            if (setmealVO.getStatus () == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException (MessageConstant.SETMEAL_ON_SALE);
            }
        });
    
    
        // 再刪關聯表
        ids.forEach (id -> {
            log.info ("要刪除的關聯表id：{}", id);
            setmealDishMapper.deleteBySetmealId (id);
        });
    
        // 先刪套餐表
        return setmealMapper.deleteSetmealBatch (ids);
    }
    
    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    public List<Setmeal> list (Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list (setmeal);
        return list;
    }
    
    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById (Long id) {
        
        return setmealMapper.getDishItemBySetmealId (id);
    }
    
    
}
