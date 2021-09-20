package dev.keiji.cocoa.android.ui.risk_state

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.keiji.cocoa.android.entity.RiskEvent
import dev.keiji.cocoa.android.repository.RiskEventRepository

class RiskDetailViewModel(
    private val riskEventRepository: RiskEventRepository
) : ViewModel() {

    private val _riskEventList = MutableLiveData<List<RiskEvent>>()

    public val riskEventList: LiveData<List<RiskEvent>>
        get() = _riskEventList

    fun loadAll() {
        _riskEventList.value = riskEventRepository.findAll()
    }
}
