package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.sky.constant.PasswordConstant.DEFAULT_PASSWORD;

/**
 * 员工业务实现类。
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录。
     *
     * @param employeeLoginDTO 前端提交的用户名和密码
     * @return 登录成功的员工实体
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        // 注意：当前是明文比对，数据库里存的就是明文密码（初始账号 admin / 123456）
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工。
     *
     * @param employeeDTO 前端提交的员工信息（姓名、用户名、手机号、性别、身份证号等）
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //1、使用属性拷贝，将DTO中相同字段的值复制到Employee实体对象
        BeanUtils.copyProperties(employeeDTO, employee);

        //2、设置账号状态为启用（新增员工默认启用）
        employee.setStatus(StatusConstant.ENABLE);

        //3、设置默认密码为123456，并对默认密码进行md5加密后存储
        employee.setPassword(DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes()));

        //4、设置创建时间和更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //5、设置创建人和修改人（当前固定为1，TODO 后期应从登录会话中获取当前操作人id）
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    /**
     * 分页查询员工。
     *
     * @param employeePageQueryDTO 前端提交的分页查询参数（姓名、页码、每页显示记录数）
     * @return 分页查询结果（包含员工列表、总记录数）
     */
    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Integer status, Integer id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(Long.valueOf(id))
                .build();

        employeeMapper.changeStatus(employee);
    }

}