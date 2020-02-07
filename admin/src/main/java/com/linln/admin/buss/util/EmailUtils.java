 package com.linln.admin.buss.util;

 import org.springframework.mail.SimpleMailMessage;
 import org.springframework.mail.javamail.JavaMailSenderImpl;
  
 import java.util.Properties;
  
 /***
  * @Author 淡漠Vip
  *
  * @Email itdreamlmc@163.com
  *
  * @Date Create in 17:29 2017/9/11 0011
  */
 public class EmailUtils {
  
     private static JavaMailSenderImpl javaMailSender;
  
     private static final String userName = "18514587664@163.com";
  
     private static final String password = "sqm20200108";
  
     private static final String host = "smtp.163.com";
  
     private static final int port = 465;
  
     //定义收件人列表
     private static final String[] revicedUserName = {"caterina.wang@rocalimitedhk.com","rx.zhou@rocalimitedhk.com","rocalimitedhk@hotmail.com"};
  
  
     static {
         javaMailSender = new JavaMailSenderImpl();
         javaMailSender.setHost(host);//链接服务器
         javaMailSender.setPort(port);
         javaMailSender.setUsername(userName);//账号
         javaMailSender.setPassword(password);//密码
         javaMailSender.setDefaultEncoding("UTF-8");
  
         Properties properties = new Properties();
         properties.setProperty("mail.smtp.auth", "true");//开启认证
         properties.setProperty("mail.debug", "true");//启用调试
         properties.setProperty("mail.smtp.timeout", "10000");//设置链接超时
         properties.setProperty("mail.smtp.port", Integer.toString(port));//设置端口
         properties.setProperty("mail.smtp.socketFactory.port", Integer.toString(port));//设置ssl端口
         properties.setProperty("mail.smtp.socketFactory.fallback", "false");
         properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
         javaMailSender.setJavaMailProperties(properties);
     }
  
     /***
      * 发送项目异常 代码提醒
      * @param msg
      */
     public static void sendEmail(String msg,String subject) {
         //开启线程异步发送  防止发送请求时间过长
         new Thread(new Runnable() {
             @Override
             public void run() {
                 if (revicedUserName != null && revicedUserName.length > 0) {
                     SimpleMailMessage mailMessage = new SimpleMailMessage();
                     mailMessage.setFrom(userName);
                     mailMessage.setSubject(subject);
                     mailMessage.setText(msg);
                     mailMessage.setTo(revicedUserName);
                     //发送邮件
                     javaMailSender.send(mailMessage);
                 }
             }
         }).start();
     }
  
 }