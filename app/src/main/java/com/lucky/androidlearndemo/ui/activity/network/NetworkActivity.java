package com.lucky.androidlearndemo.ui.activity.network;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.lucky.androidlearndemo.Constants;
import com.lucky.androidlearndemo.GlideEngine;
import com.lucky.androidlearndemo.R;
import com.lucky.androidlearndemo.base.BaseActivity;
import com.lucky.androidlearndemo.listener.WebSocketEventListener;
import com.lucky.androidlearndemo.manger.WebSocketManager;
import com.lucky.androidlearndemo.ui.view.DialogTools;
import com.lucky.androidlearndemo.util.FileUtil;
import com.lucky.androidlearndemo.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkActivity extends BaseActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        OkHttpClient okHttpClient = getOKHttpClient();

        //GET请求
        findViewById(R.id.get_request).setOnClickListener(view -> {
            sendGetRequest(okHttpClient);
        });

        //POST请求
        findViewById(R.id.post_request).setOnClickListener(view -> {
            sendPostRequest(okHttpClient);
        });

        //单文件上传
        findViewById(R.id.upload_file_request).setOnClickListener(view -> {

            PictureSelector.create(this).openGallery(SelectMimeType.ofImage()).setImageEngine(GlideEngine.createGlideEngine()).forResult(new OnResultCallbackListener<LocalMedia>() {
                @Override
                public void onResult(ArrayList<LocalMedia> result) {
                    for (LocalMedia localMedia : result) {
                        uploadFile(okHttpClient, new File(localMedia.getPath()));
                    }
                }

                @Override
                public void onCancel() {

                }
            });

        });

        //多文件上传
        findViewById(R.id.upload_files_request).setOnClickListener(view -> {
            PictureSelector.create(this).openGallery(SelectMimeType.ofAll()).setImageEngine(GlideEngine.createGlideEngine()).forResult(new OnResultCallbackListener<LocalMedia>() {
                @Override
                public void onResult(ArrayList<LocalMedia> result) {
                    uploadFiles(okHttpClient, result);
                }

                @Override
                public void onCancel() {

                }
            });

        });

        //启动websocket
        findViewById(R.id.web_socket_start).setOnClickListener(view -> {
            if (WebSocketManager.getInstance().isConnect()) {
                ToastUtil.showToast("WebSocket已经启动");
                return;
            }
            WebSocketManager.getInstance().init(new WebSocketListenerImpl());
        });

        //关闭websocket
        findViewById(R.id.web_socket_close).setOnClickListener(view -> {
            if (!WebSocketManager.getInstance().isConnect()) {
                ToastUtil.showToast("请先启动WebSocket");
                return;
            }
            WebSocketManager.getInstance().close();
        });

        //websocket发送消息
        findViewById(R.id.web_socket_send).setOnClickListener(view -> {
            if (!WebSocketManager.getInstance().isConnect()) {
                ToastUtil.showToast("请先启动WebSocket");
                return;
            }
            WebSocketManager.getInstance().sendMessage("Hello,WebSocket");
        });

        findViewById(R.id.socket_io).setOnClickListener(view -> {
            startActivity(new Intent(this, SocketIoActivity.class));
        });

        findViewById(R.id.fab).setOnClickListener(view -> {
            DialogTools.ShowInputDialog(this);
        });
    }

    OkHttpClient getOKHttpClient() {
        return new OkHttpClient.Builder().readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .proxy(Proxy.NO_PROXY)
                .build();
    }

    private void sendGetRequest(OkHttpClient okHttpClient) {
        // 构造 Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(Constants.HTTP_URL + Constants.BASE_URL + "/login?user=lijie&pass=123").build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ToastUtil.showToast(response.body().string());
            }
        });
    }

    private void sendPostRequest(OkHttpClient okHttpClient) {
        Request.Builder builder = new Request.Builder();
        //提交表单的方式
        RequestBody requestBodyForm = new FormBody.Builder().add("user", "lijie").add("pass", "123").build();
        //提交json
        String json = "{\"user\":\"lijie\",\"pass\":\"123\"}";
        RequestBody requestBodyJSon = RequestBody.create(MediaType.parse("application/json"), json);
        // 构造 Request
        Request request = builder.post(requestBodyJSon).url(Constants.HTTP_URL + Constants.BASE_URL + "/login").build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ToastUtil.showToast(response.body().string());
            }
        });
    }

    private void uploadFile(OkHttpClient okHttpClient, File file) {
        Request.Builder builder = new Request.Builder();
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse(FileUtil.getMIMEType(file)), file);
        // 构造 Request
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), requestBodyFile).build();
        Request request = builder.post(requestBody).url(Constants.HTTP_URL + Constants.BASE_URL + "/upload").build();

        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ToastUtil.showToast(response.body().string());
            }
        });
    }

    private void uploadFiles(OkHttpClient okHttpClient, ArrayList<LocalMedia> result) {
        Map<String, String> map = new HashMap();
        map.put("key", "value");
        // 构造 Request
        Request.Builder builderRequest = new Request.Builder();

        MediaType mediaType = MediaType.parse("multipart/form-data; charset=utf-8");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("data", "{\"user\":\"lijie\",\"pass\":\"123\"}");
        for (int i = 0; i < result.size(); i++) { //对文件进行遍历
            File file = new File(result.get(i).getPath()); //生成文件
            //根据文件的后缀名，获得文件类型
            builder.addFormDataPart( //给Builder添加上传的文件
                    "file",  //请求的名字
                    file.getName(), //文件的文字，服务器端用来解析的
                    RequestBody.create(mediaType, file) //创建RequestBody，把上传的文件放入
            );
        }
        RequestBody requestBody = builder.build();
        Request request = builderRequest.post(requestBody).url(Constants.HTTP_URL + Constants.BASE_URL + "/upload_files").build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ToastUtil.showToast(response.body().string());
            }
        });
    }

    class WebSocketListenerImpl implements WebSocketEventListener {

        @Override
        public void onConnectSuccess() {
            ToastUtil.showToast("connectSuccess");
        }

        @Override
        public void onMessage(String message) {
            ToastUtil.showToast("收到消息:" + message);
        }

        @Override
        public void onClose() {
            ToastUtil.showToast("onClose");
        }

        @Override
        public void onConnectFailed() {
            ToastUtil.showToast("onConnectFailed");
        }
    }
}
