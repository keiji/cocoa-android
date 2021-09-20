package dev.keiji.cocoa.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.keiji.cocoa.android.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, HomeFragment.newInstance())
                .commit()
        }
    }
}
