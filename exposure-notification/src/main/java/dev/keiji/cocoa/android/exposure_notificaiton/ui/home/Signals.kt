package dev.keiji.cocoa.android.exposure_notificaiton.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.keiji.cocoa.android.exposure_notificaiton.R

@Composable
fun Signals() {
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
            painter = painterResource(R.drawable.baseline_circle_24),
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(12.dp))
        Image(
            painter = painterResource(R.drawable.baseline_circle_24),
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(12.dp))
        Image(
            painter = painterResource(R.drawable.baseline_circle_24),
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
    }
    Spacer(Modifier.height(16.dp))
    Text(
        text = "感染リスクが高い接触があります。日々の生活を見直してくださいとか書きたいけど、それが許されるのかとか、そもそも感染リスクという表記がOKなのかとか。",
        style = MaterialTheme.typography.body2
    )
}
