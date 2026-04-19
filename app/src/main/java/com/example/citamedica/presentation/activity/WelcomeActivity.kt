package com.example.citamedica.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import com.example.citamedica.R
import com.example.citamedica.data.models.Cita
import com.example.citamedica.data.models.MensajeResponse
import com.example.citamedica.data.repository.CitaMedicaRepository
import com.example.citamedica.data.session.SessionManager
import com.example.citamedica.presentation.components.LoadingDialog
import com.example.citamedica.presentation.viewmodels.CitasViewModel
import com.example.citamedica.ui.theme.CitaMedicaTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitaMedicaTheme {
                WelcomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    viewModel: CitasViewModel = viewModel()
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var citaAEliminar by remember { mutableStateOf<Cita?>(null) }
    var mostrarDialogoSalir by remember { mutableStateOf(false) }

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            mostrarDialogoSalir = true
        }
    }

    if (mostrarDialogoSalir) {
        ConfirmarSalirDialog(
            onCancelar = { mostrarDialogoSalir = false },
            onConfirmar = {
                mostrarDialogoSalir = false
                val idPaciente = SessionManager(context).obtenerIdPaciente()
                if (idPaciente != -1) {
                    eliminarTokenFcm(idPaciente)
                }
                (context as? Activity)?.finishAffinity()
            }
        )
    }

    val appIcon = remember {
        context.getDrawable(R.mipmap.ic_launcher)!!.toBitmap().asImageBitmap()
    }

    LaunchedEffect(Unit) {
        val idPaciente = SessionManager(context).obtenerIdPaciente()
        if (idPaciente != -1) {
            viewModel.cargarCitas(idPaciente)
            registrarTokenFcm(idPaciente)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val idPaciente = SessionManager(context).obtenerIdPaciente()
                if (idPaciente != -1) {
                    viewModel.cargarCitas(idPaciente)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorMostrado()
        }
    }

    LaunchedEffect(viewModel.citaEliminadaMensaje) {
        viewModel.citaEliminadaMensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onCitaEliminadaMensajeMostrado()
        }
    }

    LoadingDialog(
        isVisible = viewModel.isLoading || viewModel.isEliminando,
        message = if (viewModel.isEliminando) "Eliminando cita..." else "Cargando citas..."
    )

    citaAEliminar?.let { cita ->
        ConfirmarEliminarDialog(
            cita = cita,
            onCancelar = { citaAEliminar = null },
            onConfirmar = {
                viewModel.eliminarCita(cita.idCita)
                citaAEliminar = null
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = appIcon,
                        contentDescription = "Icono CitaMedica",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CitaMedica",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            val intent = Intent(context, PerfilActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Doctores") },
                    icon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            val intent = Intent(context, AgendarCitaActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    selected = false,
                    onClick = {
                        val session = SessionManager(context)
                        val idPaciente = session.obtenerIdPaciente()
                        if (idPaciente != -1) {
                            eliminarTokenFcm(idPaciente)
                        }
                        session.limpiar()
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF0F4F8),
            topBar = {
                TopAppBar(
                    title = { Text("CitaMedica") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            val intent = Intent(context, AgendarCitaActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agendar cita")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Mis Citas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Text(
                    text = "Próximas citas médicas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PullToRefreshBox(
                    isRefreshing = viewModel.isLoading,
                    onRefresh = {
                        val idPaciente = SessionManager(context).obtenerIdPaciente()
                        if (idPaciente != -1) {
                            viewModel.cargarCitas(idPaciente)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (viewModel.citas.isEmpty() && !viewModel.isLoading) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No tienes citas registradas",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.citas, key = { it.idCita }) { cita ->
                                CitaCard(
                                    cita = cita,
                                    onEliminar = { citaAEliminar = cita },
                                    onAgregarCalendario = {
                                        agregarCitaACalendario(context, cita)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CitaCard(
    cita: Cita,
    onEliminar: () -> Unit,
    onAgregarCalendario: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cita.nombreDoctor.trim(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
                EstadoBadge(estado = cita.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = cita.fecha,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = cita.hora,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar cita",
                        tint = Color(0xFFC62828)
                    )
                }
            }

            if (cita.estado.uppercase() == "ACEPTADA") {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onAgregarCalendario,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.EventAvailable,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Añadir a calendario",
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmarEliminarDialog(
    cita: Cita,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Eliminar cita") },
        text = {
            Text(
                "¿Seguro que deseas eliminar la cita con ${cita.nombreDoctor.trim()} " +
                        "el ${cita.fecha} a las ${cita.hora}?"
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Eliminar", color = Color(0xFFC62828))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
private fun ConfirmarSalirDialog(
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Salir de la aplicación") },
        text = { Text("¿Seguro que deseas salir?") },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Salir", color = Color(0xFFC62828))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
private fun EstadoBadge(estado: String) {
    val (bg, fg) = colorPorEstado(estado)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado,
            color = fg,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun agregarCitaACalendario(context: Context, cita: Cita) {
    val (inicioMillis, finMillis) = calcularHorarioCita(cita.fecha, cita.hora)
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, "Cita con ${cita.nombreDoctor.trim()}")
        putExtra(CalendarContract.Events.DESCRIPTION, cita.motivoCita)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicioMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, finMillis)
    }
    context.startActivity(intent)
}

private fun calcularHorarioCita(diaSemana: String, hora: String): Pair<Long, Long> {
    val diaObjetivo = when (diaSemana.lowercase()) {
        "lunes" -> Calendar.MONDAY
        "martes" -> Calendar.TUESDAY
        "miercoles", "miércoles" -> Calendar.WEDNESDAY
        "jueves" -> Calendar.THURSDAY
        "viernes" -> Calendar.FRIDAY
        "sabado", "sábado" -> Calendar.SATURDAY
        "domingo" -> Calendar.SUNDAY
        else -> Calendar.MONDAY
    }
    val partes = hora.split(":")
    val horas = partes.getOrNull(0)?.toIntOrNull() ?: 9
    val minutos = partes.getOrNull(1)?.toIntOrNull() ?: 0

    val cal = Calendar.getInstance()
    var diferencia = diaObjetivo - cal.get(Calendar.DAY_OF_WEEK)
    if (diferencia < 0) diferencia += 7
    cal.add(Calendar.DAY_OF_MONTH, diferencia)
    cal.set(Calendar.HOUR_OF_DAY, horas)
    cal.set(Calendar.MINUTE, minutos)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val inicio = cal.timeInMillis
    val fin = inicio + 60 * 60 * 1000L
    return inicio to fin
}

private fun registrarTokenFcm(idPaciente: Int) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { tarea ->
        if (!tarea.isSuccessful) {
            Log.e("WelcomeActivity", "No se pudo obtener token FCM", tarea.exception)
            return@addOnCompleteListener
        }
        val token = tarea.result ?: return@addOnCompleteListener
        Log.d("WelcomeActivity", "Token FCM obtenido: $token")

        CitaMedicaRepository().registrarTokenFcm(idPaciente, token)
            .enqueue(object : Callback<MensajeResponse> {
                override fun onResponse(
                    call: Call<MensajeResponse>,
                    response: Response<MensajeResponse>
                ) {
                    Log.d("WelcomeActivity", "Token registrado en servidor: ${response.code()}")
                }

                override fun onFailure(call: Call<MensajeResponse>, t: Throwable) {
                    Log.e("WelcomeActivity", "Error al registrar token FCM", t)
                }
            })
    }
}

private fun eliminarTokenFcm(idPaciente: Int) {
    CitaMedicaRepository().eliminarTokenFcm(idPaciente)
        .enqueue(object : Callback<MensajeResponse> {
            override fun onResponse(
                call: Call<MensajeResponse>,
                response: Response<MensajeResponse>
            ) {
                Log.d("WelcomeActivity", "Token eliminado en servidor: ${response.code()}")
            }

            override fun onFailure(call: Call<MensajeResponse>, t: Throwable) {
                Log.e("WelcomeActivity", "Error al eliminar token FCM", t)
            }
        })
}

private fun colorPorEstado(estado: String): Pair<Color, Color> = when (estado.uppercase()) {
    "PENDIENTE" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
    "ACEPTADA" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    "CANCELADA" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    else -> Color(0xFFEEEEEE) to Color(0xFF616161)
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    CitaMedicaTheme {
        WelcomeScreen()
    }
}
