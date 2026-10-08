package cl.duoc.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.registroDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "registro"
)

data class DatosGuardados(
    val nombre: String,
    val correo: String
)

class RegistroPreferences(private val context: Context) {
    private val claveNombre = stringPreferencesKey("nombre")
    private val claveCorreo = stringPreferencesKey("correo")

    suspend fun leer(): DatosGuardados {
        val prefs = context.registroDataStore.data.first()
        return DatosGuardados(
            nombre = prefs[claveNombre] ?: "",
            correo = prefs[claveCorreo] ?: ""
        )
    }

    suspend fun guardar(nombre: String, correo: String) {
        context.registroDataStore.edit { prefs ->
            prefs[claveNombre] = nombre
            prefs[claveCorreo] = correo
        }
    }
}
