package net.freeapis.io;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import sun.font.Script;

import javax.script.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private ExecutorService executorService;

    public HttpServer() throws IOException {
        this(DEFAULT_SERVER_PORT, DEFAULT_MAX_CONNECTION);
    }

    public HttpServer(int port, int maxConn) throws IOException {
        this.maxConnection = maxConn;
        this.serverSocket = new ServerSocket(port);
        executorService = new ThreadPoolExecutor(
                1,DEFAULT_MAX_CONNECTION,2000, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(10));
    }

    private void handleSocket(Socket socket) throws Exception{
        InputStream inputStream = socket.getInputStream();
        LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(inputStream));
        String requestLine = lineReader.readLine();
        String[] requestLineMeta = requestLine.split(" ");
        Request request = new Request();
        request.setInputStream(inputStream);
        request.setCharacterEncoding("UTF-8");
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

        String contentType = requestHeader.getContentType();
        if (contentType != null && contentType.contains("multipart/form-data")) {
            int contentLength = Integer.parseInt(requestHeader.getContentLength());
            request.isMultipart(true);
            String boundary = contentType.split("; ")[1].split("=")[1];
            String beginOfFile = MULTIPART_BOUNDARY_FIX + boundary;

            char[] contents = new char[contentLength];
            lineReader.read(contents);
            String[] parts = new String(contents).split(beginOfFile);
            for (int i = 1; i < parts.length - 1; i++) {
                int partSplitPos = parts[i].indexOf(CRLF + CRLF);
                String partHeader = parts[i].substring(0, partSplitPos);
                String partBody = parts[i].substring(partSplitPos + (CRLF + CRLF).length());
                System.out.println(partHeader);
                System.out.println(partBody);
            }
        }

        String fullUrl = request.getUrl();
        String[] urlParts = fullUrl.split("\\?");
        String url = urlParts[0].substring(urlParts[0].lastIndexOf("/") + 1);
        String[] queryStrings = urlParts.length > 1 ? urlParts[1].split("&") : null;

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
        ScriptContext scriptContext = scriptEngine.getContext();
        if(queryStrings != null){
            String[] pair = null;
            for(String queryString : queryStrings){
                pair = queryString.split("=");
                scriptContext.setAttribute(pair[0],pair[1],ScriptContext.GLOBAL_SCOPE);

            }
        }

        String responseResult = null;
        if(scriptEngine instanceof Compilable){
            Compilable compilable = (Compilable)scriptEngine;
            InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream(url);
            if(templateStream != null){
                CompiledScript script = compilable.compile(new InputStreamReader(templateStream));
                responseResult = script.eval().toString();
            }
        }

        Response response = new Response(socket);
        Header responseHeader = new Header();
        responseHeader.addHeader("Server", "Simple-Server");
        response.setHeader(responseHeader);
        response.setStatus(HttpStatus.OK);
        response.write(responseResult);
    }

    public void start() throws IOException {
        Socket clientSocket = null;
        while (!this.serverSocket.isClosed()) {
            clientSocket = this.serverSocket.accept();
            final Socket socket = clientSocket;
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        handleSocket(socket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.start();
    }
}
