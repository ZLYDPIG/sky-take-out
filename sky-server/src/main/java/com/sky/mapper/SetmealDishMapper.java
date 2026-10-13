package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealDishMapper {

    @Select("select count(id) from setmeal_dish where dish_id = #{id}")
    Integer countConnectSet(Long id);
}
