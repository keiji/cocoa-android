package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_SECRET
import androidx.core.app.NotificationManagerCompat

interface LocalNotificationManager {
    fun prepareNotificationChannels()
    fun notifyDetectExposureHighRisk()
    fun notifyWeeklyReport()
    fun notifyWatchdog()
}

class LocalNotificationManagerImpl(
    private val applicationContext: Context,
    @DrawableRes private val smallIcon: Int,
    private val createPendingIntent: () -> PendingIntent,
) : LocalNotificationManager {
    companion object {
        private const val NOTIFICATION_CHANNEL_HIGH_RISK_EXPOSURE_DETECTED_ID =
            "high_risk_exposure_detected"

        private const val NOTIFICATION_CHANNEL_WEEKLY_REPORT_ID =
            "weekly_report"

        private const val NOTIFICATION_CHANNEL_WATCHDOG_ID =
            "watchdog"

        private const val NOTIFICATION_HIGH_RISK_ID = 0x1211
    }

    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    override fun prepareNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        // High-risk exposure detected
        val channelHighRisk = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_HIGH_RISK_EXPOSURE_DETECTED_ID,
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .setName("High-risk exposure detected")
            .setDescription("")
            .setShowBadge(false)
            .build()
        notificationManager.createNotificationChannel(channelHighRisk)

        // Weekly report
        val channelWeekly = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_WEEKLY_REPORT_ID,
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .setName("Privacy guard")
            .setDescription("Show notifications randomly to protect your privacy.")
            .setShowBadge(false)
            .build()
        notificationManager.createNotificationChannel(channelWeekly)

        // Watchdog
        val channelWatchdog = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_WATCHDOG_ID,
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .setName("Watchdog")
            // .setDescription("") TODO
            .setShowBadge(false)
            .build()
        notificationManager.createNotificationChannel(channelWatchdog)
    }

    override fun notifyDetectExposureHighRisk() {
        val notification = NotificationCompat.Builder(
            applicationContext,
            NOTIFICATION_CHANNEL_HIGH_RISK_EXPOSURE_DETECTED_ID
        )
            .setStyle(NotificationCompat.BigTextStyle())
            .setSmallIcon(smallIcon)
            .setContentTitle("レポートが作成されました") // "陽性登録者との接触の可能性があります"
            .setContentText("この通知は1週間に一度程度の間隔で表示されます。レポートを確認してください")
            .setVisibility(VISIBILITY_SECRET)
            .setContentIntent(createPendingIntent())
            .setLocalOnly(true)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_HIGH_RISK_ID, notification)
    }

    override fun notifyWeeklyReport() {
    }

    override fun notifyWatchdog() {
    }
}
