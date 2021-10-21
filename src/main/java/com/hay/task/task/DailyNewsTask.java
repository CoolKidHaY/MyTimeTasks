package com.hay.task.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hay.task.context.BaseTask;
import com.hay.task.exception.BaseException;
import com.hay.task.exception.TaskExecutedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: DailyNewsTask
 * @Author HuangYan
 * @Date: 2021/10/14 17:03
 * @Version 1.0
 * @Description: CSDN每日新闻
 */
@Component
public class DailyNewsTask implements BaseTask<List<String>> {

    private static LocalDate lastSendDate = null;

    private static final Logger log = LoggerFactory.getLogger(DailyNewsTask.class);

    @Override
    public List<String> getData() throws BaseException {
        // 今天发送了停止当前线程
        if (lastSendDate != null && lastSendDate.isEqual(LocalDate.now())){
            throw new TaskExecutedException("DailyNewsTask任务已执行");
        }
        // 获取每日新闻的url
        String url = getDailyNewsUrl();
        // 获取页面body
        String body = getHtml(url, null);
        // 返回数据
        List<String> list = parseHtml(body);
        // 设置最后一次发送时间为今天
        lastSendDate = LocalDate.now();
        log.info("发送日期：{}", lastSendDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return list;
    }

    @Override
    public String doLogin(String url, String username, String password) {
        return null;
    }

    @Override
    public String getHtml(String url, String cookie) throws BaseException{
        String body = HttpRequest.get(url).execute().body();
        if (StrUtil.isBlank(body)){
            log.info("DailyNewsTask=====>getHtml方法获取失败");
        }
        return body;
    }

    @Override
    public List<String> parseHtml(String body) {
        ArrayList<String> dataList = new ArrayList<>();
        Document document = Jsoup.parse(body);
        Element ul = document.getElementsByTag("ul").get(0);
        Elements li = ul.getElementsByTag("li");
        for (Element element : li) {
            dataList.add(element.text());
        }
        return dataList;
    }

    public String getDailyNewsUrl(){
        HttpResponse execute = HttpRequest.get("https://blog.csdn.net/csdngeeknews").execute();
        String body = execute.body();
        Document document = Jsoup.parse(body);
        Elements elements = document.getElementsByClass("article-list").get(0).getElementsByClass("article-item-box");
        List<Element> elementList = elements.subList(0, 3);
        LocalDateTime now = LocalDateTime.now();
        for (Element element : elementList) {
            Elements date = element.getElementsByClass("date");
            String dateString = date.get(0).text();
            LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // 如果是同一天并且现在时间要大于文章发送时间，则再进行判断
            boolean equal = now.toLocalDate().isEqual(dateTime.toLocalDate());
            boolean after = now.isAfter(dateTime);
            if (equal && after){
                LocalDateTime time = now.minusMinutes(5);
                long until = time.until(dateTime, ChronoUnit.SECONDS);
                // 当五分钟前的时间小于文章发送的时间，则文章在上一次检查后推送的
                if (until < 0 && until >= -300){
                    // 发
                    String url = element.getElementsByTag("a").attr("href");
                    url = url.replace("weixin_39786569", "csdngeeknews");
                    return url;
                }
            }
        }
        return "";
    }
}
