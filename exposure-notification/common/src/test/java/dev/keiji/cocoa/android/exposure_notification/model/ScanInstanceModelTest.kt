package dev.keiji.cocoa.android.exposure_notification.model

import org.junit.Assert
import org.junit.Test

class ScanInstanceModelTest {
    @Test
    fun compareTest1() {
        val object1 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )
        val object2 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(0, result)
    }

    @Test
    fun compareTest2() {
        val object1 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )
        val object2 = ScanInstanceModel(
            id = 1,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(0, result)
    }

    @Test
    fun compareTest3() {
        val object1 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 1,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )
        val object2 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(0, result)
    }

    @Test
    fun compareTest4() {
        val object1 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 1,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )
        val object2 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(-1, result)
        val sorted = listOf(object1, object2).sorted()

        Assert.assertEquals(1, sorted[0].minAttenuationDb)
        Assert.assertEquals(0, sorted[1].minAttenuationDb)
    }

    @Test
    fun compareTest5() {
        val object1 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )
        val object2 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 1,
            typicalAttenuationDb = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(1, result)
        val sorted = listOf(object1, object2).sorted()

        Assert.assertEquals(1, sorted[0].secondsSinceLastScan)
        Assert.assertEquals(0, sorted[1].secondsSinceLastScan)
    }

    @Test
    fun compareTest6() {
        val object1 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = -1,
        )
        val object2 = ScanInstanceModel(
            id = 0,
            exposureWindowId = 0,
            minAttenuationDb = 0,
            secondsSinceLastScan = 0,
            typicalAttenuationDb = 0,
        )

        val result = object1.compareTo(object2)
        Assert.assertEquals(1, result)
        val sorted = listOf(object1, object2).sorted()

        Assert.assertEquals(0, sorted[0].typicalAttenuationDb)
        Assert.assertEquals(-1, sorted[1].typicalAttenuationDb)
    }
}
