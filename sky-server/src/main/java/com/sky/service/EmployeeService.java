package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

/**
 * 员工相关业务接口。
 */
public interface EmployeeService {

    /**
     * 员工登录。
     *
     * <p>校验用户名、密码、账号状态，全部通过后返回员工实体，
     * 由 Controller 负责据此签发 JWT 令牌。</p>
     *
     * @param employeeLoginDTO 前端提交的登录信息（用户名 + 密码）
     * @return 登录成功的员工实体
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);
}
