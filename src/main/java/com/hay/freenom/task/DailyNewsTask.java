package com.hay.freenom.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hay.freenom.context.BaseTask;
import com.hay.freenom.exception.BaseException;
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
        String url = getDailyNewsUrl();
        String body = getHtml(url, null);
        return parseHtml(body);
    }

    @Override
    public String doLogin(String url, String username, String password) {
        return null;
    }

    @Override
    public String getHtml(String url, String cookie) throws BaseException{
        if (lastSendDate != null && lastSendDate.isEqual(LocalDate.now())){
            throw new BaseException("DailyNewsTask=====>getHtml已发送无须再发送");
        }
        String body = getDailyNewsUrl();
        if (StrUtil.isBlank(body)){
            log.warn("DailyNewsTask=====>获取每日新闻html失败");
            throw  new BaseException("DailyNewsTask=====>getHtml方法获取失败");
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
        // todo 解析html
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
                    // fa
                    String url = element.getElementsByTag("a").attr("href");
                    url = url.replace("weixin_39786569", "csdngeeknews");
                    HttpResponse response = HttpRequest.get(url).execute();
                    System.out.println(response.getCookieStr());
                    return response.body();
                }
            }
        }
        return null;
    }
}
