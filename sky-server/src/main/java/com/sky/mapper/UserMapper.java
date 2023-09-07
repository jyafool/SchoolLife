package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    
    /**
     * 根據openid查詢用戶
     *
     * @param openid
     * @return
     */
    @Select ("select * from user where openid = #{openid}")
//    @AutoFill (OperationType.INSERT)
    User getByOpenid (String openid);
    
    int insert (User user);
    
    @Select ("select * from user where id = #{userId}")
    User getById (Long userId);
    
    Integer countByMap (Map map);
}
