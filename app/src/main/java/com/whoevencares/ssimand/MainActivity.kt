package com.whoevencares.ssimand

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MimeTypes
import com.whoevencares.ssimand.ui.components.OpenFile
import com.whoevencares.ssimand.ui.components.TopBarNav
import com.whoevencares.ssimand.ui.theme.SSIMandTheme
import org.apache.commons.io.IOUtils

class MainActivity : ComponentActivity() {
    private var idx = -1
    private var buf: Array<ByteArray?> = arrayOfNulls(3)
    private var ssimResult: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SSIMandTheme {
                var isResult by remember {
                    mutableStateOf(false)
                }
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row {
                            TopBarNav()
                        }

                        Row(modifier = Modifier.padding(top = 15.dp)) {
                            ChooseImage(
                                modifier = Modifier.fillMaxHeight(.40f),
                            ) {
                                Log.d("OnCLick", "1")
                                idx = 0
                                getBuf()
                            }
                        }

                        Row {
                            Divider(
                                color = MaterialTheme.colorScheme.primary,
                                thickness = 1.dp,
                                modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 15.dp)
                            )
                        }

                        Row {
                            ChooseImage(
                                modifier = Modifier.fillMaxHeight(.80f),
                            ) {
                                Log.d("OnClick", "2")
                                idx = 1
                                getBuf()
                            }
                        }

                        Row {
                            Spacer(modifier = Modifier.padding(5.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FilledTonalButton(
                                onClick = {
//                                    Log.d("OnClickCompare", buf[0].contentToString())
//                                    Log.d("OnClickCompare", buf[1].contentToString())
                                    if (buf[0] != null && buf[1] != null) {
                                        ssimResult = NativeLib.newSsimBuilder(buf[0], buf[1])
                                        Log.d(
                                            "SSIM",
                                            ssimResult.toString()
                                        )
                                        isResult = true
                                    }

                                },
                                modifier = Modifier.fillMaxWidth(0.5f),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Text(
                                    "Compare",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    if (isResult) {
                        AlertDialog(
                            title = {
                                Text(text = "Result")
                            },
                            text = {
                                Text(text = ssimResult.toString())
                            },
                            onDismissRequest = {
                                isResult = !isResult
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        isResult = !isResult
                                    }
                                ) {
                                    Text("Confirm")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun getBuf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MimeTypes.IMAGE_JPEG
        }
        resultLauncher.launch(intent)
    }


    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val contentResolver = applicationContext.contentResolver
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Uri = result.data?.data ?: return@registerForActivityResult
                contentResolver.openInputStream(data)?.use { inputStream ->
                    buf[idx] = IOUtils.toByteArray(inputStream)
                }
            }
        }

    @Composable
    fun ChooseImage(modifier: Modifier = Modifier, onState: () -> Unit) {
        Column {
            Row {
                Spacer(modifier = Modifier.padding(2.dp))
            }
            Row {
                OpenFile(
                    callback = { onState() },
                    modifier = modifier.fillMaxWidth(),
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun HomePreview() {
//        var fileChosen1 by remember {
//            mutableStateOf(false)
//        }
//
//        var fileChosen2 by remember {
//            mutableStateOf(false)
//        }
        var isRsult by remember {
            mutableStateOf(false)
        }
        SSIMandTheme(darkTheme = true) {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row {
                        TopBarNav()
                    }

                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        ChooseImage(modifier = Modifier.fillMaxHeight(.40f)) {
//                            fileChosen1 = true
                        }
                    }

                    Row {
                        Divider(
                            color = MaterialTheme.colorScheme.primary,
                            thickness = 1.dp,
                            modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 15.dp)
                        )
                    }

                    Row {
                        ChooseImage(modifier = Modifier.fillMaxHeight(.80f)) {
//                            fileChosen2 = true
                        }
                    }

                    Row {
                        Spacer(modifier = Modifier.padding(5.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FilledTonalButton(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(.5f),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text("Compare", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
                if (isRsult) {
                    AlertDialog(
                        title = {
                            Text(text = "Result")
                        },
                        text = {
                            Text(text = ssimResult.toString())
                        },
                        onDismissRequest = {},
                        confirmButton = {
                            TextButton(
                                onClick = {}
                            ) {
                                Text("Confirm")
                            }
                        }
                    )
                }
            }
        }
    }
}