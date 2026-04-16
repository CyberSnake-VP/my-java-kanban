package httpservers;

import com.sun.net.httpserver.HttpServer;
import httpservers.handlers.EpicHandler;
import httpservers.handlers.SubtaskHandler;
import httpservers.handlers.TaskHandler;
import manager.Managers;
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

    public static void main(String[] args) throws IOException {
        System.out.println("Starting HttpTaskServer on port " + PORT);
        new HttpTaskServer(Managers.getDefault()).start();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtask", new SubtaskHandler(manager));
        server.start();
    }

    public void stop()  {
        server.stop(0);
    }
}
