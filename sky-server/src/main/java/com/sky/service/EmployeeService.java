package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {
    
    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    Employee login (EmployeeLoginDTO employeeLoginDTO);
    
    /**
     * 添加新員工
     *
     * @param employeeDTO
     * @return
     */
    int add (EmployeeDTO employeeDTO);
    
    /**
     * 員工分頁查詢
     *
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery (EmployeePageQueryDTO employeePageQueryDTO);
    
    /**
     * 修改員工賬號狀態
     *
     * @param status
     * @return
     */
    int setStatus (Integer status, Long id);
}
