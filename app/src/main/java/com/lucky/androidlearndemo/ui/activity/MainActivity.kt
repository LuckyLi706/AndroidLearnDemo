package com.lucky.androidlearndemo.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.lucky.androidlearndemo.R
import com.lucky.androidlearndemo.base.BaseActivity
import com.lucky.androidlearndemo.ui.activity.network.NetworkActivity
import com.uniquext.android.lightpermission.LightPermission
import com.uniquext.android.lightpermission.request.PermissionCallback

class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.buttonNetwork).setOnClickListener((View.OnClickListener {
            startActivity(Intent(this, NetworkActivity::class.java))
        }))

        //读写权限
        LightPermission
            .with(this)
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .request(object : PermissionCallback() {
                override fun onGranted() {
                    //  Camera and storage permissions have been acquired
                }

                override fun onDenied(permissions: Array<String?>?) {
                    //  Permission set rejected on request
                }

                override fun onProhibited(permissions: Array<String?>?) {
                    //  The permission set is set to "Do not ask again after prohibition"

                }
            })
    }
}