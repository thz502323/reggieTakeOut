package org.reggie.utils;

/**
 * @author : 伪中二
 * @Date : 2023/3/3
 * @Description :用于发生邮件验证码
 */

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

  @Value("${mail.HostName}")
  private String HostName;
  @Value("${mail.UserName}")
  private String Username;
  @Value("${mail.PassWord}")
  private String PassWord;
  @Value("${mail.From}")
  private String From;
/*

  @Value("${mail.Name}")
  String Name;
  @Value("${mail.SmtpPort}")
  int SmtpPort;
  @Value("${mail.SocketTimeout}")
  int SocketTimeout;
  @Value("${mail.SSLOnConnect}")
  boolean SSLOnConnect;
  @Value("${mail.StartTLSEnabled}")
  boolean StartTLSEnabled;
*/
  public void sendAuthCodeEmail(String email, String authCode) {
    try {
      SimpleEmail mail = new SimpleEmail();
      mail.setHostName(HostName);//发送邮件的服务器,这个是qq邮箱的，不用修改
      mail.setAuthentication(Username,
          PassWord);//第一个参数是对应的邮箱用户名一般就是自己的邮箱，第二个参数就是SMTP的密码,我们上面获取过了
      mail.setFrom(From, "瑞吉外卖");  //发送邮件的邮箱和发件人
      mail.setSSLOnConnect(false); //使用安全链接
      mail.setSmtpPort(587);
      mail.setStartTLSEnabled(true);//outlook必须启用这个
      mail.setSocketTimeout(25000);
      mail.addTo(email);//接收的邮箱
      mail.setSubject("外卖登录验证码");//设置邮件的主题
      mail.setMsg("尊敬的用户:你好!\n 登陆验证码为:" + authCode + "\n"
          + "     (有效期为5分钟)");//设置邮件的内容
      mail.send();//发送
    } catch (EmailException e) {
      e.printStackTrace();
    }
  }
}