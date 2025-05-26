package com.flekssina.chat.common;

public enum MessageType {
    // Запросы от клиента
    LOGIN,           // Запрос на логин
    LOGOUT,          // Запрос на выход
    CHAT_MESSAGE,    // Сообщение в чат
    LIST_USERS,      // Запрос списка пользователей

    // Сервисные сообщения
    LOGIN_SUCCESS,   // Успешная авторизация
    ERROR,           // Ошибка
    USER_LIST,       // Список пользователей

    // События
    USER_JOINED,     // Новый пользователь подключился
    USER_LEFT        // Пользователь отключился
}