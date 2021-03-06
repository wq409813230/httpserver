package net.freeapis.io;

import java.io.InputStream;

/**
 * Created by wuqiang on 2017/3/19.
 */
public class Request {

    private HttpMethod method;

    private String url;

    private String version;

    private Header header;

    private String characterEncoding;

    private boolean isMultipart;

    private InputStream inputStream;

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public HttpMethod getMethod(){
        return this.method;
    }

    public String getUrl(){
        return this.url;
    }

    public String getVersion(){
        return this.version;
    }

    public Header getHeader(){
        return this.header;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public boolean isMultipart(){
        return this.isMultipart;
    }

    public void isMultipart(boolean isMultipart){
        this.isMultipart = isMultipart;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String toString(){
        String requestProfile = method.name() + " " + url + " " + HttpServer.DEFAULT_HTTP_VERSION + "\r\n";
        for(String headerName : header.getHeaderNames()){
            requestProfile += headerName + ": " + header.getHeader(headerName) + "\r\n";
        }
        return requestProfile;
    }

}
