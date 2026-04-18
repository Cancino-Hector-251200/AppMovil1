package com.example.citamedica

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citamedica.ui.theme.CitaMedicaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitaMedicaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var nombreCompleto by rememberSaveable { mutableStateOf("") }
    var correoElectronico by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    
    // Estados para errores
    var nombreError by remember { mutableStateOf(false) }
    var correoError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFF1976D2),
        unfocusedLabelColor = Color.Gray,
        focusedBorderColor = Color(0xFF1976D2),
        unfocusedBorderColor = Color.LightGray,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red,
        errorSupportingTextColor = Color.Red
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
                .alpha(0.6f)
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 350.dp),
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bienvenido a CitaMedica",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "inicie sesión",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    val fieldModifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 300.dp)

                    OutlinedTextField(
                        value = nombreCompleto,
                        onValueChange = { 
                            nombreCompleto = it
                            nombreError = false 
                        },
                        label = { Text("Nombre completo") },
                        colors = customTextFieldColors,
                        modifier = fieldModifier,
                        singleLine = true,
                        isError = nombreError,
                        supportingText = { if (nombreError) Text("El nombre es obligatorio") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = correoElectronico,
                        onValueChange = { 
                            correoElectronico = it
                            correoError = false
                        },
                        label = { Text("Correo electrónico") },
                        colors = customTextFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = fieldModifier,
                        singleLine = true,
                        isError = correoError,
                        supportingText = { 
                            if (correoError) {
                                val message = if (correoElectronico.isEmpty()) "El correo es obligatorio" else "Formato de correo inválido"
                                Text(message)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = false
                        },
                        label = { Text("Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = customTextFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        },
                        modifier = fieldModifier,
                        singleLine = true,
                        isError = passwordError,
                        supportingText = { if (passwordError) Text("La contraseña es obligatoria") }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { 
                            nombreError = nombreCompleto.isBlank()
                            correoError = correoElectronico.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correoElectronico).matches()
                            passwordError = password.isBlank()

                            if (!nombreError && !correoError && !passwordError) {
                                val intent = Intent(context, WerlcomeActivity::class.java)
                                context.startActivity(intent)
                            }
                        },
                        modifier = fieldModifier,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Entrar", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¿No tienes una cuenta?",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "Crear una cuenta",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.clickable {
                            val intent = Intent(context, RegisActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CitaMedicaTheme {
        LoginScreen()
    }
}
