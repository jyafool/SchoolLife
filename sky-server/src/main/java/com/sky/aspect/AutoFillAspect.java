package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    
    /**
     * 切入點：面向mapper中的insert和update語句
     */
    @Pointcut (value = "execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut () {
    }
    
    /**
     * 前置通知，在通知中進行公共字段的賦值
     */
    @Before ("autoFillPointCut()")
    public void autoFill (JoinPoint joinPoint) {
        log.info ("開始進行公共字段的賦值。。。。。");
        
        // 獲取到當前被攔截的方法上的數據庫操作類型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature ();   // 方法簽名對象
        AutoFill autoFill = signature.getMethod ().getAnnotation (AutoFill.class);   // 獲得方法上的注解對象
        OperationType operationType = autoFill.value (); // 獲得數據庫操作類型
        
        // 獲取到當前被攔截的方法的參數--實體對象
        Object[] args = joinPoint.getArgs ();
        if (args == null || args.length == 0) {
            return;
        }
        
        // 定義一個規範，將實體放在第一給參數位置
        Object entity = args[0];
        
        // 準備賦值的數據
        LocalDateTime now = LocalDateTime.now ();
        Long currentId = BaseContext.getCurrentId ();
        
        // 根據當下不同的操作類型，為對應的屬性通過反射來賦值
        try {
            // 修改和新增都要對更新人和時間進行刷新
            Method setUpdateTimeMethod = entity.getClass ().getDeclaredMethod (AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUserMethod = entity.getClass ().getDeclaredMethod (AutoFillConstant.SET_UPDATE_USER, Long.class);
            
            setUpdateTimeMethod.invoke (entity, now);
            setUpdateUserMethod.invoke (entity, currentId);
            
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
        
        if (operationType == OperationType.INSERT) {
            try {
                Method setUpdateTimeMethod = entity.getClass ().getDeclaredMethod (AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass ().getDeclaredMethod (AutoFillConstant.SET_CREATE_USER, Long.class);
                
                setUpdateTimeMethod.invoke (entity, now);
                setUpdateUserMethod.invoke (entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException (e);
            }
        }
    }
}
