package dev.keiji.cocoa.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.R
import dev.keiji.cocoa.android.exposure_notiffication.ExposureNotificationWrapper

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: ExposureNotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            navController.navigate(R.id.homeFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        when (requestCode) {
            ExposureNotificationWrapper.REQUEST_EXPOSURE_NOTIFICATION_START -> {
                viewModel.start(this)
            }
        }
    }
}
