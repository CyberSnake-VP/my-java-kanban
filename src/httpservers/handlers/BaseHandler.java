package httpservers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.JsonSyntaxException;
import manager.TaskManager;

import java.io.IOException;

public abstract class BaseHandler implements HttpHandler {
    protected TaskManager manager;
    protected Gson jsonMapper;
    protected final int OK = 200;
    protected final int CREATED = 201;
    protected final int NOT_FOUND = 404;
    protected final int METHOD_NOT_ALLOWED = 405;
    protected final int NOT_ACCEPTABLE = 406;
    protected final int INTERNAL_SERVER_ERROR = 500;


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                case "POST":
                case "DELETE":
                default:
            }
        } catch (IOException e) {

        }

        
    }


}
