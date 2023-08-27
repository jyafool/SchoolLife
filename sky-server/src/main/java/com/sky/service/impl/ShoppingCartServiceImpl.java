package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service ("shoppingCartServiceImpl")
public class ShoppingCartServiceImpl implements ShoppingCartService {
    
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    
    @Resource
    private DishMapper dishMapper;
    
    @Resource
    private SetmealMapper setmealMapper;
    
    /**
     * 添加購物車
     *
     * @param shoppingCartDTO
     */
    @Override
    public void add (ShoppingCartDTO shoppingCartDTO) {
        // 判斷當前加入到購物車中的商品是否已存在
        ShoppingCart shoppingCart = new ShoppingCart ();
        BeanUtils.copyProperties (shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId ();
        shoppingCart.setUserId (userId);
        
        List<ShoppingCart> list = shoppingCartMapper.list (shoppingCart);
        
        // 如果存在，只需添加數量
        if (list != null && list.size () > 0) {
            ShoppingCart cart = list.get (0);
            cart.setNumber (cart.getNumber () + 1);
            shoppingCartMapper.updateNumberById (cart);
        } else {
            // 如果不存在，需要插入一條購物車數據
            
            // 判斷本次添加到購物車的是菜品還是套餐
            Long dishId = shoppingCartDTO.getDishId ();
            if (dishId != null) {
                // 本次添加到購物車的是菜品
                Dish dish = dishMapper.selectDishById (dishId);
                shoppingCart.setName (dish.getName ());
                shoppingCart.setImage (dish.getImage ());
                shoppingCart.setAmount (dish.getPrice ());
                
            } else {
                // 本次添加到購物車的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId ();
                SetmealVO setmealVO = setmealMapper.selectBySetmealId (setmealId);
                shoppingCart.setName (setmealVO.getName ());
                shoppingCart.setImage (setmealVO.getImage ());
                shoppingCart.setAmount (setmealVO.getPrice ());
            }
            shoppingCart.setCreateTime (LocalDateTime.now ());
            // 初次添加，數量為1
            shoppingCart.setNumber (1);
            
            shoppingCartMapper.insert (shoppingCart);
            
        }
        
        
    }
    
    @Override
    public List<ShoppingCart> list () {
        ShoppingCart shoppingCart = ShoppingCart.builder ()
                .userId (BaseContext.getCurrentId ())
                .build ();
        return shoppingCartMapper.list (shoppingCart);
    }
    
    @Override
    public void cleanShoppingCart () {
        ShoppingCart shoppingCart = ShoppingCart.builder ()
                .userId (BaseContext.getCurrentId ())
                .build ();
        shoppingCartMapper.cleanShoppingCart (shoppingCart);
    }
}
