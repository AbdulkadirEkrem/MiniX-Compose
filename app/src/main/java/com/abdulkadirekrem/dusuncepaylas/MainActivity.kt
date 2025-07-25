package com.abdulkadirekrem.dusuncepaylas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulkadirekrem.dusuncepaylas.ui.theme.DusuncePaylasTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null) {
            val intent = Intent(this, DusunceActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContent {
            DusuncePaylasTheme {
                GirisKayitEkrani(
                    onGiris = { email, sifre ->
                        auth.signInWithEmailAndPassword(email, sifre).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                Toast.makeText(this, "Hoşgeldin ${user?.email}", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, DusunceActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onKayit = { email, sifre ->
                        auth.createUserWithEmailAndPassword(email, sifre).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, DusunceActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GirisKayitEkrani(
    onGiris: (String, String) -> Unit,
    onKayit: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var sifre by remember { mutableStateOf("") }
    var sifreGorunur by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔽 LOGO GÖRÜNTÜSÜ
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Uygulama Logosu",
            modifier = Modifier
                .height(250.dp)
                .padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = sifre,
            onValueChange = { sifre = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (sifreGorunur) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (sifreGorunur) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { sifreGorunur = !sifreGorunur }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onGiris(email, sifre) },
            modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEAEAEA),
                contentColor = Color.Black
            ),shape = RoundedCornerShape(8.dp),border = BorderStroke(0.2.dp, Color.LightGray)
        ) {
            Text("Giriş Yap",fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onKayit(email, sifre) },
            modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEAEAEA),
                contentColor = Color.Black
            ),shape = RoundedCornerShape(8.dp),border = BorderStroke(0.2.dp, Color.LightGray)
        ) {
            Text("Kayıt Ol",fontSize = 18.sp)
        }
    }
}
