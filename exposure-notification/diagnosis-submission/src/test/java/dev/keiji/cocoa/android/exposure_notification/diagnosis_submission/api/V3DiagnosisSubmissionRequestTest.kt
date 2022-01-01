package dev.keiji.cocoa.android.exposure_notification.diagnosis_submission.api

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class V3DiagnosisSubmissionRequestTest {

    companion object {
        private const val EXPECTED_CLEAR_TEXT_V3 =
            "2021-12-19T19:02:00.000+09:00|jp.go.mhlw.cocoa.unit_test|S2V5RGF0YTE=.10000.140.1,S2V5RGF0YTI=.20000.141.1,S2V5RGF0YTM=.30000.142.1,S2V5RGF0YTQ=.40000.143.1,S2V5RGF0YTU=.50000.70.1|440,441|VerificationPayload THIS STRING IS MEANINGLESS"
    }

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun paddingTest() {
        val submissionRequest =
            V3DiagnosisSubmissionRequest("", emptyList(), emptyList(), "", emptyList())

        Assert.assertNotNull(submissionRequest.padding)
        Assert.assertTrue(submissionRequest.padding.length >= V3DiagnosisSubmissionRequest.MIN_PADDING_SIZE)
        Assert.assertTrue(submissionRequest.padding.length <= V3DiagnosisSubmissionRequest.MAX_PADDING_SIZE)
    }

    @Test
    fun clearTextTest() {
        val temporaryExposureKeys = listOf(
            V3DiagnosisSubmissionRequest.TemporaryExposureKey(
                "S2V5RGF0YTE=",
                10000,
                140,
                reportType = 1
            ),
            V3DiagnosisSubmissionRequest.TemporaryExposureKey(
                "S2V5RGF0YTI=",
                20000,
                141,
                reportType = 1
            ),
            V3DiagnosisSubmissionRequest.TemporaryExposureKey(
                "S2V5RGF0YTM=",
                30000,
                142,
                reportType = 1
            ),
            V3DiagnosisSubmissionRequest.TemporaryExposureKey(
                "S2V5RGF0YTQ=",
                40000,
                143,
                reportType = 1
            ),
            V3DiagnosisSubmissionRequest.TemporaryExposureKey(
                "S2V5RGF0YTU=",
                50000,
                70,
                reportType = 1
            ),
        )

        val submissionRequest = V3DiagnosisSubmissionRequest(
            idempotencyKey = "",
            regions = listOf(440, 441),
            subRegions = emptyList(),
            symptomOnsetDate = "2021-12-19T19:02:00.000+09:00",
            appPackageName = "jp.go.mhlw.cocoa.unit_test",
            processNumber = "VerificationPayload THIS STRING IS MEANINGLESS",
            temporaryExposureKeys = temporaryExposureKeys,
        )

        val clearText = submissionRequest.getClearText()
        Assert.assertEquals(EXPECTED_CLEAR_TEXT_V3, clearText)

    }
}
