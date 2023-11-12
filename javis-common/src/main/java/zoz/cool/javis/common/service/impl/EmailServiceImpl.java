package zoz.cool.javis.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import zoz.cool.javis.common.constants.MailConstant;
import zoz.cool.javis.common.constants.RedisConstant;
import zoz.cool.javis.common.exception.ApiException;
import zoz.cool.javis.common.service.EmailService;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;


@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    //Spring Boot 提供了一个发送邮件的简单抽象，使用的是下面这个接口，这里直接注入即可使用
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendMailVerifyCode(String to, String code) {
        //创建邮件正文
        Context context = new Context();
        context.setVariable("expiredTime", RedisConstant.REDIS_REGISTER_VERIFYCODE_EXPIRE / 60);
        context.setVariable("verifyCode", Arrays.asList(code.split("")));
        //将模块引擎内容解析成html字符串
        String emailContent = templateEngine.process("verifyCodeTemplate", context);
        sendHtmlMail(to, MailConstant.SUBJECT, emailContent);
    }

    /**
     * 简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件接收人
        message.setFrom(from);
        message.setTo(to);
        //邮件主题
        message.setSubject(subject);
        //邮件内容
        message.setText(content);
        log.info("开始发送邮件: to={}", to);
        //发送邮件
        mailSender.send(message);
        log.info("邮件已发送");
    }

    /**
     * html邮件
     *
     * @param to      收件人,多个时参数形式 ："xxx@xxx.com,xxx@xxx.com,xxx@xxx.com"
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        //获取MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;

        try {
            messageHelper = new MimeMessageHelper(message, true);
            //邮件接收人,设置多个收件人地址
            InternetAddress[] internetAddressTo = InternetAddress.parse(to);
            message.setFrom(from);
            messageHelper.setTo(internetAddressTo);
            //邮件主题
            message.setSubject(subject);
            //邮件内容，html格式
            messageHelper.setText(content, true);
            //发送
            log.info("开始发送邮件: to={}", to);
            mailSender.send(message);
            //日志信息
            log.info("邮件已经发送");
        } catch (Exception e) {
            log.error("发送邮件时发生异常！", e);
        }
    }

    /**
     * 带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            message.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            log.info("开始发送邮件: to={}", to);
            mailSender.send(message);
            //日志信息
            log.info("邮件已经发送。");
        } catch (Exception e) {
            log.error("发送邮件时发生异常！", e);
        }
    }
}
