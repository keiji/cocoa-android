package dev.keiji.cocoa.android.exposure_notification.ui.submit_diagnosis

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import dev.keiji.cocoa.android.exposure_notification.core.entity.TemporaryExposureKey
import dev.keiji.cocoa.android.exposure_notification.api.SubmitDiagnosisServiceApi
import junit.framework.Assert.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class SubmitDiagnosisViewModelTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var tmpFolder: TemporaryFolder

    @Mock
    private lateinit var mockContext: Context

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private var temporaryExposureKeyList: List<TemporaryExposureKey> = emptyList()

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
    fun loadExposureConfiguration(): Unit = runBlocking {
        val mockDiagnosisSubmissionServiceApi =
            mock<SubmitDiagnosisServiceApi> {
                onBlocking { submitV3(any()) } doReturn temporaryExposureKeyList
            }

        val viewModel =
            SubmitDiagnosisViewModel(
                SavedStateHandle(),
                mockDiagnosisSubmissionServiceApi
            )

        assertFalse(viewModel.isSubmittable())

        viewModel.clearHasSymptom()
        viewModel.clearProcessNumber()

        viewModel.setHasSymptom(true)
        assert(viewModel.isShowCalendar)
        assertFalse(viewModel.isSubmittable())

        viewModel.clearHasSymptom()
        viewModel.clearProcessNumber()

        viewModel.setHasSymptom(false)
        assert(viewModel.isShowCalendar)
        assertFalse(viewModel.isSubmittable())

        viewModel.clearHasSymptom()
        viewModel.clearProcessNumber()

        viewModel.setHasSymptom(true)
        viewModel.setProcessNumber("12345678")
        assert(viewModel.isSubmittable())

        viewModel.clearHasSymptom()
        viewModel.clearProcessNumber()

        viewModel.setHasSymptom(false)
        viewModel.setProcessNumber("12345678")
        assert(viewModel.isSubmittable())

        viewModel.clearHasSymptom()
        viewModel.clearProcessNumber()

        viewModel.setHasSymptom(false)
        viewModel.setProcessNumber("1234567")
        assertFalse(viewModel.isSubmittable())

        viewModel.clearHasSymptom()
        viewModel.clearProcessNumber()

        viewModel.setHasSymptom(true)
        viewModel.setProcessNumber("1234567890")
        assertFalse(viewModel.isSubmittable())
    }
}