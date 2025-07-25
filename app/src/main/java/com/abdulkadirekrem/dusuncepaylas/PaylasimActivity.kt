package com.abdulkadirekrem.dusuncepaylas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulkadirekrem.dusuncepaylas.ui.theme.DusuncePaylasTheme
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class PaylasimActivity : ComponentActivity() {
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DusuncePaylasTheme {
                var dusunce by remember { mutableStateOf("") }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {  Text(
                                text = "Opinion",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            ) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = dusunce,
                            onValueChange = { dusunce = it },
                            label = { Text("Düşünceni yaz...") },
                            placeholder = { Text("Bugün ne düşünüyorsun?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            maxLines = 8,
                            singleLine = false
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (dusunce.isBlank()) {
                                    Toast.makeText(this@PaylasimActivity, "Lütfen bir şey yaz.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val post = hashMapOf(
                                    "kullanici" to (auth.currentUser?.email ?: "Misafir"),
                                    "dusunce" to dusunce.trim(),
                                    "tarih" to Timestamp.now()
                                )

                                db.collection("Paylasimlar").add(post)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@PaylasimActivity, "Paylaşıldı ✅", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@PaylasimActivity, DusunceActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this@PaylasimActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                                    }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFCBCBCB),
                                contentColor = Color.Black
                            ),shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Paylaş", fontSize = 18.sp)
                        }

                    }
                }
            }
        }
    }
}
