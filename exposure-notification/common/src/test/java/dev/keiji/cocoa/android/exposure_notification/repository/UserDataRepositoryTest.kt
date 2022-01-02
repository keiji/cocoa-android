package dev.keiji.cocoa.android.exposure_notification.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class UserDataRepositoryTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var tmpFolder: TemporaryFolder

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)

        tmpFolder = TemporaryFolder().also {
            it.create()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()

        tmpFolder.delete()
    }

    @Test
    fun blankTest() {
    }
}
