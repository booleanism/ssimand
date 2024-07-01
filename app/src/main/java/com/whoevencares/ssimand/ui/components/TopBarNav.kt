package com.whoevencares.ssimand.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.whoevencares.ssimand.ui.theme.SSIMandTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNav() {
    TopAppBar(
        title = { Text(text = "SSIMand") }, colors = TopAppBarDefaults.topAppBarColors(
            MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopBarPreview() {
    SSIMandTheme {
        TopBarNav();
    }
}