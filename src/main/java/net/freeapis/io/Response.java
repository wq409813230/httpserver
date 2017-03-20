package net.freeapis.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wuqiang on 2017/3/19.
 */
public class Response {

    private Socket clientSocket;

    private HttpStatus status;

    private Header header;

    public Response(Socket clientSocket){
        this.header = new Header();
        this.clientSocket = clientSocket;
    }

    public void setStatus(HttpStatus status){
        this.status = status;
    }

    public void setHeader(Header header){
        this.header = header;
    }

    public void setHeader(String name,String value){
        header.addHeader(name,value);
    }

    public void write(String responseBody) throws IOException {
        if(this.clientSocket == null || this.clientSocket.isClosed())
            throw new IllegalArgumentException("socket is closed!");
        String responseData = HttpServer.DEFAULT_HTTP_VERSION + " " + status.getValue() + " " + status.name() + "\r\n";
        for(String headerName : this.header.getHeaderNames()){
            responseData += headerName + ": " + header.getHeader(headerName) + "\r\n";
        }
        responseData += "\r\n" + responseBody;
        this.clientSocket.getOutputStream().write(responseData.getBytes());
        this.clientSocket.shutdownOutput();
    }

}
