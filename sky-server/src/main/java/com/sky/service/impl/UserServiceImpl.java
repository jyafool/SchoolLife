package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service ("userServiceImpl")
public class UserServiceImpl implements UserService {
    
    // 微信服務接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    
    @Resource
    private WeChatProperties weChatProperties;
    @Resource
    private UserMapper userMapper;
    
    @Override
    public User login (UserLoginDTO userLoginDTO) {
        //調用微信接口服務，獲得當前微信用戶的openid
        String openid = getOpenid (userLoginDTO.getCode ());
        
        // 判斷openid是否爲空，如果爲空表示登錄失敗，抛出業務異常
        if (openid == null) {
            throw new LoginFailedException (MessageConstant.LOGIN_FAILED);
        }
        
        // 判斷當前用戶是否爲新用戶
        User user = userMapper.getByOpenid (openid);
        
        // 如果是新用戶，自動完成注冊
        if (user == null) {
            user = User.builder ()
                    .openid (openid)
                    .createTime (LocalDateTime.now ())
                    .build ();
            userMapper.insert (user);
        }
        
        // 返回這個用戶對象
        return user;
    }
    
    /**
     * 調用微信接口服務，獲得當前微信用戶的openid
     *
     * @param code
     * @return
     */
    private String getOpenid (String code) {
        HashMap<String, String> map = new HashMap<> ();
        map.put ("appid", weChatProperties.getAppid ());
        map.put ("secret", weChatProperties.getSecret ());
        map.put ("js_code", code);
        map.put ("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet (WX_LOGIN, map);
        
        JSONObject jsonObject = JSON.parseObject (json);
        String openid = jsonObject.getString ("openid");
        return openid;
    }
}
