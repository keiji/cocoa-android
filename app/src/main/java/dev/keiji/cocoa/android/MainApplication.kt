package dev.keiji.cocoa.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import dev.keiji.cocoa.android.work.ExposureDetectionWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    companion object {
        private const val WORKER_NAME: String = "COCOA_WORKER"
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(
            WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            createWorkRequest()
        )
    }

    private fun createWorkRequest() = PeriodicWorkRequest
        .Builder(
            ExposureDetectionWorker::class.java,
            BuildConfig.EXPOSURE_DETECTION_WORKER_INTERVAL_IN_MINUTES,
            TimeUnit.MINUTES
        )
        .setConstraints(Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }.build())
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            BuildConfig.EXPOSURE_DETECTION_WORKER_BACKOFF_DELAY_IN_MINUTES,
            TimeUnit.MINUTES
        )
        .build()
}
