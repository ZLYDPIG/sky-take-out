package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

   @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
       log.info("营业额统计");
       TurnoverReportVO turnoverReportVO = reportService.statisticsForTurnover(begin, end);
       return Result.success(turnoverReportVO);
   }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
       log.info("用户统计");
       UserReportVO userReportVO = reportService.statisticsForUsers(begin,end);
       return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
       log.info("订单统计");
       OrderReportVO orderReportVO = reportService.statisticsForOrders(begin, end);
       return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> countTop10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
       log.info("销量排名:Top10");
       SalesTop10ReportVO salesTop10ReportVO = reportService.top10(begin,end);
       return Result.success(salesTop10ReportVO);
    }
}
