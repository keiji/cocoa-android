package dev.keiji.cocoa.android.ui.diagnosis_submission

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.nearby.exposurenotification.ReportType.*
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.R
import dev.keiji.cocoa.android.databinding.FragmentDiagnosisSubmissionBinding
import dev.keiji.cocoa.android.ui.ExposureNotificationViewModel
import java.util.*

@AndroidEntryPoint
class DiagnosisSubmissionFragment : Fragment(R.layout.fragment_diagnosis_submission) {
    companion object {
        fun newInstance(): DiagnosisSubmissionFragment {
            val fragment = DiagnosisSubmissionFragment()
            val args = Bundle().apply {
            }
            return fragment.apply {
                arguments = args
            }
        }

        private fun convertToString(calendar: Calendar): String {
            return "${calendar.get(Calendar.YEAR)}" +
                    "/" +
                    "${calendar.get(Calendar.MONTH) + 1}" +
                    "/" +
                    "${calendar.get(Calendar.DAY_OF_MONTH)}"
        }
    }

    private val exposureNotificationViewModel: ExposureNotificationViewModel by activityViewModels()
    private val viewModel: DiagnosisSubmissionViewModel by viewModels()
    private var binding: FragmentDiagnosisSubmissionBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DataBindingUtil.bind<FragmentDiagnosisSubmissionBinding>(view)?.also {
            binding = it

            it.composeView.setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "agreement") {
                    composable("agreement") {
                        Agreement() {
                            navController.navigate("submission")
                        }
                    }
                    composable("submission") {
                        Submission(viewModel) {
                            exposureNotificationViewModel.getTemporaryExposureKeyHistory(
                                requireActivity(),
                                CONFIRMED_TEST
                            )
                        }
                    }
                }
            }
        }

        exposureNotificationViewModel.temporaryExposureKey.observe(viewLifecycleOwner) {
            it ?: return@observe
            viewModel.submit(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    @Composable
    fun Agreement(onClick: () -> Unit = {}) {
        MdcTheme {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(colorResource(R.color.grey100)),
                verticalArrangement = Arrangement.Top
            ) {
                AppBarAgreement() {}

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Hello World"
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(),
                        onClick = { onClick() }
                    ) {
                        Text("同意")
                    }
                }
            }
        }
    }

    @Composable
    fun AppBarAgreement(onClick: () -> Unit) {
        TopAppBar(
            title = { Text("陽性登録の前に") },
            navigationIcon = {
                IconButton(onClick = { onClick() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
    }

    private val defaultViewModel: DiagnosisSubmissionViewModel
        get() = viewModel

    @Composable
    fun Submission(
        viewModel: DiagnosisSubmissionViewModel = defaultViewModel,
        onSubmit: () -> Unit = {}
    ) {
        MdcTheme {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(colorResource(R.color.grey100)),
                verticalArrangement = Arrangement.Top
            ) {
                AppBarSubmission() {}

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AskSymptom(viewModel)

                    Spacer(Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "SMSまたはメールで届いた処理番号を入力してください"
                    )
                    Column(Modifier.padding(16.dp, 8.dp)) {
                        val processNumber = viewModel.processNumber.observeAsState()
                        TextField(
                            value = processNumber.value ?: "",
                            onValueChange = {
                                viewModel.setProcessNumber(it)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text("8桁の処理番号を入力してください")
                    }

                    Card(
                        modifier = Modifier
                            .padding(0.dp, 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .defaultMinSize(140.dp, 0.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "登録すると",
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "症状が始まった日または検査を受けた日以降にあなたと接触した人に通知が行きます",
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "※登録は匿名で行われ、氏名や連絡先など個人が特定される情報が他の人に知られることはありません\n" +
                                        "※接触した場所がどこなのか記録されたり、他の人に知られることもありません",
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(),
                        onClick = { onSubmit() },
                        enabled = viewModel.isSubmittable()
                    ) {
                        Text("登録")
                    }
                }
            }
        }
    }

    private @Composable
    fun AskSymptom(
        viewModel: DiagnosisSubmissionViewModel = defaultViewModel,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "次のような症状がありますか？"
        )
        Spacer(Modifier.height(8.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "発熱、関、呼吸困難、全身倦怠感、咽頭痛、鼻汁・鼻閉、頭痛、関節・筋肉痛、下痢、嘔気、嘔吐など",
            style = MaterialTheme.typography.caption
        )

        Spacer(Modifier.height(16.dp))

        Row() {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = viewModel.hasSymptom.observeAsState().value ?: false,
                    onClick = {
                        viewModel.setHasSymptom(true)
                    }
                )
                Text("ある")
            }

            Spacer(Modifier.width(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = !(viewModel.hasSymptom.observeAsState().value ?: true),
                    onClick = {
                        viewModel.setHasSymptom(false)
                    }
                )
                Text("ない")
            }
        }

        if (viewModel.isShowCalendar) {
            val hasSymptom = viewModel.hasSymptom.observeAsState().value ?: return

            Spacer(Modifier.height(16.dp))

            if (hasSymptom) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "症状が始まった最初の日を選択してください。"
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "直近の新型コロナウイルス感染症の検査を受けた日を入力してください。"
                )
            }

            Spacer(Modifier.height(8.dp))

            SymptomCalendar()
        }

        Spacer(Modifier.height(16.dp))
    }

    private @Composable
    fun SymptomCalendar(
    ) {
        val focusManager = LocalFocusManager.current

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "覚えている範囲で一番古い日付を入力してください。入力した日付が誰かに知られることはありません。",
            style = MaterialTheme.typography.caption
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = viewModel.symptomOnsetDate.observeAsState().value?.let { convertToString(it) }
                ?: "",
            onValueChange = {},
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    showDatePicker(focusManager)
                }
            },
        )
    }

    private fun showDatePicker(focusManager: FocusManager) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(viewModel.symptomOnsetDate.value?.timeInMillis)
                .build()
        datePicker.addOnPositiveButtonClickListener {
            val ticks = datePicker.selection ?: return@addOnPositiveButtonClickListener
            val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                timeInMillis = ticks
            }
            viewModel.setSymptomOnsetDate(calendar)
        }
        datePicker.addOnDismissListener {
            focusManager.clearFocus()
        }
        datePicker.show(childFragmentManager, "DatePicker")
    }

    @Composable
    fun AppBarSubmission(onClick: () -> Unit) {
        TopAppBar(
            title = { Text("陽性登録") },
            navigationIcon = {
                IconButton(onClick = { onClick() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
    }
}
