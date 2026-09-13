package com.fastscanner.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "سياسة الخصوصية",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "نحن نضع خصوصيتك وأمان بياناتك في قمة أولوياتنا:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "1. استخدام الكاميرا:\nيطلب التطبيق إذن الوصول إلى الكاميرا لغرض واحد فقط وهو إجراء المسح الضوئي الفوري للرموز والباركود عبر الشاشة.\n\n" +
                            "2. معالجة البيانات على جهازك (On-Device):\nتتم معالجة الصور وقراءة الرموز بالكامل على جهاز هاتفك محلياً بدون إرسال أي صورة أو فيديو إلى أي سيرفر خارجي.\n\n" +
                            "3. عدم جمع أو تخزين البيانات:\nالتطبيق لا يقوم بتسجيل أي بيانات شخصية، أو تخزين الصور، أو تتبع موقعك الجغرافي.\n\n" +
                            "4. الأمان التام:\nالتطبيق مصمم ليعمل بشكل آمن تماماً وبسرعة فائقة دون الحاجة لأي صلاحيات غير ضرورية.\n\n" +
                            "لأي استفسارات أو ملاحظات، يمكنك التواصل مباشرة مع المطور عبر البريد الإلكتروني: retwan.tech@gmail.com",
                    lineHeight = 24.sp,
                    fontSize = 14.sp
                )
            }
        }
    }
}
