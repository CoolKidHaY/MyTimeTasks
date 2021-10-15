package com.hay.freenom.service.impl;

import com.hay.freenom.config.EmailConfig;
import com.hay.freenom.entity.EmailData;
import com.hay.freenom.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @title: EmailServiceImpl
 * @Author HuangYan
 * @Date: 2021/9/29 14:04
 * @Version 1.0
 * @Description: 邮件服务
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(EmailData data, String html) throws MessagingException {
        Context context = new Context();
        context.setVariable("data", data);
        String templateContext = templateEngine.process(html, context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(emailConfig.getTo());
        helper.setSubject(emailConfig.getSubject());
        helper.setText(templateContext, true);
        helper.setFrom(emailConfig.getFrom());
        mailSender.send(message);
    }
}
