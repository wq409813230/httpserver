package net.freeapis.io;

import org.apache.commons.fileupload.MultipartStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by wuqiang on 2017/3/19.
 */
public class HttpServer {

    private static final int DEFAULT_SERVER_PORT = 8080;

    private static final int DEFAULT_MAX_CONNECTION = 10;

    private static final String CRLF = "\r\n";

    private static final String MULTIPART_BOUNDARY_FIX = "--";

    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    private ServerSocket serverSocket;

    private int maxConnection;

    public HttpServer() throws IOException {
        this(DEFAULT_SERVER_PORT, DEFAULT_MAX_CONNECTION);
    }

    public HttpServer(int port, int maxConn) throws IOException {
        this.maxConnection = maxConn;
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        Socket clientSocket = null;
        while (!this.serverSocket.isClosed()) {
            clientSocket = this.serverSocket.accept();
            InputStream inputStream = clientSocket.getInputStream();
            LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(inputStream));
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
                requestHeader.addHeader(headerMeta[0], headerMeta[1]);
            }
            request.setHeader(requestHeader);

            System.out.println(request);

            /*String body = null;
            while((body = lineReader.readLine()) != null){
                System.out.println(body);
            }*/

            int contentLength = Integer.parseInt(requestHeader.getContentLength());
            String contentType = requestHeader.getContentType();
            if (contentType != null && contentType.contains("multipart/form-data")) {
                request.isMultipart(true);
                String boundary = contentType.split("; ")[1].split("=")[1];
                String beginOfFile = MULTIPART_BOUNDARY_FIX + boundary;

                /*char[] contents = new char[contentLength];
                lineReader.read(contents);
                String[] parts = new String(contents).split(beginOfFile);
                for (int i = 1; i < parts.length - 1; i++) {
                    int partSplitPos = parts[i].indexOf(CRLF + CRLF);
                    String partHeader = parts[i].substring(0, partSplitPos);
                    String partBody = parts[i].substring(partSplitPos + (CRLF + CRLF).length());
                    System.out.println(partHeader);
                    System.out.println(partBody);
                }*/

                try {
                    MultipartStream multipartStream = new MultipartStream(inputStream, boundary.getBytes());
                    boolean nextPart = true;
                    while (nextPart) {
                        String header = multipartStream.readHeaders();
                        // process headers
                        // create some output stream
                        multipartStream.readBodyData(new FileOutputStream("D://a.jpg"));
                        nextPart = multipartStream.readBoundary();
                    }
                } catch (MultipartStream.MalformedStreamException e) {
                    // the stream failed to follow required syntax
                } catch (IOException e) {
                    // a read or write error occurred
                }
            }

            Response response = new Response(clientSocket);
            Header responseHeader = new Header();
            responseHeader.addHeader("Server", "Simple-Server");
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
