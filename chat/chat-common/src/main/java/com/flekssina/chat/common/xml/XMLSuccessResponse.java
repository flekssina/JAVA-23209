package com.flekssina.chat.common.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Ответ об успешном выполнении команды.
 */
public class XMLSuccessResponse implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private List<UserInfo> users = new ArrayList<>();

    public XMLSuccessResponse() {
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void addUser(String name, String type) {
        users.add(new UserInfo(name, type));
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    @Override
    public String toXML() {
        StringBuilder builder = new StringBuilder("<success>");

        if (sessionId != null) {
            builder.append("<session>").append(sessionId).append("</session>");
        }

        if (!users.isEmpty()) {
            builder.append("<listusers>");

            for (UserInfo user : users) {
                builder.append("<user>")
                        .append("<name>").append(user.name).append("</name>")
                        .append("<type>").append(user.type).append("</type>")
                        .append("</user>");
            }

            builder.append("</listusers>");
        }

        builder.append("</success>");

        return builder.toString();
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.SUCCESS;
    }

    public static class UserInfo {
        private String name;
        private String type;

        public UserInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}