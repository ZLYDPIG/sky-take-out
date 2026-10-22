package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateById(ShoppingCart shoppingCart);

    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, dish_flavor, name, image, number, create_time) " +
            "values (#{user_id}, #{dish_id}, #{setmeal_id}, #{dish_flavor}, #{name}, #{image}, #{number}, #{create_time})")
    void insert(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void delete(Long userId);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
