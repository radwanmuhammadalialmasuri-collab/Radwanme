package com.fastscanner.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheet(
    barcode: Barcode,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val rawValue = barcode.rawValue ?: ""
    val valueType = barcode.valueType

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getTypeName(valueType),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rawValue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            // الأزرار والإجراءات الذكية بحسب نوع الرمز
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (valueType) {
                    Barcode.TYPE_URL -> {
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawValue))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("فتح الرابط")
                        }
                    }
                    Barcode.TYPE_PRODUCT, Barcode.TYPE_ISBN -> {
                        Button(onClick = {
                            // مقارنة الأسعار والبحث عن المنتج فوراً
                            val url = "https://www.google.com/search?q=$rawValue"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("مقارنة السعر")
                        }
                    }
                    Barcode.TYPE_CONTACT_INFO -> {
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_INSERT).apply {
                                type = ContactsContract.RawContacts.CONTENT_TYPE
                                putExtra(ContactsContract.Intents.Insert.NAME, barcode.contactInfo?.name?.formattedName ?: "")
                                putExtra(ContactsContract.Intents.Insert.PHONE, barcode.contactInfo?.phones?.firstOrNull()?.number ?: "")
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("حفظ جهة الاتصال")
                        }
                    }
                }

                // زر النسخ السريع
                OutlinedButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Barcode Result", rawValue))
                    Toast.makeText(context, "تم النسخ بنجاح!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("نسخ")
                }

                // زر المشاركة
                IconButton(onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, rawValue)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "مشاركة النتيجة"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "مشاركة")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun getTypeName(type: Int): String = when (type) {
    Barcode.TYPE_URL -> "رابط ويب (Website URL)"
    Barcode.TYPE_WIFI -> "شبكة واي فاي (Wi-Fi Network)"
    Barcode.TYPE_PRODUCT -> "كود منتج تجاري (Product EAN/UPC)"
    Barcode.TYPE_ISBN -> "رقم كتاب دولي (ISBN Book)"
    Barcode.TYPE_CONTACT_INFO -> "بطاقة اتصال (vCard)"
    Barcode.TYPE_EMAIL -> "بريد إلكتروني (Email)"
    Barcode.TYPE_PHONE -> "رقم هاتف (Phone)"
    Barcode.TYPE_SMS -> "رسالة قصيرة (SMS)"
    else -> "نص / رمز شريطي (Barcode/Text)"
}
