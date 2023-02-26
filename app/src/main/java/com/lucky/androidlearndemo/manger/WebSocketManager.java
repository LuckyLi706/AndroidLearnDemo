package com.lucky.androidlearndemo.manger;

import com.lucky.androidlearndemo.Constants;
import com.lucky.androidlearndemo.listener.WebSocketEventListener;
import com.lucky.androidlearndemo.util.ToastUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

//参考 https://github.com/fomin-zhu/websocket
public class WebSocketManager {

    private static final WebSocketManager webSocketManager = new WebSocketManager();

    private WebSocketManager() {

    }

    private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> ToastUtil.showToast("WebSocket请求日志:" + message)).setLevel(HttpLoggingInterceptor.Level.BODY);

    public static WebSocketManager getInstance() {
        return webSocketManager;
    }

    private OkHttpClient client;
    private Request request;
    private WebSocketEventListener webSocketEventListener;
    private WebSocket mWebSocket;

    private boolean isConnect = false;

    public void init(WebSocketEventListener message) {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = Objects.requireNonNull(new OkHttpClient.Builder()
                        .writeTimeout(30, TimeUnit.SECONDS))
                //设置ping帧发送间隔，Websocket保活。默认Websocket会主动去ping，默认20秒一次。
                .pingInterval(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
        request = new Request.Builder().url(Constants.WEB_SOCKET_URL + Constants.BASE_URL).build();
        webSocketEventListener = message;
        connect();
    }

    /**
     * 连接
     */
    public void connect() {
        if (isConnect()) {
            ToastUtil.showToast("web socket connected");
            return;
        }
        client.newWebSocket(request, createListener());
    }

    /**
     * 是否连接
     */
    public boolean isConnect() {
        return mWebSocket != null && isConnect;
    }

    /**
     * 发送消息
     *
     * @param text 字符串
     * @return boolean
     */
    public boolean sendMessage(String text) {
        if (!isConnect()) return false;
        return mWebSocket.send(text);
    }

    /**
     * 发送消息
     *
     * @param byteString 字符集
     * @return boolean
     */
    public boolean sendMessage(ByteString byteString) {
        if (!isConnect()) return false;
        return mWebSocket.send(byteString);
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (isConnect()) {
            mWebSocket.cancel();
            mWebSocket.close(1001, "客户端主动关闭连接");
        }
    }

    private WebSocketListener createListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                ToastUtil.showToast("open:" + response);
                mWebSocket = webSocket;
                isConnect = response.code() == 101;
                if (!isConnect) {
                    //reconnect();
                } else {
                    ToastUtil.showToast("connect success.");
                    if (webSocketEventListener != null) {
                        webSocketEventListener.onConnectSuccess();
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if (webSocketEventListener != null) {
                    webSocketEventListener.onMessage(text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (webSocketEventListener != null) {
                    webSocketEventListener.onMessage(bytes.base64());
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (webSocketEventListener != null) {
                    webSocketEventListener.onClose();
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (webSocketEventListener != null) {
                    webSocketEventListener.onClose();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                if (response != null) {
                    ToastUtil.showToast("connect failed：" + response.message());
                }
                ToastUtil.showToast("connect failed throwable：" + t.getMessage());
                isConnect = false;
                if (webSocketEventListener != null) {
                    webSocketEventListener.onConnectFailed();
                }
            }
        };
    }
}
