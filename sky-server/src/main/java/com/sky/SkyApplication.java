package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 苍穹外卖项目启动类（服务端入口）。
 *
 * <p>启动前提：本机 MySQL 服务已运行，且 application-dev.yml 中的数据库账号密码正确。</p>
 */
@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理（@Transactional 生效）
@Slf4j
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
