package com.hay.freenom.quartz;

import com.hay.freenom.entity.Domain;
import com.hay.freenom.entity.EmailData;
import com.hay.freenom.exception.BaseException;
import com.hay.freenom.service.EmailService;
import com.hay.freenom.task.DailyNewsTask;
import com.hay.freenom.task.FreeNomTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.List;

/**
 * @title: ReptileJob
 * @Author HuangYan
 * @Date: 2021/9/26 16:55
 * @Version 1.0
 * @Description: 任务执行器
 */
@Component
public class ReptileJob {

    @Autowired
    private EmailService emailService;

    @Autowired
    private FreeNomTask freeNomTask;

    @Autowired
    private DailyNewsTask dailyNewsTask;

    private static final Logger log = LoggerFactory.getLogger(ReptileJob.class);

    /**
     * FreeNom
     * 每周星期一的10:30定时查询域名信息
     */
    @Scheduled(cron = "0 30 10 ? * 1")
    public void executeFreeNomTask(){
        List<Domain> domains;
        // 发送消息
        try {
            domains = freeNomTask.getData();
            EmailData<List<Domain>> data = new EmailData<>();
            data.setData(domains);
            emailService.sendEmail(data, "freeNom");
        } catch (BaseException | MessagingException e) {
            log.error("executeFreeNomTask执行失败：{}" + e.getMessage());
        }
    }

    /**
     * 每天9点到10点，每隔5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 9,10 * * ?")
    public void executeDailyNewsTask(){
        List<String> dataList;
        try {
            dataList = dailyNewsTask.getData();
            EmailData<List<String>> data = new EmailData<>();
            data.setData(dataList);
            emailService.sendEmail(data, "dailyNews");
        } catch (BaseException | MessagingException e){
            log.warn("executeDailyNewsTask执行失败：{}" + e.getMessage());
        }
    }

}