package com.example.citamedica.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.citamedica.data.models.Horario
import com.example.citamedica.data.session.SessionManager
import com.example.citamedica.presentation.components.LoadingDialog
import com.example.citamedica.presentation.viewmodels.HorariosViewModel
import com.example.citamedica.ui.theme.CitaMedicaTheme
import java.util.Calendar

class HorariosActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val idDoctor = intent.getIntExtra(EXTRA_ID_DOCTOR, -1)
        val nombreDoctor = intent.getStringExtra(EXTRA_NOMBRE_DOCTOR) ?: "Doctor"
        val especialidadDoctor = intent.getStringExtra(EXTRA_ESPECIALIDAD) ?: ""

        setContent {
            CitaMedicaTheme {
                HorariosScreen(
                    idDoctor = idDoctor,
                    nombreDoctor = nombreDoctor,
                    especialidadDoctor = especialidadDoctor,
                    onBack = { finish() }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_ID_DOCTOR = "extra_id_doctor"
        private const val EXTRA_NOMBRE_DOCTOR = "extra_nombre_doctor"
        private const val EXTRA_ESPECIALIDAD = "extra_especialidad"

        fun nuevoIntent(
            context: Context,
            idDoctor: Int,
            nombreDoctor: String,
            especialidadDoctor: String
        ): Intent {
            return Intent(context, HorariosActivity::class.java).apply {
                putExtra(EXTRA_ID_DOCTOR, idDoctor)
                putExtra(EXTRA_NOMBRE_DOCTOR, nombreDoctor)
                putExtra(EXTRA_ESPECIALIDAD, especialidadDoctor)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorariosScreen(
    idDoctor: Int,
    nombreDoctor: String,
    especialidadDoctor: String,
    onBack: () -> Unit = {},
    viewModel: HorariosViewModel = viewModel()
) {
    val context = LocalContext.current
    var horarioSeleccionado by remember { mutableStateOf<Horario?>(null) }

    val diaDeHoy = remember { obtenerDiaActual() }

    LaunchedEffect(idDoctor) {
        if (idDoctor != -1) {
            viewModel.cargarHorarios(idDoctor)
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorMostrado()
        }
    }

    LaunchedEffect(viewModel.citaAgendada) {
        viewModel.citaAgendada?.let {
            Toast.makeText(
                context,
                it.mensaje ?: "Cita agendada correctamente",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.onCitaAgendadaManejada()
            val intent = Intent(context, WelcomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    LoadingDialog(
        isVisible = viewModel.isLoading || viewModel.isAgendando,
        message = if (viewModel.isAgendando) "Agendando cita..." else "Cargando horarios..."
    )

    if (horarioSeleccionado != null) {
        AgendarCitaDialog(
            nombreDoctor = nombreDoctor,
            horario = horarioSeleccionado!!,
            onCancelar = { horarioSeleccionado = null },
            onConfirmar = { motivo ->
                val idPaciente = SessionManager(context).obtenerIdPaciente()
                val horario = horarioSeleccionado!!
                horarioSeleccionado = null
                viewModel.agendarCita(
                    horario = horario,
                    idPaciente = idPaciente,
                    motivoCita = motivo
                )
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = { Text("Horarios disponibles") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
        ) {
            item {
                DoctorHeaderCard(
                    nombre = nombreDoctor,
                    especialidad = especialidadDoctor
                )
            }

            if (viewModel.horarios.isEmpty() && !viewModel.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Este doctor no tiene horarios registrados",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(viewModel.horarios, key = { it.idHorario }) { horario ->
                    HorarioCard(
                        horario = horario,
                        esHoy = esMismoDia(horario.diaSemana, diaDeHoy),
                        onClick = { horarioSeleccionado = horario }
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorHeaderCard(nombre: String, especialidad: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F1FC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre.trim(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                if (especialidad.isNotBlank()) {
                    Text(
                        text = especialidad,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Elige el día que prefieras",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun HorarioCard(horario: Horario, esHoy: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (esHoy) {
                EtiquetaHoy()
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = horario.diaSemana,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconoCapsula(icon = Icons.Default.CalendarToday)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconoCapsula(icon = Icons.Default.AccessTime)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${quitarSegundos(horario.horaInicio)} - ${quitarSegundos(horario.horaFin)}",
                            fontSize = 14.sp,
                            color = Color(0xFF374151),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@Composable
private fun IconoCapsula(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE8F1FC)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF1976D2),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun EtiquetaHoy() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF10B981))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Hoy",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AgendarCitaDialog(
    nombreDoctor: String,
    horario: Horario,
    onCancelar: () -> Unit,
    onConfirmar: (motivo: String) -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    var motivoError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Column {
                Text("Agendar cita")
                Text(
                    text = "${nombreDoctor.trim()} • ${horario.diaSemana}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "Horario: ${quitarSegundos(horario.horaInicio)} - ${quitarSegundos(horario.horaFin)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        text = {
            OutlinedTextField(
                value = motivo,
                onValueChange = {
                    motivo = it
                    motivoError = false
                },
                label = { Text("Motivo de la cita") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                isError = motivoError,
                supportingText = {
                    if (motivoError) Text("El motivo es obligatorio")
                }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (motivo.isBlank()) {
                    motivoError = true
                } else {
                    onConfirmar(motivo.trim())
                }
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

private fun quitarSegundos(horaTexto: String): String {
    val partes = horaTexto.split(":")
    return if (partes.size >= 2) "${partes[0]}:${partes[1]}" else horaTexto
}

private fun obtenerDiaActual(): String {
    return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "lunes"
        Calendar.TUESDAY -> "martes"
        Calendar.WEDNESDAY -> "miercoles"
        Calendar.THURSDAY -> "jueves"
        Calendar.FRIDAY -> "viernes"
        Calendar.SATURDAY -> "sabado"
        Calendar.SUNDAY -> "domingo"
        else -> ""
    }
}

private fun esMismoDia(diaHorario: String, diaHoy: String): Boolean {
    val normalizado = diaHorario.lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
    return normalizado == diaHoy
}

