package io.github.skydynamic.maimaidx_android.util

import android.app.Notification
import android.content.Context
import io.github.skydynamic.maimaidx_android.App
import io.github.skydynamic.maiprober_android.R

private var notificationId = 0

fun Context.sendNotification(titleId: Int, messageId: Int) {
    App.notification.notify(
        notificationId++,
        Notification.Builder(this, App.CHANNEL_STATUS_ID)
            .setContentTitle(getText(titleId))
            .setContentText(getText(messageId))
            .setChannelId(App.CHANNEL_STATUS_ID)
            .build()
    )
}

fun Context.sendNotification(titleId: Int, message: String) {
    App.notification.notify(
        notificationId++,
        Notification.Builder(this, App.CHANNEL_STATUS_ID)
            .setContentTitle(getText(titleId))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(message)
            .setChannelId(App.CHANNEL_STATUS_ID)
            .build()
    )
}