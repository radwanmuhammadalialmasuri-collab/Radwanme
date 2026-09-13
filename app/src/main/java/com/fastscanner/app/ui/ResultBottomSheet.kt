package com.fastscanner.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheet(
    barcode: Barcode,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val rawValue = barcode.rawValue ?: "لا توجد بيانات"
    val formatName = when (barcode.format) {
        Barcode.FORMAT_QR_CODE -> "رمز QR سريع"
        Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8 -> "رمز منتج (EAN)"
        Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E -> "رمز تسوق (UPC)"
        Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39 -> "رمز شريطي كودي"
        else -> "رمز شريطي"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = formatName,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = rawValue,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // زر النسخ
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Barcode", rawValue))
                        Toast.makeText(context, "تم النسخ للحافظة!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).padding(4.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نسخ")
                }

                // فتح كرابط إذا كان URL
                if (barcode.valueType == Barcode.TYPE_URL || rawValue.startsWith("http://") || rawValue.startsWith("https://")) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawValue))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f).padding(4.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فتح")
                    }
                } else {
                    // زر البحث في جوجل
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$rawValue"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f).padding(4.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بحث")
                    }
                }

                // زر المشاركة
                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, rawValue)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة الرمز عبر"))
                    },
                    modifier = Modifier.weight(1f).padding(4.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
