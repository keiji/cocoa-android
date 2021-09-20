package dev.keiji.cocoa.android.ui.diagnosis_submission

import android.os.Bundle
import android.view.View
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.composethemeadapter.MdcTheme
import dev.keiji.cocoa.android.R
import dev.keiji.cocoa.android.databinding.FragmentDiagnosisSubmissionBinding

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
    }

    private var binding : FragmentDiagnosisSubmissionBinding? = null

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
                        Submission() {

                        }
                    }
                }
            }
        }
    }

    @Preview()
    @Composable
    fun Agreement(onClick: () -> Unit = {}) {
        MdcTheme {
            Button(onClick = { onClick() }) {
                Text("同意")
            }
        }
    }

    @Preview()
    @Composable
    fun Submission(onClick: () -> Unit = {}) {
        MdcTheme {
            Button(onClick = { onClick() }) {
                Text("登録")
            }
        }
    }
}
