package com.sky.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * C端用户业务实现类：微信登录。
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /** 微信登录凭证校验接口 */
    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登录。
     * <p>
     * 流程：前端传 code → 换取 openid → 按 openid 查库 → 查不到就自动注册 → 返回用户对象。
     *
     * @param userLoginDTO 前端提交的登录参数，其中 code 由 wx.login() 获取
     * @return 登录成功的用户实体（新用户已自动插入数据库）
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //0、参数前置校验，防止空指针异常或空请求浪费网络 IO
        if (userLoginDTO == null || StringUtils.isEmpty(userLoginDTO.getCode())) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //1、调用微信接口，用前端传来的 code 换取 openid
        String openId = getOpenId(userLoginDTO.getCode());

        //2、openid 为空说明微信校验失败（code 无效/已使用/过期等），抛业务异常由全局异常处理器统一返回
        if (StringUtils.isEmpty(openId)) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //3、根据 openid 查询用户，判断是否为新用户
        User user = userMapper.getByOpenid(openId);

        //4、新用户：自动注册一条用户记录
        if (user == null) {
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            // 插入后主键会回填到 user.id（依赖 UserMapper.insert 上的 useGeneratedKeys）
            userMapper.insert(user);
        }

        //5、返回用户对象，Controller 用它生成 jwt 令牌
        return user;
    }

    /**
     * 获取微信用户的 openid
     * <p>
     * 调用微信小程序的登录凭证校验接口 jscode2session，用前端 wx.login() 拿到的临时登录凭证 code
     * 换取当前用户的唯一标识 openid（及会话密钥 session_key）。
     * <p>
     * 接口地址：https://api.weixin.qq.com/sns/jscode2session
     * 请求方式：GET，参数拼在 url 的 query string 上
     * <p>
     * 成功返回：{"openid":"xxx","session_key":"xxx"}
     * 失败返回：{"errcode":40163,"errmsg":"code been used"} 等
     *
     * @param code 前端 wx.login() 换来的临时登录凭证 code
     * @return 微信返回的 openid；请求失败或微信返回错误码时返回 null
     */
    private String getOpenId(String code) {
        // 封装调用微信接口所需的 4 个查询参数，doGet 内部会用 URIBuilder 拼成 ?appid=..&secret=..
        Map<String, String> claims = new HashMap<>();

        // 小程序 appid，值来自配置文件 sky.wechat.appid（见 application-dev.yml）
        claims.put("appid", weChatProperties.getAppid());

        // 小程序秘钥，值来自配置文件 sky.wechat.secret，属于敏感信息，不要硬编码到代码里
        claims.put("secret", weChatProperties.getSecret());

        // 临时登录凭证 code，由前端调用 wx.login() 得到后传进来
        claims.put("js_code", code);

        // 固定值，表示使用"授权码"方式登录
        claims.put("grant_type", "authorization_code");

        // 发送 GET 请求，拿到微信返回的 JSON 字符串（请求失败时 doGet 返回空串 ""）
        String json = HttpClientUtil.doGet(WX_LOGIN, claims);

        // 响应体为空或解析失败，直接返回 null，由调用方按"登录失败"处理
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject == null) {
            return null;
        }

        // 成功时含 openid；失败时只有 errcode/errmsg，getString 会返回 null
        return jsonObject.getString("openid");
    }
}
