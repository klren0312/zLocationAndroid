package com.iotxc.foregroundservice

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var serviceFlag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnService.setOnClickListener {
            val mForegroundService: Intent
            if (serviceFlag){
                serviceFlag = false
                // 停止服务
                mForegroundService = Intent(this, ForegroundService::class.java)
                stopService(mForegroundService)
                Toast.makeText(this, "监听服务已关闭...", Toast.LENGTH_SHORT).show()
            } else {
                // 启动服务
                if (!ForegroundService.serviceIsLive) {
                    serviceFlag = true
                    // Android 8.0使用startForegroundService在前台启动新服务
                    mForegroundService = Intent(this, ForegroundService::class.java)
                    mForegroundService.putExtra("Foreground", "监听服务已启动....")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(mForegroundService)
                    } else {
                        startService(mForegroundService)
                    }
                } else {
                    Toast.makeText(this, "监听服务正在运行中...", Toast.LENGTH_SHORT).show()
                }
                runWithPermissions(Permission.ACCESS_FINE_LOCATION) {
                    if (LocationUtils.isLocationProviderEnabled(this@MainActivity)) {
                        Log.e("LOCATION", '干'.toString())
                        Latitude_text.text = ""
                        LocationUtils.getLocation(this@MainActivity, object : Callback {
                            override fun onLocationChanged(location: Location) {
                                Latitude_text.text = location.latitude.toString()
                                Longitude_text.text = location.longitude.toString()
                                Log.e("LOCATION", location.longitude.toString() + "," + location.longitude.toString())
                            }
                        })
                    } else {
                        Log.e("LOCATION","本应用需要获取地理位置，请打开获取位置的开关")
                    }
                }
            }
        }
    }
}