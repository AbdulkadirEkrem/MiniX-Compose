package com.abdulkadirekrem.dusuncepaylas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulkadirekrem.dusuncepaylas.ui.theme.DusuncePaylasTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*

data class Paylasim(
    val dusunce: String = "",
    val kullanici: String = "",
    val tarih: Date = Date()
)

class DusunceActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val paylasimList = mutableStateListOf<Paylasim>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()

        setContent {
            DusuncePaylasTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Opinion",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp
                                    )
                                )
                            }
                            ,
                            actions = {
                                IconButton(onClick = {
                                    auth.signOut()
                                    startActivity(Intent(this@DusunceActivity, MainActivity::class.java))
                                    finish()
                                }) {
                                    Icon(imageVector = Icons.Default.Logout, contentDescription = "Çıkış Yap")
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            startActivity(Intent(this@DusunceActivity, PaylasimActivity::class.java))
                        },   containerColor = Color(0xFFE0E0E0),
                            contentColor = Color.Black ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Yeni Paylaşım")
                        }
                    },
                    content = { innerPadding ->
                        LazyColumn(
                            contentPadding = innerPadding,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(paylasimList) { paylasim ->
                                PaylasimKarti(paylasim = paylasim)
                            }
                        }
                    }
                )
            }
        }

        db.collection("Paylasimlar")
            .orderBy("tarih")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                paylasimList.clear()
                snapshot?.documents?.forEach { doc ->
                    val dusunce = doc.getString("dusunce") ?: ""
                    val kullanici = doc.getString("kullanici") ?: ""
                    val tarih = doc.getDate("tarih") ?: Date()

                    paylasimList.add(Paylasim(dusunce, kullanici, tarih))
                }
            }
    }
}
@Composable
fun PaylasimKarti(paylasim: Paylasim) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())

    val backgroundColor = if (isSystemInDarkTheme()) {
        Color(0xFF2B2B2B) // koyu gri (dark mode)
    } else {
        Color(0xFFFFFFFF) // açık gri (light mode)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        border = BorderStroke(0.2.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = paylasim.dusunce,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "👤 ${paylasim.kullanici}",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Text(
                text = "🕒 ${dateFormat.format(paylasim.tarih)}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
