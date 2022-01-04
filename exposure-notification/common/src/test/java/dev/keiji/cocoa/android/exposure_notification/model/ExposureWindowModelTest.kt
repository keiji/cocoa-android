package dev.keiji.cocoa.android.exposure_notification.model

import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.Infectiousness
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ReportType
import org.junit.Assert
import org.junit.Test

class ExposureWindowModelTest {

    @Test
    fun compareTest1() {
        val object1 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(0, result)
    }

    @Test
    fun compareTest2() {
        val object1 = ExposureWindowModel(
            id = 1,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(0, result)
    }

    @Test
    fun compareTest3() {
        val object1 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefgZ",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(0, result)
    }

    @Test
    fun compareTest4() {
        val object1 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 1,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(0, result)
    }

    @Test
    fun compareTest5() {
        val object1 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 1,
            infectiousness = 0,
            reportType = 0,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(1, result)
        val sorted = listOf(object1, object2).sorted()

        Assert.assertEquals(0, sorted[0].dateMillisSinceEpoch)
        Assert.assertEquals(1, sorted[1].dateMillisSinceEpoch)
    }

    @Test
    fun compareTest6() {
        val object1 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = -1,
            infectiousness = 0,
            reportType = 0,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(-1, result)
        val sorted = listOf(object1, object2).sorted()

        Assert.assertEquals(-1, sorted[0].dateMillisSinceEpoch)
        Assert.assertEquals(0, sorted[1].dateMillisSinceEpoch)
    }

    @Test
    fun compareTest7() {
        val object1 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = Infectiousness.HIGH.ordinal,
            reportType = 0,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = Infectiousness.STANDARD.ordinal,
            reportType = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(-1, result)
        val sorted = listOf(object1, object2).sorted()

        Assert.assertEquals(Infectiousness.HIGH.ordinal, sorted[0].infectiousness)
        Assert.assertEquals(Infectiousness.STANDARD.ordinal, sorted[1].infectiousness)
    }

    @Test
    fun compareTest8() {
        val object1 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = ReportType.CONFIRMED_TEST.ordinal,
        )
        val object2 = ExposureWindowModel(
            id = 0,
            uniqueKey = "abcdefg",
            exposureDataId = 0,
            calibrationConfidence = 0,
            dateMillisSinceEpoch = 0,
            infectiousness = 0,
            reportType = ReportType.CONFIRMED_CLINICAL_DIAGNOSIS.ordinal,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(-1, result)
        val sorted = listOf(object1, object2).sorted()

        Assert.assertEquals(ReportType.CONFIRMED_TEST.ordinal, sorted[0].reportType)
        Assert.assertEquals(ReportType.CONFIRMED_CLINICAL_DIAGNOSIS.ordinal, sorted[1].reportType)
    }
}
