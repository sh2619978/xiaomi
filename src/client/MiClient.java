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

public class MiClient {

    private static MiClient piaoClient;

    private DefaultHttpClient client;

    private static List<Cookie> cookies = new ArrayList<Cookie>();

    public static MiClient getPiaoClient() throws MiClientException {
        return getPiaoClient(null);
    }

    public static MiClient getPiaoClient(String cerPath) throws MiClientException {
        if (piaoClient == null) {
            synchronized (MiClient.class) {
                if (piaoClient == null) {
                    if (StringUtils.isNotBlank(cerPath)) {
                        piaoClient = new MiClient(cerPath);
                    } else {
                        piaoClient = new MiClient();
                    }
                }
            }
        }
        return piaoClient;
    }

    private MiClient() throws MiClientException {
        this(MiClient.class.getResource("account.xiaomi.com").getFile());
    }

    private MiClient(String cerPath) throws MiClientException {
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
            throw new MiClientException("httpclient初始化失败！", e);
        }

        Scheme scheme = new Scheme("https", 443, sslSocketFactory);

        client.getConnectionManager().getSchemeRegistry().register(scheme);

        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public final HttpResponse execute(HttpUriRequest request) throws MiClientException {

        HttpResponse response;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            throw new MiClientException("httpclient请求失败！", e);
        }

        // System.out.println(client.getCookieStore().getCookies());

        // cookies.addAll(client.getCookieStore().getCookies());

        return response;

    }
    
    public static void main(String[] args) throws MiClientException {
        new MiClient();
    }
}
