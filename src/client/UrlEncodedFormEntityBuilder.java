package client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class UrlEncodedFormEntityBuilder {
    
    private List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    
    private UrlEncodedFormEntityBuilder() {
        
    }
    
    public static UrlEncodedFormEntityBuilder getBuilder() {
        return new UrlEncodedFormEntityBuilder();
    }

    public UrlEncodedFormEntityBuilder add(String name, String value) {
        parameters.add(new BasicNameValuePair(name, value));
        return this;
    }
    
    public UrlEncodedFormEntity build() {
        UrlEncodedFormEntity formEntity = null;
        try {
            formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            
        }
        return formEntity;
    }
    
    public String buildQueryUrl() {
        return "?" + URLEncodedUtils.format(parameters, "UTF-8");
    }

}
