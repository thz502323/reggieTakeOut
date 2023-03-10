package org.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author : 伪中二
 * @Date : 2023/2/26
 * @Description :项目启动类
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan//自动扫描Component
@EnableTransactionManagement//开启事务
@EnableCaching//开启注解
public class ReggieApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReggieApplication.class, args);
    log.info("启动成功...");
  }

}
