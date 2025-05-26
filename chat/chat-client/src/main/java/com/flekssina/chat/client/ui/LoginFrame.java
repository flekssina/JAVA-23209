package com.flekssina.chat.client.ui;

import com.flekssina.chat.client.ChatClient;
import com.flekssina.chat.client.ChatClientListener;
import com.flekssina.chat.client.ClientConfig;
import com.flekssina.chat.client.xml.XMLChatClient;
import com.flekssina.chat.common.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginFrame extends JFrame implements ChatClientListener {
    private static final Logger logger = LoggerFactory.getLogger(LoginFrame.class);

    private JTextField usernameField;
    private JTextField serverField;
    private JTextField portField;
    private JButton loginButton;
    private JRadioButton javaRadio;
    private JRadioButton xmlRadio;

    private ChatClient javaClient;
    private XMLChatClient xmlClient;

    // Цвет фона - нежно-розовый
    private static final Color BACKGROUND_COLOR = new Color(255, 142, 226);

    public LoginFrame() {
        initComponents();

        setTitle("Вход в чат");
        setSize(500, 350); // Увеличен размер окна еще больше
        setMinimumSize(new Dimension(500, 350)); // Задаем минимальный размер
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Установка нежно-розового фона
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR); // Установка нежно-розового фона для панели

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(12, 15, 12, 15);
        // Загрузка конфигурации
        ClientConfig config = new ClientConfig();

        // Заголовок
        JLabel titleLabel = new JLabel("Добро пожаловать в чат!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(titleLabel, constraints);

        // Имя пользователя
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        JLabel userLabel = new JLabel("Имя пользователя:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(userLabel, constraints);

        constraints.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField, constraints);

        // Сервер
        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel serverLabel = new JLabel("Сервер:");
        serverLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(serverLabel, constraints);

        constraints.gridx = 1;
        serverField = new JTextField(config.getServerHost(), 20);
        serverField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(serverField, constraints);

        // Порт
        constraints.gridx = 0;
        constraints.gridy = 3;
        JLabel portLabel = new JLabel("Порт:");
        portLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(portLabel, constraints);

        constraints.gridx = 1;
        portField = new JTextField(String.valueOf(config.getServerPort()), 20);
        portField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(portField, constraints);

        // Выбор типа клиента
        constraints.gridx = 0;
        constraints.gridy = 4;
        JLabel clientTypeLabel = new JLabel("Тип клиента:");
        clientTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(clientTypeLabel, constraints);

        constraints.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setBackground(BACKGROUND_COLOR);

        javaRadio = new JRadioButton("Java-сериализация");
        javaRadio.setFont(new Font("Arial", Font.PLAIN, 14));
        javaRadio.setSelected(true);
        javaRadio.setBackground(BACKGROUND_COLOR);

        xmlRadio = new JRadioButton("XML-протокол");
        xmlRadio.setFont(new Font("Arial", Font.PLAIN, 14));
        xmlRadio.setBackground(BACKGROUND_COLOR);

        ButtonGroup clientTypeGroup = new ButtonGroup();
        clientTypeGroup.add(javaRadio);
        clientTypeGroup.add(xmlRadio);

        radioPanel.add(javaRadio);
        radioPanel.add(xmlRadio);

        panel.add(radioPanel, constraints);

        // Кнопка входа
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(20, 10, 10, 10); // Увеличен верхний отступ для кнопки

        loginButton = new JButton("Войти в чат");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(150, 40)); // Увеличен размер кнопки
        loginButton.addActionListener(e -> login());
        panel.add(loginButton, constraints);

        getContentPane().add(panel);
        // Увеличим размер элементов
        usernameField.setPreferredSize(new Dimension(200, 30));
        serverField.setPreferredSize(new Dimension(200, 30));
        portField.setPreferredSize(new Dimension(200, 30));

        // Явно устанавливаем шрифт для лучшей читаемости
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Применяем шрифт к компонентам
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        userLabel.setFont(labelFont);
        serverLabel.setFont(labelFont);
        portLabel.setFont(labelFont);
        usernameField.setFont(fieldFont);
        serverField.setFont(fieldFont);
        portField.setFont(fieldFont);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String server = serverField.getText().trim();
        String portText = portField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Введите имя пользователя",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (server.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Введите адрес сервера",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Неверный формат порта",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создание конфигурации
        ClientConfig config = new ClientConfig() {
            @Override
            public String getServerHost() {
                return server;
            }

            @Override
            public int getServerPort() {
                return port;
            }
        };

        loginButton.setEnabled(false);
        loginButton.setText("Подключение...");

        // Подключение к серверу в зависимости от выбранного типа клиента
        if (javaRadio.isSelected()) {
            connectJavaClient(config, username);
        } else {
            connectXmlClient(config, username);
        }
    }

    private void connectJavaClient(ClientConfig config, String username) {
        // Создание Java-клиента
        javaClient = new ChatClient(config);
        javaClient.addListener(this);

        // Подключение к серверу
        try {
            javaClient.connect();
            javaClient.login(username);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка при подключении к серверу: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            loginButton.setEnabled(true);
            loginButton.setText("Войти в чат");
            logger.error("Ошибка при подключении Java-клиента к серверу", e);
        }
    }

    private void connectXmlClient(ClientConfig config, String username) {
        // Создание XML-клиента
        xmlClient = new XMLChatClient(config);
        xmlClient.addListener(this);

        // Подключение к серверу
        try {
            xmlClient.connect();
            xmlClient.login(username);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка при подключении к серверу: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            loginButton.setEnabled(true);
            loginButton.setText("Войти в чат");
            logger.error("Ошибка при подключении XML-клиента к серверу", e);
        }
    }

    @Override
    public void onLoginSuccess() {
        SwingUtilities.invokeLater(() -> {
            if (javaRadio.isSelected() && javaClient != null) {
                // Создание окна чата для Java-клиента
                ChatFrame chatFrame = new ChatFrame(javaClient);
                chatFrame.setVisible(true);
                dispose();
            } else if (xmlRadio.isSelected() && xmlClient != null) {
                // Создание окна чата для XML-клиента
                XMLChatFrame chatFrame = new XMLChatFrame(xmlClient);
                chatFrame.setVisible(true);
                dispose();
            }
        });
    }

    @Override
    public void onError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Ошибка: " + errorMessage,
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            loginButton.setEnabled(true);
            loginButton.setText("Войти в чат");
        });
    }

    @Override
    public void onMessageReceived(Message message) {
        // Не требуется обработка на экране входа
    }

    @Override
    public void onUserListReceived(String userList) {
        // Не требуется обработка на экране входа
    }

    @Override
    public void onConnectionError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Ошибка соединения: " + errorMessage,
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            loginButton.setEnabled(true);
            loginButton.setText("Войти в чат");
        });
    }
}