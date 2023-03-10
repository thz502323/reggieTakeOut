package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.Result;
import org.reggie.entity.User;
import org.reggie.service.UserService;
import org.reggie.utils.EmailUtil;
import org.reggie.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 伪中二
 * @Date : 2023/3/3
 * @Description :用于处理用户业务
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private EmailUtil emailUtil;
  /**
   * 发送手机验证码，其实是邮箱，没钱用手机
   *
   * @param user
   * @return
   */
  @PostMapping("/sendMsg")
  Result<String> sendMsg(@RequestBody User user, HttpSession session) {
    //获取手机
    String phone = user.getPhone();
    //生成4位验证
    String code = ValidateCodeUtils.generateValidateCode(4).toString();
    //发生邮件
//    emailUtil.sendAuthCodeEmail(phone, code); // TODO : 2023/3/9 后期开启
    log.info("验证码：{}",code);
    //保存验证码，和用户输入的对比
//    session.setAttribute(phone,code);
    //优化，将我们生成的code保存到redis中，并且设置有效期为5分钟
    redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
//    session.setAttribute(phone,"1234");

    return Result.success("请稍等，正在发送中...");

  }

/**
   * 用户登出
   *
   * @param request
   * @return
   */
  @PostMapping("/loginout")
  Result<String> loginout(HttpServletRequest request) {
    request.getSession().removeAttribute("user");
    return Result.success("退出成功");
  }

  /**
   * 移动端用户登录
   *
   * @param map
   * @param session
   * @return
   */
  @PostMapping("/login")
  public Result<User> login(@RequestBody Map<String,String> map, HttpSession session) {

    //获取手机号
    String phone = map.get("phone");

    //获取验证码
    String code = map.get("code");

    //从Session中获取保存的验证码
//    Object codeInSession = session.getAttribute(phone);
    //优化从redis中获取内容
    String redisCode = redisTemplate.opsForValue().get(phone);

    //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
//    if (codeInSession != null && codeInSession.equals(code)) {
    if (redisCode != null && redisCode.equals(code)) {
      //如果能够比对成功，说明登录成功

      LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(User::getPhone, phone);

      User user = userService.getOne(queryWrapper);
      if (user == null) {
        //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
        user = new User();
        user.setPhone(phone);
        user.setStatus(1);
        userService.save(user);
      }
      session.setAttribute("user", user.getId());//后期可以优化
      redisTemplate.delete(phone);
      return Result.success(user);
    }
    return Result.error("登录失败");
  }
}
