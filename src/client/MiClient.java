package client;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

public class MiClient {

    private static MiClient miClient;

    private DefaultHttpClient client;

    private static List<Cookie> cookies = new ArrayList<Cookie>();

    public MiClient() {
        this(MiClient.class.getResource("account.xiaomi.com").getFile());
    }

    private MiClient(String cerPath) {
        client = new DefaultHttpClient();

        SSLSocketFactory sslSocketFactory;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(
                    cerPath));

            // System.out.println(certificate);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("piao", certificate);

            sslSocketFactory = new SSLSocketFactory(keyStore);
        } catch (Exception e) {
            throw new RuntimeException("httpclient初始化失败！", e);
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

    public CookieStore getCookieStore() {
        return client.getCookieStore();
    }

    public static void main(String[] args) throws MiClientException {
        new MiClient();
    }
}
