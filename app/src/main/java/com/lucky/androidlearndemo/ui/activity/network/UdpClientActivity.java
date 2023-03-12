package com.lucky.androidlearndemo.ui.activity.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lucky.androidlearndemo.App;
import com.lucky.androidlearndemo.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpClientActivity extends AppCompatActivity {
    /*
     *   Data
     * */
    private final static String SEND_IP = "81.68.122.109";  //发送IP
    private final static int SEND_PORT = 12345;               //发送端口号
    private final static int RECEIVE_PORT = 22320;            //接收端口号

    private boolean listenStatus = true;  //接收线程的循环标识
    private byte[] receiveInfo;     //接收报文信息
    private byte[] buf;

    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress serverAddr;
    private SendHandler sendHandler = new SendHandler();
    private ReceiveHandler receiveHandler = new ReceiveHandler();

    private WifiManager.MulticastLock lock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_client);

        findViewById(R.id.btn_udp_connect).setOnClickListener(view -> {
        });

        findViewById(R.id.btn_udp_close).setOnClickListener(view -> {
            // disconnect();
        });

        findViewById(R.id.btn_udp_send).setOnClickListener(view -> {
            new UdpSendThread().start();
        });

        WifiManager manager = (WifiManager) App.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lock= manager.createMulticastLock("test wifi");

        new UdpReceiveThread().start();
    }


    public class UdpSendThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                buf = "i am an android developer, hello android! ".getBytes();

                // 创建DatagramSocket对象，使用随机端口
                sendSocket = new DatagramSocket();
                serverAddr = InetAddress.getByName(SEND_IP);

                DatagramPacket outPacket = new DatagramPacket(buf, buf.length,serverAddr, SEND_PORT);
                //lock.release();
                sendSocket.send(outPacket);

                sendSocket.close();
                sendHandler.sendEmptyMessage(1);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //udp接收线程
    public class UdpReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                receiveSocket = new DatagramSocket();
                receiveSocket.setSoTimeout(2000);
                serverAddr = InetAddress.getByName(SEND_IP);
                while (listenStatus) {
                    byte[] inBuf = new byte[1024];
                    DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
                    try {
                        //lock.release();
                        receiveSocket.receive(inPacket);
                        receiveInfo = inPacket.getData();
                        receiveHandler.sendEmptyMessage(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // tvMessage.setText("接收到数据了" + receiveInfo.toString());
            Toast.makeText(UdpClientActivity.this, "接收到数据了", Toast.LENGTH_SHORT).show();
        }
    }

    class SendHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //tvMessage.setText("UDP报文发送成功");
            Toast.makeText(UdpClientActivity.this, "成功发送", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenStatus = false;
        receiveSocket.close();
    }
}
