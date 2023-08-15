package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login (EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername ();
        String password = employeeLoginDTO.getPassword ();
    
        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername (username);
    
        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException (MessageConstant.ACCOUNT_NOT_FOUND);
        }
    
        //密码比对
    
        // 對前端傳過來的明文密碼進行md5加密處理
        password = DigestUtils.md5DigestAsHex (password.getBytes ());
    
        //
        if (!password.equals (employee.getPassword ())) {
            //密码错误
            throw new PasswordErrorException (MessageConstant.PASSWORD_ERROR);
        }
    
        if (employee.getStatus () == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException (MessageConstant.ACCOUNT_LOCKED);
        }
    
        //3、返回实体对象
        return employee;
    }
    
    /**
     * 添加新員工
     *
     * @param employeeDTO
     * @return
     */
    @Override
    public int add (EmployeeDTO employeeDTO) {
        // 因爲前端傳過來的只是部分員工數據，即添加員工時必須的輸入字段
        // 對於部分不需要填寫的字段，如創建時間，初始密碼統一是123456這些，就需要後臺進行自動補充
        // 再保存到數據庫
        Employee employee = new Employee ();
        
        // 對象屬性拷貝，兩對象的字段保持一致才能實現拷貝
        BeanUtils.copyProperties (employeeDTO, employee);
        
        // 再把其他沒有的字段進行完善
        
        // 用戶狀態 0表示鎖定，1表示正常
        employee.setStatus (StatusConstant.ENABLE);
        
        //
        employee.setCreateTime (LocalDateTime.now ());
        employee.setUpdateTime (LocalDateTime.now ());
        
        // 默認密碼進行初始化，需要進行md5加密再傳入數據庫
        employee.setPassword (DigestUtils.md5DigestAsHex (PasswordConstant.DEFAULT_PASSWORD.getBytes ()));
    
        //
        // TODO: 2023/8/15 後期需要改爲當前用戶登錄的id(已完成)
        employee.setCreateUser (BaseContext.getCurrentId ());
        employee.setUpdateUser (BaseContext.getCurrentId ());
    
        return employeeMapper.saveEmployee (employee);
    }
    
    /**
     * 員工分頁查詢
     *
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery (EmployeePageQueryDTO employeePageQueryDTO) {
        
        // 使用分頁插件，將參數傳遞進去，攔截查詢語句，進行員工分頁查詢
        PageHelper.startPage (employeePageQueryDTO.getPage (), employeePageQueryDTO.getPageSize ());
        
        Page<Employee> page = employeeMapper.pageQuery (employeePageQueryDTO);
        
        long total = page.getTotal ();
        List<Employee> records = page.getResult ();
        
        System.out.println ("total:" + total);
        System.out.println ("records:" + records);
        
        return new PageResult (total, records);
    }
    
    /**
     * 修改員工賬號狀態
     *
     * @param status
     * @return
     */
    @Override
    public int setStatus (Integer status, Long id) {
        // 在此應該先對屬性進行封裝，封裝對象，養成規範，
        Employee employee = Employee.builder ()
                .id (id)
                .status (status)
                .build ();
        return employeeMapper.setStatus (employee);
    }
    
}
