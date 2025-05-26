package com.flekssina.chat.client;

import com.flekssina.chat.common.Message;
import com.flekssina.chat.common.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);

    private final ClientConfig config;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String username;
    private String sessionId;
    private boolean connected = false;

    private final List<ChatClientListener> listeners = new ArrayList<>();

    public ChatClient(ClientConfig config) {
        this.config = config;
    }

    public void connect() throws IOException {
        logger.info("Подключение к серверу {}:{}", config.getServerHost(), config.getServerPort());
        socket = new Socket(config.getServerHost(), config.getServerPort());

        // Отправляем байт, указывающий на Java-клиент
        socket.getOutputStream().write(0);
        socket.getOutputStream().flush();

        // Создаем потоки ввода-вывода
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        connected = true;

        // Запуск потока для чтения сообщений
        new Thread(this::receiveMessages).start();

        logger.info("Подключен к серверу {}:{}", config.getServerHost(), config.getServerPort());
    }

    public void login(String username) throws IOException {
        this.username = username;

        Message message = new Message(MessageType.LOGIN, username, "JavaClient");
        sendMessage(message);
    }

    public void logout() {
        if (!connected || sessionId == null) {
            return;
        }

        try {
            Message message = new Message(MessageType.LOGOUT, username, sessionId);
            sendMessage(message);
        } catch (IOException e) {
            logger.error("Ошибка при выходе из чата", e);
        } finally {
            disconnect();
        }
    }

    public void sendChatMessage(String content) throws IOException {
        Message message = new Message(MessageType.CHAT_MESSAGE, username, content);
        sendMessage(message);
    }

    public void requestUserList() throws IOException {
        Message message = new Message(MessageType.LIST_USERS, username, "");
        sendMessage(message);
    }

    private void sendMessage(Message message) throws IOException {
        if (connected) {
            outputStream.writeObject(message);
            outputStream.flush();
            logger.debug("Отправлено сообщение: {}", message.getType());
        } else {
            throw new IOException("Клиент не подключен к серверу");
        }
    }

    private void receiveMessages() {
        try {
            while (connected) {
                try {
                    Message message = (Message) inputStream.readObject();
                    handleMessage(message);
                } catch (ClassNotFoundException e) {
                    logger.error("Получено сообщение неизвестного класса", e);
                }
            }
        } catch (IOException e) {
            if (connected) {
                logger.error("Ошибка при чтении сообщения", e);
                notifyConnectionError("Потеряно соединение с сервером");
                disconnect();
            }
        }
    }

    private void handleMessage(Message message) {
        if (message == null) {
            return;
        }

        switch (message.getType()) {
            case LOGIN_SUCCESS:
                this.sessionId = message.getContent();
                notifyLoginSuccess();
                break;
            case ERROR:
                notifyError(message.getContent());
                break;
            case CHAT_MESSAGE:
            case USER_JOINED:
            case USER_LEFT:
                notifyMessageReceived(message);
                break;
            case USER_LIST:
                notifyUserList(message.getContent());
                break;
            default:
                logger.warn("Неизвестный тип сообщения: {}", message.getType());
        }
    }

    public void disconnect() {
        try {
            connected = false;

            if (outputStream != null) {
                outputStream.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            logger.info("Отключен от сервера");
        } catch (IOException e) {
            logger.error("Ошибка при отключении от сервера", e);
        }
    }

    // Методы для работы с подписчиками на события

    public void addListener(ChatClientListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChatClientListener listener) {
        listeners.remove(listener);
    }

    private void notifyLoginSuccess() {
        for (ChatClientListener listener : listeners) {
            listener.onLoginSuccess();
        }
    }

    private void notifyError(String errorMessage) {
        for (ChatClientListener listener : listeners) {
            listener.onError(errorMessage);
        }
    }

    private void notifyMessageReceived(Message message) {
        for (ChatClientListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    private void notifyUserList(String userList) {
        for (ChatClientListener listener : listeners) {
            listener.onUserListReceived(userList);
        }
    }

    private void notifyConnectionError(String errorMessage) {
        for (ChatClientListener listener : listeners) {
            listener.onConnectionError(errorMessage);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public String getUsername() {
        return username;
    }
}