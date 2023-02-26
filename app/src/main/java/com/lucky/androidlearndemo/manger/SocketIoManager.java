package com.lucky.androidlearndemo.manger;

import com.lucky.androidlearndemo.Constants;
import com.lucky.androidlearndemo.util.ToastUtil;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.On;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIoManager {

    private static final String ROOM_ID = "456";

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
            mSocket = IO.socket(Constants.SOCKET_IO_URL + Constants.BASE_URL);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param event 事件 和后端的保持一致
     * @param args  消息
     */
    public void send(String event, String message) {
        if (isConnect()) {
            mSocket.emit(event, mSocket.id(), ROOM_ID, message);
        } else
            ToastUtil.showToast("当前未连接");
    }

    /**
     * 接收消息
     *
     * @param event    事件和后端发送的保持一致
     * @param listener 回调事件
     */
    public void receiver(String event, Emitter.Listener listener) {
        if (isConnect()) {
            mSocket.on(event, listener);
        } else
            ToastUtil.showToast("当前未连接");
    }

    public void disconnect() {
        if (isConnect()) {
            leaveRoom(ROOM_ID);
            mSocket.off();
            mSocket.disconnect();
        } else
            ToastUtil.showToast("当前未连接");
    }

    //连接上触发的事件。socketio的内置事件
    public void connectStatusEvent(String event, Emitter.Listener listener) {
        //内置事件，连接成功回调
        On.on(mSocket, Socket.EVENT_CONNECT, new Emitter.Listener() {
            public void call(Object... args) {
                ToastUtil.showToast(Socket.EVENT_CONNECT);
                joinRoom(ROOM_ID);
                normalMessage();
                receiver(event, listener);
            }
        });
        //内置事件，连接失败回调
        On.on(mSocket, Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            public void call(Object... args) {
                ToastUtil.showToast(Socket.EVENT_DISCONNECT);
                leaveRoom(ROOM_ID);
            }
        });
        //内置事件，连接失败回调
        On.on(mSocket, Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            public void call(Object... args) {
                ToastUtil.showToast(Socket.EVENT_CONNECT_ERROR);
            }
        });
    }

    //接收服务端的send方法发送的事件
    public void normalMessage() {
        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ToastUtil.showToast(args[0].toString());
            }
        });
    }

    //加入房间
    //监听别人加入
    public void joinRoom(String roomId) {
        mSocket.emit("join", mSocket.id(), roomId);
        mSocket.on("join", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ToastUtil.showToast(args[0].toString());
            }
        });
    }

    //离开房间
    //监听别人离开
    public void leaveRoom(String roomId) {
        mSocket.emit("leave", mSocket.id(), roomId);
        mSocket.on("leave", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ToastUtil.showToast(args[0].toString());
            }
        });
    }
}
