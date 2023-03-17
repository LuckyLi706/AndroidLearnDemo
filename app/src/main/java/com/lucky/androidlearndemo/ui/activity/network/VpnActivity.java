package com.lucky.androidlearndemo.ui.activity.network;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import androidx.annotation.Nullable;

import com.lucky.androidlearndemo.R;
import com.lucky.androidlearndemo.base.BaseActivity;
import com.lucky.androidlearndemo.ui.service.NewVpnService;

public class VpnActivity extends BaseActivity {

    public interface Prefs {
        String NAME = "connection";
        String SERVER_ADDRESS = "server.address";
        String SERVER_PORT = "server.port";
        String SHARED_SECRET = "shared.secret";
        String PROXY_HOSTNAME = "proxyhost";
        String PROXY_PORT = "proxyport";
        String ALLOW = "allow";
        String PACKAGES = "packages";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn);
        NewVpnService.prepare(this);

        findViewById(R.id.btn_connect_vpn).setOnClickListener((v)->{
            VpnService.Builder builder = new VpnService().new Builder();

            // Create a local TUN interface using predetermined addresses. In your app,
            // you typically use values returned from the VPN gateway during handshaking.
//            ParcelFileDescriptor localTunnel = builder
//                    //添加至少一个 IPv4 或 IPv6 地址以及系统指定为本地 TUN 接口地址的子网掩码。您的应用通常会在握手过程中收到来自 VPN 网关的 IP 地址和子网掩码。
//                    .addAddress("192.168.2.2", 24)
//                    //如果您希望系统通过 VPN 接口发送流量，请至少添加一个路由。路由按目标地址过滤。要接受所有流量，请设置开放路由，例如 0.0.0.0/0 或 ::/0。
//                    .addRoute("0.0.0.0", 0)
//                    .addDnsServer("192.168.1.1")
//                    .establish();

            Intent intent = VpnService.prepare(VpnActivity.this);
            if (intent != null) {
                startActivityForResult(intent, 0);
            } else {
                onActivityResult(0, RESULT_OK, null);
            }
        });
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (result == RESULT_OK) {
            startService(getServiceIntent().setAction(NewVpnService.ACTION_CONNECT));
        }
    }

    private Intent getServiceIntent() {
        return new Intent(this, NewVpnService.class);
    }
}
