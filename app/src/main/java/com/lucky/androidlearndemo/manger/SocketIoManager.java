package com.lucky.androidlearndemo.manger;

import com.lucky.androidlearndemo.Constants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIoManager {

    private static final SocketIoManager socketManager = new SocketIoManager();

    private SocketIoManager() {

    }

    private Socket mSocket;

    public static SocketIoManager getInstance() {
        return socketManager;
    }

    public boolean isConnect() {
        return mSocket != null && mSocket.connected();
    }

    public void connect() {
        if (isConnect()) {
            return;
        }
        try {
            mSocket = IO.socket(Constants.SOCKET_IO_URL);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param event   相当于房间号
     * @param message 消息
     */
    public void send(String event, String message) {
        if (isConnect()) {
            mSocket.emit(event, message);
        }
    }

    public void receiver(String event, Emitter.Listener listener) {
        if (isConnect()) {
            mSocket.on(event, listener);
        }
    }

}
