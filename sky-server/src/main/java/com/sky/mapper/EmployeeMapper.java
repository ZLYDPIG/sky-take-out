package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 员工表（employee）的数据访问接口。
 *
 * <p>@Mapper 由 MyBatis 扫描并生成实现类，Service 层直接注入即可。</p>
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee (name, phone, username, password, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{phone}, #{username}, #{password}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Employee employee);

    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void changeStatus(Employee employee);

    @Select("select * from employee where id = #{id}")
    Employee getById(Integer id);
}