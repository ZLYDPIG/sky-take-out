package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Select("select count(id) from setmeal_dish where dish_id = #{id}")
    Integer countConnectSet(Long id);

    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    void deleteBatch(@Param("ids") List<Long> ids);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);

    void deleteBySetmealId(@Param("setmealIds") List<Long> setmealIds);

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishId
     * @return
     */
    @Select("select distinct setmeal_id from setmeal_dish where dish_id = #{dishId}")
    List<Long> getSetmealIdsByDishId(Long dishId);
}
