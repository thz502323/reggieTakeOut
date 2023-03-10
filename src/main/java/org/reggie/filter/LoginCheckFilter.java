package org.reggie.filter;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.BaseUserContext;
import org.reggie.common.Result;
import org.springframework.util.AntPathMatcher;

/**
 * @author : 伪中二
 * @Date : 2023/2/27
 * @Description :配置过滤器，用来验证
 */
@Slf4j
@WebFilter(filterName = "loginCheck", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

  //路径匹配器，可以用于通配符
  static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    //1.获取请求和响应
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String requestURI = request.getRequestURI();

    //2.判断是否要处理请求
    String[] urls = {"/employee/login", "/employee/logout", "/user/login", "/user/sendMsg",
        "/user/logout", "/common/**", "/front/**", "/backend/**"};

    boolean check = check(urls, requestURI);

    log.info("访问: {}", requestURI);

    //3.用来防止登出 回退后还能进入缓存页，后面可以将登录功能，单独用一个过滤器 防止登录后浏览器点回退又到登录页面
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
    response.setDateHeader("Expires", 0); // Proxies.

    //4.根据check执行后续逻辑
    // TODO : 2023/3/4 注意，由于没验证登录人具体身份，导致user登录后可以手动访问employee页面，此时可以用生成两个拦截器，给两个身份人配置不同的访问目录 https://blog.csdn.net/csucsgoat/article/details/123475674
    //4-1、判断登录状态，如果已登录，则直接放行
    //4-1、判断登录状态，如果已登录，则直接放行
    if (check) {
      log.info("本次请求{}不需要处理", requestURI);
      filterChain.doFilter(request, response);
      return;
    }
    if (request.getSession().getAttribute("employee") != null) {
      log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));

      Long empId = (Long) request.getSession().getAttribute("employee");
      BaseUserContext.setCurrentId(empId);

      filterChain.doFilter(request, response);
      return;
    }

    //4-2、判断登录状态，如果已登录，则直接放行
    if (request.getSession().getAttribute("user") != null) {
      log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

      Long userId = (Long) request.getSession().getAttribute("user");
      BaseUserContext.setCurrentId(userId);

      filterChain.doFilter(request, response);
      return;
    }

    response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));//将json传给前端
  }

  /**
   * 用来验证请求requestURI是否匹配
   *
   * @param urls       可以直接放行的urls
   * @param requestURI
   * @return
   */
  boolean check(String[] urls, String requestURI) {
    for (String url : urls) {
      if (PATH_MATCHER.match(url, requestURI)) {
        return true;
      }
    }
    return false;
  }
}
