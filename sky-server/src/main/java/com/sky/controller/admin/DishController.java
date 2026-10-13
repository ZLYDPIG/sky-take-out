package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public Result<Long> add(@RequestBody DishDTO dishDTO){
        log.info("添加菜品：{}", dishDTO);
        Long dishId = dishService.saveWithFlavor(dishDTO);
        return Result.success(dishId);
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
       log.info("分页查询菜品：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result delete(@RequestParam("ids") List<Long> ids){
        log.info("删除菜品：{}", ids);
        dishService.deleteByIds(ids);
        return Result.success();
    }


    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdwithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("更新菜品：{}", dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status, Long id){
        log.info("修改起售状态:{}", status);
        dishService.updateStatus(status, id);
        return Result.success();
    }
}