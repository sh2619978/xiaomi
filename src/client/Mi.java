package client;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

public class Mi {

    public static String LOGIN_URL = "https://account.xiaomi.com/pass/serviceLogin";

    public static String LOGIN_ACTION_URL = "https://dynamic.12306.cn/otsweb/loginAction.do?method=login";

    public static String IMAGE_CODE_URL = "https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=lrand";

    public static String CHARSET_NAME = "UTF-8";
    
    private MiClient piaoClient;
    
    public Mi() throws MiClientException {
            init();
    }
    
    private void init() throws MiClientException {
        piaoClient = MiClient.getPiaoClient();
    }
    
    public void requestLoginInit() throws MiClientException {
        HttpGet get = new HttpGet(LOGIN_URL);

        HttpResponse response = piaoClient.execute(get);
        HttpEntity entity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            
        }
        System.out.println(result);
    }
    
    public InputStream getCodeImageInputStream() throws MiClientException {
        HttpGet imageCodeGet = new HttpGet(IMAGE_CODE_URL);
        HttpResponse imageCodeResponse = piaoClient.execute(imageCodeGet);
        try {
            return imageCodeResponse.getEntity().getContent();
        } catch (Exception e) {
            throw new MiClientException(e);
        }
    }
    
    public boolean login(String username, String password, String code) {
    	
    	
    	return true;
    }

    public static void main(String[] args) throws Exception {
//        HttpPost loginPost = new HttpPost(LOGIN_ACTION_URL);
//        UrlEncodedFormEntity loginFormEntity = UrlEncodedFormEntityBuilder.getBuilder()
//                .add("loginUser.user_name", "sh2619978")
//                .add("user.password", "ricebean")
//                .add("randCode", "dd")
//                .build();
//        loginPost.setEntity(loginFormEntity);
        
        new Mi().requestLoginInit();

    }

}
