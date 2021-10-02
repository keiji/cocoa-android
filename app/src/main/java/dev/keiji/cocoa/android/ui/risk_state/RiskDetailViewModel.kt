package dev.keiji.cocoa.android.ui.risk_state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.entity.RiskEvent
import dev.keiji.cocoa.android.repository.RiskEventRepository
import javax.inject.Inject

@HiltViewModel
class RiskDetailViewModel @Inject constructor(
    private val riskEventRepository: RiskEventRepository
) : ViewModel() {

    private val _riskEventList = MutableLiveData<List<RiskEvent>>()

    public val riskEventList: LiveData<List<RiskEvent>>
        get() = _riskEventList

    fun loadAll() {
        _riskEventList.value = riskEventRepository.findAll()
    }
}
