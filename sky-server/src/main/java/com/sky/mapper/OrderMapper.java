package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据并返回自增主键
     *
     * @param orders 订单实体
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into orders (number, status, user_id, address_book_id, order_time, checkout_time, " +
            "pay_method, pay_status, amount, remark, phone, address, user_name, consignee, " +
            "cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, " +
            "delivery_time, pack_amount, tableware_number, tableware_status) " +
            "values (#{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, " +
            "#{payMethod}, #{payStatus}, #{amount}, #{remark}, #{phone}, #{address}, #{userName}, #{consignee}, " +
            "#{cancelReason}, #{rejectionReason}, #{cancelTime}, #{estimatedDeliveryTime}, #{deliveryStatus}, " +
            "#{deliveryTime}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})")
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Select("select * from orders where status = #{status} and order_time < #{overTime}")
    List<Orders> getStatusAndTimeOut(Integer status, LocalDateTime overTime);

    Page<Orders> selectPage(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    Double getByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);


    List<GoodsSalesDTO> getSalesTop10(@Param("beginTime") LocalDateTime beginTime, @Param("endTime") LocalDateTime endTime);
}
