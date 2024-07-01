package com.whoevencares.ssimand.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whoevencares.ssimand.ui.theme.SSIMandTheme

data class FileChooser(var state: Boolean, var buf: ByteArray?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileChooser

        if (state != other.state) return false
        if (!buf.contentEquals(other.buf)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + buf.contentHashCode()
        return result
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFile(modifier: Modifier = Modifier, callback: () -> Unit) {
    var fileChosen by remember {
        mutableStateOf(FileChooser(false, null))
    }
//    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Card(
        onClick = {
            fileChosen = FileChooser(true, null)
            callback.invoke()
        },
        colors = if (fileChosen.state) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        },
        modifier = Modifier.border(
            3.dp,
            MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
            verticalArrangement = Arrangement.Center
        ) {
            if (fileChosen.state) {
                Text(
                    text = "Selected",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    Modifier.size(48.dp)
                )
                Text(text = "Open JPG file", fontSize = 15.sp)
            }
        }
    }
//    }
}

@Preview(showBackground = true)
@Composable
private fun OpenDirectoryPreview() {
    SSIMandTheme {
        OpenFile(callback = {})
    }
}