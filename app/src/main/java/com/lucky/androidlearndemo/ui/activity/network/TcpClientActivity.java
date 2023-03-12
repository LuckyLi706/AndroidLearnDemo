package com.lucky.androidlearndemo.ui.activity.network;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lucky.androidlearndemo.Constants;
import com.lucky.androidlearndemo.R;
import com.lucky.androidlearndemo.util.ToastUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClientActivity extends AppCompatActivity {
    private Socket mSocket;
    private BufferedReader mReader;   //按字符流读取
    private PrintWriter mWriter;

    private boolean isReceive = true;  //读取数据的标志
    private InputStream inputStreamReader;  //按字节流读取
    private TextView tvShowText;
    private EditText etIpPort;

    private CheckBox checkBoxByte;
    private CheckBox checkBoxLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_client);

        tvShowText = findViewById(R.id.tv_tcp_content);
        tvShowText.setMovementMethod(ScrollingMovementMethod.getInstance());
        etIpPort = findViewById(R.id.et_ip_port);
        etIpPort.setText(Constants.BASE_URL);

        checkBoxByte = findViewById(R.id.cb_byte);
        checkBoxLine = findViewById(R.id.cb_line);

        checkBoxByte.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxLine.setChecked(false);
            }
        });

        checkBoxLine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxByte.setChecked(false);
            }
        });


        findViewById(R.id.btn_tcp_connect).setOnClickListener(view -> {
            String ip_port = etIpPort.getText().toString();
            if (Constants.BASE_URL.isEmpty()) {
                ToastUtil.showToast("地址为空");
                return;
            }
            if (!Constants.BASE_URL.contains(":")) {
                ToastUtil.showToast("地址不合法");
                return;
            }
            new Thread(() -> {
                connect(ip_port.split(":")[0], Integer.parseInt(ip_port.split(":")[1]));
            }).start();
        });

        findViewById(R.id.btn_tcp_close).setOnClickListener(view -> {
            new Thread(this::close).start();
        });

        findViewById(R.id.btn_tcp_send).setOnClickListener(view -> {
            new Thread(() -> {
                if (mSocket != null && mSocket.isConnected() && mWriter != null) {
                    String message = ((EditText) findViewById(R.id.et_send_message)).getText().toString();
                    if (TextUtils.isEmpty(message)) {
                        ToastUtil.showToast("发送消息不能为空");
                        return;
                    }
                    mWriter.println(message);
                    mWriter.flush();
                    runOnUiThread(() -> {
                        tvShowText.append("发送消息：" + message + "\n");
                    });
                }
            }).start();
        });
    }

    public void connect(String mServerHost, int mServerPort) {
        try {
            // 创建Socket对象并连接服务器
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(mServerHost, mServerPort), 5000); // 连接超时时间为5秒
            isReceive = true;
            ToastUtil.showToast("连接成功");
            // 获取输入输出流
            mWriter = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()));
            new TCPReceiveThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //udp接收线程
    public class TCPReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                inputStreamReader = mSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (isReceive) {
                try {
                    if (checkBoxByte.isChecked()) {
                        int available = inputStreamReader.available();
                        if (available > 0) {
                            byte[] buffer = new byte[available];
                            inputStreamReader.read(buffer);
                            String message = new String(buffer);
                            runOnUiThread(() -> {
                                tvShowText.append("接收消息：" + message + "\n");
                            });
                        }
                    } else {
                        /*
                         使用readLine()注意事项：
                         1. 读入的数据要注意有/r或/n或/r/n，这句话意思是服务端写完数据后，会打印报文结束符/r或/n或/r/n；
                         2. 同理，客户端写数据时也要打印报文结束符，这样服务端才能读取到数据。
                         3. 没有数据时会阻塞，在数据流异常或断开时才会返回null
                         4. 使用socket之类的数据流时，要避免使用readLine()，以免为了等待一个换行/回车符而一直阻塞
                         */
                        String message = mReader.readLine();
                        runOnUiThread(() -> {
                            tvShowText.append("接收消息：" + message + "\n");
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
        // 关闭输入输出流和Socket连接
        if (mSocket != null && mSocket.isConnected()) {
            try {
                isReceive = false;
                if (mReader != null) {
                    mReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                mWriter.close();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isReceive = false;
        if (mSocket != null && mSocket.isConnected()) {
            try {
                if (mReader != null) {
                    mReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                mWriter.close();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
