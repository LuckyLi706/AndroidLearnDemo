package com.lucky.androidlearndemo.listener;

public interface WebSocketEventListener {

    void onConnectSuccess();

    void onMessage(String message);

    void onClose();

    void onConnectFailed();
}
