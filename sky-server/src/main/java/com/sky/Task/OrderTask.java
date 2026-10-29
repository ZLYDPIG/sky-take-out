package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务处理类
 */
@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时未支付订单
     * 规则：每分钟执行一次，检查下单超过 15 分钟仍未支付（PENDING_PAYMENT）的订单，自动取消
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void processTimeoutOrder() {
        log.info("处理超时未支付订单: {}", LocalDateTime.now());

        // 下单时间超过 15 分钟：当前时间减去 15 分钟
        LocalDateTime overTime = LocalDateTime.now().minusMinutes(15);

        List<Orders> ordersList = orderMapper.getStatusAndTimeOut(Orders.PENDING_PAYMENT, overTime);
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     * 规则：每天凌晨 1 点执行一次，检查上一天（当前时间 - 60分钟之前）仍处于派送中（DELIVERY_IN_PROGRESS）的订单，自动修改为已完成
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨 1 点执行一次
    public void processDeliveryOrder() {
        log.info("处理派送超时订单: {}", LocalDateTime.now());

        // 当前时间（凌晨1点）减去 60 分钟，即昨天凌晨 0 点之前的派送中订单
        LocalDateTime overTime = LocalDateTime.now().minusMinutes(60);

        List<Orders> ordersList = orderMapper.getStatusAndTimeOut(Orders.DELIVERY_IN_PROGRESS, overTime);
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
