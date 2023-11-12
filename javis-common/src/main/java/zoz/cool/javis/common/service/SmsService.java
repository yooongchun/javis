package zoz.cool.javis.common.service;

import org.springframework.scheduling.annotation.Async;

public interface SmsService {
    @Async
    public void sendSms(String phoneNumbers, String templateParam);

    @Async
    public void sendSmsVerifyCode(String phoneNumbers, String code);
}