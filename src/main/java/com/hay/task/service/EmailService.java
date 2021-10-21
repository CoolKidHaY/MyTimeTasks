package com.hay.task.service;

import com.hay.task.entity.EmailData;

import javax.mail.MessagingException;

/**
 *  邮件服务
 * @author huangYan
 */
public interface EmailService {
    void sendEmail(EmailData data, String html) throws MessagingException;
}
