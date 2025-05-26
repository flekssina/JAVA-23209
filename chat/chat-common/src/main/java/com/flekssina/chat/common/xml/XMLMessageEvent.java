package com.flekssina.chat.common.xml;

/**
 * Событие получения сообщения.
 */
public class XMLMessageEvent implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String content;
    private final String sender;

    public XMLMessageEvent(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toXML() {
        return "<event name=\"message\">" +
                "<message>" + escapeXml(content) + "</message>" +
                "<name>" + sender + "</name>" +
                "</event>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.MESSAGE_EVENT;
    }

    private String escapeXml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}