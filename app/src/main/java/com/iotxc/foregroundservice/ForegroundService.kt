package com.iotxc.foregroundservice

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat


class ForegroundService : Service() {
    
    companion object{
        /**
         * 标记服务是否启动
         */
        var serviceIsLive = false

        /**
         * 唯一前台通知ID
         */
        const val NOTIFICATION_ID = 1000
    }

    override fun onCreate() {
        super.onCreate()
        // 获取服务通知
        val notification: Notification = createForegroundNotification()

        //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 标记服务启动
        serviceIsLive = true

        // 数据获取
        val data = intent!!.getStringExtra("Foreground")
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        // 标记服务关闭
        ForegroundService.serviceIsLive = false
        // 移除通知
        stopForeground(true)
        super.onDestroy()
    }

    /**
     * 创建服务通知
     */
    private fun createForegroundNotification(): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 唯一的通知通道的id.
        val notificationChannelId = "notification_channel_id_01"

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            val channelName = "Foreground Service Notification"
            //通道的重要程度
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(notificationChannelId, channelName, importance)
            notificationChannel.description = "Channel description"
            //LED灯
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            //震动
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        val builder =
            NotificationCompat.Builder(this, notificationChannelId)
        //通知小图标
        builder.setSmallIcon(R.mipmap.ic_launcher)
        //通知标题
        builder.setContentTitle("治电安全保护")
        //通知内容
        builder.setContentText("安全监听中......")
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis())
        //设定启动的内容
        val activityIntent = Intent(this, NotificationActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        //创建通知并返回
        return builder.build()
    }
}