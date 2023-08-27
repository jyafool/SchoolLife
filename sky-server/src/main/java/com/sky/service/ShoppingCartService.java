package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    
    /**
     * 添加購物車
     *
     * @param shoppingCartDTO
     */
    void add (ShoppingCartDTO shoppingCartDTO);
    
    List<ShoppingCart> list ();
    
    void cleanShoppingCart ();
}
