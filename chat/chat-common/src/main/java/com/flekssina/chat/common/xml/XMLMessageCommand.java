package com.flekssina.chat.common.xml;

/**
 * Команда отправки сообщения.
 */
public class XMLMessageCommand implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String content;
    private final String sessionId;

    public XMLMessageCommand(String content, String sessionId) {
        this.content = content;
        this.sessionId = sessionId;
    }

    public String getContent() {
        return content;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toXML() {
        return "<command name=\"message\">" +
                "<message>" + escapeXml(content) + "</message>" +
                "<session>" + sessionId + "</session>" +
                "</command>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.MESSAGE;
    }

    private String escapeXml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}