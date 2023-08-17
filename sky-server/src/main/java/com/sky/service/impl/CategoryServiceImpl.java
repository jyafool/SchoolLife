package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service (value = "categoryServiceImpl")
public class CategoryServiceImpl implements CategoryService {
    
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;
    
    
    /**
     * 添加菜品分類：葷菜、素菜、涼菜等等
     *
     * @param categoryDTO
     * @return
     */
    @Override
    public int addNewCategory (CategoryDTO categoryDTO) {
        Category category = Category.builder ()
                .createTime (LocalDateTime.now ())
                .updateTime (LocalDateTime.now ())
                .createUser (BaseContext.getCurrentId ())
                .updateUser (BaseContext.getCurrentId ())
                .status (StatusConstant.DISABLE)
                .build ();
        
        BeanUtils.copyProperties (categoryDTO, category);
        
        return categoryMapper.insertNewCategory (category);
    }
    
    /**
     * 按類型查詢
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> getCategoryByType (Integer type) {
        return categoryMapper.selectByType (type);
    }
    
    /**
     * 分頁查詢
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult categoryPageQuery (CategoryPageQueryDTO categoryPageQueryDTO) {
        // 使用分頁插件，將參數傳遞進去，攔截查詢語句，進行菜品分頁查詢
        PageHelper.startPage (categoryPageQueryDTO.getPage (), categoryPageQueryDTO.getPageSize ());
        
        // 數據庫查詢返回封裝好的page
        Page<Category> categoryPage = categoryMapper.categoryPageQuery (categoryPageQueryDTO);
        
        
        return new PageResult (categoryPage.getTotal (), categoryPage.getResult ());
    }
    
    /**
     * 啓用、禁用分類
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public int changeStatus (Integer status, Long id) {
        // 先將參數進行封裝再傳入數據庫修改
        Category category = Category.builder ()
                .status (status)
                .id (id)
                .updateUser (BaseContext.getCurrentId ())
                .updateTime (LocalDateTime.now ())
                .build ();
        return categoryMapper.updateCategory (category);
    }
    
    /**
     * 修改分類
     *
     * @param categoryDTO
     * @return
     */
    @Override
    public int updateCategory (CategoryDTO categoryDTO) {
        // 先進行封裝成entity
        Category category = Category.builder ()
                .updateTime (LocalDateTime.now ())
                .createUser (BaseContext.getCurrentId ())
                .build ();
        BeanUtils.copyProperties (categoryDTO, category);
        
        return categoryMapper.updateCategory (category);
    }
    
    /**
     * 根據id刪除分類
     *
     * @return
     */
    @Override
    public int deleteCategoryById (Long id) {
        // TODO: 2023/8/17 這裏需要判斷當前類是否含有菜品以及關聯套餐，
        Integer count = dishMapper.countByCategoryId (id);
        if (count > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException (MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        
        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId (id);
        if (count > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException (MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        
        //删除分类数据
        
        return categoryMapper.deleteById (id);
    }
}
