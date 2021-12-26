package dev.keiji.cocoa.android.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keiji.cocoa.android.exposure_notificaiton.entity.RiskEvent
import dev.keiji.cocoa.android.exposure_notificaiton.entity.RiskLevel
import java.util.*

class RiskEventRepository(applicationContext: Context) {

    fun findAll(): List<RiskEvent> {
        return listOf(
            RiskEvent(createDateOffset(0), RiskLevel.RISK_LEVEL_LOWEST, 0),
            RiskEvent(createDateOffset(-1), RiskLevel.RISK_LEVEL_LOW, 1),
            RiskEvent(createDateOffset(-2), RiskLevel.RISK_LEVEL_HIGHEST, 6),
            RiskEvent(createDateOffset(-3), RiskLevel.RISK_LEVEL_LOW, 1),
            RiskEvent(createDateOffset(-4), RiskLevel.RISK_LEVEL_LOW, 1),
            RiskEvent(createDateOffset(-5), RiskLevel.RISK_LEVEL_HIGHEST, 4),
            RiskEvent(createDateOffset(-6), RiskLevel.RISK_LEVEL_LOW, 1),
            RiskEvent(createDateOffset(-7), RiskLevel.RISK_LEVEL_LOW, 1),
            RiskEvent(createDateOffset(-8), RiskLevel.RISK_LEVEL_LOW, 1),
            RiskEvent(createDateOffset(-9), RiskLevel.RISK_LEVEL_MEDIUM, 3),
        )
    }

    private fun createDateOffset(dayOfOffset: Int): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.DAY_OF_YEAR, dayOfOffset)
        return calendar.time
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RiskEventRepositoryModule {

    @Provides
    fun provideRiskEventRepository(
        @ApplicationContext applicationContext: Context
    ): RiskEventRepository {
        return RiskEventRepository(applicationContext);
    }
}
