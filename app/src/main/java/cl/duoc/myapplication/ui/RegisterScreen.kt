package cl.duoc.myapplication.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.myapplication.R
import cl.duoc.myapplication.navigation.Screen
import cl.duoc.myapplication.viewmodels.MainViewModel
import cl.duoc.myapplication.viewmodels.RegistroViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(mainViewModel: MainViewModel) {
    val viewModel: RegistroViewModel = viewModel()
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var intentos by rememberSaveable { mutableStateOf(0) }
    val puedeEnviar by remember {
        derivedStateOf {
            estado.nombre.isNotBlank() &&
                estado.correo.contains("@") &&
                estado.aceptaTerminos
        }
    }
    val colorBoton by animateColorAsState(
        targetValue = when {
            estado.guardando -> Color.Gray
            estado.guardado -> Color(0xFF2E7D32)
            puedeEnviar -> MaterialTheme.colorScheme.primary
            else -> Color(0xFF9E9E9E)
        },
        label = "colorBoton"
    )
    val paddingLogo by animateDpAsState(
        targetValue = if (estado.guardado) 8.dp else 40.dp,
        label = "paddingLogo"
    )
    val contexto = LocalContext.current
    var uriPendiente by remember { mutableStateOf<Uri?>(null) }

    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { guardada ->
        val pendiente = uriPendiente
        if (guardada && pendiente != null) {
            viewModel.onFotoTomada(pendiente.toString())
        }
        uriPendiente = null
    }

    fun abrirCamara() {
        val uri = viewModel.crearFoto()
        uriPendiente = uri
        camaraLauncher.launch(uri)
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Sin permiso de cámara no se puede tomar la foto")
            }
        }
    }

    fun pedirFoto() {
        val tieneCamara = contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        if (!tieneCamara) {
            scope.launch {
                snackbarHostState.showSnackbar("Este aparato no tiene cámara")
            }
            return
        }
        val permitido = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (permitido) {
            abrirCamara()
        } else {
            permisoLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val foto = remember(estado.fotoUri) {
        if (estado.fotoUri.isBlank()) {
            null
        } else {
            cargarFoto(contexto, estado.fotoUri)
        }
    }

    Box {
        Image(
            painterResource(R.drawable.images),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo App",
                modifier = Modifier.padding(paddingLogo)
            )

            if (foto != null) {
                Image(
                    bitmap = foto,
                    contentDescription = "Foto de registro",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(onClick = { pedirFoto() }) {
                Text(if (estado.fotoUri.isBlank()) "Tomar foto" else "Tomar otra foto")
            }

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre completo") },
                isError = estado.errores.nombre != null,
                supportingText = {
                    estado.errores.nombre?.let { mensaje ->
                        Text(mensaje)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo electrónico") },
                isError = estado.errores.correo != null,
                supportingText = {
                    estado.errores.correo?.let { mensaje ->
                        Text(mensaje)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = viewModel::onAceptarTerminosChange
                )
                Text("Acepto los términos")
            }

            estado.errores.aceptaTerminos?.let { mensaje ->
                Text(mensaje)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Vista previa en tiempo real")
            Text("Nombre: ${estado.nombre}")
            Text("Correo: ${estado.correo}")
            Text("Términos aceptados: ${if (estado.aceptaTerminos) "Sí" else "No"}")

            Text("Intentos: $intentos")
            Text(if (puedeEnviar) "Listo para enviar" else "Faltan datos")

            Button(
                onClick = {
                    intentos++
                    if (viewModel.validar()) {
                        viewModel.guardar()
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Errores detectados. Revisa los campos.")
                        }
                    }
                },
                enabled = !estado.guardando,
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton)
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Registrarse")
                }
            }

            AnimatedVisibility(visible = estado.guardado) {
                Text("Datos guardados en el teléfono")
            }

            Button(
                onClick = { mainViewModel.navigateTo(Screen.Home) }
            ) {
                Text("Volver")
            }

            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

private fun cargarFoto(contexto: Context, uri: String): ImageBitmap? {
    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
    val bitmap = contexto.contentResolver.openInputStream(Uri.parse(uri))?.use { entrada ->
        BitmapFactory.decodeStream(entrada, null, opciones)
    } ?: return null
    return bitmap.asImageBitmap()
}
