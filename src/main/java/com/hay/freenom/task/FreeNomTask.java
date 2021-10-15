package com.hay.freenom.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.hay.freenom.context.BaseTask;
import com.hay.freenom.entity.Domain;
import com.hay.freenom.entity.FreeNomCookie;
import com.hay.freenom.exception.BaseException;
import com.hay.freenom.exception.CookieException;
import com.hay.freenom.exception.LoginException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @title: FreeNomTask
 * @Author HuangYan
 * @Date: 2021/10/14 16:51
 * @Version 1.0
 * @Description: 域名任务
 */
@Component
public class FreeNomTask implements BaseTask<List<Domain>> {

    @Value("${freenom.username}")
    private String USERNAME;

    @Value("${freenom.password}")
    private String PASSWORD;


    private static final Logger log = LoggerFactory.getLogger(FreeNomTask.class);

    @Override
    public List<Domain> getData() throws BaseException{
        // 先登陆：https://my.freenom.com/dologin.php，需要加上Referer=https://my.freenom.com/clientarea.php?action=domains的请求头
        String freeNomToken = FreeNomCookie.getFreeNomCookie();
        String loginUrl = "https://my.freenom.com/dologin.php";
        String dataUrl = "https://my.freenom.com/domains.php?a=renewals";
        if (StrUtil.isBlank(freeNomToken)){
            String cookie = doLogin(loginUrl, USERNAME, PASSWORD);
            freeNomToken = FreeNomCookie.setFreeNomCookie(cookie);
        }
        // 根据返回的cookie获取所有域名
        StringBuffer body = new StringBuffer();
        List<Domain> domains;
        try {
            body.append(getHtml(dataUrl, freeNomToken));
            domains = parseHtml(body.toString());
        } catch (CookieException e){
            FreeNomCookie.setFreeNomCookie(doLogin(loginUrl, USERNAME, PASSWORD));
            body.append(getHtml(dataUrl, FreeNomCookie.getFreeNomCookie()));
            domains = parseHtml(body.toString());
        }
        if (CollectionUtil.isEmpty(domains)){
            log.warn("没有注册的域名");
        }
        return domains;
    }

    /**
     * 登陆返回成功后的Cookie
     * @return
     */
    @Override
    public String doLogin(String url, String username, String password) throws BaseException {
        String cookie = null;
        try {
            HashMap<String, String> fromMap = new HashMap<>();
            fromMap.put("username", username);
            fromMap.put("password", password);
            HttpResponse execute = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header(Header.REFERER, "https://my.freenom.com/domains.php?a=renewals")
                    .formStr(fromMap)
                    .timeout(20000)
                    .execute();
            // 返回了三个Set-Cookie要获取第二个的值当作下面请求的Cookie才能运行成功，我他妈差点被这个问题搞崩了
            List<String> list = execute.headerList("Set-Cookie");
            cookie = list.get(1).split(";")[0];
            // 清楚第一次请求残留的Cookie
            HttpRequest.getCookieManager().getCookieStore().removeAll();
        } catch (LoginException e){
            log.error("登陆失败");
            throw new LoginException("FreeNomTask=====>登陆失败");
        }
        return cookie;
    }

    /**
     * 请求自己所有域名信息页面,
     * @param cookie
     * @return  域名
     */
    @Override
    public String getHtml(String url, String cookie) throws BaseException {
        if (StrUtil.isBlank(cookie)){
            log.error("getDomainHtml=>cookie为空");
        }
        // 请求域名页面
        HttpResponse execute = HttpRequest.get(url)
                .header(Header.COOKIE, cookie)
                .timeout(20000)
                .execute();
        // 如果没获取成功，代表cookie过期，需要重新登陆
        if (execute.getStatus() != HttpStatus.HTTP_OK){
            throw new CookieException("cookie已过期，请重新登陆");
        }
        return execute.body();
    }

    /**
     * 将html页面解析为domain域名对象
     * @param body
     * @return
     */
    @Override
    public List<Domain> parseHtml(String body){
        List<Domain> domains = new ArrayList<>();
        Document document = Jsoup.parse(body);
        // 获取表格
        Elements table = document.getElementsByClass("table");
        Element element = table.get(0);
        // 获取tr标签
        Elements trs = element.getElementsByTag("tr");
        // 去除表头
        trs.remove(0);
        for (Element tr : trs) {
            Domain domain = new Domain();
            // 获取td
            Elements tds = tr.getElementsByTag("td");
            // 名称
            domain.setName(tds.get(0).text());
            // 状态
            String status = tds.get(1).text();
            domain.setStatus(status);
            // 状态是激活状态才设置时间
            if ("Active".equals(status)){
                // 时间
                String data = tds.get(2).text();
                String date = data.split(" ")[0];
                // 设置过期剩余时间
                domain.setExpirationLastDay(Integer.parseInt(date));
                LocalDate now = LocalDate.now();
                // 获取到域名过期时间
                LocalDate expirationDate = now.plusDays(Long.parseLong(date));
                domain.setExpirationDate(expirationDate);
                // 获取开启续费时间
                LocalDate renewDate = expirationDate.minusDays(14);
                domain.setRenewDate(renewDate);
                // 设置域名续约剩下时间
                long between = now.until(renewDate, ChronoUnit.DAYS);
                domain.setRenewOpenDay((int) between);
                if (between < 14){
                    domain.setRenewStatus(1);
                    domain.setRenewOpenDay(0);
                } else {
                    domain.setRenewStatus(0);
                }
                // 设置注册时间
                LocalDate registerDate = expirationDate.minusYears(1);
                domain.setRegisterDate(registerDate);
            }
            // 设置续约url
            Element renewUrl = tds.get(4);
            String href = renewUrl.getElementsByTag("a").attr("href");
            domain.setRenewUrl(href);
            domains.add(domain);
        }
        return domains;
    }
}
