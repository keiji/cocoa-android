package dev.keiji.cocoa.android.exposure_notification.diagnosis_submission.api

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class V3DiagnosisSubmissionRequestTest {

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun paddingTest() {
        val submissionRequest = V3DiagnosisSubmissionRequest("", emptyList(), emptyList(), "", emptyList())

        Assert.assertNotNull(submissionRequest.padding)
        Assert.assertTrue(submissionRequest.padding.length >= V3DiagnosisSubmissionRequest.MIN_PADDING_SIZE)
        Assert.assertTrue(submissionRequest.padding.length <= V3DiagnosisSubmissionRequest.MAX_PADDING_SIZE)
    }
}