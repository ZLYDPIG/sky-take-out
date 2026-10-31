package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO statisticsForTurnover(LocalDate begin, LocalDate end){
        List<LocalDate> localDateList = new ArrayList<>();

        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }
        String dataList = StringUtils.join(localDateList, ',');

        List<Double> doubleList = new ArrayList<>();

        for(LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date , LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status",Orders.COMPLETED);

            Double turnover = orderMapper.getByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            doubleList.add(turnover);
        }

        String turnoverList = StringUtils.join(doubleList, ',');

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO(dataList, turnoverList);

        return turnoverReportVO;
    }

    @Override
    public UserReportVO statisticsForUsers(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new ArrayList<>();

        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        String dataList = StringUtils.join(localDateList, ',');

        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();

            // 1. 统计截至当天的总用户数量（create_time <= endTime）
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);

            // 2. 统计当天新增用户数量（create_time >= beginTime and create_time <= endTime）
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }

        String totalUserStr = StringUtils.join(totalUserList, ',');
        String newUserStr = StringUtils.join(newUserList, ',');

        return UserReportVO.builder()
                .dateList(dataList)
                .totalUserList(totalUserStr)
                .newUserList(newUserStr)
                .build();
    }

    @Override
    public OrderReportVO statisticsForOrders(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new ArrayList<>();

        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        String dataList = StringUtils.join(localDateList, ',');

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;

        for (LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 1. 统计每日订单数（未限制 status）
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer orderCount = orderMapper.countByMap(map);
            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(orderCount);
            totalOrderCount += orderCount;

            // 2. 统计每日有效订单数（status = Orders.COMPLETED）
            map.put("status", Orders.COMPLETED);
            Integer validCount = orderMapper.countByMap(map);
            validCount = validCount == null ? 0 : validCount;
            validOrderCountList.add(validCount);
            validOrderCount += validCount;
        }

        String orderCountStr = StringUtils.join(orderCountList, ',');
        String validOrderCountStr = StringUtils.join(validOrderCountList, ',');

        // 3. 计算订单完成率（有效订单数 / 订单总数）
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(dataList)
                .orderCountList(orderCountStr)
                .validOrderCountList(validOrderCountStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin != null ? LocalDateTime.of(begin, LocalTime.MIN) : null;
        LocalDateTime endTime = end != null ? LocalDateTime.of(end, LocalTime.MAX) : null;

        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> names = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        String nameList = StringUtils.join(names, ',');
        String numberList = StringUtils.join(numbers, ',');

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

}
