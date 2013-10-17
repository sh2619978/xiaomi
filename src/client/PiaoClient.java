package client;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

public class PiaoClient {

    private static PiaoClient piaoClient;

    private DefaultHttpClient client;

    private static List<Cookie> cookies = new ArrayList<Cookie>();

    public static PiaoClient getPiaoClient() throws PiaoClientException {
        return getPiaoClient(null);
    }

    public static PiaoClient getPiaoClient(String cerPath) throws PiaoClientException {
        if (piaoClient == null) {
            synchronized (PiaoClient.class) {
                if (piaoClient == null) {
                    if (StringUtils.isNotBlank(cerPath)) {
                        piaoClient = new PiaoClient(cerPath);
                    } else {
                        piaoClient = new PiaoClient();
                    }
                }
            }
        }
        return piaoClient;
    }

    private PiaoClient() throws PiaoClientException {
        this(PiaoClient.class.getResource("account.xiaomi.com").getFile());
    }

    private PiaoClient(String cerPath) throws PiaoClientException {
        client = new DefaultHttpClient();

        SSLSocketFactory sslSocketFactory;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(
                    cerPath));

            // System.out.println(certificate);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // keyStore.load(new FileInputStream(new File("F:/piao_keystore")), "changeit".toCharArray());
            keyStore.load(null);
            keyStore.setCertificateEntry("piao", certificate);

            sslSocketFactory = new SSLSocketFactory(keyStore);
        } catch (Exception e) {
            throw new PiaoClientException("httpclient初始化失败！", e);
        }

        Scheme scheme = new Scheme("https", 443, sslSocketFactory);

        client.getConnectionManager().getSchemeRegistry().register(scheme);

        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public final HttpResponse execute(HttpUriRequest request) throws PiaoClientException {

        HttpResponse response;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            throw new PiaoClientException("httpclient请求失败！", e);
        }

        // System.out.println(client.getCookieStore().getCookies());

        // cookies.addAll(client.getCookieStore().getCookies());

        return response;

    }
    
    public static void main(String[] args) throws PiaoClientException {
        new PiaoClient();
    }
}
