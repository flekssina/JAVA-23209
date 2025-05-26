package com.flekssina.chat.client;

import com.flekssina.chat.common.Message;

public interface ChatClientListener {
    void onLoginSuccess();
    void onError(String errorMessage);
    void onMessageReceived(Message message);
    void onUserListReceived(String userList);
    void onConnectionError(String errorMessage);
}