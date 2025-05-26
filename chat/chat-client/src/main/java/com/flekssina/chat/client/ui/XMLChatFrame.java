package com.flekssina.chat.client.ui;

import com.flekssina.chat.client.ChatClientListener;
import com.flekssina.chat.client.xml.XMLChatClient;
import com.flekssina.chat.common.Message;
import com.flekssina.chat.common.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class XMLChatFrame extends JFrame implements ChatClientListener {
    private static final Logger logger = LoggerFactory.getLogger(XMLChatFrame.class);

    private static final Color BACKGROUND_COLOR = new Color(142, 204, 255);
    private static final Color SYSTEM_MESSAGE_COLOR = new Color(128, 128, 128);
    private static final Color USER_JOIN_COLOR = new Color(0, 128, 0);
    private static final Color USER_LEAVE_COLOR = new Color(128, 0, 0);
    private static final Color USER_LIST_COLOR = new Color(0, 0, 128);

    private final XMLChatClient client;
    private final JTextPane chatArea;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton refreshButton;
    private final JList<String> userList;
    private final DefaultListModel<String> userListModel;
    private final JScrollPane chatScrollPane;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Message lastMessage;  // Для отслеживания дубликатов

    public XMLChatFrame(XMLChatClient client) {
        this.client = client;
        client.addListener(this);

        setTitle("XML Чат - " + client.getUsername());
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Устанавливаем нежно-розовый фон
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Панель чата
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(250, 255, 255));
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));

        chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Список пользователей
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(new Font("Arial", Font.PLAIN, 14));
        userList.setBackground(new Color(250, 250, 255));
        userList.setSelectionBackground(new Color(220, 220, 255));

        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));

        // Панель ввода сообщения
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendButton = new JButton("Отправить");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(230, 230, 250));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        refreshButton = new JButton("Обновить список");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(230, 230, 250));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshUserList();
            }
        });

        JButton exitButton = new JButton("Выйти из чата");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setBackground(new Color(220, 225, 255));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Компоновка панели ввода
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Компоновка панели кнопок
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(exitButton);

        // Компоновка нижней панели
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Вся разметка окна
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(userScrollPane, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Обработка закрытия окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        // Запрашиваем список пользователей при входе
        refreshUserList();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            try {
                client.sendChatMessage(message);
                messageField.setText("");
                messageField.requestFocus();
            } catch (IOException e) {
                logger.error("Ошибка при отправке сообщения", e);
                JOptionPane.showMessageDialog(this,
                        "Ошибка при отправке сообщения: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshUserList() {
        try {
            client.requestUserList();
        } catch (IOException e) {
            logger.error("Ошибка при запросе списка пользователей", e);
            JOptionPane.showMessageDialog(this,
                    "Ошибка при запросе списка пользователей: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Вы действительно хотите выйти из чата?",
                "Выход", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            client.logout();
            dispose();
        }
    }

    private void appendToChatArea(Message message) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Проверка на дубликат - если предыдущее сообщение такое же (по содержанию и отправителю)
                // и оно было получено менее чем 1 секунду назад, игнорируем его
                if (lastMessage != null &&
                        lastMessage.getType() == message.getType() &&
                        lastMessage.getSender().equals(message.getSender()) &&
                        lastMessage.getContent().equals(message.getContent()) &&
                        ChronoUnit.MILLIS.between(lastMessage.getTimestamp(), message.getTimestamp()) < 1000) {
                    logger.debug("Пропущено дублирующее сообщение: {}", message.getContent());
                    return;
                }

                // Сохраняем последнее сообщение для проверки дубликатов
                lastMessage = message;

                StyledDocument doc = chatArea.getStyledDocument();

                // Форматирование времени
                String time = "[" + (message.getTimestamp() != null
                        ? message.getTimestamp().format(timeFormatter)
                        : LocalDateTime.now().format(timeFormatter)) + "] ";

                // Стиль для времени
                Style timeStyle = chatArea.addStyle("TimeStyle", null);
                StyleConstants.setForeground(timeStyle, Color.GRAY);
                StyleConstants.setItalic(timeStyle, true);

                // Добавление времени
                doc.insertString(doc.getLength(), time, timeStyle);

                // Стиль для сообщения в зависимости от типа
                Style messageStyle = chatArea.addStyle("MessageStyle", null);

                switch (message.getType()) {
                    case CHAT_MESSAGE:
                        StyleConstants.setForeground(messageStyle, Color.BLACK);
                        StyleConstants.setBold(messageStyle, true);
                        doc.insertString(doc.getLength(), message.getSender() + ": ", messageStyle);

                        StyleConstants.setBold(messageStyle, false);
                        doc.insertString(doc.getLength(), message.getContent() + "\n", messageStyle);
                        break;

                    case USER_JOINED:
                        StyleConstants.setForeground(messageStyle, USER_JOIN_COLOR);
                        StyleConstants.setItalic(messageStyle, true);
                        doc.insertString(doc.getLength(), "Пользователь " + message.getSender() + " присоединился к чату\n", messageStyle);
                        break;

                    case USER_LEFT:
                        StyleConstants.setForeground(messageStyle, USER_LEAVE_COLOR);
                        StyleConstants.setItalic(messageStyle, true);
                        doc.insertString(doc.getLength(), "Пользователь " + message.getSender() + " покинул чат\n", messageStyle);
                        break;

                    case USER_LIST:
                        StyleConstants.setForeground(messageStyle, USER_LIST_COLOR);
                        doc.insertString(doc.getLength(), "Список пользователей обновлен\n", messageStyle);
                        break;

                    case ERROR:
                        StyleConstants.setForeground(messageStyle, Color.RED);
                        doc.insertString(doc.getLength(), "Ошибка: " + message.getContent() + "\n", messageStyle);
                        break;

                    default:
                        StyleConstants.setForeground(messageStyle, SYSTEM_MESSAGE_COLOR);
                        doc.insertString(doc.getLength(), message.getContent() + "\n", messageStyle);
                }

                // Прокрутка вниз
                chatArea.setCaretPosition(doc.getLength());

            } catch (BadLocationException e) {
                logger.error("Ошибка при добавлении текста в чат", e);
            }
        });
    }

    @Override
    public void onLoginSuccess() {
        // Уже залогинены при создании окна чата
    }

    @Override
    public void onMessageReceived(Message message) {
        appendToChatArea(message);
    }

    @Override
    public void onUserListReceived(String userListStr) {
        userListModel.clear();

        String[] users = userListStr.split("\n");
        for (String user : users) {
            if (!user.trim().isEmpty()) {
                userListModel.addElement(user);
            }
        }

        // Сообщение о обновлении списка пользователей
        Message message = new Message(MessageType.USER_LIST, "", "");
        message.setTimestamp(LocalDateTime.now());
        appendToChatArea(message);
    }

    @Override
    public void onError(String errorMessage) {
        Message message = new Message(MessageType.ERROR, "", errorMessage);
        message.setTimestamp(LocalDateTime.now());
        appendToChatArea(message);

        JOptionPane.showMessageDialog(this,
                errorMessage,
                "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onConnectionError(String errorMessage) {
        JOptionPane.showMessageDialog(this,
                errorMessage,
                "Ошибка соединения", JOptionPane.ERROR_MESSAGE);

        dispose();
    }
}