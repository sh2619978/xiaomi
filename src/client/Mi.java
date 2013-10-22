package client;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Mi {

    public static String LOGIN_URL = "https://account.xiaomi.com/pass/serviceLogin";

    public static String LOGIN_ACTION_URL = "https://account.xiaomi.com/pass/serviceLoginAuth2";

    public static String IMAGE_CODE_URL = "xxx";

    public static String CHARSET_NAME = "UTF-8";

    private MiClient miClient;

    public Mi() {
        init();
    }

    private void init() {
        miClient = new MiClient();
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
            if (loginResult.contains("http://account.xiaomi.com/pass/userInfo?userId=")) {
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

    public static void main(String[] args) throws Exception {

        System.out.println(new Mi().login("sh2619978@126.com", "ricebean2013"));

    }

}
