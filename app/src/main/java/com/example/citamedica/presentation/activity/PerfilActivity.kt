package com.example.citamedica.presentation.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citamedica.data.session.SessionManager
import com.example.citamedica.presentation.components.LoadingDialog
import com.example.citamedica.presentation.viewmodels.PerfilViewModel
import com.example.citamedica.ui.theme.CitaMedicaTheme

class PerfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitaMedicaTheme {
                PerfilScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onBack: () -> Unit = {},
    viewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Masculino") }

    LaunchedEffect(Unit) {
        val idUsuario = SessionManager(context).obtenerIdUsuario()
        if (idUsuario != -1) {
            viewModel.cargarPerfil(idUsuario)
        }
    }

    LaunchedEffect(viewModel.perfil) {
        viewModel.perfil?.let { p ->
            nombre = p.nombre.trim()
            correo = p.correo
            telefono = p.telefono
            edad = p.edad
            sexo = p.sexo.ifBlank { "Masculino" }
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorMostrado()
        }
    }

    LaunchedEffect(viewModel.mensajeActualizacion) {
        viewModel.mensajeActualizacion?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMensajeActualizacionMostrado()
        }
    }

    LoadingDialog(
        isVisible = viewModel.isLoading || viewModel.isActualizando,
        message = if (viewModel.isActualizando) "Guardando cambios..." else "Cargando perfil..."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Información Personal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = edad,
                onValueChange = { nuevo ->
                    if (nuevo.all { it.isDigit() }) edad = nuevo
                },
                label = { Text("Edad") },
                leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sexo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Wc, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                RadioButton(
                    selected = sexo == "Masculino",
                    onClick = { sexo = "Masculino" }
                )
                Text("Masculino", modifier = Modifier.clickable { sexo = "Masculino" })
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = sexo == "Femenino",
                    onClick = { sexo = "Femenino" }
                )
                Text("Femenino", modifier = Modifier.clickable { sexo = "Femenino" })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val idUsuario = SessionManager(context).obtenerIdUsuario()
                    if (idUsuario == -1) {
                        Toast.makeText(context, "Sesión inválida", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.actualizarPerfil(
                            idUsuario = idUsuario,
                            nombre = nombre.trim(),
                            edad = edad.trim(),
                            sexo = sexo,
                            telefono = telefono.trim()
                        )
                    }
                },
                enabled = !viewModel.isActualizando,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    CitaMedicaTheme {
        PerfilScreen()
    }
}
