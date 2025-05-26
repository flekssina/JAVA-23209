package com.flekssina.chat.server;

import com.flekssina.chat.common.Message;
import com.flekssina.chat.common.MessageType;
import com.flekssina.chat.common.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket socket;
    private final ChatServer server;
    private User user;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean running = true;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;

        try {
            // Важно сначала создать выходной поток и вызвать flush()
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();

            // Затем создаем входной поток
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.error("Ошибка при создании потоков ввода/вывода", e);
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                try {
                    // Читаем сообщение от клиента
                    Message message = (Message) inputStream.readObject();
                    handleMessage(message);
                } catch (ClassNotFoundException e) {
                    logger.error("Получено сообщение неизвестного класса", e);
                } catch (SocketException e) {
                    // Соединение разорвано
                    logger.info("Клиент разорвал соединение");
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка при чтении сообщения", e);
        } finally {
            disconnect();
        }
    }

    private void handleMessage(Message message) {
        if (message == null) {
            logger.warn("Получено пустое сообщение");
            return;
        }

        switch (message.getType()) {
            case LOGIN:
                handleLogin(message);
                break;
            case LOGOUT:
                handleLogout(message);
                break;
            case CHAT_MESSAGE:
                handleChatMessage(message);
                break;
            case LIST_USERS:
                handleListUsers();
                break;
            default:
                logger.warn("Неизвестный тип сообщения: {}", message.getType());
        }
    }

    private void handleLogin(Message message) {
        String username = message.getSender();

        // Проверка на существование пользователя с таким именем
        if (server.isUsernameTaken(username)) {
            sendErrorMessage("Пользователь с именем " + username + " уже существует");
            return;
        }

        // Создание пользователя
        user = new User(username, message.getContent());

        // Отправка подтверждения
        Message response = new Message(MessageType.LOGIN_SUCCESS, "", user.getSessionId());
        sendMessage(response);

        // Добавление клиента к серверу и сохранение в списке перед отправкой уведомления
        server.addClient(this);

        logger.info("Пользователь {} подключился", username);

        // Отправка уведомления после регистрации клиента
        // Отправляем сообщение о подключении себе тоже
        Message selfJoinMessage = new Message(MessageType.USER_JOINED, username, "");
        selfJoinMessage.setTimestamp(LocalDateTime.now());
        sendMessage(selfJoinMessage);

        // Отправка уведомления всем остальным
        Message newUserMessage = new Message(MessageType.USER_JOINED, username, "");
        newUserMessage.setTimestamp(LocalDateTime.now());
        server.broadcastMessage(newUserMessage);

        try {
            // Небольшая пауза перед отправкой истории
            Thread.sleep(100);

            // Отправка истории сообщений новому пользователю
            server.logHistoryStatus();
            List<Message> history = server.getHistory();
            logger.info("Отправка истории: {} сообщений", history.size());

            for (Message historyMessage : history) {
                try {
                    sendMessage(historyMessage);
                    // Небольшая пауза между сообщениями
                    Thread.sleep(20);
                } catch (Exception e) {
                    logger.error("Ошибка при отправке истории: {}", e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            logger.warn("Прерывание потока при отправке истории");
        }
    }

    private void handleLogout(Message message) {
        if (user != null && message.getContent().equals(user.getSessionId())) {
            disconnect();
        } else {
            sendErrorMessage("Неверный идентификатор сессии");
        }
    }

    private void handleChatMessage(Message message) {
        if (user != null && message.getContent() != null && !message.getContent().isEmpty()) {
            // Обновляем отправителя и добавляем timestamp
            message.setSender(user.getUsername());
            message.setTimestamp(LocalDateTime.now());

            // Рассылаем всем пользователям
            server.broadcastMessage(message);

            logger.info("Сообщение от {}: {}", user.getUsername(), message.getContent());
        }
    }

    private void handleListUsers() {
        if (user != null) {
            List<User> users = server.getUsers();

            StringBuilder userListStr = new StringBuilder();
            for (User u : users) {
                userListStr.append(u.getUsername())
                        .append(" (")
                        .append(u.getClientType())
                        .append(")\n");
            }

            Message response = new Message(MessageType.USER_LIST, "", userListStr.toString());
            sendMessage(response);
        }
    }

    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            // logger.debug("Отправлено сообщение клиенту {}: тип={}",
            //     user != null ? user.getUsername() : "unknown", message.getType());
        } catch (IOException e) {
            logger.error("Ошибка при отправке сообщения клиенту {}",
                    user != null ? user.getUsername() : "unknown", e);
        }
    }

    private void sendErrorMessage(String errorMessage) {
        Message message = new Message(MessageType.ERROR, "", errorMessage);
        sendMessage(message);
    }

    public void disconnect() {
        if (user != null) {
            logger.info("Отключение пользователя: {}", user.getUsername());

            // Создаем сообщение о выходе
            Message message = new Message(MessageType.USER_LEFT, user.getUsername(), "");
            message.setTimestamp(LocalDateTime.now());

            // Удаляем клиента из списка на сервере (без отправки сообщения о выходе)
            server.removeClient(this);

            // Отправляем сообщение о выходе после удаления клиента
            // Это предотвратит получение клиентом собственного сообщения о выходе
            server.broadcastMessage(message);
        }

        try {
            running = false;

            if (outputStream != null) {
                outputStream.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error("Ошибка при закрытии соединения", e);
        }
    }

    public User getUser() {
        return user;
    }
}