package com.lucky.androidlearndemo.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.lucky.androidlearndemo.App;

/**
 * 子线程和主线程弹出Toast
 */
public class ToastUtil {

//    private static Toast toast;

    private static Handler handler;

    public static void showToast(String message) {

        //如果是主Looper直接发toast
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show();
            //子线程用MainLooper的Handler来更新UI
            //toast(message);
        } else {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
            handler.post(() -> Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show());
        }
    }

//    @SuppressLint("ShowToast")
//    private static void toast(String message) {
//        if (toast == null) {
//            toast = Toast.makeText(App.getContext(),
//                    message,
//                    Toast.LENGTH_SHORT);
//        } else {
//            toast.setText(message);
//        }
//        toast.show();
//    }
}
