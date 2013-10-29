package client;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class MiClient {

    private CookieStore cookieStore;
    private Scheme scheme;

    public MiClient() {
        cookieStore = new BasicCookieStore();

        SSLSocketFactory sslSocketFactory;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            // X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new
            // FileInputStream(
            // MiClient.class.getResource("account.xiaomi.com").getFile()));

            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(
                    "E:/account.xiaomi.com"));

            // System.out.println(certificate);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("xiaomi", certificate);

            sslSocketFactory = new SSLSocketFactory(keyStore);
        } catch (Exception e) {
            throw new RuntimeException("httpclient初始化失败！", e);
        }

        scheme = new Scheme("https", 443, sslSocketFactory);
    }

    private DefaultHttpClient getHttpClient() {
        DefaultHttpClient client = new DefaultHttpClient();

        client.getConnectionManager().getSchemeRegistry().register(scheme);

        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

        client.setRedirectHandler(new DefaultRedirectHandler() {
            @Override
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                boolean isRedirect = super.isRedirectRequested(response, context);
                if (!isRedirect) {
                    int responseCode = response.getStatusLine().getStatusCode();
                    if (responseCode == 301 || responseCode == 302) {
                        return true;
                    }
                }
                return isRedirect;
            }
        });

        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000); // 5秒超时
        HttpConnectionParams.setSoTimeout(client.getParams(), 5000);

        return client;
    }

    public final HttpResponse execute(HttpUriRequest request) throws MiClientException {
        DefaultHttpClient client = getHttpClient();

        // Create local HTTP context
        HttpContext localContext = new BasicHttpContext();

        // Bind custom cookie store to the local context
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        request.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Referer", "http://p.www.xiaomi.com/open/index.html");

        HttpResponse response;
        try {
            response = client.execute(request, localContext);
        } catch (Exception e) {
            throw new MiClientException("httpclient请求失败！", e);
        }

        // System.out.println(cookieStore);
        // System.out.println(response.getHeaders("Set-Cookie")[0].getValue());

        // System.out.println(getCookieLines());

        return response;

    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public String getCookieLines() {
        StringBuilder sb = new StringBuilder();
        List<Cookie> cookies = cookieStore.getCookies();
        sb.append("当前cookie信息: " + cookies.size() + " 条.\n");
        for (Cookie cookie : cookies) {
            sb.append(cookie.getName()).append(" = ").append(cookie.getValue()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws MiClientException {
        new MiClient().execute(new HttpGet(Mi.LOGIN_URL));
    }
}
