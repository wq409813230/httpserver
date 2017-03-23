package net.freeapis.io;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuqiang on 2017/3/19.
 */
public class Header {

    private static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";

    private static final String HEADER_NAME_CONTENT_LENGTH = "Content-Length";

    private static final String HEADER_NAME_USER_AGENT = "User-Agent";

    private Map<String,String> values;

    public Header(){
        values = new HashMap<String, String>();
    }

    public Header(Map<String,String> headers){
        if(headers == null || headers.isEmpty()){
            headers = new HashMap<String, String>();
        }
        values = headers;
    }

    public void addHeader(String name,String value){
        values.put(name,value);
    }

    public void addHeaders(Map<String,String> headers){
        values.putAll(headers);
    }

    public String getHeader(String name){
        return values != null ? values.get(name) : null;
    }

    public String getUserAgent(){
        return values != null ? values.get(HEADER_NAME_USER_AGENT) : null;
    }

    public String getContentType(){
        return values != null ? values.get(HEADER_NAME_CONTENT_TYPE) : null;
    }

    public String getContentLength(){
        return values != null ? values.get(HEADER_NAME_CONTENT_LENGTH) : null;
    }

    public String[] getHeaderNames(){
        return this.values.keySet().toArray(new String[]{});
    }
}
