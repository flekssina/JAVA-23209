package com.carfactory.factory.view;

import com.carfactory.factory.controller.FactoryController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FactoryView extends JFrame {
    private final FactoryController controller;

    private JSlider bodySupplierSlider;
    private JSlider motorSupplierSlider;
    private JSlider accessorySupplierSlider;
    private JSlider dealerSlider;
    private JSlider workerSlider;

    private JLabel bodyStorageLabel;
    private JLabel motorStorageLabel;
    private JLabel accessoryStorageLabel;
    private JLabel autoStorageLabel;

    private JLabel bodyProducedLabel;
    private JLabel motorProducedLabel;
    private JLabel accessoryProducedLabel;
    private JLabel autoProducedLabel;
    private JLabel autoSoldLabel;
    private JLabel taskQueueLabel;

    private Timer updateTimer;

    public FactoryView(FactoryController controller) {
        this.controller = controller;

        setTitle("Эмулятор работы фабрики");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        // Обработчик закрытия окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopFactory();
            }
        });

        // Таймер обновления информации
        updateTimer = new Timer(500, e -> updateInfo());
        updateTimer.start();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));

        // === ВЕРХНЯЯ ЧАСТЬ: ПАНЕЛИ СОСТОЯНИЯ ===
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        // Панель состояния складов (слева вверху)
        JPanel storagePanel = new JPanel();
        storagePanel.setLayout(new BoxLayout(storagePanel, BoxLayout.Y_AXIS));
        storagePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Состояние складов", TitledBorder.LEFT, TitledBorder.TOP));

        bodyStorageLabel = new JLabel("Склад кузовов: 0 / " + controller.getBodyStorage().getCapacity());
        motorStorageLabel = new JLabel("Склад двигателей: 0 / " + controller.getMotorStorage().getCapacity());
        accessoryStorageLabel = new JLabel("Склад аксессуаров: 0 / " + controller.getAccessoryStorage().getCapacity());
        autoStorageLabel = new JLabel("Склад автомобилей: 0 / " + controller.getAutoStorage().getCapacity());

        storagePanel.add(bodyStorageLabel);
        storagePanel.add(Box.createVerticalStrut(2)); // Малый интервал между метками
        storagePanel.add(motorStorageLabel);
        storagePanel.add(Box.createVerticalStrut(2));
        storagePanel.add(accessoryStorageLabel);
        storagePanel.add(Box.createVerticalStrut(2));
        storagePanel.add(autoStorageLabel);

        // Панель статистики (справа вверху)
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Состояние системы", TitledBorder.LEFT, TitledBorder.TOP));

        bodyProducedLabel = new JLabel("Произведено кузовов: 0");
        motorProducedLabel = new JLabel("Произведено двигателей: 0");
        accessoryProducedLabel = new JLabel("Произведено аксессуаров: 0");
        autoProducedLabel = new JLabel("Собрано автомобилей: 0");
        autoSoldLabel = new JLabel("Продано автомобилей: 0");
        taskQueueLabel = new JLabel("Задач в очереди: 0");

        statsPanel.add(bodyProducedLabel);
        statsPanel.add(Box.createVerticalStrut(2));
        statsPanel.add(motorProducedLabel);
        statsPanel.add(Box.createVerticalStrut(2));
        statsPanel.add(accessoryProducedLabel);
        statsPanel.add(Box.createVerticalStrut(2));
        statsPanel.add(autoProducedLabel);
        statsPanel.add(Box.createVerticalStrut(2));
        statsPanel.add(autoSoldLabel);
        statsPanel.add(Box.createVerticalStrut(2));
        statsPanel.add(taskQueueLabel);

        topPanel.add(storagePanel);
        topPanel.add(statsPanel);

        add(topPanel, BorderLayout.NORTH);

        // === НИЖНЯЯ ЧАСТЬ: ПОЛЗУНКИ ===
        JPanel slidersPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        slidersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Скорость работы", TitledBorder.LEFT, TitledBorder.TOP));

        // Ползунок для скорости поставщика кузовов
        JPanel bodySupplierPanel = new JPanel(new BorderLayout(100, 0));
        bodySupplierPanel.add(new JLabel("Поставщик кузовов:"), BorderLayout.WEST);
        bodySupplierSlider = createSlider(100, 3000);
        bodySupplierPanel.add(bodySupplierSlider, BorderLayout.CENTER);
        slidersPanel.add(bodySupplierPanel);

        // Ползунок для скорости поставщика двигателей
        JPanel motorSupplierPanel = new JPanel(new BorderLayout(74, 0));
        motorSupplierPanel.add(new JLabel("Поставщик двигателей:"), BorderLayout.WEST);
        motorSupplierSlider = createSlider(100, 3000);
        motorSupplierPanel.add(motorSupplierSlider, BorderLayout.CENTER);
        slidersPanel.add(motorSupplierPanel);

        // Ползунок для скорости поставщика аксессуаров
        JPanel accessorySupplierPanel = new JPanel(new BorderLayout(71, 0));
        accessorySupplierPanel.add(new JLabel("Поставщик аксессуаров:"), BorderLayout.WEST);
        accessorySupplierSlider = createSlider(100, 3000);
        accessorySupplierPanel.add(accessorySupplierSlider, BorderLayout.CENTER);
        slidersPanel.add(accessorySupplierPanel);

        // Ползунок для скорости дилеров
        JPanel dealerPanel = new JPanel(new BorderLayout(166, 0));
        dealerPanel.add(new JLabel("Дилеры:"), BorderLayout.WEST);
        dealerSlider = createSlider(100, 3000);
        dealerPanel.add(dealerSlider, BorderLayout.CENTER);
        slidersPanel.add(dealerPanel);

        // Ползунок для скорости сборщиков
        JPanel workerPanel = new JPanel(new BorderLayout(149, 0));
        workerPanel.add(new JLabel("Сборщики:"), BorderLayout.WEST);
        workerSlider = createSlider(100, 3000);
        workerPanel.add(workerSlider, BorderLayout.CENTER);
        slidersPanel.add(workerPanel);

        add(slidersPanel, BorderLayout.CENTER);

        // Кнопка выхода внизу справа
        JButton exitButton = new JButton("Выход");
        exitButton.addActionListener(e -> {
            stopFactory();
            System.exit(0);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(exitButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Настройка обработчиков событий для ползунков
        bodySupplierSlider.addChangeListener(e -> {
            if (!bodySupplierSlider.getValueIsAdjusting()) {
                controller.setBodySupplierDelay(bodySupplierSlider.getValue());
            }
        });

        motorSupplierSlider.addChangeListener(e -> {
            if (!motorSupplierSlider.getValueIsAdjusting()) {
                controller.setMotorSupplierDelay(motorSupplierSlider.getValue());
            }
        });

        accessorySupplierSlider.addChangeListener(e -> {
            if (!accessorySupplierSlider.getValueIsAdjusting()) {
                controller.setAccessorySupplierDelay(accessorySupplierSlider.getValue());
            }
        });

        dealerSlider.addChangeListener(e -> {
            if (!dealerSlider.getValueIsAdjusting()) {
                controller.setDealerDelay(dealerSlider.getValue());
            }
        });

        workerSlider.addChangeListener(e -> {
            if (!workerSlider.getValueIsAdjusting()) {
                controller.setWorkerDelay(workerSlider.getValue());
            }
        });
    }

    private JSlider createSlider(int min, int max) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, max / 2);
        slider.setMajorTickSpacing(max / 5);
        slider.setMinorTickSpacing(max / 10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }

    private void updateInfo() {
        // Обновление информации о складах
        bodyStorageLabel.setText("Склад кузовов: " + controller.getBodyStorage().getSize() + " / " + controller.getBodyStorage().getCapacity());
        motorStorageLabel.setText("Склад двигателей: " + controller.getMotorStorage().getSize() + " / " + controller.getMotorStorage().getCapacity());
        accessoryStorageLabel.setText("Склад аксессуаров: " + controller.getAccessoryStorage().getSize() + " / " + controller.getAccessoryStorage().getCapacity());
        autoStorageLabel.setText("Склад автомобилей: " + controller.getAutoStorage().getSize() + " / " + controller.getAutoStorage().getCapacity());

        // Обновление статистики
        bodyProducedLabel.setText("Произведено кузовов: " + controller.getBodyStorage().getProduced());
        motorProducedLabel.setText("Произведено двигателей: " + controller.getMotorStorage().getProduced());
        accessoryProducedLabel.setText("Произведено аксессуаров: " + controller.getAccessoryStorage().getProduced());
        autoProducedLabel.setText("Собрано автомобилей: " + controller.getAutoStorage().getProduced());
        autoSoldLabel.setText("Продано автомобилей: " + controller.getAutoStorage().getSold());
        taskQueueLabel.setText("Задач в очереди: " + controller.getThreadPool().getQueueSize());
    }

    private void stopFactory() {
        updateTimer.stop();
        controller.shutdown();
    }
}