package zoz.cool.javis.common.service;

import org.springframework.scheduling.annotation.Async;

/**
 * 发送邮件服务
 */

public interface EmailService {

    @Async
    void sendMailVerifyCode(String to, String code);

    /**
     * 发送文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    @Async
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 发送HTML邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    @Async
    void sendHtmlMail(String to, String subject, String content);

    /**
     * 发送带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件
     */
    @Async
    void sendAttachmentsMail(String to, String subject, String content, String filePath);
}