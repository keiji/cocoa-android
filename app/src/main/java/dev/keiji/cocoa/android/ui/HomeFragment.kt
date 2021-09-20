package dev.keiji.cocoa.android.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import dev.keiji.cocoa.android.R
import dev.keiji.cocoa.android.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle().apply {
            }
            return fragment.apply {
                arguments = args
            }
        }
    }

    private var binding: FragmentHomeBinding? = null;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DataBindingUtil.bind<FragmentHomeBinding>(view)?.also {
            binding = it
            it.composeView.setContent {
                MdcTheme {
                    Main()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    @Preview
    @Composable
    fun Main() {
        Column(
            modifier = Modifier
                .background(colorResource(R.color.grey100))
        ) {
            AppBar() {}

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {

                Status()

                Spacer(Modifier.width(32.dp))

                RiskLevel() {
                    findNavController().navigate(R.id.action_homeFragment_to_riskDetailFragment)
                }

                Spacer(Modifier.width(32.dp))

                SubmitDiagnosis() {
                    findNavController().navigate(R.id.action_homeFragment_to_diagnosisSubmissionFragment)
                }

                Spacer(Modifier.width(32.dp))

                LegalInfos()
            }
        }
    }

    @Composable
    fun AppBar(onClick: () -> Unit) {
        TopAppBar(
            title = { Text("COCOA") },
            actions = {
                IconButton(onClick = { onClick() }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Setting")
                }
                IconButton(onClick = { onClick() }) {
                    Icon(Icons.Filled.Help, contentDescription = "Help")
                }
            }
        )
    }

    @Composable
    fun Status() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .defaultMinSize(140.dp, 0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "正常に動作しています",
                    style = MaterialTheme.typography.body1
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "最終確認日時: 2021年9月19日 3時19分",
                style = MaterialTheme.typography.body2
            )
        }
    }

    @Composable
    fun RiskLevel(
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                        text = "感染リスク",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    Signals()

                    Spacer(Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = { onClick() },
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(text = "詳細")
                    }

                }
            }
        }
    }

    @Composable
    fun SubmitDiagnosis(
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                        text = "陽性登録",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "陽性情報の登録する云々の説明をここに書くのですが、実際に文字で書いてみなさんちゃんと読むのでしょうかという疑問はありますね。",
                            style = MaterialTheme.typography.body2
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = { onClick() },
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(text = "登録")
                    }
                }
            }
        }
    }

    @Composable
    fun LegalInfos() {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.width(8.dp))
            InfoCard("お問い合わせ") {}
            Spacer(Modifier.width(8.dp))
            InfoCard("利用規約") {}
            Spacer(Modifier.width(8.dp))
            InfoCard("プライバシーポリシー") {}
            Spacer(Modifier.width(8.dp))
            InfoCard("アクセシビリティ方針") {}
            Spacer(Modifier.width(8.dp))
            InfoCard("ライセンス") {}
            Spacer(Modifier.width(8.dp))
        }
    }

    @Composable
    fun InfoCard(
        title: String = "Preview",
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .defaultMinSize(140.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}
