package com.flekssina.chat.common.xml;

/**
 * Событие выхода пользователя.
 */
public class XMLUserLogoutEvent implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String username;

    public XMLUserLogoutEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toXML() {
        return "<event name=\"userlogout\">" +
                "<name>" + username + "</name>" +
                "</event>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.USER_LOGOUT;
    }
}