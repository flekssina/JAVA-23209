package com.flekssina.chat.common.xml;

import java.io.Serializable;

/**
 * Интерфейс для XML-сообщений, определяющий общие методы.
 */
public interface XMLMessage extends Serializable {
    /**
     * Преобразует сообщение в XML-строку.
     * @return XML-строка, представляющая сообщение.
     */
    String toXML();

    /**
     * Получает тип XML-сообщения.
     * @return Тип сообщения.
     */
    XMLMessageType getType();
}