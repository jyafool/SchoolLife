package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.RespotService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 數據統計相關接口
 */
@RestController
@RequestMapping ("/admin/report")
@Slf4j
@Api (tags = "數據統計相關接口")
public class ReportController {
    
    @Resource (name = "respotServiceImpl")
    private RespotService respotService;
    
    @GetMapping ("/turnoverStatistics")
    @ApiOperation ("營業額統計")
    public Result<TurnoverReportVO> turnoverStatistics (
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end) {
        log.info ("營業額數據統計：{}，{}", begin, end);
        return Result.success (respotService.getTurnoverStatistics (begin, end));
    }
    
    /**
     * 客戶數量數據統計
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping ("/userStatistics")
    @ApiOperation ("客戶數量數據統計")
    public Result<UserReportVO> userStatistics (
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end) {
        log.info ("客戶數量數據統計：{}，{}", begin, end);
        return Result.success (respotService.getUserStatistics (begin, end));
    }
    
    @GetMapping ("/ordersStatistics")
    @ApiOperation ("訂單統計")
    public Result<OrderReportVO> ordersStatistics (
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end) {
        log.info ("訂單數據統計：{}，{}", begin, end);
        return Result.success (respotService.ordersStatistics (begin, end));
    }
    
    @GetMapping ("/top10")
    @ApiOperation ("top10統計")
    public Result<SalesTop10ReportVO> top10Statistics (
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end) {
        log.info ("top10統計：{}，{}", begin, end);
        return Result.success (respotService.getSalesTop10 (begin, end));
    }
    
    /**
     * 導出運營數據報表
     *
     * @param response
     * @return
     */
    @GetMapping ("/export")
    @ApiOperation ("導出運營數據報表")
    public Result export (HttpServletResponse response) {
        respotService.exportBusinessData (response);
        return Result.success ();
    }
}
