package co.ryzer.ancla

import android.app.Application
import co.ryzer.ancla.notifications.NotificationHelper
import co.ryzer.ancla.work.DailyResetScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnclaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        DailyResetScheduler.schedule(this)
    }
}
