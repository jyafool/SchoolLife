package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.RespotService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service ("respotServiceImpl")
@Slf4j
public class RespotServiceImpl implements RespotService {
    
    @Resource
    private OrderMapper orderMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private WorkspaceService workspaceService;
    
    /**
     * 統計營業額
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics (LocalDate begin, LocalDate end) {
        // 當前集合用於存放從begin到end範圍内每天的日期
        List<LocalDate> dateList = new ArrayList ();
        
        dateList.add (begin);
        
        while (!begin.equals (end)) {
            begin = begin.plusDays (1);
            dateList.add (begin);
        }
        
        // 存放每天的營業額
        List<Double> turnoverList = new ArrayList ();
        for (LocalDate date : dateList) {
            // 計算每天營業額
            LocalDateTime beginTime = LocalDateTime.of (date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of (date, LocalTime.MAX);
            
            // select sum() from orders where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap ();
            map.put ("begin", beginTime);
            map.put ("end", endTime);
            map.put ("status", Orders.COMPLETED);
            
            Double turnover = orderMapper.sumByMap (map);
            turnover = turnover == null ? 0.0 : turnover;   // 判斷營業額是否為空
            turnoverList.add (turnover);
        }
        
        return TurnoverReportVO.builder ()
                .dateList (StringUtils.join (dateList, ","))
                .turnoverList (StringUtils.join (turnoverList, ","))
                .build ();
    }
    
    /**
     * 客戶數量數據統計
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics (LocalDate begin, LocalDate end) {
        // 當前集合用於存放從begin到end範圍内每天的日期
        List<LocalDate> dateList = new ArrayList ();
        
        dateList.add (begin);
        
        while (!begin.equals (end)) {
            begin = begin.plusDays (1);
            dateList.add (begin);
        }
        
        // 分別存放每天的新增用戶數量和總用戶數量
        List<Integer> newUserList = new ArrayList<> ();
        List<Integer> totalUserList = new ArrayList<> ();
        
        for (LocalDate date : dateList) {
            // 計算每天營業額
            LocalDateTime beginTime = LocalDateTime.of (date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of (date, LocalTime.MAX);
            
            // select sum() from orders where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap ();
            map.put ("end", endTime);
            
            Integer totalUser = userMapper.countByMap (map);
            
            map.put ("begin", beginTime);
            Integer newUser = userMapper.countByMap (map);
            
            newUserList.add (newUser);
            totalUserList.add (totalUser);
        }
        
        return UserReportVO.builder ()
                .dateList (StringUtils.join (dateList, ","))
                .newUserList (StringUtils.join (newUserList, ","))
                .totalUserList (StringUtils.join (totalUserList, ","))
                .build ();
    }
    
    /**
     * 訂單統計
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics (LocalDate begin, LocalDate end) {
        // 當前集合用於存放從begin到end範圍内每天的日期
        List<LocalDate> dateList = new ArrayList ();
        
        dateList.add (begin);
        
        while (!begin.equals (end)) {
            begin = begin.plusDays (1);
            dateList.add (begin);
        }
        
        // 求訂單數和有效訂單數列表
        List<Integer> orderCountList = new ArrayList ();
        List<Integer> validOrderCountList = new ArrayList ();
        int totalOrdersCount = 0;
        int totalValidOrdersCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of (date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of (date, LocalTime.MAX);
            
            // select sum() from orders where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap ();
            map.put ("end", endTime);
            map.put ("begin", beginTime);
            
            // 計算總訂單數
            int orderCount = orderMapper.orderCountByMap (map);
            // 計算每天有效訂單數
            map.put ("status", Orders.COMPLETED);
            int validOrderCount = orderMapper.orderCountByMap (map);
            
            totalOrdersCount += orderCount;
            totalValidOrdersCount += validOrderCount;
            
            orderCountList.add (orderCount);
            validOrderCountList.add (validOrderCount);
        }
        // 簡單寫法
        /*Integer integer = orderCountList.stream ().reduce (Integer::sum).get ();
        Integer integer1 = validOrderCountList.stream ().reduce (Integer::sum).get ();*/
        Double orderCompleteRate = 0.0;
        if (totalOrdersCount != 0) {
            orderCompleteRate = Double.valueOf (totalValidOrdersCount / totalOrdersCount);
        }
        
