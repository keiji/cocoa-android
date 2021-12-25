package dev.keiji.cocoa.android.ui.diagnosis_submission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.AppConstants
import dev.keiji.cocoa.android.api.DiagnosisSubmissionRequest
import dev.keiji.cocoa.android.api.DiagnosisSubmissionServiceApi
import dev.keiji.cocoa.android.entity.TemporaryExposureKey
import dev.keiji.cocoa.android.regions
import dev.keiji.cocoa.android.subregions
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DiagnosisSubmissionViewModel @Inject constructor(
    private val diagnosisSubmissionServiceApi: DiagnosisSubmissionServiceApi,
) : ViewModel() {

    val processNumber: MutableLiveData<String> = MutableLiveData()

    private val idempotencyKey = UUID.randomUUID().toString()

    private val _hasSymptomState: MutableLiveData<Boolean?> = MutableLiveData()
    val hasSymptom: LiveData<Boolean?>
        get() = _hasSymptomState

    fun setHasSymptomExist(hasSymptom: Boolean) {
        _hasSymptomState.value = hasSymptom
    }

    private val _symptomOnsetDate: MutableLiveData<Calendar> = MutableLiveData<Calendar>().also {
        it.value = Calendar.getInstance(Locale.getDefault())
    }
    val symptomOnsetDate: LiveData<Calendar>
        get() = _symptomOnsetDate

    fun setSymptomOnsetDate(calendar: Calendar) {
        _symptomOnsetDate.value = calendar
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
