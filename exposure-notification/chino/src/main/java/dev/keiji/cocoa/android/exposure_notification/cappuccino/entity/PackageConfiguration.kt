package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PackageConfiguration(
    private val bundle: Bundle
) : Parcelable