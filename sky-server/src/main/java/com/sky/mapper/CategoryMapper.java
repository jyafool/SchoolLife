package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    /**
     * 添加菜品分類：葷菜、素菜、涼菜等等
     *
     * @param category
     * @return
     */
    @Insert ("""
            insert into category
            (type, name, sort, status, create_time, update_time, create_user, update_user)
            VALUES
             (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})
            """)
    int insertNewCategory (Category category);
    
    /**
     * 按類型查詢
     *
     * @param type
     * @return
     */
    List<Category> selectByType (Integer type);
    
    /**
     * 分頁查詢
     *
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> categoryPageQuery (CategoryPageQueryDTO categoryPageQueryDTO);
    
    //@Param ("category") xxxx
    
    /**
     * 修改分類
     *
     * @param category
     * @return
     */
    int updateCategory (Category category);
    
    @Delete ("delete from category where id = #{id}")
    int deleteById (Long id);
}
