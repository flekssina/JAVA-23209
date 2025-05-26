package com.flekssina.chat.common.xml;

/**
 * Ответ об ошибке.
 */
public class XMLErrorResponse implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public XMLErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toXML() {
        return "<error>" +
                "<message>" + escapeXml(errorMessage) + "</message>" +
                "</error>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.ERROR;
    }

    private String escapeXml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}