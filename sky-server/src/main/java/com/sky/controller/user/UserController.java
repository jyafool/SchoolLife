package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RequestMapping ("/user/user")
@RestController
@Slf4j
@Api (tags = "c端用戶相關接口")
public class UserController {
    
    @Resource (name = "userServiceImpl")
    private UserService userService;
    
    @Resource
    private JwtProperties jwtProperties;
    
    @ApiOperation ("用戶登錄")
    @PostMapping ("/login")
    public Result<UserLoginVO> login (@RequestBody UserLoginDTO userLoginDTO) {
        log.info ("用戶登錄：{}", userLoginDTO);
        
        User user = userService.login (userLoginDTO);
        
        // 為微信用戶生成token
        Map<String, Object> claims = new HashMap<> ();
        claims.put (JwtClaimsConstant.USER_ID, user.getId ());
        String token = JwtUtil.createJWT (
                jwtProperties.getUserSecretKey (),
                jwtProperties.getUserTtl (),
                claims);
        
        UserLoginVO userLoginVO = UserLoginVO.builder ()
                .id (user.getId ())
                .openid (user.getOpenid ())
                .token (token)
                .build ();
        
        return Result.success (userLoginVO);
    }
    
}
