package net.freeapis.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wuqiang on 2017/3/19.
 */
public class HttpServer {

    private static final int DEFAULT_SERVER_PORT = 8080;

    private static final int DEFAULT_MAX_CONNECTION = 10;

    private static final String MULTIPART_BOUNDARY_FIX = "--";

    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    private ServerSocket serverSocket;

    private int maxConnection;

    public HttpServer() throws IOException {
        this(DEFAULT_SERVER_PORT,DEFAULT_MAX_CONNECTION);
    }

    public HttpServer(int port,int maxConn) throws IOException {
        this.maxConnection = maxConn;
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        Socket clientSocket = null;
        while (!this.serverSocket.isClosed()){
            clientSocket = this.serverSocket.accept();
            LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(clientSocket.getInputStream()));
            String requestLine = lineReader.readLine();
            String[] requestLineMeta = requestLine.split(" ");
            Request request = new Request();
            request.setMethod(HttpMethod.valueOf(requestLineMeta[0]));
            request.setUrl(requestLineMeta[1]);
            request.setVersion(requestLineMeta[2]);

            String headerLine = null;
            Header requestHeader = new Header();
            String[] headerMeta = null;
            while (!(headerLine = lineReader.readLine()).isEmpty()) {
                headerMeta = headerLine.split(": ");
                requestHeader.addHeader(headerMeta[0],headerMeta[1]);
            }
            request.setHeader(requestHeader);

            System.out.println(request);
            String body = null;
            while((body = lineReader.readLine()) != null){
                System.out.println(body);
            }

            /*String contentType = requestHeader.getContentType();
            if(contentType.contains("multipart/form-data")){
                request.isMultipart(true);
                String boundary = contentType.split("; ")[1].split("=")[1];
                String body = null;
                while(!lineReader.readLine().equals(MULTIPART_BOUNDARY_FIX + boundary))
                    continue;

                String multipartContentDisposition = lineReader.readLine();
                String multipartContentType = lineReader.readLine();

                while(!(body = lineReader.readLine()).equals(MULTIPART_BOUNDARY_FIX + boundary + MULTIPART_BOUNDARY_FIX)){
                    System.out.println(body);
                }
            }*/

            Response response = new Response(clientSocket);
            Header responseHeader = new Header();
            responseHeader.addHeader("Server","Simple-Server");
            response.setHeader(responseHeader);
            response.setStatus(HttpStatus.OK);
            response.write("123");
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.start();
    }
}
