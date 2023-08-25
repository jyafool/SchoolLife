package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Api ("套餐相關接口")
@RestController
@RequestMapping ("/admin/setmeal")
@Slf4j
public class SetmealController {
    
    
    @Resource (name = "setmealServiceImpl")
    private SetmealService setmealService;
    
    
    /**
     * 新增套餐：
     * 先添加套餐，再往套餐裏添加菜品
     *
     * @return
     */
    @ApiOperation ("新增套餐")
    @PostMapping
    public Result createSetmeal (@RequestBody SetmealDTO setmealDTO) {
        log.info ("添加的套餐:{}", setmealDTO);
        setmealService.createSetmeal (setmealDTO);
        return Result.success ();
    }
    
    @ApiOperation ("套餐分頁查詢")
    @GetMapping ("/page")
    public Result<PageResult> setmealPageQuery (SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info ("套餐分頁查詢信息:{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.setmealPageQuery (setmealPageQueryDTO);
        log.info ("查詢結果：{}", pageResult);
        return Result.success (pageResult);
    }
    
    /**
     * 根據id查詢套餐
     *
     * @param id
     * @return
     */
    @ApiOperation ("根據id查詢套餐")
    @GetMapping ("/{id}")
    public Result<SetmealVO> retrieveBySetmealId (@PathVariable Long id) {
        
        SetmealVO setmealVO = setmealService.retrieveBySetmealId (id);
        return Result.success (setmealVO);
    }
    
    /**
     * 修改套餐
     *
     * @param setmealDTO
     * @return
     */
    @ApiOperation ("修改套餐")
    @PutMapping
    public Result updateSetmeal (@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateSetmeal (setmealDTO);
        return Result.success ();
    }
    
    /**
     * 套餐的啓售停售
     *
     * @param status
     * @param id
     * @return
     */
    @ApiOperation ("套餐的啓售停售")
    @PostMapping ("/status/{status}")
    public Result changeStatus (@PathVariable Integer status, Long id) {
        
        setmealService.changeStatus (status, id);
        
        return Result.success ();
    }
    
    /**
     * (批量)刪除套餐
     *
     * @param ids
     * @return
     */
    @ApiOperation ("(批量)刪除套餐")
    @DeleteMapping
    public Result deleteSetmeal (@RequestParam List<Long> ids) {
        setmealService.deleteSetmeal (ids);
        
        return Result.success ();
    }
    
    
}
