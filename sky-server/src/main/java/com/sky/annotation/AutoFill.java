package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 第一步：自定義注解，用於表示某個方法需要進行功能字段自動填充
 */
@Target (ElementType.METHOD)  // 表示該注解作用目標只能是方法上
@Retention (RetentionPolicy.RUNTIME)
public @interface AutoFill {
    
    // 數據庫操作類型：只有在更新和插入的時候才需要對時間和操作用戶的自動填充
    OperationType value ();
    
}
