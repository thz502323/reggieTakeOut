package org.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : 伪中二
 * @Date : 2023/2/24
 * @Description :用于配置MybatisPlus的拦截器实现分页操作
 */
@Configuration
public class MybatisPlusConfig {
    //1定义拦截器容器
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor(){
    MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
    //2添加具体拦截器 这里添加分页功能
    mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
    //3.添加乐观锁拦截器
    mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    return mybatisPlusInterceptor;
  }

}
