package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping ("/admin/employee")
@Slf4j
@Api (tags = "員工相關接口")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;
    
    
    @ApiOperation (value = "添加新員工")
    @PostMapping// ("/addEmployee")
    public Result<Employee> addEmployee (@RequestBody EmployeeDTO employeeDTO) {
        log.info ("新增的員工：{}", employeeDTO);
        int addResult = employeeService.add (employeeDTO);
        log.info ("addResult:{}", addResult);
        
        return Result.success ();
    }
    
    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation (value = "員工登錄")
    @PostMapping ("/login")
    public Result<EmployeeLoginVO> login (@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        
        // 前端携帶數據發起請求被控制層接收，此時要將前端帶過來的數據進行對象的封裝，即使用dto對象
        log.info ("员工登录：{}", employeeLoginDTO);
        
        Employee employee = employeeService.login (employeeLoginDTO);
        
        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<> ();
        
        claims.put (JwtClaimsConstant.EMP_ID, employee.getId ());
        String token = JwtUtil.createJWT (
                jwtProperties.getAdminSecretKey (),
                jwtProperties.getAdminTtl (),
                claims);
        
        // 要相應參數時，即把數據model封裝回到前端視圖層進行數據展示，此時用vo對象
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder ()
                .id (employee.getId ())
                .userName (employee.getUsername ())
                .name (employee.getName ())
                .token (token)
                .build ();
        
        return Result.success (employeeLoginVO);
    }
    
    /**
     * 退出
     *
     * @return
     */
    @ApiOperation (value = "員工登出")
    @PostMapping ("/logout")
    public Result<String> logout () {
        return Result.success ();
    }
    
}
