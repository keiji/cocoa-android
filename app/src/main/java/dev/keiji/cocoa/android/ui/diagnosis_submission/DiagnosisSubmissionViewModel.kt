package dev.keiji.cocoa.android.ui.diagnosis_submission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.api.DiagnosisSubmissionRequest
import dev.keiji.cocoa.android.api.DiagnosisSubmissionServiceApi
import dev.keiji.cocoa.android.repository.TemporaryExposureKeyRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DiagnosisSubmissionViewModel @Inject constructor(
    private val temporaryExposureKeyRepository: TemporaryExposureKeyRepository,
    private val diagnosisSubmissionServiceApi: DiagnosisSubmissionServiceApi,
) : ViewModel() {
    companion object {
        private const val CLUSTER_ID = "345678"
    }

    val processNumber: MutableLiveData<String> = MutableLiveData()

    private val _symptomState: MutableLiveData<Boolean?> = MutableLiveData()
    val symptomState: LiveData<Boolean?>
        get() = _symptomState

    fun setSymptomExist(isExist: Boolean) {
        _symptomState.value = isExist
    }

    private val _symptomOnsetDate: MutableLiveData<Calendar> = MutableLiveData<Calendar>().also {
        it.value = Calendar.getInstance(Locale.getDefault())
    }
    val symptomOnsetDate: LiveData<Calendar>
        get() = _symptomOnsetDate

    fun setSymptomOnsetDate(calendar: Calendar) {
        _symptomOnsetDate.value = calendar
    }

    fun submit() {
        Timber.d("ProcessNumber ${processNumber.value ?: "null"}")

        val symptomState = symptomState.value
        if (symptomState == null) {
            Timber.w("SymptomState is not set.")
            return
        }

        val symptomOnsetDate = if (!symptomState) {
            Calendar.getInstance(Locale.getDefault())
        } else {
            _symptomOnsetDate.value
        }

        if (symptomOnsetDate == null) {
            Timber.w("symptomOnsetDate is not set.")
            return
        }

        val teks = temporaryExposureKeyRepository.getTemporaryExposureKeyList()
        if (teks.isEmpty()) {
            Timber.w("TemporaryExposureKeys is empty.")
        }

        val idempotencyKey = UUID.randomUUID().toString()
        val request = DiagnosisSubmissionRequest(
            idempotencyKey,
            symptomOnsetDate.time,
            teks
        )

        Timber.d(request.toString())

        viewModelScope.launch {
            val resultTemporaryExposureKeyList = diagnosisSubmissionServiceApi.submitV3(
                CLUSTER_ID,
                request
            )
            resultTemporaryExposureKeyList.forEach { tek ->
                Timber.d(tek.toString())
            }
        }
    }
}