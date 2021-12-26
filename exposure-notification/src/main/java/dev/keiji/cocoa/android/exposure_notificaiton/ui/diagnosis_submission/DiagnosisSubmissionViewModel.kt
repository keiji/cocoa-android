package dev.keiji.cocoa.android.exposure_notificaiton.ui.diagnosis_submission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.exposure_notificaiton.AppConstants
import dev.keiji.cocoa.android.exposure_notificaiton.entity.TemporaryExposureKey
import dev.keiji.cocoa.android.exposure_notification.api.DiagnosisSubmissionRequest
import dev.keiji.cocoa.android.exposure_notification.api.DiagnosisSubmissionServiceApi
import dev.keiji.cocoa.android.exposure_notification.regions
import dev.keiji.cocoa.android.exposure_notification.subregions
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DiagnosisSubmissionViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val diagnosisSubmissionServiceApi: DiagnosisSubmissionServiceApi,
) : ViewModel() {
    companion object {
        private const val KEY_STATE_PROCESS_NUMBER = "process_number"
        private const val KEY_STATE_HAS_SYMPTOM = "has_symptom"
        private const val KEY_STATE_SYMPTOM_ONSET_DATE = "symptom_onset_date"
    }

    private val _processNumber: MutableLiveData<String> =
        state.getLiveData(KEY_STATE_PROCESS_NUMBER)

    val processNumber: LiveData<String>
        get() = _processNumber

    fun setProcessNumber(value: String) {
        if (value.length <= AppConstants.PROCESS_NUMBER_LENGTH) {
            state[KEY_STATE_PROCESS_NUMBER] = value
        }
    }

    fun clearProcessNumber() {
        state[KEY_STATE_PROCESS_NUMBER] = ""
    }

    private val idempotencyKey = UUID.randomUUID().toString()

    private val _hasSymptomState: MutableLiveData<Boolean?> =
        state.getLiveData(KEY_STATE_HAS_SYMPTOM)
    val hasSymptom: LiveData<Boolean?>
        get() = _hasSymptomState

    fun setHasSymptom(hasSymptom: Boolean) {
        state[KEY_STATE_HAS_SYMPTOM] = hasSymptom
    }

    fun clearHasSymptom() {
        state[KEY_STATE_HAS_SYMPTOM] = null
    }

    val isShowCalendar: Boolean
        get() = hasSymptom.value != null

    private val _symptomOnsetDate: MutableLiveData<Calendar> =
        state.getLiveData(KEY_STATE_SYMPTOM_ONSET_DATE)

    val symptomOnsetDate: LiveData<Calendar>
        get() = _symptomOnsetDate

    fun setSymptomOnsetDate(calendar: Calendar) {
        state[KEY_STATE_SYMPTOM_ONSET_DATE] = calendar
    }

    fun submit(temporaryExposureKeyList: List<TemporaryExposureKey>) {
        Timber.d("ProcessNumber ${processNumber.value ?: "null"}")

        val hasSymptomSnapshot = hasSymptom.value
        if (hasSymptomSnapshot == null) {
            Timber.w("SymptomState is not set.")
            return
        }

        val symptomOnsetDate = if (!hasSymptomSnapshot) {
            Calendar.getInstance(Locale.getDefault())
        } else {
            _symptomOnsetDate.value
        }

        if (symptomOnsetDate == null) {
            Timber.w("symptomOnsetDate is not set.")
            return
        }

        if (temporaryExposureKeyList.isEmpty()) {
            Timber.w("TemporaryExposureKeys is empty.")
        }

        val request = DiagnosisSubmissionRequest(
            idempotencyKey,
            regions(),
            subregions(),
            symptomOnsetDate.time,
            temporaryExposureKeyList
        )

        Timber.d(request.toString())

        viewModelScope.launch {
            try {
                val resultTemporaryExposureKeyList = diagnosisSubmissionServiceApi.submitV3(
                    request
                )
                resultTemporaryExposureKeyList.forEach { tek ->
                    Timber.d(tek.toString())
                }
            } catch (exception: HttpException) {
                Timber.e("HttpException occurred.", exception)
            }
        }
    }

    fun isSubmittable(): Boolean {
        hasSymptom.value ?: return false
        val processNumberSnapshot = processNumber.value ?: return false
        return processNumberSnapshot.length == AppConstants.PROCESS_NUMBER_LENGTH
    }
}
