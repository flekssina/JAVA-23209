package com.flekssina.chat.common.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Класс для работы с XML-протоколом чата.
 */
public class XMLProtocol {
    private static final Logger logger = LoggerFactory.getLogger(XMLProtocol.class);

    /**
     * Преобразует XML-строку в объект сообщения.
     * @param xmlStr XML-строка
     * @return Объект сообщения
     */
    public static XMLMessage parseXML(String xmlStr) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));

            Element root = doc.getDocumentElement();
            String rootName = root.getNodeName();

            // Определяем тип сообщения по корневому элементу
            if ("command".equals(rootName)) {
                String commandName = root.getAttribute("name");
                switch (commandName) {
                    case "login":
                        return parseLoginCommand(root);
                    case "list":
                        return parseListCommand(root);
                    case "message":
                        return parseMessageCommand(root);
                    case "logout":
                        return parseLogoutCommand(root);
                }
            } else if ("success".equals(rootName)) {
                return parseSuccessResponse(root);
            } else if ("error".equals(rootName)) {
                return parseErrorResponse(root);
            } else if ("event".equals(rootName)) {
                String eventName = root.getAttribute("name");
                switch (eventName) {
                    case "message":
                        return parseMessageEvent(root);
                    case "userlogin":
                        return parseUserLoginEvent(root);
                    case "userlogout":
                        return parseUserLogoutEvent(root);
                }
            }

            throw new IllegalArgumentException("Неизвестный формат XML: " + xmlStr);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Ошибка при разборе XML: {}", xmlStr, e);
            throw new RuntimeException("Ошибка при разборе XML", e);
        }
    }

    /**
     * Отправка XML-сообщения через поток.
     * @param message Сообщение
     * @param outputStream Поток вывода
     * @throws IOException При ошибке ввода-вывода
     */
    public static void sendXML(XMLMessage message, OutputStream outputStream) throws IOException {
        String xmlStr = message.toXML();
        byte[] xmlBytes = xmlStr.getBytes(StandardCharsets.UTF_8);

        // Записываем длину сообщения (4 байта)
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(xmlBytes.length);
        outputStream.write(buffer.array());

        // Записываем само сообщение
        outputStream.write(xmlBytes);
        outputStream.flush();

        logger.debug("Отправлено XML-сообщение ({} байт): {}", xmlBytes.length, xmlStr);
    }

    /**
     * Чтение XML-сообщения из потока.
     * @param inputStream Поток ввода
     * @return Сообщение
     * @throws IOException При ошибке ввода-вывода
     */
    public static XMLMessage receiveXML(InputStream inputStream) throws IOException {
        // Читаем длину сообщения (4 байта)
        byte[] lengthBytes = new byte[4];
        int bytesRead = inputStream.read(lengthBytes);
        if (bytesRead != 4) {
            throw new IOException("Не удалось прочитать длину XML-сообщения");
        }

        ByteBuffer buffer = ByteBuffer.wrap(lengthBytes);
        int length = buffer.getInt();

        // Читаем само сообщение
        byte[] xmlBytes = new byte[length];
        bytesRead = 0;
        int totalBytesRead = 0;

        while (totalBytesRead < length) {
            bytesRead = inputStream.read(xmlBytes, totalBytesRead, length - totalBytesRead);
            if (bytesRead == -1) {
                throw new IOException("Преждевременный конец потока при чтении XML-сообщения");
            }
            totalBytesRead += bytesRead;
        }

        String xmlStr = new String(xmlBytes, StandardCharsets.UTF_8);
        logger.debug("Получено XML-сообщение ({} байт): {}", length, xmlStr);

        return parseXML(xmlStr);
    }

    /**
     * Проверяет доступность данных в потоке.
     * @param inputStream Поток ввода
     * @return true, если в потоке есть данные для чтения
     * @throws IOException При ошибке ввода-вывода
     */
    public static boolean hasData(InputStream inputStream) throws IOException {
        return inputStream.available() >= 4;
    }

    // Методы для разбора конкретных типов сообщений

    private static XMLMessage parseLoginCommand(Element root) {
        NodeList nameNodes = root.getElementsByTagName("name");
        NodeList typeNodes = root.getElementsByTagName("type");

        if (nameNodes.getLength() == 0 || typeNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствуют обязательные поля в команде входа");
        }

        String username = nameNodes.item(0).getTextContent();
        String clientType = typeNodes.item(0).getTextContent();

        return new XMLLoginCommand(username, clientType);
    }

    private static XMLMessage parseListCommand(Element root) {
        NodeList sessionNodes = root.getElementsByTagName("session");

        if (sessionNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствует идентификатор сессии в команде запроса списка");
        }

        String sessionId = sessionNodes.item(0).getTextContent();

        return new XMLListCommand(sessionId);
    }

    private static XMLMessage parseMessageCommand(Element root) {
        NodeList messageNodes = root.getElementsByTagName("message");
        NodeList sessionNodes = root.getElementsByTagName("session");

        if (messageNodes.getLength() == 0 || sessionNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствуют обязательные поля в команде отправки сообщения");
        }

        String content = messageNodes.item(0).getTextContent();
        String sessionId = sessionNodes.item(0).getTextContent();

        return new XMLMessageCommand(content, sessionId);
    }

    private static XMLMessage parseLogoutCommand(Element root) {
        NodeList sessionNodes = root.getElementsByTagName("session");

        if (sessionNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствует идентификатор сессии в команде выхода");
        }

        String sessionId = sessionNodes.item(0).getTextContent();

        return new XMLLogoutCommand(sessionId);
    }

    private static XMLMessage parseSuccessResponse(Element root) {
        NodeList sessionNodes = root.getElementsByTagName("session");
        NodeList listUsersNodes = root.getElementsByTagName("listusers");

        XMLSuccessResponse response = new XMLSuccessResponse();

        if (sessionNodes.getLength() > 0) {
            response.setSessionId(sessionNodes.item(0).getTextContent());
        }

        if (listUsersNodes.getLength() > 0) {
            Element listUsersElement = (Element) listUsersNodes.item(0);
            NodeList userNodes = listUsersElement.getElementsByTagName("user");

            for (int i = 0; i < userNodes.getLength(); i++) {
                Element userElement = (Element) userNodes.item(i);
                NodeList nameNodes = userElement.getElementsByTagName("name");
                NodeList typeNodes = userElement.getElementsByTagName("type");

                if (nameNodes.getLength() > 0 && typeNodes.getLength() > 0) {
                    String name = nameNodes.item(0).getTextContent();
                    String type = typeNodes.item(0).getTextContent();
                    response.addUser(name, type);
                }
            }
        }

        return response;
    }

    private static XMLMessage parseErrorResponse(Element root) {
        NodeList messageNodes = root.getElementsByTagName("message");

        if (messageNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствует текст сообщения об ошибке");
        }

        String errorMessage = messageNodes.item(0).getTextContent();

        return new XMLErrorResponse(errorMessage);
    }

    private static XMLMessage parseMessageEvent(Element root) {
        NodeList messageNodes = root.getElementsByTagName("message");
        NodeList nameNodes = root.getElementsByTagName("name");

        if (messageNodes.getLength() == 0 || nameNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствуют обязательные поля в событии сообщения");
        }

        String content = messageNodes.item(0).getTextContent();
        String sender = nameNodes.item(0).getTextContent();

        return new XMLMessageEvent(content, sender);
    }

    private static XMLMessage parseUserLoginEvent(Element root) {
        NodeList nameNodes = root.getElementsByTagName("name");

        if (nameNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствует имя пользователя в событии входа");
        }

        String username = nameNodes.item(0).getTextContent();

        return new XMLUserLoginEvent(username);
    }

    private static XMLMessage parseUserLogoutEvent(Element root) {
        NodeList nameNodes = root.getElementsByTagName("name");

        if (nameNodes.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствует имя пользователя в событии выхода");
        }

        String username = nameNodes.item(0).getTextContent();

        return new XMLUserLogoutEvent(username);
    }
}