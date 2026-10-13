package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    Long saveWithFlavor(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void deleteByIds(List<Long> ids);

    void updateDish(DishDTO dishDTO);

    DishVO getByIdwithFlavor(Long id);

    void updateStatus(Integer status, Long id);
}