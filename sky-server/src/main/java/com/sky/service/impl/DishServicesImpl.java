package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DishServicesImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //保存菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        //保存口味
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(!CollectionUtils.isEmpty(flavors)) {
        flavors.forEach(flavor -> {flavor.setDishId(dish.getId());});
        dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.selectPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids) || ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new DeletionNotAllowedException("菜品ID不能为空且必须为正数");
        }

        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish == null) {
                throw new DeletionNotAllowedException("菜品不存在，ID：" + id);
            }

            //判断是否存在起售中的菜品

            if (StatusConstant.ENABLE.equals(dish.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

            //判断是否存在套餐包含该菜品
            Integer count = setmealDishMapper.countConnectSet(id);
            if (count != null && count > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        //删除口味表中的数据
        dishFlavorMapper.deleteBatch(ids);

        //删除菜品表中的数据
        dishMapper.deleteBatch(ids);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDish(DishDTO dishDTO){
        //更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);

        //更新口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(!CollectionUtils.isEmpty(flavors)) {
            flavors.forEach(flavor -> {flavor.setDishId(dish.getId());});
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DishVO getByIdwithFlavor(Long id) {
        //查询菜品
        Dish dish = dishMapper.selectById(id);

        //查询口味
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateById(dish);

        // 如果是停售操作，还需要将包含当前菜品的套餐也停售
        if (StatusConstant.DISABLE.equals(status)) {
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(id);
            if (!CollectionUtils.isEmpty(setmealIds)) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            // 根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}