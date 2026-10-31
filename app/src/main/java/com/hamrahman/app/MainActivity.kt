package com.hamrahman.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import java.util.UUID

data class Topic(val id: String = UUID.randomUUID().toString(), val name: String, val description: String = "", val linkedTopicIds: Set<String> = emptySet())
data class TopicPermission(val topicId: String, val targetTopicId: String, val canView: Boolean = false, val canEdit: Boolean = false, val canLogProgress: Boolean = false, val canViewDetails: Boolean = false)
data class AuditEntry(val id: String = UUID.randomUUID().toString(), val timestampMillis: Long = System.currentTimeMillis(), val action: String, val details: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { HamrahManApp() } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamrahManApp() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var topics by remember { mutableStateOf(listOf(Topic(name = "عادت‌های روزانه", description = "پیگیری کارهای روزمره"), Topic(name = "سلامتی", description = "هدف‌ها و پیشرفت شخصی"), Topic(name = "معاملات", description = "موضوع اختیاری برای ثبت معاملات"))) }
        var history by remember { mutableStateOf(listOf<AuditEntry>()) }
        var showAdd by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        var tab by remember { mutableIntStateOf(0) }
        MaterialTheme {
            Scaffold(
                topBar = { CenterAlignedTopAppBar(title = { Text("همراه من") }) },
                floatingActionButton = { if (tab == 0) FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, contentDescription = "افزودن موضوع") } },
                bottomBar = { NavigationBar {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Add, null) }, label = { Text("موضوع‌ها") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Delete, null) }, label = { Text("تاریخچه") })
                } }
            ) { padding ->
                if (tab == 0) {
                    Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                        Text("موضوع‌های من", style = MaterialTheme.typography.headlineSmall)
                        Text("موضوع‌های دلخواهت را اضافه کن؛ ارتباط‌ها و مجوزهای پیشرفته در مراحل بعدی توسعه می‌یابند.")
                        Spacer(Modifier.height(12.dp))
                        if (topics.isEmpty()) Text("هنوز موضوعی اضافه نکرده‌ای.", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(topics, key = { it.id }) { topic ->
                                ElevatedCard(Modifier.fillMaxWidth()) {
                                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(topic.name, style = MaterialTheme.typography.titleMedium)
                                            Text(topic.description)
                                        }
                                        IconButton(onClick = {
                                            topics = topics.filterNot { it.id == topic.id }
                                            history = listOf(AuditEntry(action = "حذف موضوع", details = "موضوع «${topic.name}» حذف شد.")) + history
                                        }) { Icon(Icons.Default.Delete, contentDescription = "حذف ${topic.name}") }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                        item { Text("تاریخچهٔ تغییرات", style = MaterialTheme.typography.headlineSmall); Text("تاریخچهٔ این نمونه تا زمان بسته‌شدن برنامه در حافظه می‌ماند.") }
                        items(history, key = { it.id }) { entry -> ListItem(headlineContent = { Text(entry.action) }, supportingContent = { Text(entry.details) }); HorizontalDivider() }
                    }
                }
            }
        }
        if (showAdd) AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("موضوع جدید") },
            text = { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام موضوع") }, singleLine = true) },
            confirmButton = { TextButton(onClick = {
                val clean = name.trim()
                if (clean.isNotEmpty()) {
                    topics = topics + Topic(name = clean)
                    history = listOf(AuditEntry(action = "افزودن موضوع", details = "موضوع «$clean» ایجاد شد.")) + history
                    name = ""; showAdd = false
                }
            }) { Text("افزودن") } },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("انصراف") } }
        )
    }
}
