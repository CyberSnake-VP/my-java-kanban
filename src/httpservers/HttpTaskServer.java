package httpservers;

import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer server;
    private TaskManager manager;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.start();
    }

    public void stop() throws IOException {
        server.stop(0);
    }
}
