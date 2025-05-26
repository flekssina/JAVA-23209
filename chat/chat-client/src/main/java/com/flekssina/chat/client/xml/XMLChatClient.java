package com.flekssina.chat.client.xml;

import com.flekssina.chat.client.ChatClientListener;
import com.flekssina.chat.client.ClientConfig;
import com.flekssina.chat.common.Message;
import com.flekssina.chat.common.MessageType;
import com.flekssina.chat.common.xml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * XML-версия клиента чата.
 */
public class XMLChatClient {
    private static final Logger logger = LoggerFactory.getLogger(XMLChatClient.class);

    private final ClientConfig config;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String username;
    private String sessionId;
    private boolean connected = false;

    private final List<ChatClientListener> listeners = new ArrayList<>();

    private static final String CLIENT_TYPE = "XMLJavaClient";

    public XMLChatClient(ClientConfig config) {
        this.config = config;
    }

    public void connect() throws IOException {
        // Создаем сокет для соединения
        socket = new Socket(config.getServerHost(), config.getServerPort());

        // Отправляем байт, указывающий на XML-клиент
        socket.getOutputStream().write(1);
        socket.getOutputStream().flush();

        // Получаем потоки ввода-вывода
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        connected = true;

        // Запуск потока для чтения сообщений
        new Thread(this::receiveMessages).start();

        logger.info("XML-клиент подключен к серверу {}:{}", config.getServerHost(), config.getServerPort());
    }
    public void login(String username) throws IOException {
        this.username = username;

        XMLLoginCommand loginCommand = new XMLLoginCommand(username, CLIENT_TYPE);
        XMLProtocol.sendXML(loginCommand, outputStream);
    }

    public void logout() {
        if (!connected || sessionId == null) {
            return;
        }

        try {
            XMLLogoutCommand logoutCommand = new XMLLogoutCommand(sessionId);
            XMLProtocol.sendXML(logoutCommand, outputStream);
        } catch (IOException e) {
            logger.error("Ошибка при выходе из чата", e);
        } finally {
            disconnect();
        }
    }

    public void sendChatMessage(String content) throws IOException {
        XMLMessageCommand messageCommand = new XMLMessageCommand(content, sessionId);
        XMLProtocol.sendXML(messageCommand, outputStream);
    }

    public void requestUserList() throws IOException {
        XMLListCommand listCommand = new XMLListCommand(sessionId);
        XMLProtocol.sendXML(listCommand, outputStream);
    }

    private void receiveMessages() {
        try {
            while (connected) {
                // Проверяем, есть ли данные для чтения
                if (XMLProtocol.hasData(inputStream)) {
                    XMLMessage xmlMessage = XMLProtocol.receiveXML(inputStream);
                    handleMessage(xmlMessage);
                } else {
                    // Добавляем задержку для снижения нагрузки на CPU
                    Thread.sleep(50);
                }
            }
        } catch (IOException e) {
            if (connected) {
                logger.error("Ошибка при чтении XML-сообщения", e);
                notifyConnectionError("Потеряно соединение с сервером");
                disconnect();
            }
        } catch (InterruptedException e) {
            logger.warn("Поток чтения XML-сообщений прерван", e);
        }
    }

    private void handleMessage(XMLMessage xmlMessage) {
        switch (xmlMessage.getType()) {
            case SUCCESS:
                handleSuccessResponse((XMLSuccessResponse) xmlMessage);
                break;
            case ERROR:
                handleErrorResponse((XMLErrorResponse) xmlMessage);
                break;
            case MESSAGE_EVENT:
                handleMessageEvent((XMLMessageEvent) xmlMessage);
                break;
            case USER_LOGIN:
                handleUserLoginEvent((XMLUserLoginEvent) xmlMessage);
                break;
            case USER_LOGOUT:
                handleUserLogoutEvent((XMLUserLogoutEvent) xmlMessage);
                break;
            default:
                logger.warn("Неизвестный тип XML-сообщения: {}", xmlMessage.getType());
        }
    }

    private void handleSuccessResponse(XMLSuccessResponse response) {
        // Если получен идентификатор сессии, сохраняем его
        if (response.getSessionId() != null) {
            this.sessionId = response.getSessionId();
            notifyLoginSuccess();
        }

        // Если получен список пользователей
        if (!response.getUsers().isEmpty()) {
            StringBuilder userListStr = new StringBuilder();

            for (XMLSuccessResponse.UserInfo user : response.getUsers()) {
                userListStr.append(user.getName())
                        .append(" (")
                        .append(user.getType())
                        .append(")\n");
            }

            notifyUserList(userListStr.toString());
        }
    }

    private void handleErrorResponse(XMLErrorResponse response) {
        notifyError(response.getErrorMessage());
    }

    private void handleMessageEvent(XMLMessageEvent event) {
        Message message = new Message();
        message.setType(MessageType.CHAT_MESSAGE);
        message.setSender(event.getSender());
        message.setContent(event.getContent());
        message.setTimestamp(LocalDateTime.now());

        notifyMessageReceived(message);
    }

    private void handleUserLoginEvent(XMLUserLoginEvent event) {
        Message message = new Message();
        message.setType(MessageType.USER_JOINED);
        message.setSender(event.getUsername());
        message.setContent("");
        message.setTimestamp(LocalDateTime.now());

        notifyMessageReceived(message);
    }

    private void handleUserLogoutEvent(XMLUserLogoutEvent event) {
        Message message = new Message();
        message.setType(MessageType.USER_LEFT);
        message.setSender(event.getUsername());
        message.setContent("");
        message.setTimestamp(LocalDateTime.now());

        notifyMessageReceived(message);
    }

    public void disconnect() {
        try {
            connected = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            logger.info("XML-клиент отключен от сервера");
        } catch (IOException e) {
            logger.error("Ошибка при отключении XML-клиента от сервера", e);
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