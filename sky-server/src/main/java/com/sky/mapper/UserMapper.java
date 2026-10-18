package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据微信 openid 查询用户
     *
     * @param openId 微信用户唯一标识
     * @return 用户对象，不存在时返回 null
     */
    @Select("select * from user where openid = #{openId}")
    User getByOpenid(String openId);

    /**
     * 新增用户（注册）
     * <p>
     * useGeneratedKeys + keyProperty 让 MySQL 自增主键回填到 user.id，
     * 否则插入后 user.getId() 为 null，生成 jwt 时 claims 里会缺少 userId。
     *
     * @param user 待插入的用户对象
     */
    @Insert("insert into user (openid, create_time) values (#{openid}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}
