package dev.keiji.cocoa.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MdcTheme {
                Main()
            }
        }
    }
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

            RiskLevel() {}

            Spacer(Modifier.width(32.dp))

            SubmitDiagnosis() {}

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
            .padding(8.dp, 0.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .padding(0.dp, 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
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
                Text(
                    text = "注意",
                    style = MaterialTheme.typography.h5,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "感染リスクが高い接触があります。日々の生活を見直してくださいとか書きたいけど、それが許されるのかとか、そもそも感染リスクという表記がOKなのかとか。",
                    style = MaterialTheme.typography.body2
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onClick() }
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
            .padding(8.dp, 0.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .padding(0.dp, 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
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
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "陽性情報の登録する云々の説明をここに書くのですが、実際に文字で書いてみなさんちゃんと読むのでしょうかという疑問はありますね。",
                        style = MaterialTheme.typography.body2
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onClick() }
                ) {
                    Text(text = "登録")
                }
            }
        }
    }
}

@Composable
fun LegalInfos() {
    Column() {
        Text(
            text = "法的情報",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .padding(8.dp, 0.dp)
        )
        Row(modifier = Modifier.
        horizontalScroll(rememberScrollState())) {
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
                .padding(8.dp)
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
