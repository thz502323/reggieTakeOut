package org.reggie.config;

import com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.reggie.common.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : 伪中二
 * @Date : 2023/2/26
 * @Description :Spring config配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {//自定义静态资源映射目录
    registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
    registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
  }

  @Autowired
  private  JacksonObjectMapper jacksonObjectMapper;
  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {//用来扩展默认的消息转换器
    //1.创建消息转换对象
    MappingJackson2HttpMessageConverter MessageConverter = new MappingJackson2HttpMessageConverter();
    //2.设置我们自己的消息转换
    MessageConverter.setObjectMapper(jacksonObjectMapper);
    //3.追加我们自己打转换器到List容器中，放在0号位置，会优先使用我们的
    converters.add(0, MessageConverter);
  }
}