package com.lucky.androidlearndemo.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.lucky.androidlearndemo.Constants;

public class DialogTools {

    public static void ShowInputDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请输入请求地址");    //设置对话框标题
        final EditText edit = new EditText(context);
        edit.setText(Constants.BASE_URL);
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(edit.getText().toString())) {
                    return;
                }
                Constants.BASE_URL = edit.getText().toString();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(false);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }
}
