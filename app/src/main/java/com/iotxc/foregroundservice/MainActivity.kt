package com.iotxc.foregroundservice

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException


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
                        Latitude_text.text = ""
                        Longitude_text.text = ""
                        LocationUtils.getLocation(this@MainActivity, object : com.iotxc.foregroundservice.Callback {
                         override fun onLocationChanged(location: Location) {
                                Latitude_text.text = location.latitude.toString()
                                Longitude_text.text = location.longitude.toString()
                                Log.e("LOCATION", location.longitude.toString() + "," + location.longitude.toString())
                                val locationData = HashMap<String, String>()
                                locationData["latitude"] = location.latitude.toString()
                                locationData["longitude"] = location.longitude.toString()
                                postData(locationData)
                            }
                        })
                    } else {
                        Log.e("LOCATION","本应用需要获取地理位置，请打开获取位置的开关")
                    }
                }
            }
        }
    }

    private fun postData(data: HashMap<String, String>) {
        var client = OkHttpClient()
        var request= OKHttpRequest(client)
        request.POST("http://192.168.43.71:7001/api/v2/location", data, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("post", response.toString())
            }
        })
    }
}