package client;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.JsonUtil;

public class Mi {

    public static String LOGIN_URL = "https://account.xiaomi.com/pass/serviceLogin";

    public static String LOGIN_ACTION_URL = "https://account.xiaomi.com/pass/serviceLoginAuth2";

    public static String PAIDUI_URL_PREFIX = "http://tc.hd.xiaomi.com/hdget?callback=hdcontrol&_=";

    public static String IMAGE_CODE_URL = "xxx";

    public static String CHARSET_NAME = "UTF-8";

    private MiClient miClient;

    private boolean login;

    public Mi() {
        miClient = new MiClient();
    }

    public void visitIndex() {
        HttpGet get = new HttpGet(LOGIN_URL);
        try {
            HttpResponse response = miClient.execute(get);
        } catch (MiClientException e) {

        }
    }

    public InputStream getCodeImageInputStream() throws MiClientException {
        HttpGet imageCodeGet = new HttpGet(IMAGE_CODE_URL);
        HttpResponse imageCodeResponse = miClient.execute(imageCodeGet);
        try {
            return imageCodeResponse.getEntity().getContent();
        } catch (Exception e) {
            throw new MiClientException(e);
        }
    }

    public boolean login(String username, String password) throws MiClientException {
        HttpGet get = new HttpGet(LOGIN_URL);
        HttpResponse response = miClient.execute(get);
        HttpEntity entity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity, CHARSET_NAME);
        } catch (Exception e) {
            return false;
        }
        Document document = Jsoup.parse(result);
        Element element = document.select("form#loginForm").get(0);
        Elements inputElements = element.select("input");

        HttpPost loginPost = new HttpPost(LOGIN_ACTION_URL);
        UrlEncodedFormEntityBuilder builder = htmlInputToFormEntityBuilder(inputElements, "passToken", "callback",
                "sid", "qs", "hidden", "_sign");
        builder.add("user", username).add("pwd", password);
        UrlEncodedFormEntity formEntity = builder.build();
        loginPost.setEntity(formEntity);
        HttpResponse loginResponse = miClient.execute(loginPost);
        try {
            String loginResult = EntityUtils.toString(loginResponse.getEntity());
            // if (loginResponse.getStatusLine().getStatusCode() == 302
            // && loginResult.contains("http://account.xiaomi.com/")) {
            //
            // String userId = getCookie("userId").getValue();
            // HttpGet userGet = new HttpGet("https://account.xiaomi.com/pass/userInfo?userId=" + userId);
            // miClient.execute(userGet);
            //
            // return true;
            // }
            if (loginResult.contains("<title>小米帐户</title>")) {
                login = true;
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private UrlEncodedFormEntityBuilder htmlInputToFormEntityBuilder(Elements inputElements, String... inputName) {
        Set<String> inputSet = null;
        if (inputName != null) {
            inputSet = new HashSet<String>(Arrays.asList(inputName));
        }
        UrlEncodedFormEntityBuilder builder = UrlEncodedFormEntityBuilder.getBuilder();
        for (Element one : inputElements) {
            if (StringUtils.isNotBlank(one.attr("name"))) {
                if (inputName.length == 0 || (inputName.length > 0 && inputSet.contains(one.attr("name")))) {
                    builder.add(one.attr("name"), one.attr("value"));
                }
            }
        }
        return builder;
    }

    public CookieStore getCookieStore() {
        return miClient.getCookieStore();
    }

    public String getCookieLines() {
        return miClient.getCookieLines();
    }

    public Cookie getCookie(String name) {
        CookieStore cookieStore = miClient.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    public Map<String, String> paidui() {
        Map<String, String> resultMap = new HashMap<String, String>();
        String result = null;
        try {
            HttpGet get = new HttpGet(PAIDUI_URL_PREFIX + System.currentTimeMillis());
            HttpResponse response = miClient.execute(get);
            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, CHARSET_NAME);
            
            System.out.println(get);
            System.out.println(result);
        } catch (Exception e) {
            resultMap.put("msg", "请求发生异常: " + e);
            return resultMap;
        }
        
        if (is404Page(result)) {
            resultMap.put("msg", "返回页面404");
            return resultMap;
        }
        if (result.contains("hdcontrol")) {
            String buyUrl = "http://t.hd.xiaomi.com/s/";
            String jsonData = StringUtils.substringBeforeLast(StringUtils.substringAfter(result, "hdcontrol("), ")")
                    .trim();
            Map<String, Object> hdMap = JsonUtil.toBean(jsonData, Map.class);
            if (hdMap != null) {
                Object statusObj = hdMap.get("status");
                if (statusObj != null) {
                    Map<String, Object> statusMap = (Map<String, Object>) statusObj;
                    Object allowObj = statusMap.get("allow");
                    if (allowObj != null && ((Boolean) allowObj) == true) {
                        // miphone
                        Object mpObj = statusMap.get("miphone");
                        if (mpObj != null) {
                            Map<String, Object> mpMap = (Map<String, Object>) mpObj;
                            if (mpMap.get("hdurl") != null) {
                                String hdurlStr = (String) mpMap.get("hdurl");
                                if (StringUtils.isNotBlank(hdurlStr)) {
                                    resultMap.put("miphonehdurl", buyUrl + hdurlStr);
                                } else {
                                    resultMap.put("msg", "排队地址返回miphone的hdurl为空");
                                }
                            } else {
                                resultMap.put("msg", "排队地址返回miphone的hdurl为空");
                            }
                        } else {
                            resultMap.put("msg", "排队地址返回miphone对象为空");
                        }
                    } else {
                        resultMap.put("msg", "排队地址返回allow为false");
                    }
                } else {
                    resultMap.put("msg", "排队地址返回status为空");
                }
            } else {
                resultMap.put("msg", "排队地址返回数据不正确");
            }
        } else {
            resultMap.put("msg", "排队地址返回没有hdcontrol()");
        }
        return resultMap;
    }

    public boolean isLogin() {
        return login;
    }

    private boolean is404Page(String responseStr) {
        if (responseStr.contains("很抱歉，小米暂时无法处理您的访问请求")) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(new Mi().paidui());

    }

}
