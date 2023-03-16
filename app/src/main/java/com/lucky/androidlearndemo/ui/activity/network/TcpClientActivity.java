package com.lucky.androidlearndemo.ui.activity.network;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.CheckBox;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    //线程池
    private ExecutorService executorSocket = Executors.newCachedThreadPool();

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

        //清除界面的数据
        findViewById(R.id.btn_tcp_clear).setOnClickListener((v) -> {
            tvShowText.setText("");
        });

        findViewById(R.id.btn_tcp_connect).setOnClickListener(view -> {
            String ip_port = etIpPort.getText().toString();
            if (ip_port.isEmpty()) {
                ToastUtil.showToast("地址为空");
                return;
            }
            if (!ip_port.contains(":")) {
                ToastUtil.showToast("地址不合法");
                return;
            }
            executorSocket.execute(() -> {
                connect(ip_port.split(":")[0], Integer.parseInt(ip_port.split(":")[1]));
            });
        });

        findViewById(R.id.btn_tcp_close).setOnClickListener(view -> {
            if (mSocket != null && mSocket.isConnected() && !mSocket.isClosed()) {
                ToastUtil.showToast("客户端socket断开");
            }
            executorSocket.execute(this::close);
        });

        findViewById(R.id.btn_tcp_send).setOnClickListener(view -> {
            if (isConnect() && mWriter != null) {
                String message = ((EditText) findViewById(R.id.et_send_message)).getText().toString();
                if (TextUtils.isEmpty(message)) {
                    ToastUtil.showToast("发送消息不能为空");
                    return;
                }
                executorSocket.execute(() -> {
                    sendMessage(message);
                });
            } else {
                ToastUtil.showToast("请先建立连接");
            }
        });
    }

    //连接
    public void connect(String mServerHost, int mServerPort) {
        try {
            if (isConnect()) {
                ToastUtil.showToast("已经建立连接,不要重复连接");
                return;
            }
            // 创建Socket对象并连接服务器
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(mServerHost, mServerPort), 5000); // 连接超时时间为5秒
            isReceive = true;
            ToastUtil.showToast("连接成功");
            // 获取输入输出流
            mWriter = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()));
            executorSocket.execute(this::receiveMessage);   //开启接收消息线程
            executorSocket.execute(this::sendHeart);   //开启心跳线程
        } catch (Exception e) {
            ToastUtil.showToast("连接异常," + e.getMessage());
        }
    }

    //socket发送消息
    private void sendMessage(String message) {
        mWriter.print(message);
        mWriter.flush();
        runOnUiThread(() -> {
            tvShowText.append("发送消息：" + message + "\n");
        });
    }

    //socket接收消息
    private void receiveMessage() {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //socket的心跳包，每3秒发起心跳，根据心跳来检测当前是否连接
    private void sendHeart() {
        while (isReceive) {
            try {
                mSocket.sendUrgentData(0xff);
                Thread.sleep(3000);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    ToastUtil.showToast("服务端socket断开，" + e.getMessage());
                });
                close();
            }
        }
    }

    public void close() {
        // 关闭输入输出流和Socket连接
        if (mSocket != null) {
            try {
                isReceive = false;
                if (mReader != null) {
                    mReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (mWriter != null) {
                    mWriter.close();
                }
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
        executorSocket.shutdownNow();
    }

    /**
     * isConnected方法所判断的并不是Socket对象的当前连接状态，而是Socket对象是否曾经连接成功过，
     * 如果成功连接过，即使现在isClose返回true，isConnected仍然返回true。
     * 因此，要判断当前的Socket对象是否处于连接状态，必须同时使用isClose和isConnected方法，
     * 即只有当isClose返回false，isConnected返回true的时候Socket对象才处于连接状态。下面的代码演示了上述Socket对象的各种状态的产生过程
     */
    public boolean isConnect() {
        return mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
    }
}
