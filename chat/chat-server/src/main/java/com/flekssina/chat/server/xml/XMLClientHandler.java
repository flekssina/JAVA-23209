package com.flekssina.chat.server.xml;

import com.flekssina.chat.common.Message;
import com.flekssina.chat.common.MessageType;
import com.flekssina.chat.common.User;
import com.flekssina.chat.common.xml.*;
import com.flekssina.chat.server.ChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Обработчик XML-клиента на сервере.
 */
public class XMLClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(XMLClientHandler.class);

    private final Socket socket;
    private final ChatServer server;
    private User user;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean running = true;

    public XMLClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            logger.error("Ошибка при создании потоков ввода/вывода", e);
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                if (XMLProtocol.hasData(inputStream)) {
                    XMLMessage message = XMLProtocol.receiveXML(inputStream);
                    handleMessage(message);
                } else {
                    // Добавляем задержку для снижения нагрузки на CPU
                    Thread.sleep(50);
                }
            }
        } catch (SocketException e) {
            logger.info("XML-клиент разорвал соединение");
        } catch (IOException e) {
            logger.error("Ошибка при чтении XML-сообщения", e);
        } catch (InterruptedException e) {
            logger.warn("Поток XML-клиента прерван", e);
        } finally {
            disconnect();
        }
    }

    private void handleMessage(XMLMessage message) {
        switch (message.getType()) {
            case LOGIN:
                handleLogin((XMLLoginCommand) message);
                break;
            case LOGOUT:
                handleLogout((XMLLogoutCommand) message);
                break;
            case MESSAGE:
                handleChatMessage((XMLMessageCommand) message);
                break;
            case LIST:
                handleListUsers((XMLListCommand) message);
                break;
            default:
                logger.warn("Неизвестный тип XML-сообщения: {}", message.getType());
        }
    }

    private void handleLogin(XMLLoginCommand command) {
        String username = command.getUsername();
        String clientType = command.getClientType();

        // Проверка на существование пользователя с таким именем
        if (server.isUsernameTaken(username)) {
            sendError("Пользователь с именем " + username + " уже существует");
            return;
        }

        user = new User(username, clientType);

        // Отправка сессионного идентификатора
        XMLSuccessResponse response = new XMLSuccessResponse();
        response.setSessionId(user.getSessionId());
        sendMessage(response);

        // Сохранение пользователя на сервере
        server.addXmlClient(this);

        // Уведомление всех о новом пользователе - делаем через сервер
        server.broadcastXmlMessage(new XMLUserLoginEvent(username), true);

        logger.info("XML-пользователь {} подключился", username);

        try {
            // Даем время на стабилизацию соединения
            Thread.sleep(100);

            // Конвертируем историю сообщений в XML-формат
            List<Message> history = server.getHistory();
            for (Message historyMessage : history) {
                if (historyMessage.getType() == MessageType.CHAT_MESSAGE) {
                    XMLMessageEvent event = new XMLMessageEvent(
                            historyMessage.getContent(),
                            historyMessage.getSender());
                    sendMessage(event);
                    // Небольшая пауза между сообщениями
                    Thread.sleep(20);
                }
            }
        } catch (InterruptedException e) {
            logger.warn("Прерывание потока при отправке истории");
        }
    }

    private void handleLogout(XMLLogoutCommand command) {
        if (user != null && command.getSessionId().equals(user.getSessionId())) {
            disconnect();
        } else {
            sendError("Неверный идентификатор сессии");
        }
    }

    private void handleChatMessage(XMLMessageCommand command) {
        if (user != null && command.getSessionId().equals(user.getSessionId())) {
            // Создаем обычное сообщение для истории
            Message chatMessage = new Message();
            chatMessage.setType(MessageType.CHAT_MESSAGE);
            chatMessage.setSender(user.getUsername());
            chatMessage.setContent(command.getContent());
            chatMessage.setTimestamp(LocalDateTime.now());

            // Добавление в историю
            server.addToHistory(chatMessage);

            // Создаем XML-сообщение для отправки клиентам
            XMLMessageEvent event = new XMLMessageEvent(command.getContent(), user.getUsername());

            // Отправка всем через сервер
            server.broadcastXmlMessage(event, true);

            // Отправляем подтверждение
            sendMessage(new XMLSuccessResponse());

            logger.info("XML-сообщение от {}: {}", user.getUsername(), command.getContent());
        } else {
            sendError("Неверный идентификатор сессии");
        }
    }

    private void handleListUsers(XMLListCommand command) {
        if (user != null && command.getSessionId().equals(user.getSessionId())) {
            List<User> users = server.getUsers();

            XMLSuccessResponse response = new XMLSuccessResponse();

            for (User u : users) {
                response.addUser(u.getUsername(), u.getClientType());
            }

            sendMessage(response);
        } else {
            sendError("Неверный идентификатор сессии");
        }
    }

    public void sendMessage(XMLMessage message) {
        try {
            XMLProtocol.sendXML(message, outputStream);
            // logger.debug("Отправлено XML-сообщение пользователю {}: {}",
            //        user != null ? user.getUsername() : "unknown", message.getType());
        } catch (IOException e) {
            logger.error("Ошибка при отправке XML-сообщения пользователю {}",
                    user != null ? user.getUsername() : "unknown", e);
        }
    }

    private void sendError(String errorMessage) {
        sendMessage(new XMLErrorResponse(errorMessage));
    }

    public void disconnect() {
        if (user != null) {
            logger.info("Отключение XML-пользователя: {}", user.getUsername());

            // Удаляем клиента из списка на сервере
            server.removeXmlClient(this);

            // Уведомляем всех о выходе пользователя
            server.broadcastXmlMessage(new XMLUserLogoutEvent(user.getUsername()), true);
        }

        try {
            running = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error("Ошибка при закрытии XML-соединения", e);
        }
    }

    public User getUser() {
        return user;
    }
}