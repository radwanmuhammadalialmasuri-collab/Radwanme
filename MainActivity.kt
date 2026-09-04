package com.fastscanner.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.fastscanner.app.data.AppDatabase
import com.fastscanner.app.data.BarcodeRecord
import com.fastscanner.app.ui.GeneratorScreen
import com.fastscanner.app.ui.ResultBottomSheet
import com.fastscanner.app.ui.ScannerScreen
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)

        setContent {
            var hasCameraPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                )
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                hasCameraPermission = isGranted
            }

            LaunchedEffect(Unit) {
                if (!hasCameraPermission) {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            var selectedTab by remember { mutableIntStateOf(0) }
            var scannedBarcode by remember { mutableStateOf<Barcode?>(null) }
            val scope = rememberCoroutineScope()

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "المسح") },
                            label = { Text("ماسح") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.QrCode, contentDescription = "إنشاء") },
                            label = { Text("إنشاء") }
                        )
                    }
                }
            ) { innerPadding ->
                if (hasCameraPermission) {
                    when (selectedTab) {
                        0 -> ScannerScreen(
                            onBarcodeScanned = { barcode ->
                                scannedBarcode = barcode
                                // حفظ في السجل تلقائياً
                                scope.launch {
                                    database.barcodeDao().insert(
                                        BarcodeRecord(
                                            rawValue = barcode.rawValue ?: "",
                                            displayValue = barcode.displayValue ?: "",
                                            format = barcode.format,
                                            valueType = barcode.valueType
                                        )
                                    )
                                }
                            }
                        )
                        1 -> GeneratorScreen()
                    }
                }

                scannedBarcode?.let { barcode ->
                    ResultBottomSheet(
                        barcode = barcode,
                        onDismiss = { scannedBarcode = null }
                    )
                }
            }
        }
    }
}
