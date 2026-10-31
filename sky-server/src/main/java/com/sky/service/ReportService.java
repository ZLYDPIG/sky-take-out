package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.vo.OrderReportVO;

import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    TurnoverReportVO statisticsForTurnover(LocalDate begin, LocalDate end);

    UserReportVO statisticsForUsers(LocalDate begin, LocalDate end);

    OrderReportVO statisticsForOrders(LocalDate begin, LocalDate end);

    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);
}