        return OrderReportVO.builder ()
                .dateList (StringUtils.join (dateList, ","))
                .orderCountList (StringUtils.join (orderCountList, ","))
                .validOrderCountList (StringUtils.join (validOrderCountList, ","))
                .totalOrderCount (totalOrdersCount)
                .validOrderCount (totalValidOrdersCount)
                .orderCompletionRate (orderCompleteRate)
                .build ();
    }
    
    @Override
    public SalesTop10ReportVO getSalesTop10 (LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of (begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of (begin, LocalTime.MAX);
        
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10 (beginTime, endTime);
        
        List<String> names = salesTop10.stream ().map (GoodsSalesDTO::getName).collect (Collectors.toList ());
        String nameList = StringUtils.join (names, ",");
        
        List<Integer> numbers = salesTop10.stream ().map (GoodsSalesDTO::getNumber).collect (Collectors.toList ());
        String numberList = StringUtils.join (numbers, ",");
        
        return SalesTop10ReportVO.builder ()
                .nameList (nameList)
                .numberList (numberList)
                .build ();
    }
    
    /**
     * 導出運營數據報表
     *
     * @param response
     */
    @Override
    public void exportBusinessData (HttpServletResponse response) {
        // 1、查詢數據庫，獲取營業數據————查詢最近三十天運營數據
        LocalDate dateBegin = LocalDate.now ().minusDays (30);
        LocalDate dateEnd = LocalDate.now ().minusDays (1);
        
        // 查詢概覽數據
        BusinessDataVO businessDataVO = workspaceService.getBusinessData (LocalDateTime.of (dateBegin, LocalTime.MIN), LocalDateTime.of (dateEnd, LocalTime.MAX));
        
        // 2、通過poi將數據寫入到excel文件中
        InputStream inputStream = this.getClass ().getClassLoader ().getResourceAsStream ("template/运营数据报表模板.xlsx");
        
        try {
            // 基於模板文件創建一個新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook (inputStream);
            
            // 填充數據
            XSSFSheet sheet = excel.getSheet ("Sheet1");
            
            // 時間
            sheet.getRow (1).getCell (1).setCellValue ("時間：" + dateBegin + "至" + dateEnd);
            
            // 數據
            XSSFRow row3 = sheet.getRow (3);
            row3.getCell (2).setCellValue (businessDataVO.getTurnover ());
            row3.getCell (4).setCellValue (businessDataVO.getOrderCompletionRate ());
            row3.getCell (6).setCellValue (businessDataVO.getNewUsers ());
            
            XSSFRow row4 = sheet.getRow (4);
            row4.getCell (2).setCellValue (businessDataVO.getValidOrderCount ());
            row4.getCell (4).setCellValue (businessDataVO.getUnitPrice ());
            
            // 填充明細數據
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays (i);
                // 查詢當天營業數據
                BusinessDataVO businessData = workspaceService.getBusinessData (LocalDateTime.of (date, LocalTime.MIN), LocalDateTime.of (date, LocalTime.MAX));
                
                // 獲取某一行
                XSSFRow row = sheet.getRow (7 + i);
                row.getCell (1).setCellValue (date.toString ());
                row.getCell (2).setCellValue (businessData.getTurnover ());
                row.getCell (3).setCellValue (businessData.getValidOrderCount ());
                row.getCell (4).setCellValue (businessData.getOrderCompletionRate ());
                row.getCell (5).setCellValue (businessData.getUnitPrice ());
                row.getCell (6).setCellValue (businessData.getNewUsers ());
                
            }
            
            // 3、通過輸出流將excel文件下載到客戶端瀏覽器
            ServletOutputStream outputStream = response.getOutputStream ();
            excel.write (outputStream);
            
            // 4、關閉資源
            outputStream.close ();
            excel.close ();
            
        } catch (IOException e) {
            throw new RuntimeException (e);
        }
        
        
    }
}
