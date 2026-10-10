package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {
    }

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("自动填充参数");

        //获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        //获取方法注解
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);

        //获取数据库操作类型
        OperationType operationType = autoFill.value();

        // 获取待填充的实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("自动填充方法必须传入实体对象");
        }
        Object entity = args[0]; //运行类型是实体类

        LocalDateTime now = LocalDateTime.now(); //当前时间
        Long currentId = BaseContext.getCurrentId();//当前登录用户id

        if (OperationType.INSERT.equals(operationType)) {
            try {
                //获取方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //判断操作类型

                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);

            } catch (ReflectiveOperationException e) {
                log.error("自动填充公共字段失败", e);
                throw new IllegalStateException("自动填充公共字段失败", e);
            }
        }
        else if (OperationType.UPDATE.equals(operationType)) {
            try {
                //获取方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //判断操作类型
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);

            } catch (ReflectiveOperationException e) {
                log.error("自动填充公共字段失败", e);
                throw new IllegalStateException("自动填充公共字段失败", e);
            }
        }
    }
}
