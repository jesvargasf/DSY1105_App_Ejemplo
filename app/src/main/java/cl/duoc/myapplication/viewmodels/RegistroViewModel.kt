package cl.duoc.myapplication.viewmodels

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.myapplication.data.RegistroPreferences
import java.io.File
import cl.duoc.myapplication.model.RegistroErrores
import cl.duoc.myapplication.model.RegistroUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = RegistroPreferences(application)
    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val guardado = preferences.leer()
            _estado.update {
                it.copy(nombre = guardado.nombre, correo = guardado.correo)
            }
        }
    }

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                guardado = false,
                errores = it.errores.copy(nombre = null)
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                guardado = false,
                errores = it.errores.copy(correo = null)
            )
        }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update {
            it.copy(
                aceptaTerminos = valor,
                guardado = false,
                errores = it.errores.copy(aceptaTerminos = null)
            )
        }
    }

    fun validar(): Boolean {
        val actual = _estado.value
        val errores = RegistroErrores(
            nombre = if (actual.nombre.isBlank()) "El nombre es obligatorio" else null,
            correo = when {
                actual.correo.isBlank() -> "El correo es obligatorio"
                !actual.correo.contains("@") -> "El correo debe incluir @"
                else -> null
            },
            aceptaTerminos = if (!actual.aceptaTerminos) "Debes aceptar los términos" else null
        )
        _estado.update { it.copy(errores = errores) }
        return errores.nombre == null && errores.correo == null && errores.aceptaTerminos == null
    }

    fun guardar() {
        val actual = _estado.value
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            delay(800)
            preferences.guardar(actual.nombre, actual.correo)
            _estado.update { it.copy(guardando = false, guardado = true) }
        }
    }

    fun crearFoto(): Uri {
        val aplicacion = getApplication<Application>()
        val directorio = File(aplicacion.cacheDir, "fotos")
        if (!directorio.exists()) {
            directorio.mkdirs()
        }
        val archivo = File(directorio, "foto-${System.currentTimeMillis()}.jpg")
        archivo.createNewFile()
        return FileProvider.getUriForFile(
            aplicacion,
            "cl.duoc.myapplication.fileprovider",
            archivo
        )
    }

    fun onFotoTomada(uri: String) {
        _estado.update { it.copy(fotoUri = uri) }
    }
}
