package dev.keiji.cocoa.android.ui.risk_state

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.R
import dev.keiji.cocoa.android.databinding.FragmentRiskDetailBinding
import dev.keiji.cocoa.android.exposure_notificaiton.entity.RiskEvent
import dev.keiji.cocoa.android.ui.Signals
import java.text.DateFormat

@AndroidEntryPoint
class RiskDetailFragment : Fragment(R.layout.fragment_risk_detail) {

    private val viewModel: RiskDetailViewModel by viewModels()

    private var binding: FragmentRiskDetailBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DataBindingUtil.bind<FragmentRiskDetailBinding>(view)?.also {
            binding = it

            it.composeView.setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "exposure_information") {
                    composable("exposure_information") {
                        RiskDetail(viewModel)
                    }
                }
            }
        }

        viewModel.loadAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    @Composable
    fun RiskDetail(viewModel: RiskDetailViewModel) {
        val riskEventList = viewModel.riskEventList.observeAsState()

        MdcTheme {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(colorResource(R.color.grey100)),
                verticalArrangement = Arrangement.Top
            ) {
                AppBar() {}

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))

                        RiskLevelHeader()

                        Spacer(Modifier.height(16.dp))
                    }

                    riskEventList.value?.forEach {
                        item {
                            RiskLevelRow(it)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    private val dateFormatter = DateFormat.getDateInstance()

    private @Composable
    fun RiskLevelRow(riskEvent: RiskEvent) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Spacer(Modifier.width(12.dp))

            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .padding(8.dp, 8.dp),
            ) {
                Text(
                    modifier = Modifier,
                    text = dateFormatter.format(riskEvent.date)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    modifier = Modifier,
                    text = "${riskEvent.count}件",
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }

    private @Composable
    fun RiskLevelHeader() {
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
                    Signals()
                }
            }
        }
    }

    @Composable
    fun AppBar(onClick: () -> Unit) {
        TopAppBar(
            title = { Text("感染リスク") },
            navigationIcon = {
                IconButton(onClick = { onClick() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
    }
}
