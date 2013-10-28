package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;

/**
 * Json util class.
 * 
 * @author zxwu
 */
public final class JsonUtil {
    private static Log logger = LogFactory.getLog(JsonUtil.class);
    private static ObjectMapper mapper = null;
    private static ObjectMapper propertyNamingStrategyMapper = null;

    /**
     * Constructor
     */
    private JsonUtil() {
    }

    private static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
            mapper = new ObjectMapper();
            mapper.setDateFormat(df);
        }
        return mapper;
    }

    private static ObjectMapper getPropertyNamingStrategyMapper() {
        if (propertyNamingStrategyMapper == null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
            propertyNamingStrategyMapper = new ObjectMapper();
            propertyNamingStrategyMapper.setDateFormat(df);
            propertyNamingStrategyMapper
                    .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        }
        return propertyNamingStrategyMapper;
    }

    /**
     * From json string to bean.
     * 
     * @param <T> bean class type.
     * @param json json string.
     * @param clazz bean class.
     * @return the bean instance.
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        if (!StringUtils.isEmpty(json)) {
            try {
                return getObjectMapper().readValue(json, clazz);
            } catch (Exception e) {
                logger.error("JSONString : " + json, e);
            }
        }
        return null;
    }

    /**
     * From json string to bean.
     * 
     * @param <T> bean class type.
     * @param json json string.
     * @param clazz bean class.
     * @return the bean instance.
     */
    public static <T> T toBeanPropertyNamingStrategy(String json, Class<T> clazz) {
        if (!StringUtils.isEmpty(json)) {
            try {
                return getPropertyNamingStrategyMapper().readValue(json, clazz);
            } catch (Exception e) {
                logger.error("JSONString : " + json, e);
            }
        }
        return null;
    }

    /**
     * To json string.
     * 
     * @param object the object.
     * @return json string.
     */
    public static String toString(Object object) {
        return toString(object, getObjectMapper());
    }

    /**
     * To json string base on given mapper.
     * 
     * @param object object
     * @param mapper mapper
     * @return json string.
     */
    public static String toString(Object object, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error(e, e);
            return "";
        }
    }

    /**
     * 将json的数组转化成文本
     * 
     * @param json json
     * @param separator separator
     * @return text
     */
    public static String convertJsonArrayToText(String json, String separator) {
        if (StringUtils.isEmpty(json)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        List<String> list = toBean(json, List.class);
        for (String s : list) {
            sb.append(separator).append(s);
        }
        if (sb.length() > 0) {
            return sb.substring(separator.length());
        } else {
            return sb.toString();
        }
    }

}
