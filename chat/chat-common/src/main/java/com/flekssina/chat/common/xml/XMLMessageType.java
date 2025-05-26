package com.flekssina.chat.common.xml;

/**
 * Перечисление типов XML-сообщений в чате.
 */
public enum XMLMessageType {
    // Команды от клиента
    LOGIN,          // Вход в чат
    LOGOUT,         // Выход из чата
    LIST,           // Запрос списка пользователей
    MESSAGE,        // Отправка сообщения

    // Ответы сервера
    SUCCESS,        // Успешное выполнение команды
    ERROR,          // Ошибка

    // События от сервера
    USER_LOGIN,     // Вход пользователя
    USER_LOGOUT,    // Выход пользователя
    MESSAGE_EVENT   // Сообщение в чат
}