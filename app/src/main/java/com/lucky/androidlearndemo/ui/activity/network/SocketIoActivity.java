package com.lucky.androidlearndemo.ui.activity.network;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lucky.androidlearndemo.R;
import com.lucky.androidlearndemo.base.BaseActivity;
import com.lucky.androidlearndemo.manger.SocketIoManager;
import com.lucky.androidlearndemo.model.ChatModel;
import com.lucky.androidlearndemo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketIoActivity extends BaseActivity {

    private static final String EVENT_MESSAGE = "information";
    private RecyclerView recyclerView;
    List<ChatModel> chatModelList = new ArrayList<ChatModel>();
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_io);
        initRecycler();

        final EditText editText = findViewById(R.id.et_chat_message);
        //连接
        findViewById(R.id.btn_connect).setOnClickListener((v) -> {
            if (SocketIoManager.getInstance().isConnect()) {
                ToastUtil.showToast("已经连接");
                return;
            }
            connect();
        });
        //断开连接
        findViewById(R.id.btn_disconnect).setOnClickListener((v) -> {
            SocketIoManager.getInstance().disconnect();
        });
        //发送消息
        findViewById(R.id.btn_send).setOnClickListener((v) -> {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                ToastUtil.showToast("输入框不能为空");
                return;
            }
            SocketIoManager.getInstance().send(EVENT_MESSAGE, editText.getText().toString());
            sendMessage(editText.getText().toString());
        });
    }

    private void connect() {
        SocketIoManager.getInstance().connect();
        SocketIoManager.getInstance().connectStatusEvent(EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                receiveMessage(args[0].toString());
            }
        });
    }

    private void initRecycler() {
        chatModelList.clear();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_chat_content);
        //给recyclerView创建布局方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //创建适配器
        chatAdapter = new ChatAdapter(chatModelList);
        recyclerView.setAdapter(chatAdapter);
    }

    /**
     * 发送信息
     *
     * @param message
     */
    void sendMessage(String message) {
        ChatModel chatModel = new ChatModel(R.mipmap.ic_launcher, "金城武", message, ChatModel.SEND);
        chatModelList.add(chatModel);
        chatAdapter.notifyItemInserted(chatModelList.size() - 1);
        recyclerView.scrollToPosition(chatModelList.size() - 1);
    }

    /**
     * 接收信息
     *
     * @param message
     */
    void receiveMessage(String message) {
        runOnUiThread(() -> {
            ChatModel chatModel = new ChatModel(R.mipmap.ic_launcher, "邱淑贞", message, ChatModel.RECEIVE);
            chatModelList.add(chatModel);
            chatAdapter.notifyItemInserted(chatModelList.size() - 1);
            recyclerView.scrollToPosition(chatModelList.size() - 1);
        });
    }


    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        //存放数据
        List<ChatModel> chatModelList;


        //通过构造函数传入数据
        public ChatAdapter(List<ChatModel> dataList) {
            this.chatModelList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //布局加载器
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyle_view_chat_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * 位置对应的数据与holder进行绑定
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatModel chatModel = chatModelList.get(position);
            if (chatModel.getType() == ChatModel.RECEIVE) {
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightNameTextView.setText(chatModel.getName());
                holder.rightContentTextView.setText(chatModel.getContent());
                holder.rightImageView.setImageResource(chatModel.getImgId());
            } else {
                holder.rightLayout.setVisibility(View.GONE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftNameTextView.setText(chatModel.getName());
                holder.leftContentTextView.setText(chatModel.getContent());
                holder.leftImageView.setImageResource(chatModel.getImgId());
            }

        }

        /**
         * 获取数据长度
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return chatModelList.size();
        }

        /**
         * 缓存页面布局，页面快速滚动时不必每次都重新创建View
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView leftImageView;
            TextView leftNameTextView;
            TextView leftContentTextView;
            LinearLayout leftLayout;

            ImageView rightImageView;
            TextView rightNameTextView;
            TextView rightContentTextView;
            LinearLayout rightLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                leftImageView = (ImageView) itemView.findViewById(R.id.left_image);
                leftContentTextView = (TextView) itemView.findViewById(R.id.left_content);
                leftNameTextView = (TextView) itemView.findViewById(R.id.left_name);
                leftLayout = (LinearLayout) itemView.findViewById(R.id.left_bubble);

                rightImageView = (ImageView) itemView.findViewById(R.id.right_image);
                rightContentTextView = (TextView) itemView.findViewById(R.id.right_content);
                rightNameTextView = (TextView) itemView.findViewById(R.id.right_name);
                rightLayout = (LinearLayout) itemView.findViewById(R.id.right_bubble);

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketIoManager.getInstance().disconnect();
    }
}
