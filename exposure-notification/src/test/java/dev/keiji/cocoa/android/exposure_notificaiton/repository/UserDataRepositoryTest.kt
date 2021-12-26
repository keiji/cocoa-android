package dev.keiji.cocoa.android.exposure_notificaiton.repository

import android.content.Context
import android.content.SharedPreferences
import dev.keiji.cocoa.android.common.provider.DateTimeProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner.Silent::class)
class UserDataRepositoryTest {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
    }

}