package httpservers.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import httpservers.adapters.DurationAdapter;
import httpservers.adapters.InstantAdapter;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

public abstract class BaseHandler implements HttpHandler {
    protected TaskManager manager;
    protected Gson jsonMapper;
    protected final int OK = 200;
    protected final int CREATED = 201;
    protected final int NOT_FOUND = 404;
    protected final int METHOD_NOT_ALLOWED = 405;
    protected final int NOT_ACCEPTABLE = 406;
    protected final int INTERNAL_SERVER_ERROR = 500;
    protected final int BAD_REQUEST = 400;


    public BaseHandler(TaskManager manager) {
        this.manager = manager;
        jsonMapper = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    processGet(path, exchange);
                    break;
                case "POST":
                    processPost(path, exchange);
                    break;
                case "DELETE":
                    processDelete(path, exchange);
                    break;
                default: sendResponse(exchange, METHOD_NOT_ALLOWED, "Method not allowed");
            }
        } catch (IOException e) {
            try {
                sendResponse(exchange, INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
            } catch (IOException ex) {
                System.out.println("Не удалось отправить ответ: " + ex.getMessage());
            }
        }
    }

    abstract void processGet(String path, HttpExchange exchange) throws IOException;

    abstract void processPost(String path, HttpExchange exchange) throws IOException;

    abstract void processDelete(String path, HttpExchange exchange) throws IOException;


    protected void sendResponse(HttpExchange ex, int code, String message) throws IOException {
        byte[] answer = message.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(code, answer.length);
        ex.getResponseBody().write(answer);
        ex.getResponseBody().flush();
        ex.close();
    }


    protected boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
