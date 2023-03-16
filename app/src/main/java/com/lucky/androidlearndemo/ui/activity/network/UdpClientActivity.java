package com.lucky.androidlearndemo.ui.activity.network;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lucky.androidlearndemo.Constants;
import com.lucky.androidlearndemo.R;
import com.lucky.androidlearndemo.util.ToastUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *   UDP 协议能发但是收不到消息
 *   目前在安卓上面有这个现象，目前仍然没有解决。
 */
public class UdpClientActivity extends AppCompatActivity {

    private EditText etIpPort;
    private TextView tvShowText;

    private DatagramSocket sendSocket = null;
    private DatagramSocket receiveSocket = null;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_client);

        tvShowText = findViewById(R.id.tv_udp_content);
        tvShowText.setMovementMethod(ScrollingMovementMethod.getInstance());
        etIpPort = findViewById(R.id.et_ip_port);
        etIpPort.setText(Constants.BASE_URL);

        findViewById(R.id.btn_udp_send).setOnClickListener(view -> {
            String ipPort = etIpPort.getText().toString();
            String message = ((EditText) findViewById(R.id.et_message)).getText().toString();
            if (ipPort.isEmpty()) {
                ToastUtil.showToast("当前地址不能为空");
                return;
            }
            if (message.isEmpty()) {
                ToastUtil.showToast("发送消息内容不能为空");
                return;
            }
            executorService.execute(() -> {
                sendMessage(message, ipPort);
            });
        });

        try {
            receiveSocket = new DatagramSocket(8888);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        executorService.execute(this::receiveMessage);
    }

    private void sendMessage(String message, String ipPort) {

        byte[] data = message.getBytes();
        DatagramPacket dpSend = null;
        try {
            dpSend = new DatagramPacket(data, data.length, InetAddress.getByName(ipPort.split(":")[0]), Integer.parseInt(ipPort.split(":")[1]));
            double start = System.currentTimeMillis();
            sendSocket = new DatagramSocket();
            sendSocket.send(dpSend);
            sendSocket.close();
            //double end = System.currentTimeMillis();
            //double times = end - start;

//            byte[] receiveData = new byte[1024];
//            DatagramPacket dpReceive = new DatagramPacket(receiveData, receiveData.length);
//            try {
//                receiveSocket.receive(dpReceive);
//                System.out.println(new String(receiveData));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isRunning = true;

    private void receiveMessage() {

        while (true) {
            if (isRunning) {
                byte[] receiveData = new byte[1024];
                DatagramPacket dpReceive = null;
                // ipList.clear();
                dpReceive = new DatagramPacket(receiveData, receiveData.length);
                try {
                    receiveSocket.receive(dpReceive);
                    System.out.println(new String(receiveData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String recIp = dpReceive.getAddress().toString().substring(1);
                if (dpReceive != null) {

                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
