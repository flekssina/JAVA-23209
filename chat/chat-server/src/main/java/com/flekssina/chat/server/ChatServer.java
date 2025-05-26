package com.flekssina.chat.server;

import com.flekssina.chat.common.ChatConstants;
import com.flekssina.chat.common.Message;
import com.flekssina.chat.common.MessageType;
import com.flekssina.chat.common.User;
import com.flekssina.chat.common.xml.XMLMessage;
import com.flekssina.chat.common.xml.XMLMessageEvent;
import com.flekssina.chat.common.xml.XMLUserLoginEvent;
import com.flekssina.chat.common.xml.XMLUserLogoutEvent;
import com.flekssina.chat.server.xml.XMLClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private final ServerConfig config;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final List<XMLClientHandler> xmlClients = new CopyOnWriteArrayList<>();
    private final List<Message> messageHistory = Collections.synchronizedList(new ArrayList<>());
    private ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private boolean running = false;

    public ChatServer(ServerConfig config) {
        this.config = config;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(config.getPort());
            running = true;
            logger.info("Сервер запущен на порту {}", config.getPort());

            acceptClients();
        } catch (IOException e) {
            logger.error("Ошибка при запуске сервера", e);
        }
    }

    private void acceptClients() {
        new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Новое соединение: {}", clientSocket.getInetAddress());

                    // Создаем потоки ввода-вывода
                    int clientType;
                    try {
                        clientType = clientSocket.getInputStream().read();
                        logger.info("Тип клиента: {}", clientType);
                    } catch (IOException e) {
                        logger.error("Ошибка при чтении типа клиента", e);
                        clientSocket.close();
                        continue;
                    }

                    if (clientType == 0) { // Java-сериализация
                        logger.info("Определен Java-клиент");
                        ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                        executor.submit(clientHandler);
                    } else if (clientType == 1) { // XML
                        logger.info("Определен XML-клиент");
                        XMLClientHandler xmlClientHandler = new XMLClientHandler(clientSocket, this);
                        executor.submit(xmlClientHandler);
                    } else {
                        logger.warn("Неизвестный тип клиента: {}", clientType);
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    if (running) {
                        logger.error("Ошибка при принятии соединения", e);
                    }
                }
            }
        }).start();
    }

    public void stop() {
        try {
            running = false;

            // Отключение всех клиентов
            for (ClientHandler client : clients) {
                client.disconnect();
            }

            for (XMLClientHandler xmlClient : xmlClients) {
                xmlClient.disconnect();
            }

            executor.shutdown();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            logger.info("Сервер остановлен");
        } catch (IOException e) {
            logger.error("Ошибка при остановке сервера", e);
        }
    }

    // Методы для Java-клиентов

    public void addClient(ClientHandler client) {
        clients.add(client);

        // Уведомление XML-клиентов о новом Java-пользователе
        User user = client.getUser();
        if (user != null) {
            XMLUserLoginEvent event = new XMLUserLoginEvent(user.getUsername());
            broadcastXmlMessage(event, false);
        }
    }

    public void removeClient(ClientHandler client) {
        if (clients.remove(client)) {
            logger.info("Java-клиент удален из списка: {}",
                    client.getUser() != null ? client.getUser().getUsername() : "unknown");
        }
    }

    public void broadcastMessage(Message message) {
        logger.debug("Рассылка сообщения: тип={}, отправитель={}", message.getType(), message.getSender());

        // Добавление в историю только сообщений чата
        if (message.getType() == MessageType.CHAT_MESSAGE) {
            addToHistory(message);
        }

        // Отправка сообщения Java-клиентам
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }

        // Отправка сообщения XML-клиентам только если это сообщение от Java-клиента
        // Это избавит от дублирования, когда мы конвертируем XML->Java->XML
        boolean fromJavaClient = true;

        // Проверяем, пришло ли сообщение от XML-клиента
        // Если да, то оно уже было отправлено XML-клиентам в broadcastXmlMessage
        for (XMLClientHandler xmlClient : xmlClients) {
            if (xmlClient.getUser() != null &&
                    xmlClient.getUser().getUsername().equals(message.getSender())) {
                fromJavaClient = false;
                break;
            }
        }

        // Отправляем XML-клиентам только если сообщение от Java-клиента
        if (fromJavaClient) {
            if (message.getType() == MessageType.CHAT_MESSAGE) {
                XMLMessageEvent event = new XMLMessageEvent(message.getContent(), message.getSender());
                broadcastXmlMessage(event, false); // false означает не преобразовывать обратно в Java
            } else if (message.getType() == MessageType.USER_JOINED) {
                XMLUserLoginEvent event = new XMLUserLoginEvent(message.getSender());
                broadcastXmlMessage(event, false);
            } else if (message.getType() == MessageType.USER_LEFT) {
                XMLUserLogoutEvent event = new XMLUserLogoutEvent(message.getSender());
                broadcastXmlMessage(event, false);
            }
        }
    }

    // Методы для XML-клиентов

    public void addXmlClient(XMLClientHandler client) {
        xmlClients.add(client);

        // Уведомление Java-клиентов о новом XML-пользователе
        User user = client.getUser();
        if (user != null) {
            Message message = new Message();
            message.setType(MessageType.USER_JOINED);
            message.setSender(user.getUsername());
            message.setContent("");
            message.setTimestamp(LocalDateTime.now());

            // Отправка только Java-клиентам без общего broadcast
            for (ClientHandler javaClient : clients) {
                javaClient.sendMessage(message);
            }
        }
    }

    public void removeXmlClient(XMLClientHandler client) {
        if (xmlClients.remove(client)) {
            logger.info("XML-клиент удален из списка: {}",
                    client.getUser() != null ? client.getUser().getUsername() : "unknown");

            // Уведомление Java-клиентов о выходе XML-пользователя
            User user = client.getUser();
            if (user != null) {
                Message message = new Message();
                message.setType(MessageType.USER_LEFT);
                message.setSender(user.getUsername());
                message.setContent("");
                message.setTimestamp(LocalDateTime.now());

                // Отправка только Java-клиентам без общего broadcast
                for (ClientHandler javaClient : clients) {
                    javaClient.sendMessage(message);
                }
            }
        }
    }

    public void broadcastXmlMessage(XMLMessage message, boolean convertToJava) {
        // Отправка XML-сообщения XML-клиентам
        for (XMLClientHandler client : xmlClients) {
            client.sendMessage(message);
        }

        // Преобразование XML-сообщения в Java-сообщение для Java-клиентов
        if (convertToJava) {
            if (message instanceof XMLMessageEvent) {
                XMLMessageEvent event = (XMLMessageEvent) message;

                Message javaMsg = new Message();
                javaMsg.setType(MessageType.CHAT_MESSAGE);
                javaMsg.setSender(event.getSender());
                javaMsg.setContent(event.getContent());
                javaMsg.setTimestamp(LocalDateTime.now());

                // Добавление в историю
                addToHistory(javaMsg);

                // Отправка Java-клиентам
                for (ClientHandler client : clients) {
                    client.sendMessage(javaMsg);
                }
            }
            else if (message instanceof XMLUserLoginEvent) {
                XMLUserLoginEvent event = (XMLUserLoginEvent) message;

                Message javaMsg = new Message();
                javaMsg.setType(MessageType.USER_JOINED);
                javaMsg.setSender(event.getUsername());
                javaMsg.setContent("");
                javaMsg.setTimestamp(LocalDateTime.now());

                // Отправка Java-клиентам
                for (ClientHandler client : clients) {
                    client.sendMessage(javaMsg);
                }
            }
            else if (message instanceof XMLUserLogoutEvent) {
                XMLUserLogoutEvent event = (XMLUserLogoutEvent) message;

                Message javaMsg = new Message();
                javaMsg.setType(MessageType.USER_LEFT);
                javaMsg.setSender(event.getUsername());
                javaMsg.setContent("");
                javaMsg.setTimestamp(LocalDateTime.now());

                // Отправка Java-клиентам
                for (ClientHandler client : clients) {
                    client.sendMessage(javaMsg);
                }
            }
        }
    }

    // Общие методы

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        // Java-клиенты
        for (ClientHandler client : clients) {
            if (client.getUser() != null) {
                users.add(client.getUser());
            }
        }

        // XML-клиенты
        for (XMLClientHandler client : xmlClients) {
            if (client.getUser() != null) {
                users.add(client.getUser());
            }
        }

        return users;
    }

    public boolean isUsernameTaken(String username) {
        // Проверка среди Java-клиентов
        for (ClientHandler client : clients) {
            if (client.getUser() != null && client.getUser().getUsername().equals(username)) {
                return true;
            }
        }

        // Проверка среди XML-клиентов
        for (XMLClientHandler client : xmlClients) {
            if (client.getUser() != null && client.getUser().getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    public void addToHistory(Message message) {
        if (message.getType() == MessageType.CHAT_MESSAGE) {
            messageHistory.add(message);
            logger.debug("Добавлено сообщение в историю от {}: {}", message.getSender(), message.getContent());

            // Ограничиваем размер истории
            if (messageHistory.size() > ChatConstants.HISTORY_SIZE) {
                messageHistory.remove(0);
            }
        }
    }

    public List<Message> getHistory() {
        synchronized (messageHistory) {
            // Возвращаем копию, чтобы избежать проблем с конкурентным доступом
            return new ArrayList<>(messageHistory);
        }
    }

    public void logHistoryStatus() {
        logger.info("Текущий размер истории сообщений: {}", messageHistory.size());

        if (!messageHistory.isEmpty()) {
            logger.info("Первое сообщение в истории: {} от {}",
                    messageHistory.get(0).getContent(),
                    messageHistory.get(0).getSender());

            logger.info("Последнее сообщение в истории: {} от {}",
                    messageHistory.get(messageHistory.size() - 1).getContent(),
                    messageHistory.get(messageHistory.size() - 1).getSender());
        }
    }
}